package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cuota;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Cuota.
 * Define operaciones de acceso a datos especÃ­ficas para Cuota.
 */
@Local
public interface CuotaDao extends GenericoDao<Cuota, Long> {

    List<Cuota> listarCuotasPorMatricula(int codigoMatricula);
    List<Cuota> listarCuotasPendientesPorMatricula(int codigoMatricula);
    void guardarCuota(Cuota cuota);
    void actualizarCuota(Cuota cuota);
    void eliminarCuota(Cuota cuota);
}
