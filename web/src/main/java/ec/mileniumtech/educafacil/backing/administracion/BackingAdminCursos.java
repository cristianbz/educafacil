/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanAdminCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosOfertaCurso;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 *@author christian  Jul 13, 2024
 *
 */
@Named("backingAdminCursos")
@ViewScoped
public class BackingAdminCursos implements Serializable{


	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingAdminCursos.class);
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@Inject
	@Getter
	private BeanAdminCursos beanAdminCursos;
	
	@EJB
	private AdministracionService administracionService;
	
	@PostConstruct
	public void init() {
		cargarOfertaCursosActivos();
		cargarArea();
		cargaTipoCapacitacion();
		cargarInstructor();
		getBeanAdminCursos().setNuevaOfertaCurso(false);
		getBeanAdminCursos().setObjetoEvaluacion(new ObjetoEvaluacion());
		cargaObjetosEvaluacion();
		getBeanAdminCursos().setListaEvaluacionCursoAsig(new ArrayList<>());


	}
	/**
	 * Carga los objetos de Evaluacion
	 */
	public void cargaObjetosEvaluacion() {
		getBeanAdminCursos().setListaObjetoEvaluacion(administracionService.listarObjetosEvaluacion());
	}
	
	/**
	 * Carga los tipos de encuestas
	 */
	public void cargaTiposEncuestas() {
		if(getBeanAdminCursos().getObjetoEvaluacion().getObjeId()!=null) {
			getBeanAdminCursos().setListaEvaluacionCursoAsig(administracionService.listarEvaluacionesPorCurso(getBeanAdminCursos().getOfertaCursos().getOcurId(),getBeanAdminCursos().getObjetoEvaluacion().getObjeId()));
			getBeanAdminCursos().setListaTipoEncuestas(administracionService.listarTiposEncuestasPorObjeto(getBeanAdminCursos().getObjetoEvaluacion().getObjeId()));
		}
	}
	
	/**
	 * Carga la oferta de cursos activos
	 */
	public void cargarOfertaCursosActivos() {
		getBeanAdminCursos().setListaOfertaCursos(administracionService.listarOfertaCursosActivosOrdenados());
	}
	/**
	 * Carga las areas
	 */
	public void cargarArea() {
		getBeanAdminCursos().setListaAreas(administracionService.listarAreasOrdenadas());
	}
	/**
	 * Carga las Especialidades
	 */
	public void cargaEspecialidades() {
		getBeanAdminCursos().setListaEspecialidad(administracionService.listarEspecialidadesPorAreaOrdenadas(getBeanAdminCursos().getCodigoArea()));
		getBeanAdminCursos().setListaCurso(new ArrayList<>());
	}
	/**
	 * Carga la lista de instructores
	 */
	public void cargarInstructor() {
		getBeanAdminCursos().setListaInstructores(administracionService.listarInstructoresOrdenados());
	}
	/**
	 * Carga los cursos
	 */
	public void cargarCursos() {
		getBeanAdminCursos().setListaCurso(administracionService.listarCursosPorAreaEspecialidadOrdenados(getBeanAdminCursos().getCodigoArea(), getBeanAdminCursos().getCodigoEspecialidad()));
	}
	/**
	 * Carga los tipos de capacitacion
	 */
	public void cargaTipoCapacitacion() {
		getBeanAdminCursos().setListaCatalogo(administracionService.listarTipoCapacitacionOrdenados());
	}
	/**
	 * Permite editar una oferta de curso
	 */
	public void edicionOfertaCurso() {
		getBeanAdminCursos().setNuevaOfertaCurso(false);
		getBeanAdminCursos().setCodigoArea(getBeanAdminCursos().getOfertaCursos().getOfertaCapacitacion().getArea().getAreaId());
		cargaEspecialidades();
		Mensaje.actualizarComponente("comboCurso");
		getBeanAdminCursos().setCodigoEspecialidad(getBeanAdminCursos().getOfertaCursos().getOfertaCapacitacion().getEspecialidad().getEspeId());
		cargarCursos();
		getBeanAdminCursos().setCodigoCurso(getBeanAdminCursos().getOfertaCursos().getOfertaCapacitacion().getCurso().getCursId());
		getBeanAdminCursos().setCodigoTipoCurso(getBeanAdminCursos().getOfertaCursos().getOcurTipo());
		getBeanAdminCursos().setCodigoInstructor(getBeanAdminCursos().getOfertaCursos().getInstructor().getInstId());
		if (! getBeanAdminCursos().getOfertaCursos().getOcurEstado().equals(EnumEstadosOfertaCurso.ANULADO.getCodigo()))
			getBeanAdminCursos().setAnularCurso(false);

		Mensaje.verDialogo("dlgOfertaCursos");
	}
	/**
	 * Graba una oferta de curso
		 */
	public void grabarCurso() {
		OfertaCapacitacion ofertaCapacitacion=administracionService.buscarOfertaCapacitacion(getBeanAdminCursos().getCodigoArea(), getBeanAdminCursos().getCodigoEspecialidad(), getBeanAdminCursos().getCodigoCurso());
		getBeanAdminCursos().setOfertaCapacitacion(ofertaCapacitacion);
		Instructor instructor=new Instructor();
		instructor.setInstId(getBeanAdminCursos().getCodigoInstructor());
		getBeanAdminCursos().getOfertaCursos().setInstructor(instructor);
		getBeanAdminCursos().getOfertaCursos().setOcurTipo(getBeanAdminCursos().getCodigoTipoCurso());
		getBeanAdminCursos().getOfertaCursos().setOfertaCapacitacion(getBeanAdminCursos().getOfertaCapacitacion());
		if(getBeanAdminCursos().isAnularCurso())
			getBeanAdminCursos().getOfertaCursos().setOcurEstado(EnumEstadosOfertaCurso.ANULADO.getCodigo());
		else
			getBeanAdminCursos().getOfertaCursos().setOcurEstado(EnumEstadosOfertaCurso.INICIADO.getCodigo());
		
		administracionService.guardarOfertaCurso(getBeanAdminCursos().getOfertaCursos());

		getBeanAdminCursos().setEditarOfertaCurso(false);
		getBeanAdminCursos().setOfertaCursos(new OfertaCursos());
		cargarOfertaCursosActivos();
		Mensaje.ocultarDialogo("dlgOfertaCursos");
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));	
	}
	
	/**
	 * Nueva oferta de curso
	 */
	public void nuevoOfertaCurso() {
		getBeanAdminCursos().setAnularCurso(false);
		getBeanAdminCursos().setNuevaOfertaCurso(true);
		getBeanAdminCursos().setOfertaCursos(new OfertaCursos());
		getBeanAdminCursos().setCodigoArea(0);
		getBeanAdminCursos().setCodigoCurso(0);
		getBeanAdminCursos().setCodigoEspecialidad(0);
		getBeanAdminCursos().setCodigoInstructor(0);
		getBeanAdminCursos().setCodigoTipoCurso("");
		getBeanAdminCursos().setListaEspecialidad(new ArrayList<>());
		getBeanAdminCursos().setListaCurso(new ArrayList<>());
		Mensaje.verDialogo("dlgOfertaCursos");
	}
	
	/**
	 * Abre el dialogo de asignacion de encuestas y vacia las listas
	 */
	public void asignarEncuestas() {
		getBeanAdminCursos().setObjetoEvaluacion(new ObjetoEvaluacion());
		getBeanAdminCursos().setListaTipoEncuestas(new ArrayList<>());
		getBeanAdminCursos().setListaEncuestasSelect(new ArrayList<>());
		getBeanAdminCursos().setListaEvaluacionCursoAsig(new ArrayList<>());
		if(getBeanAdminCursos().getListaObjetoEvaluacion().size()>0) {
			getBeanAdminCursos().setListaTipoEncuestas(administracionService.listarTiposEncuestasPorObjeto(getBeanAdminCursos().getListaObjetoEvaluacion().get(0).getObjeId()));
			getBeanAdminCursos().setListaEvaluacionCursoAsig(administracionService.listarEvaluacionesPorCurso(getBeanAdminCursos().getOfertaCursos().getOcurId(), getBeanAdminCursos().getListaObjetoEvaluacion().get(0).getObjeId()));
			Mensaje.verDialogo("dlgAsignacionEncuestasCurso");
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregarDatosEncu"));
		}
	}
	/**
	 * Guarda los datos de las encuestas asignadas
	 */
	public void asignarEvaluacionesCurso() {
		if(getBeanAdminCursos().getListaEncuestasSelect().size()==0) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.nodatos"));
		}else {


			for (TipoEncuesta tipoEncuesta : getBeanAdminCursos().getListaEncuestasSelect()) {
				EvaluacionCurso evcu = new EvaluacionCurso();
				evcu.setTipoEncuesta(tipoEncuesta);
				evcu.setOfertacursos(getBeanAdminCursos().getOfertaCursos());
				evcu.setEvcuEstado(true);

				// Verificar si el tipo de encuesta ya está asignado
				boolean encuestaYaAsignada = false;
				for (EvaluacionCurso evaluacionCurso : getBeanAdminCursos().getListaEvaluacionCursoAsig()) {
					if (evaluacionCurso.getOfertacursos().getOcurId()==evcu.getOfertacursos().getOcurId()&& evaluacionCurso.getTipoEncuesta().getTipeId()==evcu.getTipoEncuesta().getTipeId()) {
						encuestaYaAsignada = true;
						break;
					}
				}

				if (encuestaYaAsignada) {
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado"));			

				} else {
					getBeanAdminCursos().getListaEvaluacionCursoAsig().add(evcu);
				}
			}
		}
	}

	/**
	 * Se encarga de guardar la evaluacion Curso
	 */
	public void guardarEvaluacionCurso() {
		for (EvaluacionCurso evaluaciaoCurso : getBeanAdminCursos().getListaEvaluacionCursoAsig()) 
			administracionService.agregarEvaluacionCurso(evaluaciaoCurso);
			
		cargarOfertaCursosActivos();
		Mensaje.ocultarDialogo("dlgAsignacionEncuestasCurso");
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));
	}
	/**
	 * 	Se encarga de eliminar la encuesta seleccionada
	 */
	public void eliminarEvaluacionCurso() {
		getBeanAdminCursos().getListaEvaluacionCursoAsig().remove(getBeanAdminCursos().getEvaluacionCurso());
		getBeanAdminCursos().getEvaluacionCurso().setEvcuEstado(false);;
		administracionService.agregarEvaluacionCurso(getBeanAdminCursos().getEvaluacionCurso());
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.eliminar"));
	}
}



