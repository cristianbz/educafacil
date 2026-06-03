package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Establecimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Subgraph;
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
        	// 1. Definimos el Entity Graph para Factura
        	EntityGraph<Factura> graph = getEntityManager().createEntityGraph(Factura.class);

        	// 2. Agregamos las relaciones simples a cargar
        	graph.addAttributeNodes("cliente", "puntoEmision");

        	// 3. Para las relaciones anidadas profundas, creamos "Subgraphs"
        	// Esto equivale a: puntoEmision -> establecimientos -> empresaMatriz
        	Subgraph<PuntoEmision> puntoGraph = graph.addSubgraph("puntoEmision");
        	Subgraph<Establecimiento> estGraph = puntoGraph.addSubgraph("establecimientos");
        	estGraph.addAttributeNodes("empresaMatriz");

        	// Esto equivale a: detalles -> item
        	Subgraph<DetalleFactura> detalleGraph = graph.addSubgraph("detalles");
        	detalleGraph.addAttributeNodes("item");

        	// 4. Tu consulta JPQL queda limpia y totalmente legal para JPA (sin FETCH ni alias raros)
        	TypedQuery<Factura> query = getEntityManager().createQuery(
        	    "SELECT f FROM Factura f WHERE f.id = :id", Factura.class);

        	query.setParameter("id", id);

        	// 5. Le pasamos el gráfico como una "pista" (Hint) a la consulta
        	query.setHint("jakarta.persistence.loadgraph", graph); 
        	// Nota: Si usas una versión antigua de Java EE, usa "javax.persistence.loadgraph"

        	Factura factura = query.getSingleResult();
//        	return factura;
//            TypedQuery<Factura> query = getEntityManager().createQuery(
//            		"SELECT f FROM Factura f " +
//            			    "JOIN FETCH f.cliente " +
//            			    "JOIN FETCH f.puntoEmision pe " +
//            			    "JOIN FETCH pe.establecimientos est " +
//            			    "JOIN FETCH est.empresaMatriz " +
//            			    "LEFT JOIN FETCH f.detalles d " +
//            			    "LEFT JOIN FETCH d.item " + // <--- ¡ESTA ES LA MAGIA!
//            			    "WHERE f.id = :id", Factura.class);
//            query.setParameter("id", id);
//            Factura factura = query.getSingleResult();
////            if (factura.getDetalles() != null) {
////                for (DetalleFactura df : factura.getDetalles()) {
////                    if (df.getItem() != null) {
////                        df.getItem().getNombre(); // Forzar inicialización del proxy
////                    }
////                }
////            }
            return factura;
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
     * Lista todas las facturas cargando el cliente y documento electronico para la UI del día.
     * @return Lista de facturas
     */
    public java.util.List<Factura> listarTodasLasFacturasDelDia() {
        try {
            TypedQuery<Factura> query = getEntityManager().createQuery(
                "SELECT f FROM Factura f " +
                "JOIN FETCH f.cliente " +
                "LEFT JOIN FETCH f.documentoElectronico " +
                "WHERE f.fechaEmision=CURRENT_DATE " +
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
    public java.util.List<Factura> buscarFacturasPorFiltros(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin, String identificacion, String numeroAutorizacion,String estadoAutorizacion) {
        try {
            StringBuilder jpql = new StringBuilder("SELECT f FROM Factura f ");
            jpql.append("JOIN FETCH f.cliente ");
            jpql.append("JOIN FETCH f.documentoElectronico ");
            jpql.append("WHERE f.documentoElectronico.estado = :estadoAutorizacion ");

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
            if (estadoAutorizacion != null && !estadoAutorizacion.trim().isEmpty()){
            	query.setParameter("estadoAutorizacion", estadoAutorizacion);            	
            }
            

            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al filtrar facturas para reporte", "FACTURA-FILTER-ERR", e);
        }
    }
}
