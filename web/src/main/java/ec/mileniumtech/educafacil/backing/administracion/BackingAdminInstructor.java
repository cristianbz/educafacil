/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanAdminInstructor;
import ec.mileniumtech.educafacil.dao.impl.CapacitacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FormacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.InstructorDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.service.InstructorDataService;
import ec.mileniumtech.educafacil.service.MatriculaDataService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
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
@Named
@ViewScoped
public class BackingAdminInstructor implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(BackingAdminInstructor.class);
	
	@Inject
	@Getter
	private BeanAdminInstructor beanAdminInstructor;
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@EJB
	@Getter
	private InstructorDataService instructorDataService;

	@EJB
	@Getter
	private MatriculaDataService matriculaDataService;
	
	/**
	 * Carga los instructores
	 */
	public void cargarInstructores() {
		getBeanAdminInstructor().setListaInstructores(new ArrayList<>());
		getBeanAdminInstructor().setListaInstructores(instructorDataService.listaInstructores());
	}
	
	@PostConstruct
	public void init() {
		cargarInstructores();

		getBeanAdminInstructor().setInstructor(new Instructor());
	}
	/**
	 * Para crear un nuevo instructor
	 */
	public void nuevoInstructor() {
		getBeanAdminInstructor().setCedula("");
		getBeanAdminInstructor().setPersona(new Persona());
		getBeanAdminInstructor().setInstructor(new Instructor());
		getBeanAdminInstructor().setMostrarDatosInstructor(true);		
		Mensaje.verDialogo("dlgNuevoInstructor");
		
	}
	/**
	 * Muestra el dialogo grabar
	 */
	public void mostrarDialogoGrabaDatosPersonales() {
		Mensaje.verDialogo("dlggrabar");
	}
	/**
	 * Graba los datos personales del instructor
	 */
	public void grabarDatosPersonales() {
		getBeanAdminInstructor().getInstructor().setPersona(getBeanAdminInstructor().getPersona());
		instructorDataService.agregarActualizarInstructor(getBeanAdminInstructor().getInstructor());			
		cargarInstructores();
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.procesoexito"));
		Mensaje.ocultarDialogo("dlgNuevoInstructor");
	}
	public void mostrarInstructor() {
		getBeanAdminInstructor().setCedula(getBeanAdminInstructor().getInstructor().getPersona().getPersDocumentoIdentidad());
		getBeanAdminInstructor().setPersona(getBeanAdminInstructor().getInstructor().getPersona());
		Mensaje.verDialogo("dlgNuevoInstructor");
	}
	public void buscaPersonaCedula() {
		getBeanAdminInstructor().setPersona(matriculaDataService.buscarPersonaPorCedula(getBeanAdminInstructor().getCedula()));
		if(getBeanAdminInstructor().getPersona()==null) {
			getBeanAdminInstructor().setPersona(new Persona());
			getBeanAdminInstructor().getPersona().setPersDocumentoIdentidad(getBeanAdminInstructor().getCedula());
		}
	}
}
