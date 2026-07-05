package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Retencion.
 * Define operaciones de acceso a datos especÃ­ficas para Retencion.
 */
@Local
public interface RetencionDao extends GenericoDao<Retencion, Integer> {

    Retencion buscarRetencionPorId(Integer id);
    List<Retencion> listarTodas();
    void actualizarRetencion(Retencion retencion);
    List<Retencion> buscarRetencionesPorFiltros(LocalDate fechaInicio, LocalDate fechaFin, String identificacion, String numeroAutorizacion, String estadoAutorizacion);
}
