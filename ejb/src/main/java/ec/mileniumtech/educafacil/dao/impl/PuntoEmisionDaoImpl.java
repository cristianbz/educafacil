package ec.mileniumtech.educafacil.dao.impl;


import ec.mileniumtech.educafacil.dao.PuntoEmisionDao;import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
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
public class PuntoEmisionDaoImpl extends GenericoDaoImpl<PuntoEmision, Integer> implements PuntoEmisionDao {

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
                "SELECT pe FROM PuntoEmision pe LEFT JOIN FETCH pe.establecimientos LEFT JOIN FETCH pe.establecimientos.empresaMatriz WHERE pe.estado = true", PuntoEmision.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar puntos de emisión activos", "PUEM-LIST-ERR", e);
        }
    }
    /**
     * Devuelve la lista de puntos de emisión por establecimiento.
     * @param estaId ID del establecimiento.
     * @return Lista de puntos de emisión.
     */
    public List<PuntoEmision> listarPuntosEmisionPorEstablecimiento(int estaId) {
        try {
            TypedQuery<PuntoEmision> query = getEntityManager().createQuery(
                "SELECT pe FROM PuntoEmision pe LEFT JOIN FETCH pe.establecimientos LEFT JOIN FETCH pe.establecimientos.empresaMatriz WHERE pe.establecimientos.estaId = :estaId ORDER BY pe.codigo", PuntoEmision.class);
            query.setParameter("estaId", estaId);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar puntos de emisión por establecimiento", "PUEM-ESTA-LIST-ERR", e);
        }
    }

    /**
     * Busca un punto de emisión por ID cargando su establecimiento y empresa matriz.
     * @param id ID del punto de emisión.
     * @return Punto de emisión con relaciones inicializadas.
     */
    public PuntoEmision buscarPuntoEmisionPorId(Integer id) {
        try {
            TypedQuery<PuntoEmision> query = getEntityManager().createQuery(
                "SELECT pe FROM PuntoEmision pe LEFT JOIN FETCH pe.establecimientos LEFT JOIN FETCH pe.establecimientos.empresaMatriz WHERE pe.id = :id", PuntoEmision.class);
            query.setParameter("id", id);
            List<PuntoEmision> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (PersistenceException e) {
            throw new SystemException("Error al buscar punto de emisión por id", "PUEM-FIND-ERR", e);
        }
    }
}
