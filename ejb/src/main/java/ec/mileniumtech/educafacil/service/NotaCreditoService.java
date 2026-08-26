package ec.mileniumtech.educafacil.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.dao.NotaCreditoDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.service.strategy.NotaCreditoSriStrategy;
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
    private NotaCreditoSriStrategy notaCreditoStrategy;

    @EJB
    private ProcesamientoSriAsincronoService procesamientoSriAsincrono;

    /**
     * Persiste la nota de crédito con estado {@code EN_PROCESO} y dispara el
     * procesamiento electrónico del SRI de forma asíncrona (Paso 3 del plan).
     *
     * @param notaCreditoEntity nota de crédito a procesar.
     * @throws IllegalStateException si la nota de crédito no quedó persistida.
     */
    public void procesarNotaCreditoElectronica(NotaCredito notaCreditoEntity) throws Exception {
        notaCreditoDao.guardar(notaCreditoEntity);
        notaCreditoStrategy.marcarEnProceso(notaCreditoEntity);
        if (notaCreditoEntity.getId() == null) {
            throw new IllegalStateException("La nota de crédito debe tener ID antes de iniciar el procesamiento electrónico.");
        }
        log.info("Disparando procesamiento asíncrono de nota de crédito: {}", notaCreditoEntity.getId());
        procesamientoSriAsincrono.procesarNotaCredito(notaCreditoEntity.getId());
    }
}


