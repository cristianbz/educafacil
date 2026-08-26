package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
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
    private RetencionSriStrategy retencionStrategy;

    @EJB
    private ProcesamientoSriAsincronoService procesamientoSriAsincrono;

    /**
     * Marca la retención en estado {@code EN_PROCESO} y dispara el procesamiento
     * electrónico del SRI de forma asíncrona (Paso 3 del plan).
     *
     * @param retencionEntity retención ya persistida (debe tener ID).
     * @throws IllegalStateException si la retención no tiene ID.
     */
    public void procesarRetencionElectronica(Retencion retencionEntity) throws Exception {
        if (retencionEntity.getId() == null) {
            throw new IllegalStateException("La retención debe estar persistida antes de iniciar el procesamiento electrónico.");
        }
        retencionStrategy.marcarEnProceso(retencionEntity);
        log.info("Disparando procesamiento asíncrono de retención: {}", retencionEntity.getId());
        procesamientoSriAsincrono.procesarRetencion(retencionEntity.getId());
    }
}
