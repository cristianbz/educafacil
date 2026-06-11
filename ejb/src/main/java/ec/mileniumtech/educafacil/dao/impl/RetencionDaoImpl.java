package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO para la entidad Retencion.
 */
@LocalBean
@Stateless
public class RetencionDaoImpl extends GenericoDaoImpl<Retencion, Integer> {

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
    public java.util.List<Retencion> buscarRetencionesPorFiltros(
            java.time.LocalDate fechaInicio,
            java.time.LocalDate fechaFin,
            String identificacion,
            String numeroAutorizacion,
            String estadoAutorizacion) {
    try {
        StringBuilder jpql = new StringBuilder("SELECT r FROM Retencion r ");
        
        // 1. Solo dejamos los JOINs normales con alias necesarios para el WHERE
        jpql.append("JOIN r.egreso e ");
        jpql.append("JOIN e.proveedor p ");
        jpql.append("WHERE 1=1 ");

        if (fechaInicio != null) {
            jpql.append("AND r.fechaEmision >= :fechaInicio ");
        }
        if (fechaFin != null) {
            jpql.append("AND r.fechaEmision <= :fechaFin ");
        }
        if (identificacion != null && !identificacion.trim().isEmpty()) {
            jpql.append("AND p.provRuc = :identificacion ");
        }
        if (numeroAutorizacion != null && !numeroAutorizacion.trim().isEmpty()) {
            jpql.append("AND (r.claveAcceso = :numeroAutorizacion OR r.numeroAutorizacion = :numeroAutorizacion) ");
        }
        if (estadoAutorizacion != null && !estadoAutorizacion.trim().isEmpty()) {
            jpql.append("AND r.estado = :estadoAutorizacion ");
        }

        jpql.append(" ORDER BY r.fechaEmision DESC, r.id DESC");

        TypedQuery<Retencion> query = getEntityManager().createQuery(jpql.toString(), Retencion.class);

        // 2. SOLUCIÓN AL ERROR: Creamos un Entity Graph dinámico para hacer el FETCH de forma externa
        jakarta.persistence.EntityGraph<Retencion> graph = getEntityManager().createEntityGraph(Retencion.class);
        // Le decimos que cargue de forma temprana 'egreso' y anidamos la carga de 'proveedor'
        jakarta.persistence.Subgraph<Egresos> egresoSubgraph = graph.addSubgraph("egreso");
        egresoSubgraph.addAttributeNodes("proveedor");
        
        // Le pasamos el grafo como un 'hint' a la consulta antes de ejecutarla
        query.setHint("jakarta.persistence.fetchgraph", graph);

        // 3. Asignación normal de parámetros
        if (fechaInicio != null) query.setParameter("fechaInicio", java.sql.Date.valueOf(fechaInicio));
        if (fechaFin != null) query.setParameter("fechaFin", java.sql.Date.valueOf(fechaFin));
        if (identificacion != null && !identificacion.trim().isEmpty()) query.setParameter("identificacion", identificacion);
        if (numeroAutorizacion != null && !numeroAutorizacion.trim().isEmpty()) query.setParameter("numeroAutorizacion", numeroAutorizacion);
        if (estadoAutorizacion != null && !estadoAutorizacion.trim().isEmpty()) query.setParameter("estadoAutorizacion", estadoAutorizacion);

        return query.getResultList();
    } catch (PersistenceException e) {
        throw new SystemException("Error al filtrar retenciones para reporte", "RETENCION-FILTER-ERR", e);
    }
}
}
