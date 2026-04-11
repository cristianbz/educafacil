/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleEvaluaCurso;
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
public class DetalleEvaluaCursoDaoImpl extends GenericoDaoImpl<DetalleEvaluaCurso, Long>{
	public DetalleEvaluaCursoDaoImpl() {
		
	}
	public DetalleEvaluaCursoDaoImpl(EntityManager em, Class<DetalleEvaluaCurso> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<DetalleEvaluaCurso> listaDeDetallesDeEvaluacionDeCursos(){
		try {
			Query query=getEntityManager().createNamedQuery(DetalleEvaluaCurso.CARGAR_DETALLE_EVALUACION);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al listar detalles de evaluación de cursos", "DEVC-LIST-ERR", e);
		}
	}
	
	public void guardarEncuesta(DetalleEvaluaCurso detalle){
		try{
			if (detalle.getDevcId() == null)
				getEntityManager().persist(detalle);
			else
				getEntityManager().merge(detalle);
			
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en encuesta de evaluación", "DEVC-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en encuesta de evaluación", "DEVC-UNEXPECTED-ERR", e);
		}	
	}
}
