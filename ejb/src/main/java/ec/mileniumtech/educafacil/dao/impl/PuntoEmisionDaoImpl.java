package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO para la entidad PuntoEmision.
 */
@LocalBean
@Stateless
public class PuntoEmisionDaoImpl extends GenericoDaoImpl<PuntoEmision, Integer> {

    public PuntoEmisionDaoImpl() {
        super();
    }

    public PuntoEmisionDaoImpl(EntityManager em, Class<PuntoEmision> entityClass) {
        super(em, entityClass);
    }

    /**
     * Devuelve la lista de puntos de emisión activos.
     * @return Lista de puntos de emisión.
     */
    public List<PuntoEmision> listarPuntosEmisionActivos() {
        try {
            TypedQuery<PuntoEmision> query = getEntityManager().createQuery(
                "SELECT pe FROM PuntoEmision pe WHERE pe.estado = true", PuntoEmision.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar puntos de emisión activos", "PUEM-LIST-ERR", e);
        }
    }
}
