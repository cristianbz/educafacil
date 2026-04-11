/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
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
public class RespuestasDaoImpl extends GenericoDaoImpl<Respuestas, Long>{
	public RespuestasDaoImpl() {
		
	}
	public RespuestasDaoImpl(EntityManager em, Class<Respuestas> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<Respuestas> listaRespuestas(){
		try {
			Query query=getEntityManager().createNamedQuery(Respuestas.CARGAR_RESPUESTAS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista respuestas", "RESPU-LIST-ERR", e);
		}
	}
	
	public Respuestas agregActualizarRespuestas(Respuestas respuestas) {
		try{
			if(respuestas.getRespId()==null)
				getEntityManager().persist(respuestas);
			else
				getEntityManager().merge(respuestas);
			return respuestas;
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en respuestas", "RESPU-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en respuestas", "RESPU-UNEXPECTED-ERR", e);
		}		
	}
	
	public List<Respuestas> listaRespuestasPorCategoria(int codigoCategoria){
		try {
			Query query=getEntityManager().createNamedQuery(Respuestas.CARGAR_RESPUESTAS_POR_CATEGORIA);
			query.setParameter("codigo", codigoCategoria);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista respuestas por categoria", "RESPU-LIST-ERR", e);
		}
	}

}
