package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.FacturaDao;
import ec.mileniumtech.educafacil.dao.NotaCreditoDao;
import ec.mileniumtech.educafacil.dao.RetencionDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadoDocumentoElectronico;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Proceso programado de <b>reconciliación SRI</b> (Paso 4 del plan de
 * tolerancia a la latencia del SRI).
 *
 * <p>Cuando el envío/recepción del comprobante quedó en estado
 * {@code ENVIADO}, {@code EN_PROCESO} o {@code PENDIENTE} (el SRI recibió el
 * comprobante pero la autorización no se resolvió de forma síncrona), este
 * scheduler consulta periódicamente la autorización por la clave de acceso ya
 * persistida y actualiza el estado a {@code AUTORIZADO} o {@code RECHAZADO}.</p>
 *
 * <p>Cada comprobante se procesa de forma <b>asíncrona</b> para no bloquear el
 * hilo del timer; toda la lógica de consulta/reintentos vive en
 * {@link ProcesadorDocumentosElectronicos#reconciliar}.</p>
 */
@Singleton
@Startup
public class ReconciliacionSriScheduler {

    private static final Logger log = LogManager.getLogger(ReconciliacionSriScheduler.class);

    @EJB
    private FacturaDao facturaDao;

    @EJB
    private NotaCreditoDao notaCreditoDao;

    @EJB
    private RetencionDao retencionDao;

    @EJB
    private ProcesamientoSriAsincronoService procesamientoSriAsincrono;

    private List<String> estadosReconciliacion;

    @PostConstruct
    public void init() {
        estadosReconciliacion = List.of(
                EnumEstadoDocumentoElectronico.ENVIADO.getLabel(),
                EnumEstadoDocumentoElectronico.EN_PROCESO.getLabel(),
                EnumEstadoDocumentoElectronico.PENDIENTE.getLabel());
        log.info("ReconciliacionSriScheduler inicializado. Frecuencia: cada 2 minutos.");
    }

    /**
     * Ejecuta la reconciliación de comprobantes pendientes cada 2 minutos.
     */
    @Schedule(hour = "*", minute = "*/2", second = "0", persistent = false)
    public void reconciliarPendientes() {
        try {
            reconciliarFacturas();
            reconciliarNotasCredito();
            reconciliarRetenciones();
        } catch (Exception e) {
            log.error("Error general en la reconciliación SRI.", e);
        }
    }

    private void reconciliarFacturas() {
        try {
            List<Factura> facturas = facturaDao.listarFacturasPorEstadosReconciliacion(estadosReconciliacion);
            if (facturas.isEmpty()) {
                return;
            }
            log.info("Reconciliación SRI: {} facturas pendientes por consultar.", facturas.size());
            for (Factura f : facturas) {
                procesamientoSriAsincrono.reconciliarFactura(f.getId());
            }
        } catch (Exception e) {
            log.error("Error al reconciliar facturas.", e);
        }
    }

    private void reconciliarNotasCredito() {
        try {
            List<NotaCredito> notas = notaCreditoDao.listarNotasPorEstadosReconciliacion(estadosReconciliacion);
            if (notas.isEmpty()) {
                return;
            }
            log.info("Reconciliación SRI: {} notas de crédito pendientes por consultar.", notas.size());
            for (NotaCredito nc : notas) {
                procesamientoSriAsincrono.reconciliarNotaCredito(nc.getId());
            }
        } catch (Exception e) {
            log.error("Error al reconciliar notas de crédito.", e);
        }
    }

    private void reconciliarRetenciones() {
        try {
            List<Retencion> retenciones = retencionDao.listarRetencionesPorEstadosReconciliacion(estadosReconciliacion);
            if (retenciones.isEmpty()) {
                return;
            }
            log.info("Reconciliación SRI: {} retenciones pendientes por consultar.", retenciones.size());
            for (Retencion r : retenciones) {
                procesamientoSriAsincrono.reconciliarRetencion(r.getId());
            }
        } catch (Exception e) {
            log.error("Error al reconciliar retenciones.", e);
        }
    }
}