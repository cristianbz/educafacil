/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
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
public class FormacionDaoImpl extends GenericoDaoImpl<Formacion, Long>{
	public FormacionDaoImpl() {
		
	}
	public FormacionDaoImpl(EntityManager em, Class<Formacion> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega actualiza una formacion del instructor
	 * @param formacion
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregaActualizaFormacion(Formacion formacion) throws DaoException, EntidadDuplicadaException{
		try{
			if (formacion.getFormId()==0)
				getEntityManager().persist(formacion);
			else
				getEntityManager().merge(formacion);
			
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
	/**
	 * Devuelve la lista de formaciones del instructor
	 * @param codigoInstructor
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Formacion> listaFormaciones(int codigoInstructor) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Formacion.LISTADO_FORMACIONES);
			query.setParameter("codigoInstructor", codigoInstructor);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
