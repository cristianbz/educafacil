package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Estudiante.
 * Define operaciones de acceso a datos especÃ­ficas para Estudiante.
 */
@Local
public interface EstudianteDao extends GenericoDao<Estudiante, Long> {

    List<Estudiante> estudiantesPorApellido(String apellidos);
    Estudiante estudiantesPorCedula(String cedula);
    void actualizaEstudiante(Estudiante estudiante);
}
