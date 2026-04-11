package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

/**
* @author christian Aug 27, 2025
*/
/**
 * 
 */
@Stateless
public class PersonaService {
	@Inject
	private PersonaDaoImpl personaDaoImpl;
	
	public Persona buscarPersonaPorCedulaCorreo(String cedula, String correo) {
		Persona p=null;
		try {
			p = personaDaoImpl.buscarPersonaPorCedulaCorreo(cedula, correo);
		} catch (Exception e) {
			throw new SystemException("Error al buscar persona", "PERS-SINGLE-ERR", e);
		}
		return p;
	}
}

