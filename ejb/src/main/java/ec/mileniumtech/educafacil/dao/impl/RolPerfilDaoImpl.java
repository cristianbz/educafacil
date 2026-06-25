/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.RolPerfil;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * DAO para la entidad RolPerfil (relación Rol-Perfil).
 *
 * @author christian
 */
@LocalBean
@Stateless
public class RolPerfilDaoImpl extends GenericoDaoImpl<RolPerfil, Integer> {

    public RolPerfilDaoImpl() {
    }

    public RolPerfilDaoImpl(EntityManager em, Class<RolPerfil> entityClass) {
        super(em, entityClass);
    }

    /**
     * Lista las asociaciones RolPerfil activas para un rol dado.
     */
    public List<RolPerfil> listarPerfilesPorRol(Integer rolId) {
        try {
            TypedQuery<RolPerfil> query = getEntityManager()
                    .createQuery(
                            "SELECT rp FROM RolPerfil rp " +
                            "JOIN FETCH rp.perfil " +
                            "WHERE rp.rol.rolId = :rolId AND rp.estado = true",
                            RolPerfil.class);
            query.setParameter("rolId", rolId);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar perfiles del rol", "RPER-LIST-ERR", e);
        }
    }

    /**
     * Verifica si ya existe una asignación para el rol y perfil dados.
     */
    public RolPerfil buscarRolPerfil(Integer rolId, Integer perfilId) {
        try {
            TypedQuery<RolPerfil> query = getEntityManager()
                    .createQuery(
                            "SELECT rp FROM RolPerfil rp " +
                            "WHERE rp.rol.rolId = :rolId AND rp.perfil.id = :perfilId",
                            RolPerfil.class);
            query.setParameter("rolId", rolId);
            query.setParameter("perfilId", perfilId);
            List<RolPerfil> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            throw new SystemException("Error al buscar RolPerfil", "RPER-FIND-ERR", e);
        }
    }

    /**
     * Genera el siguiente ID para RolPerfil.
     */
    public Integer siguienteId() {
        try {
            Object result = getEntityManager()
                    .createQuery("SELECT COALESCE(MAX(rp.id), 0) FROM RolPerfil rp")
                    .getSingleResult();
            return ((Number) result).intValue() + 1;
        } catch (Exception e) {
            throw new SystemException("Error al obtener siguiente id de RolPerfil", "RPER-SEQ-ERR", e);
        }
    }

    /**
     * Elimina (física) la asignación entre un rol y un perfil.
     */
    public void eliminarPorRolYPerfil(Integer rolId, Integer perfilId) {
        try {
            getEntityManager()
                    .createQuery(
                            "DELETE FROM RolPerfil rp " +
                            "WHERE rp.rol.rolId = :rolId AND rp.perfil.id = :perfilId")
                    .setParameter("rolId", rolId)
                    .setParameter("perfilId", perfilId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new SystemException("Error al eliminar RolPerfil", "RPER-DEL-ERR", e);
        }
    }
}
