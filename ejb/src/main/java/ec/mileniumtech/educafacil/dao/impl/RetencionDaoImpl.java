package ec.mileniumtech.educafacil.dao.impl;


import ec.mileniumtech.educafacil.dao.RetencionDao;import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para la entidad Retencion.
 */
@LocalBean
@Stateless
public class RetencionDaoImpl extends GenericoDaoImpl<Retencion, Integer> implements RetencionDao {

    public RetencionDaoImpl() {
        super();
    }

    public RetencionDaoImpl(EntityManager em, Class<Retencion> entityClass) {
        super(em, entityClass);
    }

    /**
     * Busca una retención por su ID cargando sus detalles.
     * @param id ID de la retención.
     * @return La retención encontrada.
     */
    public Retencion buscarRetencionPorId(Integer id) {
        try {
            TypedQuery<Retencion> query = getEntityManager().createQuery(
                "SELECT r FROM Retencion r " +
                "JOIN FETCH r.puntoEmision " +
                "JOIN FETCH r.puntoEmision.establecimientos.empresaMatriz " +
                "JOIN FETCH r.egreso " +
                "JOIN FETCH r.egreso.proveedor " +
                "LEFT JOIN FETCH r.detalles " +
                "WHERE r.id = :id", Retencion.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (PersistenceException e) {
            throw new SystemException("Error al buscar retención con detalles", "RETENCION-FIND-ERR", e);
        }
    }

    /**
     * Lista todas las retenciones para la UI.
     * @return Lista de retenciones.
     */
    public List<Retencion> listarTodas() {
        try {
            TypedQuery<Retencion> query = getEntityManager().createQuery(
                "SELECT r FROM Retencion r " +
                "JOIN FETCH r.egreso " +
                "JOIN FETCH r.egreso.proveedor " +
                "ORDER BY r.id DESC", Retencion.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar retenciones", "RETENCION-LIST-ERR", e);
        }
    }

    /**
     * Actualiza la retención.
     * @param retencion Entidad a actualizar.
     */
    public void actualizarRetencion(Retencion retencion) {
        try {
            getEntityManager().merge(retencion);
        } catch (PersistenceException e) {
            throw new SystemException("Error al actualizar retención", "RETENCION-UPDATE-ERR", e);
        }
    }
    public List<Retencion> buscarRetencionesPorFiltros(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String identificacion,
            String numeroAutorizacion,
            String estadoAutorizacion) {
        try {
            StringBuilder jpql = new StringBuilder("""
                SELECT r FROM Retencion r 
                JOIN FETCH r.egreso 
                JOIN FETCH r.egreso.proveedor 
                WHERE 1=1
                """);

            Map<String, Object> params = new HashMap<>();

            if (fechaInicio != null) {
                jpql.append(" AND r.fechaEmision >= :fechaInicio");
                params.put("fechaInicio", fechaInicio);
            }
            if (fechaFin != null) {
                jpql.append(" AND r.fechaEmision <= :fechaFin");
                params.put("fechaFin", fechaFin);
            }
            if (identificacion != null && !identificacion.isBlank()) {
                jpql.append(" AND r.egreso.proveedor.provRuc = :identificacion");
                params.put("identificacion", identificacion.trim());
            }
            if (numeroAutorizacion != null && !numeroAutorizacion.isBlank()) {
                jpql.append(" AND (r.claveAcceso = :numeroAutorizacion OR r.numeroAutorizacion = :numeroAutorizacion)");
                params.put("numeroAutorizacion", numeroAutorizacion.trim());
            }
            if (estadoAutorizacion != null && !estadoAutorizacion.isBlank()) {
                jpql.append(" AND r.estado = :estadoAutorizacion");
                params.put("estadoAutorizacion", estadoAutorizacion.trim());
            }

            jpql.append(" ORDER BY r.fechaEmision DESC, r.id DESC");

            TypedQuery<Retencion> query = getEntityManager().createQuery(jpql.toString(), Retencion.class);
            params.forEach(query::setParameter);

            return query.getResultList();

        } catch (PersistenceException e) {
            throw new SystemException("Error al filtrar retenciones para reporte", "RETENCION-FILTER-ERR", e);
        }
    }
}
