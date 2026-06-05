package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.service.strategy.ProcesadorDocumentosElectronicos;
import ec.mileniumtech.educafacil.service.strategy.RetencionSriStrategy;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class RetencionIntegracionService {

    @EJB
    private ProcesadorDocumentosElectronicos procesador;

    @EJB
    private RetencionSriStrategy retencionStrategy;

    public void procesarRetencionElectronica(Retencion retencionEntity) throws Exception {
        procesador.procesar(retencionEntity, retencionStrategy);
    }
}
