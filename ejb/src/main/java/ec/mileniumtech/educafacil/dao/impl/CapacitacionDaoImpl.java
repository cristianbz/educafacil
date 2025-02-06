/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
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
public class CapacitacionDaoImpl extends GenericoDaoImpl<Capacitacion,Long>{
	public CapacitacionDaoImpl() {
		
	}
	public CapacitacionDaoImpl(EntityManager em, Class<Capacitacion> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega una capacitacion
	 * @param capacitacion
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarActualizarCapacitacion(Capacitacion capacitacion) throws DaoException, EntidadDuplicadaException{
		try{
			if (capacitacion.getCapaId()==0)
				getEntityManager().persist(capacitacion);
			else
				getEntityManager().merge(capacitacion);
			
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
	 * Devuelve la lista de capacitaciones seguidas por el instructor
	 * @param codigoInstructor
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Capacitacion> listaCapacitaciones(int codigoInstructor) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Capacitacion.LISTADO_CAPACITACIONES);
			query.setParameter("codigoInstructor", codigoInstructor);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
