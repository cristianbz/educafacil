/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
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
public class TipoEncuestaDaoImpl extends GenericoDaoImpl<TipoEncuesta, Long>{
	public TipoEncuestaDaoImpl() {
		
	}
	public TipoEncuestaDaoImpl(EntityManager em, Class<TipoEncuesta> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<TipoEncuesta> listaDeTiposDeEncuestas(){
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
			throw new SystemException("Error al consultar lista tipo encuesta", "TENCU-LIST-ERR", e);
		}
	}
	public List<TipoEncuesta> listaDeTiposDeEncuestasPorOe(int codigo){
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
			throw new SystemException("Error al consultar lista tipo encuesta por codigo objeto_evaluacion", "TENCU-LIST-ERR", e);
		}
	}
	public TipoEncuesta actualizarTipoEncuesta(TipoEncuesta tipoEncuesta) {
		try{
			if(tipoEncuesta.getTipeId()==null)
				getEntityManager().persist(tipoEncuesta);
			else
				getEntityManager().merge(tipoEncuesta);
			return tipoEncuesta;
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en tipo encuesta", "TENCU-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en tipo encuesta", "TENCU-UNEXPECTED-ERR", e);
		}		
	}
}
