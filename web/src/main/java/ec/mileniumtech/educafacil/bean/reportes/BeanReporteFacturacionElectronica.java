/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.bean.reportes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.line.LineChartModel;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteReporteDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de datos para el Reporte de Facturaci&oacute;n Electr&oacute;nica.
 * Almacena los KPIs, modelos PrimeFaces Charts y lista de comprobantes.
 *
 * @author Christian Baez — Jul 2026
 */
@Named
@ViewScoped
@Getter
@Setter
public class BeanReporteFacturacionElectronica implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Per&iacute;odo seleccionado en el filtro (formato yyyy-MM) */
    private String fechaFiltro;

    // ---- KPIs ----
    private int    facturasEmitidas;
    private int    autorizadasSri;
    private int    anuladas;
    private BigDecimal totalFacturado;

    // ---- Gr&aacute;ficos ----
    /** Modelo para el BarChart de comprobantes autorizados vs anulados por mes */
    private BarChartModel comprobantesMensualesModel;

    /** Modelo para el LineChart de monto facturado por mes */
    private LineChartModel montoFacturadoMensualModel;

    // ---- Tabla ----
    private List<ComprobanteReporteDto> listaComprobantes;
}
