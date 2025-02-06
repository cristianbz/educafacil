/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.MedioInformacion;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

/**
*@author christian  Jun 15, 2024
*
*/
@LocalBean
@Stateless
public class MedioInformacionDaoImpl extends GenericoDaoImpl<MedioInformacionDaoImpl, Long>{
	public MedioInformacionDaoImpl() {
		
	}
	public MedioInformacionDaoImpl(EntityManager em, Class<MedioInformacionDaoImpl> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Consulta los medios de comunicacion existentes
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<MedioInformacion> listaMediosInformacion() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(MedioInformacion.LISTADO_MEDIOS_INFORMACION);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
