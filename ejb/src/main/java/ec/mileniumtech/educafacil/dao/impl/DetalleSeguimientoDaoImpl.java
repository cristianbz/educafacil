/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
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
public class DetalleSeguimientoDaoImpl extends GenericoDaoImpl<DetalleSeguimiento, Long> {
	public DetalleSeguimientoDaoImpl() {
		
	}
	public DetalleSeguimientoDaoImpl(EntityManager em, Class<DetalleSeguimiento> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	public void agregarDetalle(DetalleSeguimiento detalle) {
		try{
			if (detalle.getDsegId() == 0)
				getEntityManager().persist(detalle);
			else
				getEntityManager().merge(detalle);
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en detalle de seguimiento", "DSEG-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en detalle de seguimiento", "DSEG-UNEXPECTED-ERR", e);
		}	
	}

	@SuppressWarnings("unchecked")
	public List<DetalleSeguimiento> listaDetalle(Integer seguimiento) {
		try {
			Query query=getEntityManager().createNamedQuery(DetalleSeguimiento.LISTA_DETALLE);
			query.setParameter("seguimiento", seguimiento);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al listar detalle de seguimiento", "DSEG-LIST-ERR", e);
		}
	}
}
