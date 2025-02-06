/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
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
public class RespuestasDaoImpl extends GenericoDaoImpl<Respuestas, Long>{
	public RespuestasDaoImpl() {
		
	}
	public RespuestasDaoImpl(EntityManager em, Class<Respuestas> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<Respuestas> listaRespuestas()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Respuestas.CARGAR_RESPUESTAS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public Respuestas agregActualizarRespuestas(Respuestas respuestas)throws DaoException,EntidadDuplicadaException {
		try{
			if(respuestas.getRespId()==null)
				getEntityManager().persist(respuestas);
			else
				getEntityManager().merge(respuestas);
			return respuestas;
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
	
	public List<Respuestas> listaRespuestasPorCategoria(int codigoCategoria)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Respuestas.CARGAR_RESPUESTAS_POR_CATEGORIA);
			query.setParameter("codigo", codigoCategoria);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}

}
