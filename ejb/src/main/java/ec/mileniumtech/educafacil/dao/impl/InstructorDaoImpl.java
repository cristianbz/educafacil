/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

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
	}
	/**
	 * Devuelve la lista de instructores
	 * @return List<Instructor>
	 */
	@SuppressWarnings("unchecked")
	public List<Instructor> listaInstructores() {
		try {
			Query query=getEntityManager().createNamedQuery(Instructor.LISTADO_INSTRUCTORES);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al listar instructores", "INST-LIST-ERR", e);
		}
	}
	/**
	 * Agrega actualiza un instructor
	 * @param instructor
	 */
	public void agregarActualizarInstructor(Instructor instructor) {
		try{
			if(instructor.getPersona().getPersId()==0)
				getEntityManager().persist(instructor.getPersona());
			else
				getEntityManager().merge(instructor.getPersona());
			
			if(instructor.getInstId()==0)
				getEntityManager().persist(instructor);
			else
				getEntityManager().merge(instructor);
			
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en instructor", "INST-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en instructor", "INST-UNEXPECTED-ERR", e);
		}	
	}
}
