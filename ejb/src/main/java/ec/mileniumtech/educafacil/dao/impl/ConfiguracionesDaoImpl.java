/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
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
public class ConfiguracionesDaoImpl extends GenericoDaoImpl<Configuraciones,Long>{
	public ConfiguracionesDaoImpl() {
		
	}
	public ConfiguracionesDaoImpl(EntityManager em, Class<Configuraciones> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Retorna la lista de configuraciones
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Configuraciones> listaConfiguraciones()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Configuraciones.LISTA_CONFIGURACIONES);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}

}
