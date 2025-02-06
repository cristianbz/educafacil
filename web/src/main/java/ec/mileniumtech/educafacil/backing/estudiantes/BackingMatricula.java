/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;


import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.impl.EmpresaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] cbaez
 *
 */
@ViewScoped
@Named
public class BackingMatricula implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingMatricula.class);
	@Inject
	@Getter
	private ComponenteBackingMatriculaInscripcion componenteBackingMatriculaInscripcion;
	@Getter
	@Setter
	private boolean esMatricula;
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	@Getter
	@EJB
	private EmpresaDaoImpl empresaServicioImpl; 
	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaCursosServicioImpl; 
	
	@PostConstruct
	public void init() {
		setEsMatricula(true);
		componenteBackingMatriculaInscripcion.setEsMatricula(esMatricula);
		cargaEmpresas();
		try {
			componenteBackingMatriculaInscripcion.getBeanInscripcionMatricula().setListaOfertaCursos(getOfertaCursosServicioImpl().listaOfertaCursosActivos());
			componenteBackingMatriculaInscripcion.getBeanInscripcionMatricula().setListaOfertaCursos(componenteBackingMatriculaInscripcion.getBeanInscripcionMatricula().getListaOfertaCursos().stream().sorted((c1,c2)->c1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(c2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
		} catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarOfertaCursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "init" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Grabara la matricula
	 */
	public void grabarMatricula() {
		try {
			getComponenteBackingMatriculaInscripcion().grabar();

		}catch(Exception e) {			
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabarMatricula"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabarMatricula" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Mostrar el dialogo grabar
	 */
	public void mostrarDialogoGrabar() {
		if(FacesMessage.SEVERITY_ERROR.toString().trim().equals("ERROR 2")){
			Mensaje.verDialogo("dlgMatricula");
		}
	}
	/**
	 * Realiza una nueva matricula
	 */
	public void nuevaInscripcion() {
		getComponenteBackingMatriculaInscripcion().nuevaInscripcionMatricula();
	}
	/**
	 * Agrega una empresa
	 **/
	public void agregarEmpresa() {
		try {
			getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getEmpresa().setEmprEstado(true);
			getEmpresaServicioImpl().agregarEmpresa(getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getEmpresa());
			getComponenteBackingMatriculaInscripcion().setNuevaEmpresa(false);
			cargaEmpresas();
		} catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabarEmpresa"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "agregarEmpresa" + ": ").append(e.getMessage()));
		} catch (EntidadDuplicadaException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.entidadDuplicada"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "agregarEmpresa" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Lista de empresas existentes.
	 */
	public void cargaEmpresas() {
		try {
			getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().setListaEmpresas(new ArrayList<>());
			getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().setListaEmpresas(getEmpresaServicioImpl().listaEmpresas());
		} catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarEmpresas"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargaEmpresas" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Selecciona una empresa y cierra el cuadro de dialogo
	 */
	public void seleccionarEmpresa() {
		if(getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getEmpresaSeleccionada()!=null && !getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getEmpresaSeleccionada().getEmprNombre().isEmpty()) {
			
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.empresaSeleccionada").concat(": ").concat( getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getEmpresaSeleccionada().getEmprNombre()) );
			Mensaje.ocultarDialogo("dlgEmpresa");
			Mensaje.actualizarComponente(":form:matricula:growl");
			Mensaje.actualizarComponente(":form:matricula:tabs:panelEsMatricula");
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.seleccioneEmpresa"));
			Mensaje.actualizarComponente(":form:matricula:growl");
		}
	}
	/**
	 * Muestra el cuadro de dialogo para seleccionar una empresa
	 */
	public void mostrarDialogoSeleccionEmpresa() {
		if(getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getMatricula().isMatrFacturacionEmpresa()) { 
			Mensaje.verDialogo("dlgEmpresa");			
		}else {
			getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getMatricula().setMatrFacturacionEmpresa(false);
			getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().setEmpresaSeleccionada(new Empresa());
		}
	}
	
	public void cancelarIngresoMatricula() {
		getComponenteBackingMatriculaInscripcion().getBeanInscripcionMatricula().getMatricula().setMatrFacturacionEmpresa(false);
		getComponenteBackingMatriculaInscripcion().setNuevaEmpresa(false);
		Mensaje.ocultarDialogo("dlgEmpresa");
		Mensaje.actualizarComponente(":form:matricula:tabs:panelEsMatricula");
	}

	
	
}
