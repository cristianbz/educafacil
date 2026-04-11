/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
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
public class PreguntaDaoImpl extends GenericoDaoImpl<Pregunta, Long>{
	public PreguntaDaoImpl() {
		
	}
	public PreguntaDaoImpl(EntityManager em, Class<Pregunta> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<Pregunta> listaDePreguntas(){
		try {
			Query query=getEntityManager().createNamedQuery(Pregunta.CARGAR_PREGUNTA);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  preguntas", "PREGU-LIST-ERR", e);
		}
	}
	public Pregunta agregarActualizarPregunta(Pregunta pregunta) {
		try{
			if(pregunta.getPregId()==null)
				getEntityManager().persist(pregunta);
			else
				getEntityManager().merge(pregunta);
			return pregunta;
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en pregunta", "PREGU-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en pregunta", "PREGU-UNEXPECTED-ERR", e);
		}	
	}
	
	public List<Pregunta> listaPreguntasPorCategoria(int codigoCategoriaP){
		try {
			Query query=getEntityManager().createNamedQuery(Pregunta.CARGAR_PREGUNTA_POR_CATEGORIA);
			query.setParameter("codigo", codigoCategoriaP);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  preguntas por categoria", "PREGU-LIST-ERR", e);
		}
	}
}
