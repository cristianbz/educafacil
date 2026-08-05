/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.bean.reportes;

import java.io.Serializable;
import java.util.List;

import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.line.LineChartModel;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de datos para el Reporte de Encuestas de Seguimiento.
 * Almacena los KPIs, modelos PrimeFaces Charts y lista de encuestas por período.
 *
 * @author Christian Baez — Jul 2026
 */
@Named
@ViewScoped
@Getter
@Setter
public class BeanReporteEncuestas implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Período seleccionado en el filtro (formato yyyy-MM) */
    private String fechaFiltro;

    // ---- KPIs ----
    private int    encuestasActivas;
    private int    totalEncuestas;
    private double tasaRespuesta;
    private int    respondieron;
    private int    asignados;
    private int    pendientes;
    private double satisfaccionPromedio;

    // ---- Comparaciones vs mes anterior ----
    private double tasaRespuestaDiff;
    private double satisfaccionDiff;

    // ---- Gráficos ----
    /** Modelo para el gráfico de barras horizontal de Tasa de Respuesta por Encuesta (%) */
    private HorizontalBarChartModel tasaRespuestaModel;

    /** Modelo para el gráfico de líneas de Satisfacción Promedio Mensual */
    private LineChartModel satisfaccionMensualModel;

    // ---- Tabla ----
    private List<EncuestaPeriodoDto> listaEncuestasPeriodo;
}
