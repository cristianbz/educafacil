/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
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
public class EvaluacionCursoDaoImpl extends GenericoDaoImpl<EvaluacionCurso, Long>{
	public EvaluacionCursoDaoImpl() {
		
	}
	public EvaluacionCursoDaoImpl(EntityManager em, Class<EvaluacionCurso> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<EvaluacionCurso> listaDeEvaluacionesDeCurso()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(EvaluacionCurso.CARGAR_EVALUACION_CURSO);
			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/***
	 * Lista las evaluaciones por curso y objeto evaluacion 
	 * @param codigo  codigo del curso
	 * @param codigoobj codigo del objeto de evaluacion
	 * @return
	 * @throws DaoException
	 */
	public List<EvaluacionCurso> listaDeEvaluacionesPorCurso(int codigo, int codigoobj)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(EvaluacionCurso.CARGAR_ENCUESTAS_POR_CURSO_OBJETOEVALUACION);
			query.setParameter("codigo", codigo);
			query.setParameter("codigoobj", codigoobj);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Lista las encuestas por el curso activo
	 * @param codigo codigo de curso
	 * @return
	 * @throws DaoException
	 */
	public List<EvaluacionCurso> listaDeEvaluacionesDeCursoActivas(int codigoC)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(EvaluacionCurso.CARGAR_ENCUESTAS_POR_CURSO_ACTIVO);
			query.setParameter("codigoOferta", codigoC);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public EvaluacionCurso agregarEvaluacionCurso(EvaluacionCurso evaluacionCurso)throws DaoException,EntidadDuplicadaException {
		try{
			if(evaluacionCurso.getEvcuId()==null)
				getEntityManager().persist(evaluacionCurso);
			else
				getEntityManager().merge(evaluacionCurso);
			return evaluacionCurso;
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
