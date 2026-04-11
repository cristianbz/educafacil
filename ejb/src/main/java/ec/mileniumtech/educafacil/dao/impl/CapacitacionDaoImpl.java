/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
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
public class CapacitacionDaoImpl extends GenericoDaoImpl<Capacitacion,Long>{
public CapacitacionDaoImpl() {
		
	}
	public CapacitacionDaoImpl(EntityManager em, Class<Capacitacion> entityClass) {
		super(em, entityClass);
	}
	/**
	 * Agrega una capacitacion
	 * @param capacitacion
	 */
	public void agregarActualizarCapacitacion(Capacitacion capacitacion) {
		try{
			if (capacitacion.getCapaId()==0)
				getEntityManager().persist(capacitacion);
			else
				getEntityManager().merge(capacitacion);
			
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en capacitación", "CAP-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en capacitación", "CAP-UNEXPECTED-ERR", e);
		}	
	}
	/**
	 * Devuelve la lista de capacitaciones seguidas por el instructor
	 * @param codigoInstructor
	 * @return List<Capacitacion>
	 */
	@SuppressWarnings("unchecked")
	public List<Capacitacion> listaCapacitaciones(int codigoInstructor) {
		try {
			Query query=getEntityManager().createNamedQuery(Capacitacion.LISTADO_CAPACITACIONES);
			query.setParameter("codigoInstructor", codigoInstructor);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al listar capacitaciones", "CAP-LIST-ERR", e);
		}
	}
}
