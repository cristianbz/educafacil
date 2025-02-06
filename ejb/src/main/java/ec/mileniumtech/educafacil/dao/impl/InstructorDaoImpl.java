/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
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
public class InstructorDaoImpl extends GenericoDaoImpl<Instructor, Long>{
	public InstructorDaoImpl() {
		
	}
	public InstructorDaoImpl(EntityManager em, Class<Instructor> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Devuelve la lista de instructores
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Instructor> listaInstructores() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Instructor.LISTADO_INSTRUCTORES);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Agrega actualiza un instructor
	 * @param instructor
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarActualizarInstructor(Instructor instructor) throws DaoException,EntidadDuplicadaException{
		try{
			Persona persona=new Persona();
			if(instructor.getPersona().getPersId()==0)
				getEntityManager().persist(instructor.getPersona());
			else
				getEntityManager().merge(instructor.getPersona());
			persona=instructor.getPersona();
			instructor.setPersona(persona);
			if(instructor.getInstId()==0)
				getEntityManager().persist(instructor);
			else
				getEntityManager().merge(instructor);
			
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
