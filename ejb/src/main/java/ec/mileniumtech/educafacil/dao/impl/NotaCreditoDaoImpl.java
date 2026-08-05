package ec.mileniumtech.educafacil.dao.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.mileniumtech.educafacil.dao.NotaCreditoDao;import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
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
public class NotaCreditoDaoImpl extends GenericoDaoImpl<NotaCredito, Integer> implements NotaCreditoDao {
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
    /**
     * Metodo para buscar las notas de credito
     * @fechaInicio la fecha de creacion de la nota
     */
    public java.util.List<NotaCredito> buscarNotasCreditoPorFiltros(
            java.time.LocalDate fechaInicio,
            java.time.LocalDate fechaFin,
            String identificacion,
            String numeroAutorizacion,
            String estadoAutorizacion) {
        
        try {
            StringBuilder jpql = new StringBuilder("""
                SELECT nc FROM NotaCredito nc 
                JOIN FETCH nc.cliente 
                WHERE 1=1
                """);

            Map<String, Object> params = new HashMap<>();

            if (fechaInicio != null) {
                jpql.append(" AND nc.fechaEmision >= :fechaInicio");
                params.put("fechaInicio", fechaInicio);
            }
            if (fechaFin != null) {
                jpql.append(" AND nc.fechaEmision <= :fechaFin");
                params.put("fechaFin", fechaFin);
            }
            if (identificacion != null && !identificacion.isBlank()) {
                jpql.append(" AND nc.cliente.numeroIdentificacion = :identificacion");
                params.put("identificacion", identificacion.trim());
            }
            if (numeroAutorizacion != null && !numeroAutorizacion.isBlank()) {
                jpql.append(" AND (nc.claveAcceso = :numeroAutorizacion OR nc.numeroAutorizacion = :numeroAutorizacion)");
                params.put("numeroAutorizacion", numeroAutorizacion.trim());
            }
            if (estadoAutorizacion != null && !estadoAutorizacion.isBlank()) {
                jpql.append(" AND nc.estado = :estadoAutorizacion");
                params.put("estadoAutorizacion", estadoAutorizacion.trim());
            }

            jpql.append(" ORDER BY nc.fechaEmision DESC, nc.id DESC");

            TypedQuery<NotaCredito> query = getEntityManager().createQuery(jpql.toString(), NotaCredito.class);
            params.forEach(query::setParameter);

            return query.getResultList();

        } catch (PersistenceException e) {
            throw new SystemException("Error al filtrar notas de crédito para reporte", "NOTACREDITO-FILTER-ERR", e);
        }
    }
}
