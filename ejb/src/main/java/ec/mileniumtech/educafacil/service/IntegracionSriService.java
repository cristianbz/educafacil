package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.service.strategy.FacturaSriStrategy;
import ec.mileniumtech.educafacil.service.strategy.ProcesadorDocumentosElectronicos;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
@LocalBean
public class IntegracionSriService {

    private static final Logger log = LogManager.getLogger(IntegracionSriService.class);


    @EJB
    private ProcesadorDocumentosElectronicos procesador;

    @EJB
    private FacturaSriStrategy facturaStrategy;

    public void procesarFacturaElectronica(Factura facturaEntity) throws Exception {
        log.info("Iniciando procesamiento de factura electrónica: {}", facturaEntity.getId());
        procesador.procesar(facturaEntity, facturaStrategy);
        log.info("Factura electrónica procesada exitosamente: {}", facturaEntity.getId());
    }
}
