/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Seguimiento;
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
public class SeguimientoDaoImpl extends GenericoDaoImpl<Seguimiento, Long>{
	public SeguimientoDaoImpl() {
		
	}
	public SeguimientoDaoImpl(EntityManager em, Class<Seguimiento> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega / Actualiza un seguimiento
	 * @param seguimiento
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarActualizarSeguimiento(Seguimiento seguimiento) throws DaoException,EntidadDuplicadaException{
		try{			
			if (seguimiento.getSegmId()==0) {
				getEntityManager().persist(seguimiento);
			}else {
				getEntityManager().merge(seguimiento);
			}
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new DaoException(e);
		} 	catch (Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Retorna el seguimiento a un estudiante
	 * @param matricula
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Seguimiento> listaSeguimientoMatricula(int matricula) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Seguimiento.BUSCAR_POR_MATRICULA);
			query.setParameter("matricula", matricula);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
