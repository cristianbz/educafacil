package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad EvaluacionCurso.
 * Define operaciones de acceso a datos especÃ­ficas para EvaluacionCurso.
 */
@Local
public interface EvaluacionCursoDao extends GenericoDao<EvaluacionCurso, Long> {

    List<EvaluacionCurso> listaDeEvaluacionesDeCurso();
    List<EvaluacionCurso> listaDeEvaluacionesPorCurso(int codigo, int codigoobj);
    List<EvaluacionCurso> listaDeEvaluacionesDeCursoActivas(int codigoC);
    EvaluacionCurso agregarEvaluacionCurso(EvaluacionCurso evaluacionCurso);
}
