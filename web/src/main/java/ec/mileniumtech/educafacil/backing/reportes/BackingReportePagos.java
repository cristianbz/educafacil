package ec.mileniumtech.educafacil.backing.reportes;
/**
 * @author Christian Baez Jul 2026
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.pie.PieChartOptions;

import ec.mileniumtech.educafacil.bean.reportes.BeanReportesPagos;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.service.facade.ContabilidadFacade;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumFormaPago;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * Backing bean para el dashboard Reporte de Pagos.
 * Genera KPIs y produce modelos PrimeFaces Charts
 * (LineChart de ingresos mensuales y PieChart de métodos de pago).
 */
@Named
@ViewScoped
public class BackingReportePagos implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingReportePagos.class);

    @EJB
    @Getter
    private ContabilidadFacade contabilidadFacade;
    
    @EJB
    @Getter
    private MatriculaFacade matriculaFacade;

    @Inject
    @Getter
    private BeanReportesPagos beanReportes;

    // -----------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (getBeanReportes().getFechaFiltro() == null || getBeanReportes().getFechaFiltro().isEmpty()) {
            int anioActual = Calendar.getInstance().get(Calendar.YEAR);
            int mesActual  = Calendar.getInstance().get(Calendar.MONTH) + 1;
            getBeanReportes().setFechaFiltro(anioActual + "-" + String.format("%02d", mesActual));
        }
        calcularMetricasYGraficos();
    }

    // -----------------------------------------------------------------------
    public void actualizarFiltro() {
        try {
            String period = getBeanReportes().getFechaFiltro();
            String[] parts = period.split("-");
            int mes = Integer.parseInt(parts[1]);
            if (mes >= 1 && mes <= 12) {
                calcularMetricasYGraficos();
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Información",
                        "Reporte actualizado para el período " + period);
            } else {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "El mes seleccionado no es válido.");
            }
        } catch (Exception e) {
            log.error("Error al actualizar filtro de pagos", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el reporte.");
        }
    }

    // -----------------------------------------------------------------------
    public void exportarReporte() {
        Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Exportación", "El reporte se está exportando.");
    }

    // -----------------------------------------------------------------------
    private void calcularMetricasYGraficos() {
        try {
            String period = getBeanReportes().getFechaFiltro();
            String[] parts = period.split("-");
            int anio = Integer.parseInt(parts[0]);
            int mes  = Integer.parseInt(parts[1]);

            // --- Rango del mes seleccionado ---
            Calendar cal = Calendar.getInstance();
            cal.set(anio, 0, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date fechaInicio = cal.getTime();

            cal.set(anio, mes-1, 1, 0, 0, 0);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date fechaFin = cal.getTime();

            // --- Rango YTD (1 Ene → fin del mes seleccionado) ---
            Calendar calYtdInicio = Calendar.getInstance();
            calYtdInicio.set(anio, 0, 1, 0, 0, 0);
            calYtdInicio.set(Calendar.MILLISECOND, 0);
            Date fechaYtdInicio = calYtdInicio.getTime();

            // --- Consultas al DAO ---
            List<DtoFlujoDinero> ingresosMes = contabilidadFacade.buscaIngresosReporteria(fechaInicio, fechaFin);
            if (ingresosMes == null) ingresosMes = new ArrayList<>();

            List<DtoFlujoDinero> ingresosYtd = contabilidadFacade.buscaIngresosReporteria(fechaYtdInicio, fechaFin);
            if (ingresosYtd == null) ingresosYtd = new ArrayList<>();

            List<Pagos> todosPagos = contabilidadFacade.listarTodosLosPagosPorRangoFechas(fechaInicio,fechaFin);
            if (todosPagos == null) todosPagos = new ArrayList<>();

            // --- KPI: Total Cobrado en el mes ---
            double totalCobrado = ingresosMes.stream().mapToDouble(DtoFlujoDinero::getValor).sum();
            getBeanReportes().setTotalCobrado(totalCobrado);

            // --- KPI: Ingresos YTD ---
            double totalYtd = ingresosYtd.stream().mapToDouble(DtoFlujoDinero::getValor).sum();
            getBeanReportes().setIngresosYtd(totalYtd);

            // --- KPI: Pendiente de cobro (cuotas pendientes del mes) ---
            // Aproximación: pagos registrados sin factura en el período
            long facturasAbiertas = todosPagos.stream()
                    .filter(p -> p.getPagoFecha() != null
                            && !p.getPagoFecha().before(fechaInicio)
                            && !p.getPagoFecha().after(fechaFin)
                            && (p.getPagoNumeroFactura() == null || p.getPagoNumeroFactura().isEmpty()))
                    .count();

            BigDecimal pendiente = todosPagos.stream()
                    .filter(p -> p.getPagoFecha() != null
                            && !p.getPagoFecha().before(fechaInicio)
                            && !p.getPagoFecha().after(fechaFin)
                            && (p.getPagoNumeroFactura() == null || p.getPagoNumeroFactura().isEmpty()))
                    .flatMap(p -> (p.getDetallePagos() != null ? p.getDetallePagos().stream() : new ArrayList<DetallePagos>().stream()))
                    // CORRECCIÓN 1: Cambiamos mapToDouble por map y manejamos el nulo con BigDecimal.ZERO
                    .map(d -> d.getDepaValor() != null ? d.getDepaValor() : BigDecimal.ZERO)
                    // CORRECCIÓN 2: Reemplazamos .sum() por .reduce()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            getBeanReportes().setPendienteCobro(pendiente);
            getBeanReportes().setFacturasAbiertas((int) facturasAbiertas);

            // --- KPI: Mora (detallepagos inactivos del mes) ---
            BigDecimal mora = todosPagos.stream()
                    .filter(p -> p.getPagoFecha() != null
                            && !p.getPagoFecha().before(fechaInicio)
                            && !p.getPagoFecha().after(fechaFin))
                    .flatMap(p -> (p.getDetallePagos() != null ? p.getDetallePagos().stream() : new ArrayList<DetallePagos>().stream()))
                    .filter(d -> !d.isDepaEstado())
                    // CORRECCIÓN 1: Cambiamos mapToDouble por map y usamos BigDecimal.ZERO para nulos
                    .map(d -> d.getDepaValor() != null ? d.getDepaValor() : BigDecimal.ZERO)
                    // CORRECCIÓN 2: Reemplazamos .sum() por .reduce()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long alumnosEnMora = todosPagos.stream()
                    .filter(p -> p.getPagoFecha() != null
                            && !p.getPagoFecha().before(fechaInicio)
                            && !p.getPagoFecha().after(fechaFin)
                            && p.getDetallePagos() != null
                            && p.getDetallePagos().stream().anyMatch(d -> !d.isDepaEstado()))
                    .map(p -> p.getMatricula() != null ? p.getMatricula().getMatrId() : -1)
                    .distinct().count();
            getBeanReportes().setTotalMora(mora);
            getBeanReportes().setAlumnosEnMora((int) alumnosEnMora);

            // --- Tendencias (comparación con mes anterior) ---
            Calendar calPrev = Calendar.getInstance();
            calPrev.set(anio, mes - 2, 1, 0, 0, 0);
            calPrev.set(Calendar.MILLISECOND, 0);
            Date fechaPrevInicio = calPrev.getTime();
            calPrev.set(Calendar.DAY_OF_MONTH, calPrev.getActualMaximum(Calendar.DAY_OF_MONTH));
            calPrev.set(Calendar.HOUR_OF_DAY, 23); calPrev.set(Calendar.MINUTE, 59); calPrev.set(Calendar.SECOND, 59);
            Date fechaPrevFin = calPrev.getTime();

            List<DtoFlujoDinero> ingresosPrev = contabilidadFacade.buscaIngresosReporteria(fechaPrevInicio, fechaPrevFin);
            if (ingresosPrev == null) ingresosPrev = new ArrayList<>();
            double totalCobradoPrev = ingresosPrev.stream().mapToDouble(DtoFlujoDinero::getValor).sum();

            getBeanReportes().setTendenciaCobrado(calcularTendenciaStr(totalCobrado, totalCobradoPrev));
            getBeanReportes().setTendenciaCobradoSubio(totalCobrado >= totalCobradoPrev);
            getBeanReportes().setTendenciaMora(calcularTendenciaStr(mora.doubleValue(), 0));
            getBeanReportes().setTendenciaMoraSubio(false);
            getBeanReportes().setTendenciaYtd(calcularTendenciaStr(totalYtd, totalCobradoPrev * mes));
            getBeanReportes().setTendenciaYtdSubio(true);

            // --- Gráficos PrimeFaces Charts ---
            generarModeloIngresosMensuales(anio, mes);
            generarModeloMetodosPago(todosPagos, fechaInicio, fechaFin);

            // --- Pagos recientes (últimos 10 del mes) ---
            final Date fInicio = fechaInicio;
            final Date fFin    = fechaFin;
            List<Pagos> recientes = todosPagos.stream()
                    .filter(p -> p.getPagoFecha() != null
                            && !p.getPagoFecha().before(fInicio)
                            && !p.getPagoFecha().after(fFin))
                    .sorted((a, b) -> b.getPagoFecha().compareTo(a.getPagoFecha()))
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());
            getBeanReportes().setListaPagosRecientes(recientes);

        } catch (Exception e) {
            log.error("Error al calcular métricas de pagos", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al procesar los datos de pagos.");
        }
    }

    // -----------------------------------------------------------------------
    /** Genera el LineChartModel para PrimeFaces con series Cobrado y Pendiente */
    private void generarModeloIngresosMensuales(int anio, int mesLimite) {
        try {
            String[] nombresMeses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
//            int limitMonth = (Calendar.getInstance().get(Calendar.YEAR) == anio) ? mesLimite : 12;
            int limitMonth = mesLimite;

            // Acumular cobrado por mes
            List<Double> cobradoValues   = new ArrayList<>();
            List<Double> pendienteValues = new ArrayList<>();
            List<String> labels          = new ArrayList<>();

            Calendar calMes = Calendar.getInstance();
            for (int i = 1; i <= limitMonth; i++) {
                calMes.set(anio, i - 1, 1, 0, 0, 0); calMes.set(Calendar.MILLISECOND, 0);
                Date ini = calMes.getTime();
                calMes.set(Calendar.DAY_OF_MONTH, calMes.getActualMaximum(Calendar.DAY_OF_MONTH));
                calMes.set(Calendar.HOUR_OF_DAY, 23); calMes.set(Calendar.MINUTE, 59); calMes.set(Calendar.SECOND, 59);
                Date fin = calMes.getTime();

                List<DtoFlujoDinero> lista = contabilidadFacade.buscaIngresosReporteria(ini, fin);
                double totalMes = (lista != null) ? lista.stream().mapToDouble(DtoFlujoDinero::getValor).sum() : 0.0;
                List<DtoFlujoDinero> deudores= matriculaFacade.buscaDeudasPagosReporteria(ini, fin);
                double totalDeuda = (deudores != null) ? deudores.stream().mapToDouble(DtoFlujoDinero::getValor).sum() : 0.0;
                cobradoValues.add(totalMes);
                pendienteValues.add(totalDeuda);
                labels.add(nombresMeses[i - 1]);
            }

            // Crear dataset de Cobrado
            LineChartDataSet cobradoSet = new LineChartDataSet();
            cobradoSet.setLabel("Cobrado");
            cobradoSet.setData(new ArrayList<>(cobradoValues));
            cobradoSet.setBorderColor("#3b82f6");
            cobradoSet.setBackgroundColor("rgba(59, 130, 246, 0.12)");
            cobradoSet.setTension(0.4);
            cobradoSet.setPointRadius(5);
            cobradoSet.setPointBackgroundColor("#3b82f6");
            cobradoSet.setFill(true);

            // Crear dataset de Pendiente
            LineChartDataSet pendienteSet = new LineChartDataSet();
            pendienteSet.setLabel("Pendiente");
            pendienteSet.setData(new ArrayList<>(pendienteValues));
            pendienteSet.setBorderColor("#f59e0b");
            pendienteSet.setBackgroundColor("rgba(245, 158, 11, 0.05)");
            pendienteSet.setTension(0.4);
            pendienteSet.setPointRadius(5);
            pendienteSet.setPointBackgroundColor("#f59e0b");
            pendienteSet.setBorderDash(Arrays.asList(6, 4));
            pendienteSet.setFill(true);

            // Armar ChartData
            ChartData data = new ChartData();
            data.addChartDataSet(cobradoSet);
            data.addChartDataSet(pendienteSet);
            data.setLabels(labels);

            // Opciones del gráfico
            LineChartOptions options = new LineChartOptions();

            // Título
            Title title = new Title();
            title.setDisplay(false);
            options.setTitle(title);

            // Tooltip
            Tooltip tooltip = new Tooltip();
            tooltip.setEnabled(true);
            options.setTooltip(tooltip);

            // Leyenda
            Legend legend = new Legend();
            legend.setDisplay(true);
            legend.setPosition("bottom");
            LegendLabel legendLabel = new LegendLabel();
            legendLabel.setFontColor("#374151");
            legendLabel.setFontSize(13);
            legend.setLabels(legendLabel);
            options.setLegend(legend);

            // Asignar modelo
            LineChartModel model = new LineChartModel();
            model.setData(data);
            model.setOptions(options);
            getBeanReportes().setIngresosMensualesModel(model);

        } catch (Exception e) {
            log.error("Error al generar modelo ingresos mensuales", e);
            getBeanReportes().setIngresosMensualesModel(new LineChartModel());
        }
    }

    // -----------------------------------------------------------------------
    /** Genera el PieChartModel para PrimeFaces con distribución de métodos de pago */
    private void generarModeloMetodosPago(List<Pagos> todosPagos, Date fechaInicio, Date fechaFin) {
        try {
            Map<String, Double> metodos = new HashMap<>();

            for (Pagos pago : todosPagos) {
                if (pago.getPagoFecha() == null
                        || pago.getPagoFecha().before(fechaInicio)
                        || pago.getPagoFecha().after(fechaFin)) continue;
                if (pago.getDetallePagos() == null) continue;

                for (DetallePagos dp : pago.getDetallePagos()) {
                    String metodo = (dp.getDepaFormaPago() != null && !dp.getDepaFormaPago().isEmpty())
                            ? dp.getDepaFormaPago() : "Otros";
//                    metodo=EnumFormaPago.EFECTIVO.getCodigo().equals(metodo)?EnumFormaPago.EFECTIVO.getLabel():EnumFormaPago.TARJETA.getCodigo().equals(metodo)?EnumFormaPago.TARJETA.getLabel():EnumFormaPago.TRANSFERENCIA.getCodigo().equals(metodo)?EnumFormaPago.TRANSFERENCIA.getLabel():"Otro";
                    metodo = EnumFormaPago.obtenerLabelPorCodigo(metodo);
                    double val = dp.getDepaValor() != null ? dp.getDepaValor().doubleValue() : 0.0;
                    metodos.merge(metodo, val, Double::sum);
                }
            }

            // Si no hay datos reales, mostramos categorías de ejemplo
            if (metodos.isEmpty()) {
                metodos.put("Transferencia", 48.0);
                metodos.put("Tarjeta Crédito", 27.0);
                metodos.put("Efectivo", 15.0);
                metodos.put("Débito Automático", 10.0);
            }

            List<String> labels = new ArrayList<>(metodos.keySet());
            List<Number> values = metodos.values().stream().map(Double::valueOf).collect(Collectors.toList());

            // Colores para cada segmento
            List<String> colors = Arrays.asList(
                    "#3b82f6", "#10b981", "#f59e0b", "#a855f7", "#ef4444", "#6366f1",
                    "#ec4899", "#14b8a6", "#f97316", "#84cc16"
            );

            // Crear dataset
            PieChartDataSet dataSet = new PieChartDataSet();
            dataSet.setData(new ArrayList<>(values));
            dataSet.setBackgroundColor(colors.subList(0, Math.min(colors.size(), labels.size())));

            // Armar ChartData
            ChartData data = new ChartData();
            data.addChartDataSet(dataSet);
            data.setLabels(labels);

            // Opciones
            PieChartOptions options = new PieChartOptions();

            // Leyenda
            Legend legend = new Legend();
            legend.setDisplay(true);
            legend.setPosition("bottom");
            LegendLabel legendLabel = new LegendLabel();
            legendLabel.setFontColor("#374151");
            legendLabel.setFontSize(12);
            legend.setLabels(legendLabel);
            options.setLegend(legend);

            // Tooltip
            Tooltip tooltip = new Tooltip();
            tooltip.setEnabled(true);
            options.setTooltip(tooltip);

            // Asignar modelo
            PieChartModel model = new PieChartModel();
            model.setData(data);
            model.setOptions(options);
            getBeanReportes().setMetodosPagoModel(model);

        } catch (Exception e) {
            log.error("Error al generar modelo métodos de pago", e);
            getBeanReportes().setMetodosPagoModel(new PieChartModel());
        }
    }

    // -----------------------------------------------------------------------
    private String calcularTendenciaStr(double actual, double anterior) {
        if (anterior == 0) {
            return actual > 0 ? "▲ 100% vs mes anterior" : "0% vs mes anterior";
        }
        double diff = (actual - anterior) / anterior * 100.0;
        if (diff >= 0) {
            return String.format("▲ %.0f%% vs mes anterior", diff);
        } else {
            return String.format("▼ %.0f%% vs mes anterior", Math.abs(diff));
        }
    }
  
    // -----------------------------------------------------------------------
    /** Formatea la fecha del pago para mostrar en la tabla */
    public String formatFechaPago(Pagos pago) {
        if (pago == null || pago.getPagoFecha() == null) return "-";
        return new SimpleDateFormat("dd/MM/yyyy").format(pago.getPagoFecha());
    }

    /** Devuelve el valor total de un pago sumando sus detalles */
    public BigDecimal obtenerValorTotalPago(Pagos pago) {
        if (pago == null || pago.getDetallePagos() == null) {
            return BigDecimal.ZERO;
        }
        
        return pago.getDetallePagos().stream()
                // Si el valor es null, usamos BigDecimal.ZERO como respaldo seguro
                .map(d -> d.getDepaValor() != null ? d.getDepaValor() : BigDecimal.ZERO)
                // Sumamos todos los BigDecimal usando reduce
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Devuelve la forma de pago del primer detalle (si existe) */
    public String obtenerMetodoPago(Pagos pago) {
        if (pago == null || pago.getDetallePagos() == null || pago.getDetallePagos().isEmpty()) return "-";
        String metodo = pago.getDetallePagos().get(0).getDepaFormaPago();
        metodo=EnumFormaPago.obtenerLabelPorCodigo(metodo);
        return (metodo != null && !metodo.isEmpty()) ? metodo : "-";
    }

    /** Devuelve colores badge para el método de pago */
    public String obtenerColorMetodo(Pagos pago) {
        String m = obtenerMetodoPago(pago).toLowerCase();
        if (m.contains("transfer"))    return "#eff6ff";
        if (m.contains("tarjeta"))     return "#dcfce7";
        if (m.contains("efectivo"))    return "#fef3c7";
        if (m.contains("d\u00e9bito") || m.contains("debito")) return "#f3e8ff";
        return "#f3f4f6";
    }

    /** Devuelve el estado del pago: Pagado o Pendiente */
    public String obtenerEstadoPago(Pagos pago) {
        if (pago == null || pago.getDetallePagos() == null || pago.getDetallePagos().isEmpty())
            return "Pendiente";
        boolean todosActivos = pago.getDetallePagos().stream().allMatch(DetallePagos::isDepaEstado);
        return todosActivos ? "Pagado" : "Pendiente";
    }

    /** Colores badge de estado */
    public String obtenerColorEstado(Pagos pago) {
        return "Pagado".equals(obtenerEstadoPago(pago)) ? "#d1fae5" : "#fef9c3";
    }

    public String obtenerColorTextoEstado(Pagos pago) {
        return "Pagado".equals(obtenerEstadoPago(pago)) ? "#065f46" : "#92400e";
    }

    /** Nombre del alumno del pago */
    public String obtenerNombreAlumno(Pagos pago) {
        try {
            if (pago == null || pago.getMatricula() == null) return "-";
            var persona = pago.getMatricula().getEstudiante().getPersona();
            return persona.getPersApellidos() + " " + persona.getPersNombres();
        } catch (Exception e) {
            return "-";
        }
    }

    /** Comprobante formateado (usa pagoNumeroFactura o genera uno con el ID) */
    public String obtenerComprobante(Pagos pago) {
        if (pago == null) return "-";
        if (pago.getPagoNumeroFactura() != null && !pago.getPagoNumeroFactura().isEmpty()) {
            return pago.getPagoNumeroFactura();
        }
        return "PAG-" + String.format("%08d", pago.getPagoId());
    }

    /** Concepto del pago (observación o mensualidad por defecto) */
    public String obtenerConcepto(Pagos pago) {
        if (pago == null) return "-";
        if (pago.getPagoObservacion() != null && !pago.getPagoObservacion().isEmpty()) {
            return pago.getPagoObservacion();
        }
        return "Pago registrado";
    }
}
