package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Establecimiento;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Establecimiento.
 * Define operaciones de acceso a datos especÃ­ficas para Establecimiento.
 */
@Local
public interface EstablecimientoDao extends GenericoDao<Establecimiento, Integer> {

    List<Establecimiento> listarEstablecimientosPorEmpresa(int empmId);
}
