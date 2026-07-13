package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Pagos.
 * Define operaciones de acceso a datos espec\u00edficas para Pagos.
 */
@Local
public interface PagosDao extends GenericoDao<Pagos, Long> {

    void agregarPago(Pagos pago);
    void actualizarPago(Pagos pago);
    List<Pagos> listarTodosLosPagos();
    List<DetallePagos> buscaPagosPorMatricula(int codigoMatricula);
    List<DtoFlujoDinero> buscaIngresosReporteria(Date fechaInicial, Date fechaFinal);
    List<Pagos> listarTodosLosPagosPorRangoFecha(Date fechaInicial, Date fechaFinal);
}
