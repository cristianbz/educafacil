/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.pie.PieChartModel;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
@Getter
@Setter
public class BeanReporteria implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<DtoFlujoDinero> listaIngresos;
	private List<DtoFlujoDinero> listaEgresos;
	private Date fechaInicial;
	private Date fechaFinal;
	private BarChartModel modelGraficoE;
	private BarChartModel modelGraficoI;
	private PieChartModel modelPieFechas;
	private double totalIngresos;
	private double totalEgresos;
	private String fechasReporte;
	
	
	
}
