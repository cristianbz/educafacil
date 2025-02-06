/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
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
public class TipoEncuestaDaoImpl extends GenericoDaoImpl<TipoEncuesta, Long>{
	public TipoEncuestaDaoImpl() {
		
	}
	public TipoEncuestaDaoImpl(EntityManager em, Class<TipoEncuesta> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<TipoEncuesta> listaDeTiposDeEncuestas()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(TipoEncuesta.CARGAR_TIPOS_ENCUESTAS);
			for(Object objeto:query.getResultList()) {
				TipoEncuesta tepr =(TipoEncuesta) objeto;
				
				Iterator itera = tepr.getTipoEncuestaPregunta().iterator();
				while (itera.hasNext()) {
					TipoEncuestaPregunta tepreg = (TipoEncuestaPregunta)itera.next();
					if(tepreg.isTeprEstado()== false)
						itera.remove();
					Hibernate.initialize(tepreg);
				}
			}
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public List<TipoEncuesta> listaDeTiposDeEncuestasPorOe(int codigo)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(TipoEncuesta.CARGAR_TIPOS_ENCUESTAS_POR_OE);
			query.setParameter("codigo", codigo);
			for(Object objeto:query.getResultList()) {
				TipoEncuesta tepr =(TipoEncuesta) objeto;
				
				Iterator itera = tepr.getTipoEncuestaPregunta().iterator();
				while (itera.hasNext()) {
					TipoEncuestaPregunta tepreg = (TipoEncuestaPregunta)itera.next();
					if(tepreg.isTeprEstado()== false)
						itera.remove();
					Hibernate.initialize(tepreg);
				}
			}
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public TipoEncuesta actualizarTipoEncuesta(TipoEncuesta tipoEncuesta)throws DaoException,EntidadDuplicadaException {
		try{
			if(tipoEncuesta.getTipeId()==null)
				getEntityManager().persist(tipoEncuesta);
			else
				getEntityManager().merge(tipoEncuesta);
			return tipoEncuesta;
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
