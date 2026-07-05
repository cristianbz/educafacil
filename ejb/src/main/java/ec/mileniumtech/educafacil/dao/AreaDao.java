package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Area.
 * Define operaciones de acceso a datos especÃ­ficas para Area.
 */
@Local
public interface AreaDao extends GenericoDao<Area, Long> {

    List<Area> listaDeAreas();
}
