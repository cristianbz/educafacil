package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad PuntoEmision.
 * Define operaciones de acceso a datos especÃ­ficas para PuntoEmision.
 */
@Local
public interface PuntoEmisionDao extends GenericoDao<PuntoEmision, Integer> {

    List<PuntoEmision> listarPuntosEmisionActivos();
    List<PuntoEmision> listarPuntosEmisionPorEstablecimiento(int estaId);
}
