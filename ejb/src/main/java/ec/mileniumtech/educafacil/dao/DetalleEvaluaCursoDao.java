package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleEvaluaCurso;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad DetalleEvaluaCurso.
 * Define operaciones de acceso a datos especÃ­ficas para DetalleEvaluaCurso.
 */
@Local
public interface DetalleEvaluaCursoDao extends GenericoDao<DetalleEvaluaCurso, Long> {

    List<DetalleEvaluaCurso> listaDeDetallesDeEvaluacionDeCursos();
    void guardarEncuesta(DetalleEvaluaCurso detalle);
}
