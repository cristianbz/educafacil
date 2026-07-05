package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad DetalleSeguimiento.
 * Define operaciones de acceso a datos especÃ­ficas para DetalleSeguimiento.
 */
@Local
public interface DetalleSeguimientoDao extends GenericoDao<DetalleSeguimiento, Long> {

    void agregarDetalle(DetalleSeguimiento detalle);
    List<DetalleSeguimiento> listaDetalle(Integer seguimiento);
}
