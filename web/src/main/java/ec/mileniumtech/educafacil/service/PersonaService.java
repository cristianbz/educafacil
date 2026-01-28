package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
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
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
}

