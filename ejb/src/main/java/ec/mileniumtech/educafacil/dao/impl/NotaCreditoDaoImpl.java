package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
/**
 * DAO para la entidad NotaCredito.
 */
@LocalBean
@Stateless
public class NotaCreditoDaoImpl extends GenericoDaoImpl<NotaCredito, Integer> {
    public NotaCreditoDaoImpl() {
        super();
    }
    public NotaCreditoDaoImpl(EntityManager em, Class<NotaCredito> entityClass) {
        super(em, entityClass);
    }
    /**
     * Busca una nota de crédito por su ID cargando sus detalles y cliente.
     * @param id ID de la nota de crédito.
     * @return La nota de crédito encontrada.
     */
    public NotaCredito buscarNotaCreditoPorId(Integer id) {
        try {
            TypedQuery<NotaCredito> query = getEntityManager().createQuery(
                "SELECT nc FROM NotaCredito nc " +
                "JOIN FETCH nc.cliente " +
                "JOIN FETCH nc.puntoEmision " +
                "JOIN FETCH nc.puntoEmision.establecimientos.empresaMatriz " +
                "JOIN FETCH nc.factura " +
                "LEFT JOIN FETCH nc.detalles " +
                "WHERE nc.id = :id", NotaCredito.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (PersistenceException e) {
            throw new SystemException("Error al buscar nota de crédito con detalles", "NOTACREDITO-FIND-ERR", e);
        }
    }
    
    /**
     * Lista todas las notas de crédito.
     * @return Lista de notas de crédito
     */
    public java.util.List<NotaCredito> listarTodas() {
        try {
            TypedQuery<NotaCredito> query = getEntityManager().createQuery(
                "SELECT nc FROM NotaCredito nc " +
                "JOIN FETCH nc.cliente " +
                "JOIN FETCH nc.factura " +
                "ORDER BY nc.id DESC", NotaCredito.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar notas de crédito", "NOTACREDITO-LIST-ERR", e);
        }
    }
    public java.util.List<NotaCredito> buscarNotasCreditoPorFiltros(
            java.time.LocalDate fechaInicio,
            java.time.LocalDate fechaFin,
            String identificacion,
            String numeroAutorizacion,
            String estadoAutorizacion) {
    try {
        StringBuilder jpql = new StringBuilder("SELECT nc FROM NotaCredito nc ");
        
        // SOLUCCIÓN: JOIN normal con alias para el WHERE + JOIN FETCH sin alias para la carga de datos
        jpql.append("JOIN nc.cliente c "); 
        jpql.append("JOIN FETCH nc.cliente "); 
        
        jpql.append("WHERE 1=1 ");

        if (fechaInicio != null) {
            jpql.append("AND nc.fechaEmision >= :fechaInicio ");
        }
        if (fechaFin != null) {
            jpql.append("AND nc.fechaEmision <= :fechaFin ");
        }
        if (identificacion != null && !identificacion.trim().isEmpty()) {
            // Aquí usamos el alias 'c' del JOIN normal
            jpql.append("AND c.numeroIdentificacion = :identificacion ");
        }
        if (numeroAutorizacion != null && !numeroAutorizacion.trim().isEmpty()) {
            jpql.append("AND (nc.claveAcceso = :numeroAutorizacion OR nc.numeroAutorizacion = :numeroAutorizacion) ");
        }
        if (estadoAutorizacion != null && !estadoAutorizacion.trim().isEmpty()) {
            jpql.append("AND nc.estado = :estadoAutorizacion ");
        }

        jpql.append(" ORDER BY nc.fechaEmision DESC, nc.id DESC");

        TypedQuery<NotaCredito> query = getEntityManager().createQuery(jpql.toString(), NotaCredito.class);

        if (fechaInicio != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin != null) query.setParameter("fechaFin", fechaFin);
        if (identificacion != null && !identificacion.trim().isEmpty()) query.setParameter("identificacion", identificacion);
        if (numeroAutorizacion != null && !numeroAutorizacion.trim().isEmpty()) query.setParameter("numeroAutorizacion", numeroAutorizacion);
        if (estadoAutorizacion != null && !estadoAutorizacion.trim().isEmpty()) query.setParameter("estadoAutorizacion", estadoAutorizacion);

        return query.getResultList();
    } catch (PersistenceException e) {
        throw new SystemException("Error al filtrar notas de crédito para reporte", "NOTACREDITO-FILTER-ERR", e);
    }
}
}
