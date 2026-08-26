package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Factura.
 * Define operaciones de acceso a datos especÃ­ficas para Factura.
 */
@Local
public interface FacturaDao extends GenericoDao<Factura, Integer> {

    Factura buscarFacturaPorId(Integer id);
    void actualizarFactura(Factura factura);
    List<Factura> listarTodasLasFacturas();
    List<Factura> listarTodasLasFacturasDelDia();
    List<Factura> buscarFacturasPorFiltros(LocalDate fechaInicio, LocalDate fechaFin, String identificacion, String numeroAutorizacion,String estadoAutorizacion);
    List<Factura> listarFacturasPorEstadosReconciliacion(List<String> estados);
}
