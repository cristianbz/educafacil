/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PlanificacionCurso;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * DAO para la entidad PlanificacionCurso.
 *
 * @author christian
 */
@LocalBean
@Stateless
public class PlanificacionCursoDaoImpl extends GenericoDaoImpl<PlanificacionCurso, Integer> {

    public PlanificacionCursoDaoImpl() {
    }

    public PlanificacionCursoDaoImpl(EntityManager em, Class<PlanificacionCurso> entityClass) {
        super(em, entityClass);
    }

    /**
     * Lista todas las planificaciones de cursos ordenadas por fecha de inicio.
     * Se evita el doble LEFT JOIN FETCH encadenado sobre una relación opcional
     * (instructor → persona) que genera HibernateException; en su lugar se
     * cargan las asociaciones de forma lazy y se deja que el contexto de
     * persistencia las resuelva en la vista.
     *
     * @return lista de PlanificacionCurso
     */
    public List<PlanificacionCurso> listarTodas() {
        try {
            TypedQuery<PlanificacionCurso> query = getEntityManager()
                    .createQuery(
                            "SELECT DISTINCT pc FROM PlanificacionCurso pc "
                            + "LEFT JOIN FETCH pc.curso "
                            + "LEFT JOIN FETCH pc.instructor "
                            + "ORDER BY pc.plcuFechaInicio, pc.plcuHoraInicio",
                            PlanificacionCurso.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new SystemException("Error al listar planificaciones de cursos", "PLCU-LIST-ERR", e);
        }
    }

    /**
     * Lista planificaciones de cursos para una semana dada
     * (entre fechaInicio y fechaFin, inclusive).
     * <p>
     * Nota: se usa una subconsulta con IN en lugar de FETCH+WHERE para evitar
     * el problema de Hibernate con JOIN FETCH y ORDER BY sobre colecciones
     * filtradas. La carga del instructor y curso se realiza en una pasada
     * independiente a través de los IDs obtenidos.
     * </p>
     *
     * @param fechaInicio inicio del rango (LocalDate)
     * @param fechaFin    fin del rango (LocalDate)
     * @return lista filtrada y ordenada
     */
    public List<PlanificacionCurso> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            // 1. Obtener IDs del rango sin FETCH para evitar el problema HHH-1
            TypedQuery<Integer> idsQuery = getEntityManager()
                    .createQuery(
                            "SELECT pc.plcuId FROM PlanificacionCurso pc "
                            + "WHERE pc.plcuFechaInicio >= :fechaInicio "
                            + "AND pc.plcuFechaInicio <= :fechaFin "
                            + "ORDER BY pc.plcuFechaInicio, pc.plcuHoraInicio",
                            Integer.class);
            idsQuery.setParameter("fechaInicio", fechaInicio);
            idsQuery.setParameter("fechaFin", fechaFin);
            List<Integer> ids = idsQuery.getResultList();

            if (ids == null || ids.isEmpty()) {
                return new ArrayList<>();
            }

            // 2. Cargar las entidades completas con FETCH usando los IDs
            TypedQuery<PlanificacionCurso> fetchQuery = getEntityManager()
                    .createQuery(
                            "SELECT DISTINCT pc FROM PlanificacionCurso pc "
                            + "LEFT JOIN FETCH pc.curso "
                            + "LEFT JOIN FETCH pc.instructor "
                            + "WHERE pc.plcuId IN :ids",
                            PlanificacionCurso.class);
            fetchQuery.setParameter("ids", ids);
            List<PlanificacionCurso> resultado = fetchQuery.getResultList();

            // 3. Re-ordenar según el orden original de fechas/hora
            resultado.sort((a, b) -> {
                int cmpFecha = a.getPlcuFechaInicio().compareTo(b.getPlcuFechaInicio());
                if (cmpFecha != 0) return cmpFecha;
                Integer horaA = a.getPlcuHoraInicio() != null ? a.getPlcuHoraInicio() : 0;
                Integer horaB = b.getPlcuHoraInicio() != null ? b.getPlcuHoraInicio() : 0;
                return horaA.compareTo(horaB);
            });

            return resultado;

        } catch (Exception e) {
            throw new SystemException("Error al listar planificaciones por rango de fechas", "PLCU-RANGE-ERR", e);
        }
    }

    /**
     * Agrega una nueva planificación de curso.
     *
     * @param planificacion entidad a persistir
     */
    public void agregarPlanificacion(PlanificacionCurso planificacion) {
        try {
            getEntityManager().persist(planificacion);
            getEntityManager().flush();
        } catch (Exception e) {
            throw new SystemException("Error al agregar planificación de curso", "PLCU-ADD-ERR", e);
        }
    }

    /**
     * Actualiza una planificación de curso existente.
     *
     * @param planificacion entidad a actualizar
     */
    public void actualizarPlanificacion(PlanificacionCurso planificacion) {
        try {
            getEntityManager().merge(planificacion);
            getEntityManager().flush();
        } catch (Exception e) {
            throw new SystemException("Error al actualizar planificación de curso", "PLCU-UPD-ERR", e);
        }
    }

    /**
     * Elimina una planificación de curso por su ID.
     *
     * @param plcuId identificador de la planificación
     */
    public void eliminarPlanificacion(Integer plcuId) {
        try {
            PlanificacionCurso pc = getEntityManager().find(PlanificacionCurso.class, plcuId);
            if (pc != null) {
                getEntityManager().remove(pc);
                getEntityManager().flush();
            }
        } catch (Exception e) {
            throw new SystemException("Error al eliminar planificación de curso", "PLCU-DEL-ERR", e);
        }
    }
}
