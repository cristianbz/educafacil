package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Seguimiento;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Seguimiento.
 * Define operaciones de acceso a datos especÃ­ficas para Seguimiento.
 */
@Local
public interface SeguimientoDao extends GenericoDao<Seguimiento, Long> {

    void agregarActualizarSeguimiento(Seguimiento seguimiento);
    List<Seguimiento> listaSeguimientoMatricula(int matricula);
}
