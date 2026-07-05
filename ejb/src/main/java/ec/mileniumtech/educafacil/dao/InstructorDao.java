package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Instructor.
 * Define operaciones de acceso a datos especÃ­ficas para Instructor.
 */
@Local
public interface InstructorDao extends GenericoDao<Instructor, Long> {

    List<Instructor> listaInstructores();
    void agregarActualizarInstructor(Instructor instructor);
}
