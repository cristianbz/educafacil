/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * DAO para la entidad Perfil.
 *
 * @author christian
 */
@LocalBean
@Stateless
public class PerfilDaoImpl extends GenericoDaoImpl<Perfil, Integer> {

    public PerfilDaoImpl() {
    }

    public PerfilDaoImpl(EntityManager em, Class<Perfil> entityClass) {
        super(em, entityClass);
    }

    /**
     * Lista todos los perfiles (activos e inactivos).
     */
    @SuppressWarnings("unchecked")
    public List<Perfil> listarTodosPerfiles() {
        try {
            TypedQuery<Perfil> query = getEntityManager()
                    .createQuery("SELECT p FROM Perfil p ORDER BY p.nombre", Perfil.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar perfiles", "PERF-LIST-ERR", e);
        }
    }

    /**
     * Lista únicamente los perfiles con estado activo (true).
     */
    public List<Perfil> listarPerfilesActivos() {
        try {
            TypedQuery<Perfil> query = getEntityManager()
                    .createQuery("SELECT p FROM Perfil p WHERE p.estado = true ORDER BY p.nombre", Perfil.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar perfiles activos", "PERF-LIST-ACT-ERR", e);
        }
    }

    /**
     * Busca un perfil por su identificador.
     */
    public Perfil buscarPerfilPorId(Integer id) {
        try {
            return getEntityManager().find(Perfil.class, id);
        } catch (Exception e) {
            throw new SystemException("Error al buscar perfil por id", "PERF-FIND-ERR", e);
        }
    }
}
