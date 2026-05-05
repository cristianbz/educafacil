package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

/**
 * DAO para la entidad Factura.
 */
@LocalBean
@Stateless
public class FacturaDaoImpl extends GenericoDaoImpl<Factura, Integer> {

    public FacturaDaoImpl() {
        super();
    }

    public FacturaDaoImpl(EntityManager em, Class<Factura> entityClass) {
        super(em, entityClass);
    }

    /**
     * Busca una factura por su ID cargando sus detalles y cliente.
     * @param id ID de la factura.
     * @return La factura encontrada.
     */
    public Factura buscarFacturaPorId(Integer id) {
        try {
            TypedQuery<Factura> query = getEntityManager().createQuery(
                "SELECT f FROM Factura f " +
                "JOIN FETCH f.cliente " +
                "JOIN FETCH f.puntoEmision " +
                "JOIN FETCH f.puntoEmision.establecimientos.empresaMatriz " +
                "LEFT JOIN FETCH f.detalles " +
                "WHERE f.id = :id", Factura.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (PersistenceException e) {
            throw new SystemException("Error al buscar factura con detalles", "FACTURA-FIND-ERR", e);
        }
    }
    
    /**
     * Actualiza la factura en la base de datos.
     * @param factura Entidad a actualizar.
     */
    public void actualizarFactura(Factura factura) {
        try {
            getEntityManager().merge(factura);
        } catch (PersistenceException e) {
            throw new SystemException("Error al actualizar factura", "FACTURA-UPDATE-ERR", e);
        }
    }

    /**
     * Lista todas las facturas cargando el cliente y documento electronico para la UI.
     * @return Lista de facturas
     */
    public java.util.List<Factura> listarTodasLasFacturas() {
        try {
            TypedQuery<Factura> query = getEntityManager().createQuery(
                "SELECT f FROM Factura f " +
                "JOIN FETCH f.cliente " +
                "LEFT JOIN FETCH f.documentoElectronico " +
                "ORDER BY f.id DESC", Factura.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar facturas", "FACTURA-LIST-ERR", e);
        }
    }


    /**
     * Busca facturas por filtros para reportería.
     * @param fechaInicio Fecha inicial de emisión.
     * @param fechaFin Fecha final de emisión.
     * @param identificacion Identificación del cliente.
     * @param numeroAutorizacion Clave de acceso o número de autorización.
     * @return Lista de facturas que coinciden con los criterios.
     */
    public java.util.List<Factura> buscarFacturasPorFiltros(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin, String identificacion, String numeroAutorizacion) {
        try {
            StringBuilder jpql = new StringBuilder("SELECT f FROM Factura f ");
            jpql.append("JOIN FETCH f.cliente ");
            jpql.append("JOIN FETCH f.documentoElectronico ");
            jpql.append("WHERE f.documentoElectronico.estado = 'AUTORIZADO' ");

            if (fechaInicio != null) {
                jpql.append("AND f.fechaEmision >= :fechaInicio ");
            }
            if (fechaFin != null) {
                jpql.append("AND f.fechaEmision <= :fechaFin ");
            }
            if (identificacion != null && !identificacion.trim().isEmpty()) {
                jpql.append("AND f.cliente.persona.persIdentificacion = :identificacion ");
            }
            if (numeroAutorizacion != null && !numeroAutorizacion.trim().isEmpty()) {
                jpql.append("AND (f.documentoElectronico.claveAcceso = :numeroAutorizacion OR f.documentoElectronico.numeroAutorizacion = :numeroAutorizacion) ");
            }

            jpql.append("ORDER BY f.fechaEmision DESC, f.id DESC");

            TypedQuery<Factura> query = getEntityManager().createQuery(jpql.toString(), Factura.class);

            if (fechaInicio != null) {
                query.setParameter("fechaInicio", fechaInicio);
            }
            if (fechaFin != null) {
                query.setParameter("fechaFin", fechaFin);
            }
            if (identificacion != null && !identificacion.trim().isEmpty()) {
                query.setParameter("identificacion", identificacion);
            }
            if (numeroAutorizacion != null && !numeroAutorizacion.trim().isEmpty()) {
                query.setParameter("numeroAutorizacion", numeroAutorizacion);
            }

            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al filtrar facturas para reporte", "FACTURA-FILTER-ERR", e);
        }
    }
}
