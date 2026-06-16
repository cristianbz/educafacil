package ec.mileniumtech.educafacil.service.facade;

import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.EgresoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PagosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ProveedorDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Proveedor;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class ContabilidadFacade {

    @EJB
    private EgresoDaoImpl egresoDao;

    @EJB
    private ProveedorDaoImpl proveedorDao;

    @EJB
    private PagosDaoImpl pagosDao;

    // ========== Egresos ==========

    public void agregarActualizarEgreso(Egresos egreso) {
        egresoDao.agregarActualizarEgreso(egreso);
    }

    public List<Egresos> listaEgresos() {
        return egresoDao.listaEgresos();
    }

    public List<Egresos> listaEgresosFechas(Date fechaUno, Date fechaDos) {
        return egresoDao.listaEgresosFechas(fechaUno, fechaDos);
    }

    public List<DtoFlujoDinero> buscaEgresosReporteria(Date fechaInicial, Date fechaFinal) {
        return egresoDao.buscaEgresosReporteria(fechaInicial, fechaFinal);
    }

    // ========== Proveedores ==========

    public void agregarActualizarProveedor(Proveedor proveedor) {
        proveedorDao.agregarActualizarProveedor(proveedor);
    }

    public List<Proveedor> listaProveedores() {
        return proveedorDao.listaProveedores();
    }

    public Proveedor validaProveedor(String ruc) {
        return proveedorDao.validaProveedor(ruc);
    }

    // ========== Pagos ==========

    public List<DetallePagos> buscaPagosPorMatricula(int codigoMatricula) {
        return pagosDao.buscaPagosPorMatricula(codigoMatricula);
    }

    public List<DtoFlujoDinero> buscaIngresosReporteria(Date fechaInicial, Date fechaFinal) {
        return pagosDao.buscaIngresosReporteria(fechaInicial, fechaFinal);
    }

    public List<Pagos> listarTodosLosPagos() {
        return pagosDao.listarTodosLosPagos();
    }
}
