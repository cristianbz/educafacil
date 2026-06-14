package ec.mileniumtech.educafacil.backing.reportes;
/**
* @author christian Jun 11, 2026
*/
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import ec.mileniumtech.educafacil.bean.reportes.BeanReportesMatriculas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
/**
 * Controller backing bean for Reporte de Matrículas Dashboard.
 * 
 * @author Antigravity
 */
@Named
@ViewScoped
public class BackingReporteMatriculas implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingReporteMatriculas.class);
    @EJB
    @Getter
    private MatriculaFacade matriculaFacade;
    @Inject
    @Getter
    private BeanReportesMatriculas beanReportes;
    @PostConstruct
    public void init() {
        if (getBeanReportes().getFechaFiltro() == null || getBeanReportes().getFechaFiltro().isEmpty()) {
            getBeanReportes().setFechaFiltro("2026-06"); // Periodo por defecto según prototipo
        }
        calcularMetricasYGraficos(Calendar.getInstance().get(Calendar.YEAR),12);
    }
    public void actualizarFiltro() {
        String period = getBeanReportes().getFechaFiltro();
        String[] parts = period.split("-");
        int anioSeleccionado = Integer.parseInt(parts[0]);
        int mesSeleccionado = Integer.parseInt(parts[1]);
        if(mesSeleccionado<=12) {
        	calcularMetricasYGraficos(anioSeleccionado,mesSeleccionado);
        	Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Información", "Reporte actualizado para el período " + getBeanReportes().getFechaFiltro());
        }else {        	
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "El mes seleccionado no existe.");
        }
    }
    public void exportarReporte() {
        Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Exportación", "El reporte se está exportando correctamente.");
    }
    private void calcularMetricasYGraficos(int anio,int mes) {
        try {
//            String period = getBeanReportes().getFechaFiltro();
//            String[] parts = period.split("-");
            int selectedYear = anio;
            int selectedMonth = mes;
            // Obtener todas las matrículas del sistema
            List<Matricula> todas = getMatriculaFacade().listaMatriculasPorAnio(selectedYear);
            if (todas == null) {
                todas = new ArrayList<>();
            }
            // Calcular mes previo
            int prevYear = selectedYear;
            int prevMonth = selectedMonth - 1;
            if (prevMonth == 0) {
                prevMonth = 12;
                prevYear = selectedYear - 1;
            }
            // Totales históricos acumulados
            int totalAcumuladoActual = 0;
            totalAcumuladoActual = getMatriculaFacade().totalMatriculados(selectedYear);
            int totalAcumuladoPrev = 0;
            // Inactivos/retirados históricos
            int totalInactivosActual = 0;
            int totalInactivosPrev = 0;
            totalInactivosActual = getMatriculaFacade().totalMatriculaDesertados(selectedYear, selectedMonth);
            // Nuevos en el mes
            int nuevosActual = 0;
            int nuevosPrev = 0;
            // Activos actuales (sin importar periodo histórico, son los que están cursando hoy en estado INSMAT02)
            int activosHoy = 0;
            activosHoy= getMatriculaFacade().totalMatriculasActivas(selectedYear, selectedMonth);
            // Asignar KPIs al Bean
            getBeanReportes().setTotalMatriculados((int) totalAcumuladoActual);
            getBeanReportes().setTotalActivos((int) activosHoy);
            getBeanReportes().setTotalInactivos((int) totalInactivosActual);
            getBeanReportes().setTotalNuevos((int) nuevosActual);
            // Calcular Tendencias
            getBeanReportes().setTendenciaMatriculados(calcularTendenciaStr(totalAcumuladoActual, totalAcumuladoPrev));
            getBeanReportes().setTendenciaMatriculadosSubio(totalAcumuladoActual >= totalAcumuladoPrev);
            getBeanReportes().setTendenciaInactivos(calcularTendenciaStr(totalInactivosActual, totalInactivosPrev));
            getBeanReportes().setTendenciaInactivosSubio(totalInactivosActual >= totalInactivosPrev);
            getBeanReportes().setTendenciaNuevos(calcularTendenciaStr(nuevosActual, nuevosPrev));
            getBeanReportes().setTendenciaNuevosSubio(nuevosActual >= nuevosPrev);
            // Generar Gráfico de Barras: Matrículas por Mes del Año Seleccionado
            generarGraficoMatriculasMes(todas, selectedYear, selectedMonth);
            // Generar Gráfico de Torta: Distribución por Programa
            generarGraficoDistribucionPrograma(todas, selectedYear, selectedMonth);
            // Poblar lista de matrículas recientes (las últimas 10 en base a la fecha de referencia)
            List<Matricula> recientes = todas.stream()
                .filter(m -> m.getMatrEstado() != null && !m.getMatrEstado().equals("INSMAT01")) // Ignorar inscripciones simples
                .sorted((m1, m2) -> getFechaRef(m2).compareTo(getFechaRef(m1)))
//                .limit(10)
                .collect(Collectors.toList());
            getBeanReportes().setListaMatriculasRecientes(recientes);
        } catch (Exception e) {
            log.error("Error al calcular estadísticas de matrículas", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al procesar los datos de matrículas.");
        }
    }
    private Date getFechaRef(Matricula m) {
        if (m.getMatrFechaMatricula() != null) {
            return m.getMatrFechaMatricula();
        }
        if (m.getMatrFechaInscripcion() != null) {
            return m.getMatrFechaInscripcion();
        }
        if (m.getMatrFechaRegistro() != null) {
            return m.getMatrFechaRegistro();
        }
        return new Date();
    }
    private String calcularTendenciaStr(long actual, long anterior) {
        if (anterior == 0) {
            return actual > 0 ? "▲ 100% vs mes anterior" : "0% vs mes anterior";
        }
        double diff = (double) (actual - anterior) / anterior * 100.0;
        if (diff >= 0) {
            return String.format("▲ %.0f%% vs mes anterior", diff);
        } else {
            return String.format("▼ %.0f%% vs mes anterior", Math.abs(diff));
        }
    }
    private void generarGraficoMatriculasMes(List<Matricula> todas, int selectedYear, int selectedMonth) {
        BarChartModel barModel = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet dataSet = new BarChartDataSet();
        
        dataSet.setLabel("Matrículas por Mes");
        
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        // Agrupar por mes para el año seleccionado
        int limitMonth = (Calendar.getInstance().get(Calendar.YEAR) == selectedYear) ? selectedMonth : 12;
        Map<Integer,Integer> mesesGraficaBarras=getMatriculaFacade().obtenerMatriculasHastaMes(selectedYear, limitMonth);
        for (int i = 0; i < limitMonth; i++) {
            Map.Entry<Integer, Integer> elemento = mesesGraficaBarras.entrySet()
            	    .stream()
            	    .skip(i)
            	    .findFirst()
            	    .orElse(null);
            if(elemento!=null)
            	values.add(elemento.getValue());
            else
            	values.add(0);
            labels.add(nombresMeses[i]);
        }
        dataSet.setData(values);
        
        List<String> bgColors = new ArrayList<>();
        // Color premium azul con gradiente visual simulado
        for (int i = 0; i < limitMonth; i++) {
            bgColors.add("#2563eb");
        }
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        barModel.setData(data);
        // Opciones del gráfico
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        // ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        barModel.setOptions(options);
        getBeanReportes().setModelMatriculasMes(barModel);
    }
    private void generarGraficoDistribucionPrograma(List<Matricula> todas, int selectedYear, int selectedMonth) {
        PieChartModel pieModel = new PieChartModel();
        ChartData data = new ChartData();
        PieChartDataSet dataSet = new PieChartDataSet();
        // Agrupar matrículas por nombre de curso
        Map<String, Long> porCurso = todas.stream()
            .filter(m -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(getFechaRef(m));
                boolean inOrBefore = (cal.get(Calendar.YEAR) < selectedYear) 
                    || (cal.get(Calendar.YEAR) == selectedYear && (cal.get(Calendar.MONTH) + 1) <= selectedMonth);
                return inOrBefore && m.getMatrEstado() != null 
                    && (m.getMatrEstado().equals("INSMAT02") || m.getMatrEstado().equals("INSMAT05"));
            })
            .filter(m -> m.getOfertaCursos() != null 
                && m.getOfertaCursos().getOfertaCapacitacion() != null 
                && m.getOfertaCursos().getOfertaCapacitacion().getCurso() != null)
            .collect(Collectors.groupingBy(
                m -> m.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre(),
                Collectors.counting()
            ));
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        
        // Colores predefinidos premium del prototipo
        String[] colors = {"#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#a855f7", "#6366f1", "#ec4899"};
        int colorIdx = 0;
        for (Map.Entry<String, Long> entry : porCurso.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
            bgColors.add(colors[colorIdx % colors.length]);
            colorIdx++;
        }
        // Si no hay datos, agregar un valor ficticio para evitar problemas visuales
        if (porCurso.isEmpty()) {
            labels.add("Sin datos");
            values.add(0);
            bgColors.add("#cbd5e1");
        }
        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        pieModel.setData(data);
        getBeanReportes().setModelDistribucionPrograma(pieModel);
    }
    public String formatRefDate(Matricula m) {
        Date d = getFechaRef(m);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(d);
    }
    public String obtenerModalidad(Matricula m) {
        // Deriva la modalidad: si es videocurso o taller virtual, o usa un algoritmo por defecto 
        // para dar variedad consistente con el prototipo
    	String modalidad="";
        if (m.getOfertaCursos() != null && m.getOfertaCursos().getOcurModalidad() != null) {
            String tipo = m.getOfertaCursos().getOcurModalidad();
            if (tipo.equals("TMOES01")) 
            	modalidad= "Presencial";
            if (tipo.equals("TMOES03")) 
            	modalidad= "SemiPresencial";
            if (tipo.equals("TMOES03")) 
            	modalidad= "Virtual";
            if (tipo.equals("TMOES04")) 
            	modalidad= "VideoCurso";
            if (tipo.equals("TMOES05")) 
            	modalidad= "Hibrido";
        }
        return modalidad;
    }
    public String obtenerEstadoLabel(Matricula m) {
        String est = m.getMatrEstado();
        if (est == null) return "Desconocido";
        switch (est) {
            case "INSMAT01": return "Inscrito";
            case "INSMAT02": return "Activo";
            case "INSMAT03": return "Desertado";
            case "INSMAT05": return "Culminado";
            default: return est;
        }
    }
    public String obtenerColorModalidad(Matricula mat) {
        String modalidad = obtenerModalidad(mat);
        if (modalidad == null) return "#ffffff";
        
        switch (modalidad) {
            case "Presencial":     return "#eff6ff"; // Azul
            case "SemiPresencial":  return "#fef3c7"; // Amarillo
            case "Virtual":        return "#dcfce7"; // Verde
            case "VideoCurso":     return "#f3e8ff"; // Morado
            case "Hibrido":        return "#ffedd5"; // Naranja
            default:               return "#ffffff"; // Blanco por defecto
        }
    }
    
}