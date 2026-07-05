package ec.mileniumtech.educafacil.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.dao.PagosDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Servicio para manejar la lÃ³gica de pagos y su vinculaciÃ³n con la facturaciÃ³n electrÃ³nica.
 */
@Stateless
@LocalBean
public class PagosService {

    private static final Logger log = LogManager.getLogger(PagosService.class);

    @EJB
    private PagosDao pagosDao;

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

