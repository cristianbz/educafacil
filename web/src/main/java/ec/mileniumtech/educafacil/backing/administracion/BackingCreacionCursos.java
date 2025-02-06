/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanCreacionCursos;
import ec.mileniumtech.educafacil.dao.excepciones.AreaException;
import ec.mileniumtech.educafacil.dao.excepciones.CursoException;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.excepciones.EspecialidadException;
import ec.mileniumtech.educafacil.dao.excepciones.OfertaCapacitacionException;
import ec.mileniumtech.educafacil.dao.impl.AreaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EspecialidadDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCapacitacionDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosOfertaCurso;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumTipoCapacitacion;
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
	@Getter
	private CursoDaoImpl cursoServicioImpl;
	@EJB
	@Getter
	private AreaDaoImpl areaServicioImpl;
	@EJB
	@Getter
	private EspecialidadDaoImpl especialidadServicioImpl;
	@EJB
	@Getter
	private OfertaCapacitacionDaoImpl ofertaCapacitacionServicioImpl;
	
	@PostConstruct
	public void init() {
		try {
			
			getBeanCreacionCursos().setAsignarOferta(false);
			getBeanCreacionCursos().setListaCursos(new ArrayList<>());
			getBeanCreacionCursos().setListaCursos(getCursoServicioImpl().listaCursos());
			
			getBeanCreacionCursos().setListaAreas(new ArrayList<>());
			getBeanCreacionCursos().setListaAreas(getAreaServicioImpl().listaDeAreas());
			
			getBeanCreacionCursos().setListaEspecialidades(new ArrayList<>());
			getBeanCreacionCursos().setListaEspecialidades(getEspecialidadServicioImpl().listaDeEspecialidades());
			
			getBeanCreacionCursos().setListaOfertaCapacitacion(new ArrayList<>());
			getBeanCreacionCursos().setListaOfertaCapacitacion(getOfertaCapacitacionServicioImpl().listarOfertasCapacitacion());

			getBeanCreacionCursos().setCurso(new Curso());
			getBeanCreacionCursos().setListaAreas(getBeanCreacionCursos().getListaAreas().stream().sorted((a1,a2)->a1.getAreaNombre().compareTo(a2.getAreaNombre())).collect(Collectors.toList()));
			
			getBeanCreacionCursos().setListaCursos(getBeanCreacionCursos().getListaCursos().stream().sorted((c1,c2)->c1.getCursNombre().compareTo(c2.getCursNombre())).collect(Collectors.toList()));
			getBeanCreacionCursos().setListaEspecialidades(getBeanCreacionCursos().getListaEspecialidades().stream().sorted((e1,e2)->e1.getEspeNombre().compareTo(e2.getEspeNombre())).collect(Collectors.toList()));
			
			getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarcursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "init" + ": ").append(e.getMessage()));
		}
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
	 * Permite ocultar el panel de creaci�n Oferta de Capacitaci�n
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
	 * Permite editar una Oferta de Capacitaci�n
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
//		getBeanCreacionCursos().setCurso(new Curso());
		
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
		if(getBeanCreacionCursos().getOfertaCapacitacion().getOfcaId()==0) 
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
		try {
			OfertaCursos ofertaCursos=new OfertaCursos();
			Instructor instructor=new Instructor();
			Persona persona = new Persona();
			persona.setPersId(1);
			instructor.setInstId(1);
			instructor.setPersona(persona);
			ofertaCursos.setOcurFechaInicio(new Date());
			ofertaCursos.setOcurFechaFin(new Date());
			ofertaCursos.setOcurDescuento(0);
			ofertaCursos.setOcurDuracion(0);
			ofertaCursos.setOcurEstado(EnumEstadosOfertaCurso.PORDEFECTO.getCodigo());
			ofertaCursos.setOcurPorDefecto(true);
			ofertaCursos.setOcurTipo(EnumTipoCapacitacion.CURSO.getCodigo());
			ofertaCursos.setInstructor(instructor);
			ofertaCursos.setOfertaCapacitacion(getBeanCreacionCursos().getOfertaCapacitacion());
			try {
				getOfertaCapacitacionServicioImpl().agregarOfertaCapacitacion(getBeanCreacionCursos().getOfertaCapacitacion(),ofertaCursos);
			} catch (EntidadDuplicadaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getBeanCreacionCursos().setListaOfertaCapacitacion(new ArrayList<>());
			getBeanCreacionCursos().setListaOfertaCapacitacion(getOfertaCapacitacionServicioImpl().listarOfertasCapacitacion());
			getBeanCreacionCursos().setAsignarOferta(false);
			getBeanCreacionCursos().setCursoActivo(false);
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabarOfertaCurso"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabarOferta" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Permite grabar / actualizar un curso
	 */
	public void grabarActualizarCurso() {
		try {	
			
			if(getBeanCreacionCursos().getCurso().getCursId()==0) {
				getBeanCreacionCursos().setCodigoArea(0);
				getBeanCreacionCursos().setCodigoEspecialidad(0);
				getBeanCreacionCursos().setCursoActivo(false);
			}
			getCursoServicioImpl().actualizar(getBeanCreacionCursos().getCurso());
			getBeanCreacionCursos().setListaCursos(new ArrayList<>());
			getBeanCreacionCursos().setListaCursos(getCursoServicioImpl().listaCursos());
			Mensaje.ocultarDialogo("dlgGrabaCurso");
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabar"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabarActualizarCurso" + ": ").append(e.getMessage()));
		}
	}

}
