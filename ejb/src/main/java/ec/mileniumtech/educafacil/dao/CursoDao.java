package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Curso.
 * Define operaciones de acceso a datos especÃ­ficas para Curso.
 */
@Local
public interface CursoDao extends GenericoDao<Curso, Long> {

    List<Curso> listaCursos();
    List<Curso> listaOfertaCursosActivos();
}
