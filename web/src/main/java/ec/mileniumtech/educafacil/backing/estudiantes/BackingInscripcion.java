/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.util.stream.Collectors;


import org.apache.log4j.Logger;


import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
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
public class BackingInscripcion implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingInscripcion.class);
	@Inject
	@Getter
	private ComponenteBackingMatriculaInscripcion componenteBackingMatriculaInscripcion;
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaCursosServicioImpl; 
	
	@Getter
	@Setter
	private boolean esInscripcion;
	
	@PostConstruct
	public void init() {
		setEsInscripcion(true);
		componenteBackingMatriculaInscripcion.setEsInscripcion(esInscripcion);
		try {
			componenteBackingMatriculaInscripcion.getBeanInscripcionMatricula().setListaOfertaCursos(getOfertaCursosServicioImpl().listaOfertaCursosPorDefecto());
			componenteBackingMatriculaInscripcion.getBeanInscripcionMatricula().setListaOfertaCursos(componenteBackingMatriculaInscripcion.getBeanInscripcionMatricula().getListaOfertaCursos().stream().sorted((c1,c2)->c1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(c2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
		}catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarOfertaCursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "init" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Grabar datos de la persona
	 */
	public void grabarInscripcion() {
		try {
			getComponenteBackingMatriculaInscripcion().grabar();
		}catch(Exception e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabarInscripcion"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabarInscripcion" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Muestra el dialogo para grabar una inscripcion
	 */
	public void mostrarDialogoGrabar() {		
		if(FacesMessage.SEVERITY_ERROR.toString().trim().equals("ERROR 2")){
			if(getComponenteBackingMatriculaInscripcion().validaDatosPersona())
				Mensaje.verDialogo("dlgInscribir");
			else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.datosEstudiante"));	
				Mensaje.actualizarComponente(":form:matricula:growl");
			}
		}
	}
	/**
	 * Realiza una nueva inscripcion
	 */
	public void nuevaInscripcion() {
		getComponenteBackingMatriculaInscripcion().nuevaInscripcionMatricula();
	}

}
