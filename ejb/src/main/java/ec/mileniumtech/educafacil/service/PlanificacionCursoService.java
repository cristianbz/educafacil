/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.time.LocalDate;
import java.util.List;

import ec.mileniumtech.educafacil.dao.PlanificacionCursoDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PlanificacionCurso;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para manejar la lógica de planificación de cursos (calendario).
 *
 * @author christian
 */
@Stateless
@LocalBean
public class PlanificacionCursoService {

    private static final Logger log = LogManager.getLogger(PlanificacionCursoService.class);

    @EJB
    private PlanificacionCursoDao planificacionCursoDao;

    /**
     * Lista todas las planificaciones de cursos ordenadas por fecha e hora de inicio.
     */
    public List<PlanificacionCurso> listarPlanificacionesCursos() {
        return planificacionCursoDao.listarTodas();
    }

    /**
     * Lista las planificaciones de cursos en un rango de fechas dado (vista semanal).
     *
     * @param fechaInicio inicio del rango
     * @param fechaFin    fin del rango
     */
    public List<PlanificacionCurso> listarPlanificacionesPorSemana(LocalDate fechaInicio, LocalDate fechaFin) {
        return planificacionCursoDao.listarPorRangoFechas(fechaInicio, fechaFin);
    }

    /**
     * Guarda o actualiza una planificación de curso.
     *
     * @param planificacion entidad a persistir o actualizar
     */
    public void guardarPlanificacionCurso(PlanificacionCurso planificacion) {
        if (planificacion.getPlcuId() == null || planificacion.getPlcuId() == 0) {
            planificacionCursoDao.agregarPlanificacion(planificacion);
        } else {
            planificacionCursoDao.actualizarPlanificacion(planificacion);
        }
    }

    /**
     * Elimina una planificación de curso por su ID.
     *
     * @param plcuId identificador de la planificación a eliminar
     */
    public void eliminarPlanificacionCurso(Integer plcuId) {
        planificacionCursoDao.eliminarPlanificacion(plcuId);
    }
}
