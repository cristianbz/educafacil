package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.EmpresaMatrizDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PagosDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Servicio para manejar la lógica de pagos y su vinculación con la facturación electrónica.
 */
@Stateless
@LocalBean
public class PagosService {

    @EJB
    private PagosDaoImpl pagosDao;

    /**
     * Registra un pago en el sistema.
     * 
     * @param pago Entidad Pagos a registrar.
     * @throws Exception Si ocurre un error en el registro.
     */
    public void registrarPagoYFacturar(Pagos pago) throws Exception {
        // 1. Registrar el pago en la base de datos
        pagosDao.agregarPago(pago);
    }
}
