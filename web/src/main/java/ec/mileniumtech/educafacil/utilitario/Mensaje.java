/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitario;

import java.util.List;

import org.primefaces.PrimeFaces;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

/**
*@author christian  Jul 7, 2024
*
*/
public class Mensaje {
	public static void verMensaje(FacesMessage.Severity s, String message, String detail) {
		FacesMessage facesMessage = new FacesMessage(s, message, detail);
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}
	/**
	 * Visualiza un mensaje con detalles
	 * @param clienteId
	 * @param s
	 * @param message
	 * @param detail
	 */
	public static void verMensaje(String clienteId, FacesMessage.Severity s, String message, String detail) {
		FacesMessage facesMessage = new FacesMessage(s, message, detail);
		FacesContext.getCurrentInstance().addMessage(clienteId, facesMessage);
	}
	/**
	 * Visualiza un cuadro de dialogo
	 * @param widget
	 */
	public static void verDialogo(String widget) {
		PrimeFaces.current().executeScript("PF('"+widget+"').show()");
	}
	/**
	 * Oculta un cuadro de dialogo
	 * @param widget
	 */
	public static void ocultarDialogo(String widget) {
		
		PrimeFaces.current().executeScript("PF('"+widget+"').hide()");
	}
	/**
	 * Actualiza un componente
	 * @param id
	 */
	public static void actualizarComponente(String id) {
		PrimeFaces.current().ajax().update(id);
		
	}
	/**
	 * Visualiza errores
	 * @param errors
	 * @param mensajes
	 */
	public static void verErrores(List<String> errors, MensajesBacking mensajes) {
		for (String error : errors) {
			verMensaje(FacesMessage.SEVERITY_ERROR, mensajes.getPropiedad(error) + " " + mensajes.getPropiedad("campoRequerido"), null);
		}

	}
	/**
	 * Obtiene url del servidor
	 * @return
	 */
	public static String obtenerUrlServidor() {
		String url="";
		String serverName=FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		String contexto=FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();		
		int puerto=FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort();
		String certificado="http";
		if(puerto==8443) {
			certificado+="s";
		}
		url=certificado+"://"+serverName+":"+puerto+contexto;		
		return url;
	}
}

