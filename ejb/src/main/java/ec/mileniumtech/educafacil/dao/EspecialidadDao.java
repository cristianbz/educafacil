package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Especialidad.
 * Define operaciones de acceso a datos especÃ­ficas para Especialidad.
 */
@Local
public interface EspecialidadDao extends GenericoDao<Especialidad, Long> {

    List<Especialidad> listaDeEspecialidades();
}
