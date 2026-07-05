package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.service.strategy.ProcesadorDocumentosElectronicos;
import ec.mileniumtech.educafacil.service.strategy.RetencionSriStrategy;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
@LocalBean
public class RetencionIntegracionService {

    private static final Logger log = LogManager.getLogger(RetencionIntegracionService.class);


    @EJB
    private ProcesadorDocumentosElectronicos procesador;

    @EJB
    private RetencionSriStrategy retencionStrategy;

    public void procesarRetencionElectronica(Retencion retencionEntity) throws Exception {
        log.info("Iniciando procesamiento de retención electrónica: {}", retencionEntity.getNumero());
        procesador.procesar(retencionEntity, retencionStrategy);
        log.info("Retención electrónica procesada exitosamente: {}", retencionEntity.getNumero());
    }
}
