package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.FacturaDao;
import ec.mileniumtech.educafacil.dao.NotaCreditoDao;
import ec.mileniumtech.educafacil.dao.RetencionDao;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.service.strategy.FacturaSriStrategy;
import ec.mileniumtech.educafacil.service.strategy.NotaCreditoSriStrategy;
import ec.mileniumtech.educafacil.service.strategy.ProcesadorDocumentosElectronicos;
import ec.mileniumtech.educafacil.service.strategy.RetencionSriStrategy;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadoDocumentoElectronico;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio que ejecuta el envío y autorización de comprobantes ante el SRI de
 * forma <b>asíncrona</b> (Paso 3 del plan de tolerancia a latencia del SRI).
 *
 * <p>El flujo web no debe bloquearse esperando una respuesta del SRI. Este
 * servicio recibe únicamente los <b>IDs</b> de las entidades (nunca la entidad
 * en memoria, pues sus relaciones LAZY dejarían de funcionar en otro hilo) y
 * recarga la entidad dentro de la transacción del hilo asíncrono.</p>
 *
 * <p>El llamador debe haber persistido previamente el comprobante con estado
 * {@link EnumEstadoDocumentoElectronico#EN_PROCESO} para que la UI pueda
 * mostrar "se está procesando" de inmediato.</p>
 */
@Stateless
@LocalBean
public class ProcesamientoSriAsincronoService {

    private static final Logger log = LogManager.getLogger(ProcesamientoSriAsincronoService.class);

    /** Número de reintentos al recargar la entidad (carrera con el commit del llamador). */
    private static final int MAX_REINTENTOS_CARGA = 5;
    /** Espera entre reintentos de carga (ms). */
    private static final long ESPERA_CARGA_MS = 1_000L;

    @EJB
    private FacturaDao facturaDao;

    @EJB
    private NotaCreditoDao notaCreditoDao;

    @EJB
    private RetencionDao retencionDao;

    @EJB
    private ProcesadorDocumentosElectronicos procesador;

    @EJB
    private FacturaSriStrategy facturaStrategy;

    @EJB
    private NotaCreditoSriStrategy notaCreditoStrategy;

    @EJB
    private RetencionSriStrategy retencionStrategy;

    @jakarta.inject.Inject
    private jakarta.enterprise.event.Event<ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent> comprobanteEvent;

    /**
     * Procesa la factura electrónica de forma asíncrona.
     *
     * @param facturaId      ID de la factura ya persistida.
     * @param infoAdicional  Información adicional transitoria a incluir en el XML.
     */
    @Asynchronous
    public void procesarFactura(Integer facturaId, List<InfoAdicionalDto> infoAdicional) {
        try {
            Factura factura = cargarFacturaConReintentos(facturaId);
            if (factura == null) {
                log.error("No se pudo cargar la factura {} para el procesamiento asíncrono.", facturaId);
                return;
            }
            factura.setListaInfoAdicional(infoAdicional != null ? infoAdicional : factura.getListaInfoAdicional());
            procesador.procesar(factura, facturaStrategy);
            log.info("Factura {} procesada asíncronamente.", facturaId);

            // Notificar a la UI mediante evento CDI para Faces Push / WebSockets
            Factura facturaFin = facturaDao.buscarFacturaPorId(facturaId);
            if (facturaFin != null) {
                String estado = facturaFin.getDocumentoElectronico() != null ? facturaFin.getDocumentoElectronico().getEstado() : "ENVIADO";
                String numero = facturaFin.getNumero();
                boolean exitoso = "AUTORIZADO".equals(estado);
                String mensaje = exitoso 
                    ? "Factura " + numero + " AUTORIZADA por el SRI."
                    : "Factura " + numero + " procesada con estado: " + estado;
                comprobanteEvent.fire(new ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent("FACTURA", facturaId, numero, estado, mensaje, exitoso));
            }
        } catch (Exception e) {
            log.error("Error al procesar asíncronamente la factura {}. Queda para reconciliación.", facturaId, e);
            comprobanteEvent.fire(new ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent("FACTURA", facturaId, null, "ERROR", "Error al procesar factura: " + e.getMessage(), false));
        }
    }

    /**
     * Procesa la nota de crédito electrónica de forma asíncrona.
     *
     * @param notaCreditoId ID de la nota de crédito ya persistida.
     */
    @Asynchronous
    public void procesarNotaCredito(Integer notaCreditoId) {
        try {
            NotaCredito nc = cargarNotaCreditoConReintentos(notaCreditoId);
            if (nc == null) {
                log.error("No se pudo cargar la nota de crédito {} para el procesamiento asíncrono.", notaCreditoId);
                return;
            }
            procesador.procesar(nc, notaCreditoStrategy);
            log.info("Nota de crédito {} procesada asíncronamente.", notaCreditoId);

            // Notificar a la UI mediante evento CDI para Faces Push / WebSockets
            NotaCredito ncFin = notaCreditoDao.buscarNotaCreditoPorId(notaCreditoId);
            if (ncFin != null) {
                String estado = ncFin.getEstado() != null ? ncFin.getEstado() : "ENVIADO";
                String numero = ncFin.getNumero();
                boolean exitoso = "AUTORIZADO".equals(estado);
                String mensaje = exitoso 
                    ? "Nota de Crédito " + numero + " AUTORIZADA por el SRI."
                    : "Nota de Crédito " + numero + " procesada con estado: " + estado;
                comprobanteEvent.fire(new ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent("NOTA_CREDITO", notaCreditoId, numero, estado, mensaje, exitoso));
            }
        } catch (Exception e) {
            log.error("Error al procesar asíncronamente la nota de crédito {}. Queda para reconciliación.", notaCreditoId, e);
            comprobanteEvent.fire(new ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent("NOTA_CREDITO", notaCreditoId, null, "ERROR", "Error al procesar nota de crédito: " + e.getMessage(), false));
        }
    }

    /**
     * Procesa el comprobante de retención electrónico de forma asíncrona.
     *
     * @param retencionId ID de la retención ya persistida.
     */
    @Asynchronous
    public void procesarRetencion(Integer retencionId) {
        try {
            Retencion ret = cargarRetencionConReintentos(retencionId);
            if (ret == null) {
                log.error("No se pudo cargar la retención {} para el procesamiento asíncrono.", retencionId);
                return;
            }
            procesador.procesar(ret, retencionStrategy);
            log.info("Retención {} procesada asíncronamente.", retencionId);
        } catch (Exception e) {
            log.error("Error al procesar asíncronamente la retención {}. Queda para reconciliación.", retencionId, e);
        }
    }

    private Factura cargarFacturaConReintentos(Integer facturaId) throws InterruptedException {
        for (int i = 1; i <= MAX_REINTENTOS_CARGA; i++) {
            Factura f = facturaDao.buscarFacturaPorId(facturaId);
            if (f != null) {
                return f;
            }
            if (i < MAX_REINTENTOS_CARGA) {
                Thread.sleep(ESPERA_CARGA_MS);
            }
        }
        return null;
    }

    private NotaCredito cargarNotaCreditoConReintentos(Integer notaCreditoId) throws InterruptedException {
        for (int i = 1; i <= MAX_REINTENTOS_CARGA; i++) {
            NotaCredito nc = notaCreditoDao.buscarNotaCreditoPorId(notaCreditoId);
            if (nc != null) {
                return nc;
            }
            if (i < MAX_REINTENTOS_CARGA) {
                Thread.sleep(ESPERA_CARGA_MS);
            }
        }
        return null;
    }

    private Retencion cargarRetencionConReintentos(Integer retencionId) throws InterruptedException {
        for (int i = 1; i <= MAX_REINTENTOS_CARGA; i++) {
            Retencion r = retencionDao.buscarRetencionPorId(retencionId);
            if (r != null) {
                return r;
            }
            if (i < MAX_REINTENTOS_CARGA) {
                Thread.sleep(ESPERA_CARGA_MS);
            }
        }
        return null;
    }

    /**
     * Reconciliación asíncrona de una factura: consulta la autorización del SRI
     * por la claveAcceso ya persistida y actualiza el estado en BD.
     *
     * @param facturaId ID de la factura.
     */
    @Asynchronous
    public void reconciliarFactura(Integer facturaId) {
        try {
            Factura factura = cargarFacturaConReintentos(facturaId);
            if (factura == null || factura.getDocumentoElectronico() == null
                    || factura.getDocumentoElectronico().getClaveAcceso() == null) {
                log.warn("Reconciliación: factura {} sin documento electrónico o clave de acceso.", facturaId);
                return;
            }
            procesador.reconciliar(factura, factura.getDocumentoElectronico().getClaveAcceso(), facturaStrategy);
            
            Factura facturaFin = facturaDao.buscarFacturaPorId(facturaId);
            if (facturaFin != null) {
                String estado = facturaFin.getDocumentoElectronico() != null ? facturaFin.getDocumentoElectronico().getEstado() : "ENVIADO";
                String numero = facturaFin.getNumero();
                boolean exitoso = "AUTORIZADO".equals(estado);
                String mensaje = exitoso 
                    ? "Factura " + numero + " AUTORIZADA por el SRI (Reconciliación)."
                    : "Factura " + numero + " en estado: " + estado;
                comprobanteEvent.fire(new ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent("FACTURA", facturaId, numero, estado, mensaje, exitoso));
            }
        } catch (Exception e) {
            log.error("Error al reconciliar factura {}.", facturaId, e);
        }
    }

    /**
     * Reconciliación asíncrona de una nota de crédito.
     *
     * @param notaCreditoId ID de la nota de crédito.
     */
    @Asynchronous
    public void reconciliarNotaCredito(Integer notaCreditoId) {
        try {
            NotaCredito nc = cargarNotaCreditoConReintentos(notaCreditoId);
            if (nc == null || nc.getClaveAcceso() == null) {
                log.warn("Reconciliación: nota de crédito {} sin clave de acceso.", notaCreditoId);
                return;
            }
            procesador.reconciliar(nc, nc.getClaveAcceso(), notaCreditoStrategy);
            
            NotaCredito ncFin = notaCreditoDao.buscarNotaCreditoPorId(notaCreditoId);
            if (ncFin != null) {
                String estado = ncFin.getEstado() != null ? ncFin.getEstado() : "ENVIADO";
                String numero = ncFin.getNumero();
                boolean exitoso = "AUTORIZADO".equals(estado);
                String mensaje = exitoso 
                    ? "Nota de Crédito " + numero + " AUTORIZADA por el SRI (Reconciliación)."
                    : "Nota de Crédito " + numero + " en estado: " + estado;
                comprobanteEvent.fire(new ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent("NOTA_CREDITO", notaCreditoId, numero, estado, mensaje, exitoso));
            }
        } catch (Exception e) {
            log.error("Error al reconciliar nota de crédito {}.", notaCreditoId, e);
        }
    }

    /**
     * Reconciliación asíncrona de una retención.
     *
     * @param retencionId ID de la retención.
     */
    @Asynchronous
    public void reconciliarRetencion(Integer retencionId) {
        try {
            Retencion ret = cargarRetencionConReintentos(retencionId);
            if (ret == null || ret.getClaveAcceso() == null) {
                log.warn("Reconciliación: retención {} sin clave de acceso.", retencionId);
                return;
            }
            procesador.reconciliar(ret, ret.getClaveAcceso(), retencionStrategy);
        } catch (Exception e) {
            log.error("Error al reconciliar retención {}.", retencionId, e);
        }
    }
}