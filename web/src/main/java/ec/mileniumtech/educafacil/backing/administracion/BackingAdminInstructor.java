/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanAdminInstructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.service.facade.InstructorFacade;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BackingAdminInstructor implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(BackingAdminInstructor.class);
	
	private transient JasperReport cachedReport;
//	private JasperReport jasperReport;

	@Inject
	@Getter
	private BeanAdminInstructor beanAdminInstructor;
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@EJB
	@Getter
	private InstructorFacade instructorDataService;

	@EJB
	@Getter
	private MatriculaFacade matriculaDataService;
	
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
		getBeanAdminInstructor().setListaFormaciones(new ArrayList<>());
		getBeanAdminInstructor().setListaCapacitaciones(new ArrayList<>());
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
		cargarFormaciones();
		cargarCapacitaciones();
		Mensaje.verDialogo("dlgNuevoInstructor");
	}
	public void buscaPersonaCedula() {
		getBeanAdminInstructor().setPersona(matriculaDataService.buscarPersonaPorCedula(getBeanAdminInstructor().getCedula()));
		if(getBeanAdminInstructor().getPersona()==null) {
			getBeanAdminInstructor().setPersona(new Persona());
			getBeanAdminInstructor().getPersona().setPersDocumentoIdentidad(getBeanAdminInstructor().getCedula());
		}
	}
	/**
	 * Carga las formaciones del instructor seleccionado
	 */
	public void cargarFormaciones() {
		int instId = getBeanAdminInstructor().getInstructor().getInstId();
		if(instId > 0) {
			getBeanAdminInstructor().setListaFormaciones(instructorDataService.listaFormaciones(instId));
		} else {
			getBeanAdminInstructor().setListaFormaciones(new ArrayList<>());
		}
	}
	/**
	 * Carga las capacitaciones del instructor seleccionado
	 */
	public void cargarCapacitaciones() {
		int instId = getBeanAdminInstructor().getInstructor().getInstId();
		if(instId > 0) {
			getBeanAdminInstructor().setListaCapacitaciones(instructorDataService.listaCapacitaciones(instId));
		} else {
			getBeanAdminInstructor().setListaCapacitaciones(new ArrayList<>());
		}
	}
	/**
	 * Prepara dialogo para nueva formacion
	 */
	public void nuevaFormacion() {
		getBeanAdminInstructor().setFormacionSelected(new Formacion());
		getBeanAdminInstructor().getFormacionSelected().setInstructor(getBeanAdminInstructor().getInstructor());
		getBeanAdminInstructor().setMostrarDialogoFormacion(true);
		Mensaje.verDialogo("dlgNuevaFormacion");
	}
	/**
	 * Prepara dialogo para editar una formacion
	 * @param formacion
	 */
	public void editarFormacion(Formacion formacion) {
		getBeanAdminInstructor().setFormacionSelected(formacion);
		getBeanAdminInstructor().setMostrarDialogoFormacion(true);
		Mensaje.verDialogo("dlgNuevaFormacion");
	}
	/**
	 * Graba una formacion
	 */
	public void grabarFormacion() {
		Instructor instructor = getBeanAdminInstructor().getInstructor();
		if(instructor.getInstId() == 0) {
			Persona persona = getBeanAdminInstructor().getPersona();
			if(persona == null) {
				persona = new Persona();
				getBeanAdminInstructor().setPersona(persona);
			}
			instructor.setPersona(persona);
			instructorDataService.agregarActualizarInstructor(instructor);
			getBeanAdminInstructor().setPersona(instructor.getPersona());
		}
		getBeanAdminInstructor().getFormacionSelected().setInstructor(instructor);
		instructorDataService.agregaActualizaFormacion(getBeanAdminInstructor().getFormacionSelected());
		cargarFormaciones();
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.procesoexito"));
		Mensaje.ocultarDialogo("dlgNuevaFormacion");
	}
	/**
	 * Elimina una formacion
	 * @param formacion
	 */
	public void eliminarFormacion(Formacion formacion) {
		instructorDataService.eliminarFormacion(formacion);
		cargarFormaciones();
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.procesoexito"));
	}
	/**
	 * Prepara dialogo para nueva capacitacion
	 */
	public void nuevaCapacitacion() {
		getBeanAdminInstructor().setCapacitacionSelected(new Capacitacion());
		getBeanAdminInstructor().getCapacitacionSelected().setInstructor(getBeanAdminInstructor().getInstructor());
		getBeanAdminInstructor().setMostrarDialogoCapacitacion(true);
		Mensaje.verDialogo("dlgNuevaCapacitacion");
	}
	/**
	 * Prepara dialogo para editar una capacitacion
	 * @param capacitacion
	 */
	public void editarCapacitacion(Capacitacion capacitacion) {
		getBeanAdminInstructor().setCapacitacionSelected(capacitacion);
		getBeanAdminInstructor().setMostrarDialogoCapacitacion(true);
		Mensaje.verDialogo("dlgNuevaCapacitacion");
	}
	/**
	 * Graba una capacitacion
	 */
	public void grabarCapacitacion() {
		Instructor instructor = getBeanAdminInstructor().getInstructor();
		if(instructor.getInstId() == 0) {
			Persona persona = getBeanAdminInstructor().getPersona();
			if(persona == null) {
				persona = new Persona();
				getBeanAdminInstructor().setPersona(persona);
			}
			instructor.setPersona(persona);
			instructorDataService.agregarActualizarInstructor(instructor);
			getBeanAdminInstructor().setPersona(instructor.getPersona());
		}
		getBeanAdminInstructor().getCapacitacionSelected().setInstructor(instructor);
		instructorDataService.agregarActualizarCapacitacion(getBeanAdminInstructor().getCapacitacionSelected());
		cargarCapacitaciones();
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.procesoexito"));
		Mensaje.ocultarDialogo("dlgNuevaCapacitacion");
	}
	/**
	 * Elimina una capacitacion
	 * @param capacitacion
	 */
	public void eliminarCapacitacion(Capacitacion capacitacion) {
		instructorDataService.eliminarCapacitacion(capacitacion);
		cargarCapacitaciones();
		Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.procesoexito"));
	}
	
	public void generarPdfInstructor() {
		try {
			Instructor instructor = getBeanAdminInstructor().getInstructor();
			if (instructor == null) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, getMensajesBacking().getPropiedad("info"), "Seleccione un instructor primero.");
				return;
			}
			Persona persona = instructor.getPersona();
			if (persona == null) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, getMensajesBacking().getPropiedad("info"), "El instructor no tiene datos personales.");
				return;
			}
			cargarFormaciones();
			cargarCapacitaciones();

			if(cachedReport == null) {
				InputStream jrxmlStream = getClass().getResourceAsStream("/reports/reporteInstructor.jrxml");
				if(jrxmlStream == null) {
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "No se encontró el reporte JRXML");
					return;
				}
				cachedReport = JasperCompileManager.compileReport(jrxmlStream);
			}

			Map<String, Object> params = new HashMap<>();
			params.put("instructor", instructor);
			params.put("persona", persona);
			params.put("formaciones", getBeanAdminInstructor().getListaFormaciones());
			params.put("capacitaciones", getBeanAdminInstructor().getListaCapacitaciones());

			JasperPrint jasperPrint = JasperFillManager.fillReport(cachedReport, params, new JREmptyDataSource(1));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

			FacesContext context = FacesContext.getCurrentInstance();
			jakarta.faces.context.ExternalContext externalContext = context.getExternalContext();
			byte[] pdfBytes = baos.toByteArray();
			String nombreArchivo = persona.getPersApellidos() + "_" + persona.getPersNombres() + "_" + instructor.getInstId() + ".pdf";
			externalContext.responseReset();
			externalContext.setResponseContentType("application/pdf");
			externalContext.setResponseContentLength(pdfBytes.length);
			externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
			OutputStream responseOutput = externalContext.getResponseOutputStream();
			responseOutput.write(pdfBytes);
			responseOutput.flush();
			context.responseComplete();

		} catch (JRException | IOException e) {
			log.error("Error al generar PDF del instructor", e);
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error al generar el PDF");
		}		
	}
}
