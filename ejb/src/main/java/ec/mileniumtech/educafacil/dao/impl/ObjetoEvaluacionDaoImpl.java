/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
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
public class ObjetoEvaluacionDaoImpl extends GenericoDaoImpl<ObjetoEvaluacion, Long>{
	public ObjetoEvaluacionDaoImpl() {
		
	}
	public ObjetoEvaluacionDaoImpl(EntityManager em, Class<ObjetoEvaluacion> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<ObjetoEvaluacion> listaDeObjetosDeEvaluacion()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(ObjetoEvaluacion.CARGAR_OBJETO_EVALUACION);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * metodo que actualiza o guarda un objeto de evaluacion
	 * @param objetoEvaluacion
	 * @return
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public ObjetoEvaluacion actualizarObjetoEvaluacion(ObjetoEvaluacion objetoEvaluacion)throws DaoException,EntidadDuplicadaException {
		try{
			if(objetoEvaluacion.getObjeId()==null)
				getEntityManager().persist(objetoEvaluacion);
			else
				getEntityManager().merge(objetoEvaluacion);
			return objetoEvaluacion;
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
