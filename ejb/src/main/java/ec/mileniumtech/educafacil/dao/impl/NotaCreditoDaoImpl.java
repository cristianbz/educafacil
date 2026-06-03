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
}
