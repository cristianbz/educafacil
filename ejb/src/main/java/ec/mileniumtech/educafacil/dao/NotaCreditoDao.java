package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad NotaCredito.
 * Define operaciones de acceso a datos especÃ­ficas para NotaCredito.
 */
@Local
public interface NotaCreditoDao extends GenericoDao<NotaCredito, Integer> {

    NotaCredito buscarNotaCreditoPorId(Integer id);
    List<NotaCredito> listarTodas();
    List<NotaCredito> buscarNotasCreditoPorFiltros(LocalDate fechaInicio, LocalDate fechaFin, String identificacion, String numeroAutorizacion, String estadoAutorizacion);
}
