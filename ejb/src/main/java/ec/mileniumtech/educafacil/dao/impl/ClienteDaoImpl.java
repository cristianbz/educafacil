package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO para la entidad Cliente.
 */
@LocalBean
@Stateless
public class ClienteDaoImpl extends GenericoDaoImpl<Cliente, Integer> {

    public ClienteDaoImpl() {
        super();
    }

    public ClienteDaoImpl(EntityManager em, Class<Cliente> entityClass) {
        super(em, entityClass);
    }

    /**
     * Busca un cliente por su número de identificación.
     * @param identificacion Cédula o RUC.
     * @return El cliente encontrado o null.
     */
    public Cliente buscarPorIdentificacion(String identificacion) {
        try {
            TypedQuery<Cliente> query = getEntityManager().createQuery(
                "SELECT c FROM Cliente c WHERE c.numeroIdentificacion = :identificacion", Cliente.class);
            query.setParameter("identificacion", identificacion);
            List<Cliente> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (PersistenceException e) {
            throw new SystemException("Error al buscar cliente por identificación", "CLIENTE-FIND-ERR", e);
        }
    }
}
