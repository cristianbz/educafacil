package ec.mileniumtech.educafacil.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.dao.NotaCreditoDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.service.strategy.NotaCreditoSriStrategy;
import ec.mileniumtech.educafacil.service.strategy.ProcesadorDocumentosElectronicos;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class NotaCreditoService {

    private static final Logger log = LogManager.getLogger(NotaCreditoService.class);

    @EJB
    private NotaCreditoDao notaCreditoDao;

    @EJB
    private ProcesadorDocumentosElectronicos procesador;

    @EJB
    private NotaCreditoSriStrategy notaCreditoStrategy;

    public void procesarNotaCreditoElectronica(NotaCredito notaCreditoEntity) throws Exception {
        notaCreditoDao.guardar(notaCreditoEntity);
        procesador.procesar(notaCreditoEntity, notaCreditoStrategy);
    }
}


