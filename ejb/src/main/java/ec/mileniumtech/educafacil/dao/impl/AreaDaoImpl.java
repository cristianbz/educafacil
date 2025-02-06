package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@LocalBean
@Stateless
public class AreaDaoImpl extends GenericoDaoImpl<Area,Long>{

	public AreaDaoImpl() {
		
	}
	public AreaDaoImpl(EntityManager em, Class<Area> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Consulta la lista de areas
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Area> listaDeAreas()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Area.LISTA_DE_AREAS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}

