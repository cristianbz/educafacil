/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.reportes;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.pie.PieChartModel;

/**
 * @author [ Christian Baez ] christian Aug 30, 2022
 *
 */
@Named
@ViewScoped
@Getter
@Setter
public class BeanReportesMatriculas implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<Matricula> listaMatriculas;
	private List<Curso> listaCursos;
	private List<OfertaCursos> listaOfertaCursos;
	private Curso cursoSeleccionado;
	private OfertaCursos ofertaSeleccionada;
	private boolean activaInfoCurso;
	private Matricula matriculaSeleccionada;
	// Nuevas propiedades para el dashboard
	private String fechaFiltro;
	private int totalMatriculados;
	private int totalActivos;
	private int totalInactivos;
	private int totalNuevos;
	private String tendenciaMatriculados;
	private String tendenciaInactivos;
	private String tendenciaNuevos;
	private boolean tendenciaMatriculadosSubio;
	private boolean tendenciaInactivosSubio;
	private boolean tendenciaNuevosSubio;
	private BarChartModel modelMatriculasMes;
	private PieChartModel modelDistribucionPrograma;
	private List<Matricula> listaMatriculasRecientes;
}
