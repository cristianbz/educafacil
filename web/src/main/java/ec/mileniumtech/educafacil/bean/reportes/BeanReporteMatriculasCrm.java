/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.bean.reportes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [Christian Báez] Oct 14, 2023
 *
 */
@ViewScoped
@Named
@Getter
@Setter
public class BeanReporteMatriculasCrm implements Serializable{

	private static final long serialVersionUID = 1L;
	private BigDecimal totalSeguimiento;
	private BigDecimal totalAbandonado;
	private BigDecimal totalMatriculado;
	private BigDecimal totalCandidato;
	private BigDecimal totalProximaOcasion;
	private BigDecimal totalMatriculados;
	private BigDecimal totalDesertados;
	private BigDecimal totalGastoCampania;
	private List<DtoMatriculasCurso> listaInteresadosCurso;
	private List<DtoMatriculasCurso> listaVentasCurso;
	private List<DtoMatriculasCurso> listaAbandonaCurso;
	private List<DtoMatriculasCurso> listaGastoPublicidad;
	private HorizontalBarChartModel  barmodel;
	private ChartData data;
	private BarChartDataSet barCharDataSet;
	private HorizontalBarChartModel  barmodelVenta;
	private ChartData dataVenta;
	private BarChartDataSet barCharDataSetVenta;
	private HorizontalBarChartModel  barmodelAbandona;
	private ChartData dataAbandona;
	private BarChartDataSet barCharDataSetAbandona;
}
