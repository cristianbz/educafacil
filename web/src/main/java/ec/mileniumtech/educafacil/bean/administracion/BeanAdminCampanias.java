/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
/**
*@author christian  Jul 7, 2024
*
*/
@Named
@ViewScoped
public class BeanAdminCampanias implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private List<Campania> listaCampanias;
	
	@Getter
	@Setter
	private List<Curso> listaCursos;
	
	@Getter
	@Setter
	private Campania campaniaSeleccionada;
	
	@Getter
	@Setter
	private int cursoSeleccionado;
	
	@Getter
	@Setter
	private String codigoTipo;
	
	@Getter
	@Setter
	private BigInteger prospectos;
	
	@Getter
	@Setter
	private BigInteger prospectosSeguimiento;
	
	@Getter
	@Setter
	private BigInteger prospectosMatriculados;
	
	@Getter
	@Setter
	private BigInteger prospectosCandidatos;
	
	@Getter
	@Setter
	private BigInteger prospectosAbandonados;
	
	@Getter
	@Setter
	private BigInteger prospectosProximaOcasion;
	
	@Getter
	@Setter
	private HorizontalBarChartModel modelGrafico;
	
	@Getter
	@Setter
	private boolean mostrarGrafica;
}

