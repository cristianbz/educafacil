package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.PlanificacionCurso;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad PlanificacionCurso.
 * Define operaciones de acceso a datos espec\u00edficas para PlanificacionCurso.
 */
@Local
public interface PlanificacionCursoDao extends GenericoDao<PlanificacionCurso, Integer> {

    List<PlanificacionCurso> listarTodas();
    List<PlanificacionCurso> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    void agregarPlanificacion(PlanificacionCurso planificacion);
    void actualizarPlanificacion(PlanificacionCurso planificacion);
    void eliminarPlanificacion(Integer plcuId);
}
