/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CategoriaRespuesta;
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
public class CategoriaRespuestaDaoImpl extends GenericoDaoImpl<CategoriaRespuesta,Long>{
	public CategoriaRespuestaDaoImpl() {
		
	}
	public CategoriaRespuestaDaoImpl(EntityManager em, Class<CategoriaRespuesta> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<CategoriaRespuesta> listaDeCategorias()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(CategoriaRespuesta.CARGAR_CATEGORIA);
			for (Object object : query.getResultList()) {
				CategoriaRespuesta catRes = (CategoriaRespuesta) object;
				Iterator itera = catRes.getRespuestas().iterator();
				while (itera.hasNext()) {
					Respuestas respuesta = (Respuestas)itera.next();
					if(respuesta.isRespEstado()== false)
						itera.remove();
					Hibernate.initialize(respuesta);
				}
			}
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public CategoriaRespuesta actualizarCategoriaRespuesta(CategoriaRespuesta categoriaRespuesta)throws DaoException,EntidadDuplicadaException {
		try{
			if(categoriaRespuesta.getCatrId()==null)
				getEntityManager().persist(categoriaRespuesta);
			else
				getEntityManager().merge(categoriaRespuesta);
			return categoriaRespuesta;
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
	
	public CategoriaRespuesta buscaCategoria(int codigoCategoria)throws DaoException{
		try {
			CategoriaRespuesta categoria=null;
			Query query=getEntityManager().createNamedQuery(CategoriaRespuesta.BUSCAR_CATEGORIA);
			query.setParameter("codigo", codigoCategoria);
			categoria = (CategoriaRespuesta)query.getSingleResult();
			for (Respuestas resp : categoria.getRespuestas()) {
					Hibernate.initialize(resp);
			}
			
			return categoria;
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
