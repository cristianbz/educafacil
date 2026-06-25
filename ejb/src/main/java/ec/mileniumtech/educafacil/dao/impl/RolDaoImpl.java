/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * DAO para la entidad Rol.
 *
 * @author christian
 */
@LocalBean
@Stateless
public class RolDaoImpl extends GenericoDaoImpl<Rol, Integer> {

    public RolDaoImpl() {
    }

    public RolDaoImpl(EntityManager em, Class<Rol> entityClass) {
        super(em, entityClass);
    }

    /**
     * Lista todos los roles (activos e inactivos).
     */
    public List<Rol> listarTodosRoles() {
        try {
            TypedQuery<Rol> query = getEntityManager()
                    .createQuery("SELECT r FROM Rol r ORDER BY r.rolNombre", Rol.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar roles", "ROL-LIST-ERR", e);
        }
    }

    /**
     * Lista únicamente los roles con estado activo (true).
     */
    public List<Rol> listarRolesActivos() {
        try {
            TypedQuery<Rol> query = getEntityManager()
                    .createQuery("SELECT r FROM Rol r WHERE r.rolEstado = true ORDER BY r.rolNombre", Rol.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar roles activos", "ROL-LIST-ACT-ERR", e);
        }
    }

    /**
     * Busca un rol por su identificador.
     */
    public Rol buscarRolPorId(Integer id) {
        try {
            return getEntityManager().find(Rol.class, id);
        } catch (Exception e) {
            throw new SystemException("Error al buscar rol por id", "ROL-FIND-ERR", e);
        }
    }
}
