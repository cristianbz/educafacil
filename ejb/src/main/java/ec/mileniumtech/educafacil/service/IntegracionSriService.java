package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
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

    @EJB
    private ProcesamientoSriAsincronoService procesamientoSriAsincrono;

    /**
     * Persiste la factura con estado {@code EN_PROCESO} y dispara el
     * procesamiento electrónico del SRI de forma asíncrona (Paso 3 del plan).
     *
     * <p>El método retorna de inmediato; el envío/autorización ocurre en un
     * hilo {@code @Asynchronous}. El resultado final (AUTORIZADO/RECHAZADO) se
     * reflejará en la entidad y será visible via el mecanismo de refresco de la
     * UI.</p>
     *
     * @param facturaEntity factura ya persistida (debe tener ID).
     * @throws IllegalStateException si la factura no tiene ID (no persistida).
     */
    public void procesarFacturaElectronica(Factura facturaEntity) throws Exception {
        if (facturaEntity.getId() == null) {
            throw new IllegalStateException("La factura debe estar persistida antes de iniciar el procesamiento electrónico.");
        }
        facturaStrategy.marcarEnProceso(facturaEntity);
        log.info("Disparando procesamiento asíncrono de factura electrónica: {}", facturaEntity.getId());
        List<InfoAdicionalDto> infoAdicional = facturaEntity.getListaInfoAdicional();
        procesamientoSriAsincrono.procesarFactura(facturaEntity.getId(), infoAdicional);
        log.info("Procesamiento asíncrono de factura {} lanzado.", facturaEntity.getId());
    }
}
