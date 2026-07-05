/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.EmpresaMatrizDao;
import ec.mileniumtech.educafacil.dao.EstablecimientoDao;
import ec.mileniumtech.educafacil.dao.PagosDao;
import ec.mileniumtech.educafacil.dao.PuntoEmisionDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Establecimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para manejar la lógica de empresas, establecimientos, puntos de emisión y pagos.
 *
 * @author christian
 */
@Stateless
@LocalBean
public class EmpresaService {

    private static final Logger log = LogManager.getLogger(EmpresaService.class);

    @EJB
    private EmpresaMatrizDao empresaDao;

    @EJB
    private EstablecimientoDao establecimientoDao;

    @EJB
    private PuntoEmisionDao puntoEmisionDao;

    @EJB
    private PagosDao pagosDao;

    // =========================================================
    // Métodos de Empresa Matriz
    // =========================================================

    /**
     * Lista todas las empresas activas.
     */
    public List<EmpresaMatriz> listarEmpresas() {
        return empresaDao.listaEmpresas();
    }

    /**
     * Guarda o actualiza una empresa.
     */
    public void guardarEmpresa(EmpresaMatriz empresa) {
        empresaDao.agregarEmpresa(empresa);
    }

    // =========================================================
    // Métodos de Pagos
    // =========================================================

    /**
     * Lista todos los pagos.
     */
    public List<Pagos> listarPagos() {
        return pagosDao.listarTodosLosPagos();
    }

    /**
     * Actualiza un pago.
     */
    public void actualizarPago(Pagos pago) {
        pagosDao.actualizarPago(pago);
    }

    // =========================================================
    // Métodos de Establecimientos
    // =========================================================

    /**
     * Lista establecimientos por empresa.
     */
    public List<Establecimiento> listarEstablecimientosPorEmpresa(int empmId) {
        return establecimientoDao.listarEstablecimientosPorEmpresa(empmId);
    }

    /**
     * Guarda o actualiza un establecimiento.
     */
    public void guardarEstablecimiento(Establecimiento establecimiento) {
        if (establecimiento.getEstaId() == null) {
            establecimientoDao.guardar(establecimiento);
        } else {
            establecimientoDao.actualizar(establecimiento);
        }
    }

    // =========================================================
    // Métodos de Puntos de Emisión
    // =========================================================

    /**
     * Lista puntos de emisión por establecimiento.
     */
    public List<PuntoEmision> listarPuntosEmisionPorEstablecimiento(int estaId) {
        return puntoEmisionDao.listarPuntosEmisionPorEstablecimiento(estaId);
    }

    /**
     * Guarda o actualiza un punto de emisión.
     */
    public void guardarPuntoEmision(PuntoEmision puntoEmision) {
        if (puntoEmision.getId() == null) {
            puntoEmisionDao.guardar(puntoEmision);
        } else {
            puntoEmisionDao.actualizar(puntoEmision);
        }
    }
}
