/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.bean.encuestas;

import java.io.Serializable;
import java.util.List;

import org.primefaces.model.TreeNode;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleEvaluaCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [Christian Báez] Jan 20, 2024
 *
 */
@Named
@ViewScoped
public class BeanAplicarEncuesta implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private List<EvaluacionCurso> listaEvaluacionCurso;
	
	
	@Getter
	@Setter
	private List<OfertaCursos> listaCursosMatriculados;
	
	@Getter
	@Setter
	private EvaluacionCurso evaluacionCurso;
	
	@Getter
	@Setter
	private List<TipoEncuestaPregunta> listaEncuestas;
	
	@Getter
	@Setter
	private TreeNode<EvaluacionCurso> raiz;
	@Getter
	@Setter
	private TreeNode nodoSeleccionado;
	@Getter
	@Setter
	private TreeNode<EvaluacionCurso> cursos;
	@Getter
	@Setter
	private TreeNode<EvaluacionCurso> evaluaciones;
	@Getter
	@Setter
	private String nombreCursoSeleccionado;
	@Getter
	@Setter
	private String nombreEncuestaSeleccionada;
	
	@Getter
	@Setter
	private List<Respuestas> listaRespuestas;
	
	@Getter
	@Setter
	private int posicionPregunta;
	@Getter
	@Setter
	private String nombrePregunta;
	
	@Getter
	@Setter
	private int codigoRespuesta;
	
	@Getter
	@Setter
	private List<DetalleEvaluaCurso> listaRespuestasAGrabar;
	
	@Getter
	@Setter
	private int evaluacionCursoId;
	
	@Getter
	@Setter
	private int matriculasId;
	
	@Getter
	@Setter
	private int codigoEstudiante;
	
	@Getter
	@Setter
	private int ofertaCursos;
	

		
}
