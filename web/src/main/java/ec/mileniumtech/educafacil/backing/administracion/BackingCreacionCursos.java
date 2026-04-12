package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanCreacionCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 *@author christian  Oct 25, 2024
 *
 */
@Named("backingCreacionCursos")
@ViewScoped
public class BackingCreacionCursos implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingCreacionCursos.class);
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@Inject
	@Getter
	private BeanCreacionCursos beanCreacionCursos;
	
	@EJB
	private AdministracionService administracionService;
	
	@PostConstruct
	public void init() {
			getBeanCreacionCursos().setAsignarOferta(false);
			getBeanCreacionCursos().setListaCursos(administracionService.listarTodosCursosOrdenados());
			getBeanCreacionCursos().setListaAreas(administracionService.listarAreasOrdenadas());
			getBeanCreacionCursos().setListaEspecialidades(administracionService.listarTodasEspecialidadesOrdenadas());
			getBeanCreacionCursos().setListaOfertaCapacitacion(administracionService.listarOfertasCapacitacion());

			getBeanCreacionCursos().setCurso(new Curso());
			getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());
	}
	/**
	 * Permite crear un nuevo curso
	 */
	public void nuevoCurso() {
		getBeanCreacionCursos().setCurso(new Curso());
		getBeanCreacionCursos().setAsignarOferta(false);
	}
	/**
	 * Permite crear una nueva Oferta de Capacitacion
	 */
	public void nuevaOferta() {
		getBeanCreacionCursos().setAsignarOferta(true);
		getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());
		getBeanCreacionCursos().setCursoActivo(false);
		getBeanCreacionCursos().setCodigoArea(0);
		getBeanCreacionCursos().setCodigoCurso(0);
		getBeanCreacionCursos().setCodigoEspecialidad(0);
		Mensaje.verDialogo("dlgNuevoCurso");
	}
	/**
	 * Permite ocultar el panel de creación Oferta de Capacitación
	 */
	public void ocultarOferta() {
		getBeanCreacionCursos().setAsignarOferta(false);
		getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());
		getBeanCreacionCursos().setCursoActivo(false);
		getBeanCreacionCursos().setCodigoArea(0);
		getBeanCreacionCursos().setCodigoCurso(0);
		getBeanCreacionCursos().setCodigoEspecialidad(0);
		Mensaje.ocultarDialogo("dlgNuevoCurso");
	}
	/**
	 * Permite editar una Oferta de Capacitación
	 */
	public void editarOferta() {
		if(getBeanCreacionCursos().getOfertaCapacitacion()!=null) {
			getBeanCreacionCursos().setAsignarOferta(true);
			getBeanCreacionCursos().setCursoActivo(getBeanCreacionCursos().getOfertaCapacitacion().isOfcaEstado());
			getBeanCreacionCursos().setCodigoArea(getBeanCreacionCursos().getOfertaCapacitacion().getArea().getAreaId());
			getBeanCreacionCursos().setCodigoCurso(getBeanCreacionCursos().getOfertaCapacitacion().getCurso().getCursId());
			getBeanCreacionCursos().setCodigoEspecialidad(getBeanCreacionCursos().getOfertaCapacitacion().getEspecialidad().getEspeId());
			Mensaje.verDialogo("dlgNuevoCurso");
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.editarOferta"));	
		}
	}
	/**
	 * Permite mostrar el cuadro dialogo actualizar curso
	 */
	public void mostrarDialogoActualizaCurso() {
			if(getBeanCreacionCursos().getCodigoCurso()>0) {
				getBeanCreacionCursos().setCurso(getBeanCreacionCursos().getListaCursos().stream().filter(c-> c.getCursId()==getBeanCreacionCursos().getCodigoCurso()).collect(Collectors.toList()).get(0));				
				Mensaje.verDialogo("dlgGrabaCurso");
			}					
	}
	/**
	 * Muestra el cuadro de dialogo nuevo curso
	 */
	public void mostrarDialogoNuevoCurso() {		
			getBeanCreacionCursos().setCurso(new Curso());				
			Mensaje.verDialogo("dlgGrabaCurso");		
	}
	/**
	 * Muestra el dialogo Grabar Oferta
	 */
	public void mostrarDialogoGrabaOferta() {
		if(getBeanCreacionCursos().getOfertaCapacitacion().getOfcaId()==null ) 
			getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());			
		getBeanCreacionCursos().getOfertaCapacitacion().setArea(getBeanCreacionCursos().getListaAreas().stream().filter(a->a.getAreaId()==getBeanCreacionCursos().getCodigoArea()).collect(Collectors.toList()).get(0));
		getBeanCreacionCursos().getOfertaCapacitacion().setCurso(getBeanCreacionCursos().getListaCursos().stream().filter(c->c.getCursId()==getBeanCreacionCursos().getCodigoCurso()).collect(Collectors.toList()).get(0));
		getBeanCreacionCursos().getOfertaCapacitacion().setEspecialidad(getBeanCreacionCursos().getListaEspecialidades().stream().filter(e->e.getEspeId()==getBeanCreacionCursos().getCodigoEspecialidad()).collect(Collectors.toList()).get(0));
		getBeanCreacionCursos().getOfertaCapacitacion().setOfcaEstado(getBeanCreacionCursos().isCursoActivo());
		Mensaje.verDialogo("dlgGrabaOferta");
	}
	/**
	 * Muestra el dialogo Grabar Oferta
	 */
	public void grabarOferta() {
			administracionService.guardarNuevaOfertaCapacitacion(getBeanCreacionCursos().getOfertaCapacitacion());
			
			getBeanCreacionCursos().setListaOfertaCapacitacion(administracionService.listarOfertasCapacitacion());
			getBeanCreacionCursos().setAsignarOferta(false);
			getBeanCreacionCursos().setCursoActivo(false);
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
	}
	/**
	 * Permite grabar / actualizar un curso
	 */
	public void grabarActualizarCurso() {
			if(getBeanCreacionCursos().getCurso().getCursId()==0) {
				getBeanCreacionCursos().setCodigoArea(0);
				getBeanCreacionCursos().setCodigoEspecialidad(0);
				getBeanCreacionCursos().setCursoActivo(false);
			}
			administracionService.actualizarCurso(getBeanCreacionCursos().getCurso());
			getBeanCreacionCursos().setListaCursos(administracionService.listarTodosCursosOrdenados());
			Mensaje.ocultarDialogo("dlgGrabaCurso");
	}
}
