package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Establecimiento;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO para la entidad Establecimiento.
 */
@LocalBean
@Stateless
public class EstablecimientoDaoImpl extends GenericoDaoImpl<Establecimiento, Integer> {

    public EstablecimientoDaoImpl() {
        super();
    }

    public EstablecimientoDaoImpl(EntityManager em, Class<Establecimiento> entityClass) {
        super(em, entityClass);
    }

    /**
     * Devuelve la lista de establecimientos por empresa.
     * @param empmId ID de la empresa matriz.
     * @return Lista de establecimientos.
     */
    public List<Establecimiento> listarEstablecimientosPorEmpresa(int empmId) {
        try {
            TypedQuery<Establecimiento> query = getEntityManager().createQuery(
                "SELECT e FROM Establecimiento e WHERE e.empresaMatriz.empmId = :empmId ORDER BY e.estaCodigo", Establecimiento.class);
            query.setParameter("empmId", empmId);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new SystemException("Error al listar establecimientos por empresa", "ESTA-LIST-ERR", e);
        }
    }
    
}
