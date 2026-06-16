package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.service.strategy.FacturaSriStrategy;
import ec.mileniumtech.educafacil.service.strategy.ProcesadorDocumentosElectronicos;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class IntegracionSriService {

    @EJB
    private ProcesadorDocumentosElectronicos procesador;

    @EJB
    private FacturaSriStrategy facturaStrategy;

    public void procesarFacturaElectronica(Factura facturaEntity) throws Exception {
        procesador.procesar(facturaEntity, facturaStrategy);
    }
}
