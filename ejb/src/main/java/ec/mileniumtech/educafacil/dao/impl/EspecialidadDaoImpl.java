/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
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
public class EspecialidadDaoImpl extends GenericoDaoImpl<Especialidad, Long> {
	public EspecialidadDaoImpl() {
		
	}
	public EspecialidadDaoImpl(EntityManager em, Class<Especialidad> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Consulta la lista de especialidades
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Especialidad> listaDeEspecialidades()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Especialidad.LISTA_DE_ESPECIALIDAD);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
