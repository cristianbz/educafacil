/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing;

import java.io.Serializable;
import java.util.Properties;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitario.ObtenerPropiedades;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
*@author christian  Jul 7, 2024
*
*/
@Getter
@Setter
@Named
@ApplicationScoped
public class MensajesBacking implements Serializable{
	private static final long serialVersionUID = 1L;

    private static final Logger log = LogManager.getLogger(MensajesBacking.class);

	private static String filePath = "";
	private Properties properties;
	private ObtenerPropiedades retrieveProperties;

	public MensajesBacking() {
		retrieveProperties = new ObtenerPropiedades();
	}
	@PostConstruct
	public void init() {
		filePath = "../resources/errores_es.properties";
		properties = retrieveProperties.retrievePropertiesFromClasspath(filePath);
	}

	public String getPropiedad(String name) {
		String prop = null;
		try {
			if (properties == null) {
				init();
			}
			prop = getRetrieveProperties().getProperty(properties, name);
			if (null == prop) {
				throw new BusinessException("No existe la propiedad " + name + " en archivo " + filePath, "BIZ-MSG-NO-PROP");
			}
		} catch (Exception e) {
			log.error("Error al obtener propiedad '{}' del archivo {}", name, filePath, e);
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "ERROR", e.getMessage());
		}
		return prop;
	}

}

