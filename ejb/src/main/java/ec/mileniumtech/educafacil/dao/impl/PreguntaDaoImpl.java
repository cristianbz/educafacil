/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
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
public class PreguntaDaoImpl extends GenericoDaoImpl<Pregunta, Long>{
	public PreguntaDaoImpl() {
		
	}
	public PreguntaDaoImpl(EntityManager em, Class<Pregunta> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<Pregunta> listaDePreguntas()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Pregunta.CARGAR_PREGUNTA);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public Pregunta agregarActualizarPregunta(Pregunta pregunta)throws DaoException,EntidadDuplicadaException {
		try{
			if(pregunta.getPregId()==null)
				getEntityManager().persist(pregunta);
			else
				getEntityManager().merge(pregunta);
			return pregunta;
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
	
	public List<Pregunta> listaPreguntasPorCategoria(int codigoCategoriaP)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Pregunta.CARGAR_PREGUNTA_POR_CATEGORIA);
			query.setParameter("codigo", codigoCategoriaP);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
