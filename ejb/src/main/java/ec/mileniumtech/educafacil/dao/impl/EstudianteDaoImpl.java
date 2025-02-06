/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;

/**
*@author christian  Jun 15, 2024
*
*/
@LocalBean
@Stateless
public class EstudianteDaoImpl extends GenericoDaoImpl<Estudiante, Long>{
	public EstudianteDaoImpl() {
		
	}
	public EstudianteDaoImpl(EntityManager em, Class<Estudiante> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Busca estudiantes por el apellido
	 * @param apellidos
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Estudiante> estudiantesPorApellido(String apellidos) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Estudiante.BUSCA_POR_APELLIDO);
			query.setParameter("apellidos", "%"+apellidos.toLowerCase()+"%");
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Busca un estudiante por cedula
	 * @param cedula
	 * @return
	 * @throws DaoException
	 */
	public Estudiante estudiantesPorCedula(String cedula) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Estudiante.BUSCA_POR_CEDULA);
			query.setParameter("cedula", cedula);
			return (Estudiante) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Actualiza un estudiante
	 * @param estudiante
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void actualizaEstudiante(Estudiante estudiante) throws DaoException, EntidadDuplicadaException{
		try{
			Persona persona = estudiante.getPersona();
			getEntityManager().merge(estudiante);
			getEntityManager().merge(persona);
			
		}catch(PersistenceException e){
			 Throwable t = e.getCause();
			    while ((t != null) && !(t instanceof ConstraintViolationException)) {
			        t = t.getCause();
			    }
			    if (t instanceof ConstraintViolationException) {
			    	throw new EntidadDuplicadaException(e);
			    }
			throw new DaoException(e);
		} 	catch (Exception e) {
			throw new DaoException(e);
		}	
	}
}
