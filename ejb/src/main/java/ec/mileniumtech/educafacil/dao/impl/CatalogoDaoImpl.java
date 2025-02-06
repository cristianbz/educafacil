/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
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
public class CatalogoDaoImpl extends GenericoDaoImpl<Catalogo,Long>{
	public CatalogoDaoImpl() {
		
	}
	public CatalogoDaoImpl(EntityManager em, Class<Catalogo> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Devuelve los catalogos acorde al tipo
	 * @param tipoCatalogo
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Catalogo> catalogosPorTipo(String tipoCatalogo) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Catalogo.BUSCAR_POR_TIPO_CATALOGO);
			query.setParameter("tipoCatalogo",tipoCatalogo);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Busca catalogo por id del padre
	 * @param padre
	 * @return
	 * @throws DaoException
	 */
	public List<Catalogo> catalogosPorPadre(Catalogo padre) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Catalogo.BUSCAR_POR_PADRE);
			query.setParameter("padre",padre);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
