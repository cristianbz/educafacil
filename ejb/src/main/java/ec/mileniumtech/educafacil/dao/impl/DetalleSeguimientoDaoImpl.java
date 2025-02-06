/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
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
public class DetalleSeguimientoDaoImpl extends GenericoDaoImpl<DetalleSeguimiento, Long> {
	public DetalleSeguimientoDaoImpl() {
		
	}
	public DetalleSeguimientoDaoImpl(EntityManager em, Class<DetalleSeguimiento> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	public void agregarDetalle(DetalleSeguimiento detalle) throws DaoException,EntidadDuplicadaException {
		try{
			if (detalle.getDsegId() == 0)
				getEntityManager().persist(detalle);
			else
				getEntityManager().merge(detalle);
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

	@SuppressWarnings("unchecked")
	public List<DetalleSeguimiento> listaDetalle(Integer seguimiento) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(DetalleSeguimiento.LISTA_DETALLE);
			query.setParameter("seguimiento", seguimiento);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
