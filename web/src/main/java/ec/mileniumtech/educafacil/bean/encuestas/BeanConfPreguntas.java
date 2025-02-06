/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.bean.encuestas;

import java.io.Serializable;
import java.util.List;

import org.primefaces.model.TreeNode;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.CategoriaRespuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [Christian Báez] Dec 22, 2023
 *
 */

@Named
@ViewScoped
public class BeanConfPreguntas  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	@Getter
	@Setter
	private List<TipoEncuesta> listaTipoEncuesta;
	
	@Getter
	@Setter
	private List<CategoriaRespuesta> listaCategoriaRespuesta;
	
	@Getter
	@Setter
	private List<ObjetoEvaluacion> listaObjetoEvaluacion;
	
	@Getter
	@Setter
	private List<Respuestas> listaRespuestas;
	
	@Getter
	@Setter
	private List<Pregunta> listaPreguntas;
	
//	@Getter
//	@Setter
//	private List<TipoEncuestaPregunta> listaTipoEncuestaPregunta;
	
	@Getter
	@Setter
	private ObjetoEvaluacion objetoEvaluacion;
	
	@Getter
	@Setter
	private ObjetoEvaluacion objetoEvaluacionEditar;
	
	@Getter
	@Setter
	private CategoriaRespuesta categoriaRespuesta;
	
	@Getter
	@Setter
	private CategoriaRespuesta categoriaRespuestaEditar;

	@Getter
	@Setter
	private TipoEncuesta tipoEncuesta;
	
	@Getter
	@Setter
	private TipoEncuesta tipoEncuestaEditar;
	
	@Getter
	@Setter
	private TipoEncuestaPregunta tipoEncuestaPregunta;
	
	@Getter
	@Setter
	private TipoEncuestaPregunta tipoEncuestaPreguntaSeleccionada;
	
	@Getter
	@Setter
	private Pregunta pregunta;

	@Getter
	@Setter
	private Respuestas respuestas;
	
	@Getter
	@Setter
	private int tabActivo;
	
	@Getter
	@Setter
	private CategoriaRespuesta categoriaRespuestaSeleccionada;
	
	@Getter
	@Setter
	private int objetoEvaluacionSeleccionado;
	
	@Getter
	@Setter
	private Pregunta preguntaCategoriaRespuestaSeleccionada;
	
	@Getter
	@Setter
	private TreeNode root;
	
	@Getter
	@Setter
	private List<Pregunta> listaPreguntasSelec;
	
	@Getter
	@Setter
	private List<Pregunta> listaPreguntasTE;
	
	@Getter
	@Setter
	private Respuestas respuestaSeleccionada;
	
}
