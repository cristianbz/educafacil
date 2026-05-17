package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO para el catálogo de códigos SRI de retención.
 */
@LocalBean
@Stateless
public class CodigoSriRetencionDaoImpl extends GenericoDaoImpl<CodigoSriRetencion, Integer> {

    public CodigoSriRetencionDaoImpl() {
        super();
    }

    public CodigoSriRetencionDaoImpl(EntityManager em, Class<CodigoSriRetencion> entityClass) {
        super(em, entityClass);
    }

    /**
     * Lista los códigos SRI activos para un tipo de impuesto dado.
     *
     * @param tipoImpuesto "1" (Renta), "2" (IVA) o "6" (ISD)
     * @return Lista de códigos SRI activos ordenados por código.
     */
    public List<CodigoSriRetencion> listarPorTipoImpuesto(String tipoImpuesto) {
        try {
            TypedQuery<CodigoSriRetencion> query = getEntityManager().createQuery(
                "SELECT c FROM CodigoSriRetencion c " +
                "WHERE c.tipoImpuesto = :tipo AND c.activo = true " +
                "ORDER BY c.codigo",
                CodigoSriRetencion.class
            );
            query.setParameter("tipo", tipoImpuesto);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar códigos SRI por tipo impuesto", "CSRI-LIST-ERR", e);
        }
    }

    /**
     * Busca códigos SRI por tipo de impuesto y texto en código o descripción (para autoComplete).
     *
     * @param tipoImpuesto Tipo de impuesto.
     * @param query        Texto a buscar.
     * @return Lista filtrada.
     */
    public List<CodigoSriRetencion> buscarPorTipoYTexto(String tipoImpuesto, String query) {
        try {
            String patron = "%" + query.toLowerCase() + "%";
            TypedQuery<CodigoSriRetencion> q = getEntityManager().createQuery(
                "SELECT c FROM CodigoSriRetencion c " +
                "WHERE c.tipoImpuesto = :tipo AND c.activo = true " +
                "AND (LOWER(c.codigo) LIKE :patron OR LOWER(c.descripcion) LIKE :patron) " +
                "ORDER BY c.codigo",
                CodigoSriRetencion.class
            );
            q.setParameter("tipo", tipoImpuesto);
            q.setParameter("patron", patron);
            return q.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al buscar códigos SRI", "CSRI-SEARCH-ERR", e);
        }
    }
}
