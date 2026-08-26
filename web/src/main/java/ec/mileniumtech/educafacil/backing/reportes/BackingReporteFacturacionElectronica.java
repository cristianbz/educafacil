package ec.mileniumtech.educafacil.backing.reportes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;

import ec.mileniumtech.educafacil.bean.reportes.BeanReporteFacturacionElectronica;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteReporteDto;
import ec.mileniumtech.educafacil.service.facade.FacturacionFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * Backing bean para el reporte de Facturaci&oacute;n Electr&oacute;nica.
 * KPIs: Facturas emitidas, Autorizadas SRI, Anuladas, Total Facturado.
 * Gr&aacute;ficos: barras (autorizadas vs anuladas por mes) y l&iacute;nea (monto por mes).
 * Tabla de comprobantes con detalle.
 *
 * @author Christian Baez — Jul 2026
 */
@Named
@ViewScoped
public class BackingReporteFacturacionElectronica implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingReporteFacturacionElectronica.class);

    @EJB
    @Getter
    private FacturacionFacade facturacionFacade;

    @Inject
    @Getter
    private BeanReporteFacturacionElectronica beanReportes;

    // -----------------------------------------------------------------------
    @PostConstruct
    public void init() {
        if (beanReportes.getFechaFiltro() == null || beanReportes.getFechaFiltro().isEmpty()) {
            beanReportes.setFechaFiltro(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        calcularMetricasYGraficos();
    }

    // -----------------------------------------------------------------------
    public void actualizarFiltro() {
        try {
            String period = beanReportes.getFechaFiltro();
            YearMonth.parse(period, DateTimeFormatter.ofPattern("yyyy-MM"));
            calcularMetricasYGraficos();
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Información",
                    "Reporte actualizado para el período " + period);
        } catch (DateTimeParseException e) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "El formato del período no es válido (yyyy-MM).");
        } catch (Exception e) {
            log.error("Error al actualizar filtro", e);
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
            String period = beanReportes.getFechaFiltro();
            YearMonth ym = YearMonth.parse(period, DateTimeFormatter.ofPattern("yyyy-MM"));
            int anio = ym.getYear();
            int mes  = ym.getMonthValue();

            // --- Rango del mes seleccionado ---
            LocalDate fechaInicio = ym.atDay(1);
            LocalDate fechaFin    = ym.atEndOfMonth();

            // --- Consultar comprobantes del mes (solo Factura para KPIs) ---
            List<ComprobanteReporteDto> facturas = facturacionFacade.buscarComprobantesPorFiltros(
                    fechaInicio, fechaFin, null, null, null, "Factura");
            if (facturas == null) facturas = new ArrayList<>();

            List<ComprobanteReporteDto> notasCredito = facturacionFacade.buscarComprobantesPorFiltros(
                    fechaInicio, fechaFin, null, null, null, "Nota de Credito");
            if (notasCredito == null) notasCredito = new ArrayList<>();

            List<ComprobanteReporteDto> retenciones = facturacionFacade.buscarComprobantesPorFiltros(
                    fechaInicio, fechaFin, null, null, null, "Retencion");
            if (retenciones == null) retenciones = new ArrayList<>();

            // --- Unir todos los comprobantes para la tabla ---
            List<ComprobanteReporteDto> todos = new ArrayList<>();
            todos.addAll(facturas);
            todos.addAll(notasCredito);
            todos.addAll(retenciones);
            // Ordenar por fecha descendente
            todos.sort((a, b) -> b.getFechaEmision().compareTo(a.getFechaEmision()));
            beanReportes.setListaComprobantes(todos);

            // --- KPIs (solo sobre facturas) ---
            int emitidas = facturas.size();
            long autorizadas = facturas.stream()
                    .filter(f -> "AUTORIZADO".equalsIgnoreCase(f.getEstado()))
                    .count();
            long anuladas = facturas.stream()
                    .filter(f -> "ANULADO".equalsIgnoreCase(f.getEstado()))
                    .count();
            BigDecimal totalFacturado = facturas.stream()
                    .filter(f -> f.getTotal() != null)
                    .map(ComprobanteReporteDto::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            beanReportes.setFacturasEmitidas(emitidas);
            beanReportes.setAutorizadasSri((int) autorizadas);
            beanReportes.setAnuladas((int) anuladas);
            beanReportes.setTotalFacturado(totalFacturado);

            // --- Gráficos ---
            generarBarChartComprobantesMensuales(anio, mes);
            generarLineChartMontoFacturado(anio, mes);

        } catch (Exception e) {
            log.error("Error al calcular métricas de facturación electrónica", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al procesar los datos.");
        }
    }

    // -----------------------------------------------------------------------
    /** Genera el BarChart: comprobantes autorizados vs anulados por mes (ene - mes seleccionado) */
    private void generarBarChartComprobantesMensuales(int anio, int mesLimite) {
        try {
            String[] nombresMeses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
            List<String> labels = new ArrayList<>();
            List<Number> autorizadosData = new ArrayList<>();
            List<Number> anuladosData = new ArrayList<>();

            for (int i = 1; i <= mesLimite; i++) {
                LocalDate ini = LocalDate.of(anio, i, 1);
                LocalDate fin = ini.withDayOfMonth(ini.lengthOfMonth());

                List<ComprobanteReporteDto> facturasMes = facturacionFacade.buscarComprobantesPorFiltros(
                        ini, fin, null, null, null, "Factura");
                if (facturasMes == null) facturasMes = new ArrayList<>();

                long aut = facturasMes.stream()
                        .filter(f -> "AUTORIZADO".equalsIgnoreCase(f.getEstado()))
                        .count();
                long anu = facturasMes.stream()
                        .filter(f -> "ANULADO".equalsIgnoreCase(f.getEstado()))
                        .count();

                labels.add(nombresMeses[i - 1]);
                autorizadosData.add(aut);
                anuladosData.add(anu);
            }

            // Dataset: Autorizadas
            BarChartDataSet autSet = new BarChartDataSet();
            autSet.setLabel("Autorizadas");
            autSet.setData(new ArrayList<>(autorizadosData));
            autSet.setBackgroundColor(Arrays.asList("#10b981"));
            autSet.setBorderColor(Arrays.asList("#059669"));
            autSet.setBorderWidth(1);

            // Dataset: Anuladas
            BarChartDataSet anuSet = new BarChartDataSet();
            anuSet.setLabel("Anuladas");
            anuSet.setData(new ArrayList<>(anuladosData));
            anuSet.setBackgroundColor(Arrays.asList("#ef4444"));
            anuSet.setBorderColor(Arrays.asList("#dc2626"));
            anuSet.setBorderWidth(1);

            ChartData data = new ChartData();
            data.addChartDataSet(autSet);
            data.addChartDataSet(anuSet);
            data.setLabels(labels);

            // Opciones
            BarChartOptions options = new BarChartOptions();

            Legend legend = new Legend();
            legend.setDisplay(true);
            legend.setPosition("bottom");
            LegendLabel ll = new LegendLabel();
            ll.setFontColor("#374151");
            ll.setFontSize(12);
            legend.setLabels(ll);
            options.setLegend(legend);

            Tooltip t = new Tooltip();
            t.setEnabled(true);
            options.setTooltip(t);

            Title title = new Title();
            title.setDisplay(false);
            options.setTitle(title);

            BarChartModel model = new BarChartModel();
            model.setData(data);
            model.setOptions(options);
            beanReportes.setComprobantesMensualesModel(model);

        } catch (Exception e) {
            log.error("Error al generar bar chart comprobantes mensuales", e);
        }
    }

    // -----------------------------------------------------------------------
    /** Genera el LineChart: monto facturado por mes (ene - mes seleccionado) */
    private void generarLineChartMontoFacturado(int anio, int mesLimite) {
        try {
            String[] nombresMeses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
            List<String> labels = new ArrayList<>();
            List<Double> montos = new ArrayList<>();

            for (int i = 1; i <= mesLimite; i++) {
                LocalDate ini = LocalDate.of(anio, i, 1);
                LocalDate fin = ini.withDayOfMonth(ini.lengthOfMonth());

                List<ComprobanteReporteDto> facturasMes = facturacionFacade.buscarComprobantesPorFiltros(
                        ini, fin, null, null, null, "Factura");
                if (facturasMes == null) facturasMes = new ArrayList<>();

                double totalMes = facturasMes.stream()
                        .filter(f -> f.getTotal() != null)
                        .mapToDouble(f -> f.getTotal().doubleValue())
                        .sum();

                labels.add(nombresMeses[i - 1]);
                montos.add(totalMes);
            }

            LineChartDataSet dataset = new LineChartDataSet();
            dataset.setLabel("Monto Facturado ($)");
            dataset.setData(new ArrayList<>(montos));
            dataset.setBorderColor("#3b82f6");
            dataset.setBackgroundColor("rgba(59, 130, 246, 0.12)");
            dataset.setTension(0.4);
            dataset.setPointRadius(5);
            dataset.setPointBackgroundColor("#3b82f6");
            dataset.setFill(true);

            ChartData data = new ChartData();
            data.addChartDataSet(dataset);
            data.setLabels(labels);

            LineChartOptions options = new LineChartOptions();

            Title title = new Title();
            title.setDisplay(false);
            options.setTitle(title);

            Tooltip tooltip = new Tooltip();
            tooltip.setEnabled(true);
            options.setTooltip(tooltip);

            Legend legend = new Legend();
            legend.setDisplay(true);
            legend.setPosition("bottom");
            LegendLabel ll = new LegendLabel();
            ll.setFontColor("#374151");
            ll.setFontSize(13);
            legend.setLabels(ll);
            options.setLegend(legend);

            LineChartModel model = new LineChartModel();
            model.setData(data);
            model.setOptions(options);
            beanReportes.setMontoFacturadoMensualModel(model);

        } catch (Exception e) {
            log.error("Error al generar line chart monto facturado", e);
        }
    }

    // -----------------------------------------------------------------------
    // Métodos helper usados desde la vista para formatear datos de la tabla

    /** Formatea la fecha de emisión */
    public String formatFecha(ComprobanteReporteDto dto) {
        if (dto == null || dto.getFechaEmision() == null) return "-";
        return dto.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /** Formatea el monto con signo $ */
    public String formatMonto(ComprobanteReporteDto dto) {
        if (dto == null || dto.getTotal() == null) return "$0.00";
        return "$" + String.format("%,.2f", dto.getTotal());
    }

    /** Color badge para el estado SRI */
    public String colorEstado(ComprobanteReporteDto dto) {
        if (dto == null || dto.getEstado() == null) return "#f3f4f6";
        return switch (dto.getEstado().toUpperCase()) {
            case "AUTORIZADO" -> "#d1fae5";
            case "ANULADO"    -> "#fef2f2";
            case "EN_PROCESO" -> "#e0e7ff";
            case "ENVIADO"    -> "#cffafe";
            case "PENDIENTE"  -> "#fef9c3";
            case "RECHAZADO"  -> "#fee2e2";
            default           -> "#f3f4f6";
        };
    }

    /** Color texto para el estado SRI */
    public String colorTextoEstado(ComprobanteReporteDto dto) {
        if (dto == null || dto.getEstado() == null) return "#374151";
        return switch (dto.getEstado().toUpperCase()) {
            case "AUTORIZADO" -> "#065f46";
            case "ANULADO"    -> "#991b1b";
            case "EN_PROCESO" -> "#3730a3";
            case "ENVIADO"    -> "#155e75";
            case "PENDIENTE"  -> "#92400e";
            case "RECHAZADO"  -> "#991b1b";
            default           -> "#374151";
        };
    }

    /** Label amigable del estado */
    public String labelEstado(ComprobanteReporteDto dto) {
        if (dto == null || dto.getEstado() == null) return "-";
        return switch (dto.getEstado().toUpperCase()) {
            case "AUTORIZADO" -> "Autorizado";
            case "ANULADO"    -> "Anulado";
            case "EN_PROCESO" -> "En proceso";
            case "ENVIADO"    -> "Enviado (pendiente autorización)";
            case "PENDIENTE"  -> "Pendiente";
            case "RECHAZADO"  -> "Rechazado";
            default           -> dto.getEstado();
        };
    }

    /** Concepto: tipo de comprobante + número */
    public String obtenerConcepto(ComprobanteReporteDto dto) {
        if (dto == null) return "-";
        String tipo = dto.getTipoComprobante() != null ? dto.getTipoComprobante() : "Comprobante";
        String num  = dto.getNumero() != null ? dto.getNumero() : "";
        return tipo + " " + num;
    }

    /** Clave de acceso truncada para mejor visualización */
    public String claveAccesoTruncada(ComprobanteReporteDto dto) {
        if (dto == null || dto.getClaveAcceso() == null) return "-";
        String ca = dto.getClaveAcceso();
        if (ca.length() > 25) {
            return ca.substring(0, 12) + "..." + ca.substring(ca.length() - 8);
        }
        return ca;
    }
}
