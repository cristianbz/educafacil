package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.dao.impl.NotaCreditoDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.service.strategy.NotaCreditoSriStrategy;
import ec.mileniumtech.educafacil.service.strategy.ProcesadorDocumentosElectronicos;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class NotaCreditoService {

    @EJB
    private NotaCreditoDaoImpl notaCreditoDao;

    @EJB
    private ProcesadorDocumentosElectronicos procesador;

    @EJB
    private NotaCreditoSriStrategy notaCreditoStrategy;

    public void procesarNotaCreditoElectronica(NotaCredito notaCreditoEntity) throws Exception {
        notaCreditoDao.guardar(notaCreditoEntity);
        procesador.procesar(notaCreditoEntity, notaCreditoStrategy);
    }
}

