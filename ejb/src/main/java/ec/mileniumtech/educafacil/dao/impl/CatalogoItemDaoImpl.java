package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO para la entidad CatalogoItem.
 */
@LocalBean
@Stateless
public class CatalogoItemDaoImpl extends GenericoDaoImpl<CatalogoItem, Integer> {

    public CatalogoItemDaoImpl() {
        super();
    }

    public CatalogoItemDaoImpl(EntityManager em, Class<CatalogoItem> entityClass) {
        super(em, entityClass);
    }

    /**
     * Busca un ítem de catálogo por su código.
     * @param codigo Código del ítem.
     * @return El ítem encontrado o null.
     */
    public CatalogoItem buscarPorCodigo(String codigo) {
        try {
            TypedQuery<CatalogoItem> query = getEntityManager().createQuery(
                "SELECT ci FROM CatalogoItem ci WHERE ci.codigo = :codigo", CatalogoItem.class);
            query.setParameter("codigo", codigo);
            List<CatalogoItem> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (PersistenceException e) {
            throw new SystemException("Error al buscar CatalogoItem por código", "CATITEM-FIND-ERR", e);
        }
    }
}
