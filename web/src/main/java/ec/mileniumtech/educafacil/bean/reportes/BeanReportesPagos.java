/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */

package ec.mileniumtech.educafacil.bean.reportes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.pie.PieChartModel;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de datos para el Reporte de Pagos.
 * Almacena los KPIs, modelos PrimeFaces Charts y lista de pagos recientes.
 *
 * @author Christian Baez — Jul 2026
 */
@Named
@ViewScoped
@Getter
@Setter
public class BeanReportesPagos implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Período seleccionado en el filtro (formato yyyy-MM) */
    private String fechaFiltro;

    // ---- KPIs ----
    private double totalCobrado;
    private BigDecimal pendienteCobro;
    private BigDecimal totalMora;
    private double ingresosYtd;
    private int    alumnosEnMora;
    private int    facturasAbiertas;

    // ---- Tendencias ----
    private String  tendenciaCobrado;
    private boolean tendenciaCobradoSubio;
    private String  tendenciaMora;
    private boolean tendenciaMoraSubio;
    private String  tendenciaYtd;
    private boolean tendenciaYtdSubio;

    // ---- Datos para PrimeFaces Charts (ChartModel) ----
    /** Modelo para el LineChart de ingresos mensuales (cobrado vs pendiente) */
    private LineChartModel ingresosMensualesModel;

    /** Modelo para el PieChart de métodos de pago */
    private PieChartModel metodosPagoModel;

    // ---- Tabla de pagos recientes ----
    private List<Pagos> listaPagosRecientes;
}
