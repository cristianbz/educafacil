/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * DAO para la entidad PerfilAccion (relación Perfil-Accion).
 *
 * @author christian
 */
@LocalBean
@Stateless
public class PerfilAccionDaoImpl extends GenericoDaoImpl<PerfilAccion, Integer> {

    public PerfilAccionDaoImpl() {
    }

    public PerfilAccionDaoImpl(EntityManager em, Class<PerfilAccion> entityClass) {
        super(em, entityClass);
    }

    /**
     * Lista todas las acciones (menú / módulos) disponibles en el sistema.
     */
    public List<Accion> listarTodasAcciones() {
        try {
            TypedQuery<Accion> query = getEntityManager()
                    .createQuery("SELECT a FROM Accion a WHERE a.estado = true AND a.ruta<>'.' ORDER BY a.nombre", Accion.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar todas las acciones", "ACC-LIST-ERR", e);
        }
    }

    /**
     * Lista las asociaciones PerfilAccion activas para un perfil dado.
     */
    public List<PerfilAccion> listarAccionesPorPerfil(Integer perfilId) {
        try {
            TypedQuery<PerfilAccion> query = getEntityManager()
                    .createQuery(
                            "SELECT pa FROM PerfilAccion pa " +
                            "JOIN FETCH pa.accion " +
                            "WHERE pa.perfil.id = :perfilId AND pa.estado = true",
                            PerfilAccion.class);
            query.setParameter("perfilId", perfilId);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar acciones del perfil", "PACC-LIST-ERR", e);
        }
    }

    /**
     * Verifica si ya existe una asignacion activa para el perfil y acción dados.
     */
    public PerfilAccion buscarPerfilAccion(Integer perfilId, String accionId) {
        try {
            TypedQuery<PerfilAccion> query = getEntityManager()
                    .createQuery(
                            "SELECT pa FROM PerfilAccion pa " +
                            "WHERE pa.perfil.id = :perfilId AND pa.accion.id = :accionId",
                            PerfilAccion.class);
            query.setParameter("perfilId", perfilId);
            query.setParameter("accionId", accionId);
            List<PerfilAccion> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            throw new SystemException("Error al buscar PerfilAccion", "PACC-FIND-ERR", e);
        }
    }

    /**
     * Genera el siguiente ID para PerfilAccion (la entidad usa ID manual).
     */
    public Integer siguienteId() {
        try {
            Object result = getEntityManager()
                    .createQuery("SELECT COALESCE(MAX(pa.id), 0) FROM PerfilAccion pa")
                    .getSingleResult();
            return ((Number) result).intValue() + 1;
        } catch (Exception e) {
            throw new SystemException("Error al obtener siguiente id de PerfilAccion", "PACC-SEQ-ERR", e);
        }
    }

    /**
     * Elimina (física) la asignación entre un perfil y una acción.
     */
    public void eliminarPorPerfilYAccion(Integer perfilId, String accionId) {
        try {
            getEntityManager()
                    .createQuery(
                            "DELETE FROM PerfilAccion pa " +
                            "WHERE pa.perfil.id = :perfilId AND pa.accion.id = :accionId")
                    .setParameter("perfilId", perfilId)
                    .setParameter("accionId", accionId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new SystemException("Error al eliminar PerfilAccion", "PACC-DEL-ERR", e);
        }
    }
}
