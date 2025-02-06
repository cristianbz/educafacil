/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanFinalizarCurso;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosOfertaCurso;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BackingFinalizarCurso  implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingFinalizarCurso.class);
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@Inject
	@Getter
	private BeanFinalizarCurso beanFinalizarCurso;
	
	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaCursosServicioImpl;
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicioImpl;
	@EJB
	@Getter
	private UsuarioDaoImpl usuarioServicioImpl;
	@Getter
	@Setter
	private boolean mostrarTextArea;
	
	@PostConstruct
	public void init() {
		cargarCursosActivos();
	}
	
	/**
	 * Carga los cursos activos
	 */
	public void cargarCursosActivos() {
		try {
			getBeanFinalizarCurso().setListaCursosAbiertos(new ArrayList<>());
			getBeanFinalizarCurso().setListaCursosAbiertos(getOfertaCursosServicioImpl().listaOfertaCursosActivos());
			getBeanFinalizarCurso().setListaCursosAbiertos(getBeanFinalizarCurso().getListaCursosAbiertos().stream().sorted((c1,c2) -> c1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(c2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarcursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarCursosActivos" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Carga los alumnos del curso seleccionado
	 */
	public void cargarAlumnosCurso() {
		try {
			getBeanFinalizarCurso().setListaMatriculadosCurso(new ArrayList<>());
			getBeanFinalizarCurso().setListaMatriculadosCurso(getMatriculaServicioImpl().listaMatriculadosOEnCursoPorOferta(getBeanFinalizarCurso().getOfertaCursosSeleccionado().getOcurId()));
			getBeanFinalizarCurso().setListaMatriculadosCurso(getBeanFinalizarCurso().getListaMatriculadosCurso().stream().sorted((a1,a2) -> a1.getEstudiante().getPersona().getPersApellidos().compareTo(a2.getEstudiante().getPersona().getPersApellidos())).collect(Collectors.toList()));
			Mensaje.verDialogo("dlgFinalCurso");
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.error.cargarAlumnos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarAlumnosCurso" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Cancela la finalizacion del curso
	 */
	public void cancelarFinalizacion() {
		getBeanFinalizarCurso().setListaMatriculadosCurso(new ArrayList<>());
		getBeanFinalizarCurso().setOfertaCursosSeleccionado(new OfertaCursos());
		Mensaje.ocultarDialogo("dlgFinalCurso");
	}
	/**
	 * Procesa la finalizacion de un curso
	 */
	public void procesaFinalizacionCurso() {
		try {
			getBeanFinalizarCurso().getOfertaCursosSeleccionado().setOcurEstado(EnumEstadosOfertaCurso.FINALIZADO.getCodigo());

			getOfertaCursosServicioImpl().finalizarCursoActivo(getBeanFinalizarCurso().getOfertaCursosSeleccionado(), getBeanFinalizarCurso().getListaMatriculadosCurso());
			getBeanFinalizarCurso().setListaMatriculadosCurso(new ArrayList<>());
			getBeanFinalizarCurso().setOfertaCursosSeleccionado(null);
			cargarCursosActivos();
			Mensaje.ocultarDialogo("dlgFinalCurso");
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabarOfertaCurso"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "procesaFinalizacionCurso" + ": ").append(e.getMessage()));			
		}catch(EntidadDuplicadaException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.entidadDuplicada"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "procesaFinalizacionCurso" + ": ").append(e.getMessage()));			
		}
	}
	/**
	 * Muestra el dialogo para finalizar el curso
	 */
	public void mostrarDialogoGrabar() {
		boolean existeDesertado=false;
		for (Matricula mt : getBeanFinalizarCurso().getListaMatriculadosCurso()) {			
				
			 if(mt.getMatrEstado().trim().equals(EnumEstadosMatricula.DESERTADO.getCodigo())) {
				existeDesertado=true;
				break;
			}else if(mt.getMatrEstado().trim().equals(EnumEstadosMatricula.MATRICULADO.getCodigo()))
				mt.setMatrEstado(EnumEstadosMatricula.CULMINADO.getCodigo());
		}
		if(existeDesertado) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.detalleAbandono"));
		}else {
		if(getBeanFinalizarCurso().getListaMatriculadosCurso()!=null && !getBeanFinalizarCurso().getListaMatriculadosCurso().isEmpty()) {
			Mensaje.verDialogo("dlgFinalizar");
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noExisteOfertaCursos"));
		}
		}
	}
	public void mostrarDialogoFinalizarAlumnoCurso() {
		if(getBeanFinalizarCurso().getMatriculaSeleccionada().getMatrEstado().equals(EnumEstadosMatricula.CULMINADO.getCodigo())) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.motivoAbandono"));
		}else {
			Mensaje.verDialogo("dlgFinCursoAlumno");
		}
		
	}
	public void grabarFinalizarCursoAlumno() {
		try {			
			Usuario usuario = new Usuario();
			usuario=getUsuarioServicioImpl().consultarUsuarioPorDocumento(getBeanFinalizarCurso().getMatriculaSeleccionada().getEstudiante().getPersona().getPersDocumentoIdentidad());
			
			if(usuario!=null) {
				usuario.setUsuaEstado(false);
				getMatriculaServicioImpl().actualizaMatriculaUsuario(getBeanFinalizarCurso().getMatriculaSeleccionada(), usuario);
			}else 
				getMatriculaServicioImpl().actualizaMatricula(getBeanFinalizarCurso().getMatriculaSeleccionada());
			cargarAlumnosCurso();
			Mensaje.ocultarDialogo("dlgFinCursoAlumno");
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabar"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabarFinalizarCurso" + ": ").append(e.getMessage()));
		}
	}
}
