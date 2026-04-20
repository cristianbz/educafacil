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

    @EJB
    private EmpresaMatrizDaoImpl empresaDao;

    @EJB
    private IntegracionSriService integracionSriService;

    /**
     * Registra un pago en el sistema y dispara automáticamente el proceso de facturación electrónica.
     * 
     * @param pago Entidad Pagos a registrar.
     * @throws Exception Si ocurre un error en el registro o en la facturación.
     */
    public void registrarPagoYFacturar(Pagos pago) throws Exception {
        // 1. Registrar el pago en la base de datos
        pagosDao.agregarPago(pago);

        // 2. Obtener la información del emisor (Empresa)
        List<EmpresaMatriz> empresas = empresaDao.listaEmpresas();
        if (empresas == null || empresas.isEmpty()) {
            throw new Exception("No se encontró una empresa activa configurada para facturar.");
        }

        // Tomamos la primera empresa activa por defecto
        EmpresaMatriz empresa = empresas.get(0);

        // 3. Procesar la facturación electrónica
        try {
            integracionSriService.procesarFacturaElectronica(pago, empresa);
        } catch (Exception e) {
            // Se puede decidir si revertir la transacción o simplemente registrar el error
            // En este caso, lanzamos la excepción para informar al usuario
            throw new Exception("Error al procesar la factura electrónica: " + e.getMessage(), e);
        }
    }
}
