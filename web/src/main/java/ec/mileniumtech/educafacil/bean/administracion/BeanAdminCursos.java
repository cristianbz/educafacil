/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BeanAdminCursos implements Serializable{
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private List<OfertaCursos> listaOfertaCursos;
	@Getter
	@Setter
	private OfertaCursos ofertaCursos;
	@Getter
	@Setter
	private OfertaCapacitacion ofertaCapacitacion;
	@Getter
	@Setter
	private List<Catalogo> listaCatalogo;
	@Getter
	@Setter
	private List<Area> listaAreas;
	@Getter
	@Setter
	private List<Especialidad> listaEspecialidad;
	@Getter
	@Setter
	private List<Curso> listaCurso;
	@Getter
	@Setter
	private List<Instructor> listaInstructores;
	@Getter
	@Setter
	private boolean anularCurso;
	@Getter
	@Setter
	private boolean nuevaOfertaCurso;
	@Getter
	@Setter
	private int codigoArea;
	@Getter
	@Setter
	private int codigoEspecialidad;
	@Getter
	@Setter
	private int codigoCurso;
	@Getter
	@Setter
	private String codigoTipoCurso;
	@Getter
	@Setter
	private boolean editarOfertaCurso;
	@Getter
	@Setter
	private int codigoInstructor;
	@Getter
	@Setter
	private List<TipoEncuesta> listaTipoEncuestas;
	@Getter
	@Setter
	private List<ObjetoEvaluacion> listaObjetoEvaluacion;
	@Getter
	@Setter
	private List<TipoEncuesta> listaEncuestasSelect;
	@Getter
	@Setter
	private List<EvaluacionCurso> listaEvaluacionCursoAsig;
	@Getter
	@Setter
	private ObjetoEvaluacion objetoEvaluacion;
	@Getter
	@Setter
	private TipoEncuesta tipoEncuesta;
	@Getter
	@Setter
	private EvaluacionCurso evaluacionCurso;
	
	@PostConstruct
	public void init() {
		setEditarOfertaCurso(false);
		setOfertaCursos(new OfertaCursos());
	}
}

