package ec.mileniumtech.educafacil.push;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteElectronicoEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Servicio de Faces Push (WebSockets) para facturación electrónica y comprobantes.
 * Observa los eventos CDI disparados por el backend asíncrono y los retransmite en tiempo real
 * a los navegadores suscritos al canal WebSocket "facturacionChannel".
 */
@Named("facturacionPushService")
@ApplicationScoped
public class FacturacionPushService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(FacturacionPushService.class);

    public static final String CANAL_FACTURACION = "facturacionChannel";

    @Inject
    @Push(channel = CANAL_FACTURACION)
    private PushContext pushContext;

    /**
     * Observa eventos CDI de comprobantes electrónicos procesados y los envía al WebSocket.
     *
     * @param evento Información del comprobante y su estado emitido por el SRI.
     */
    public void onComprobanteProcesado(@Observes ComprobanteElectronicoEvent evento) {
        try {
            if (evento == null) {
                return;
            }
            log.info("Faces Push: enviando evento en canal '{}' -> tipo={}, id={}, num={}, estado={}",
                    CANAL_FACTURACION, evento.getTipoDocumento(), evento.getId(), evento.getNumero(), evento.getEstado());

            if (pushContext != null) {
                // Serializamos a JSON String para garantizar compatibilidad con el WebSocket encoder de Mojarra
                String jsonPayload = String.format(
                    "{\"tipo\":\"%s\",\"id\":%d,\"numero\":\"%s\",\"estado\":\"%s\",\"mensaje\":\"%s\",\"exitoso\":%b,\"fechaHora\":\"%s\"}",
                    evento.getTipoDocumento() != null ? escapeJson(evento.getTipoDocumento()) : "",
                    evento.getId() != null ? evento.getId() : 0,
                    evento.getNumero() != null ? escapeJson(evento.getNumero()) : "",
                    evento.getEstado() != null ? escapeJson(evento.getEstado()) : "",
                    evento.getMensaje() != null ? escapeJson(evento.getMensaje()) : "",
                    evento.isExitoso(),
                    evento.getFechaHora() != null ? escapeJson(evento.getFechaHora()) : ""
                );
                pushContext.send(jsonPayload);
                log.info("Faces Push: mensaje WebSocket enviado exitosamente a los clientes.");
            } else {
                log.warn("PushContext es nulo para el canal '{}'. Verifique que jakarta.faces.ENABLE_WEBSOCKET_ENDPOINT esté activo.", CANAL_FACTURACION);
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación WebSocket en canal '{}'", CANAL_FACTURACION, e);
        }
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Envía manualmente una notificación de texto o payload al canal.
     *
     * @param mensaje Contenido del mensaje a emitir.
     */
    public void emitirNotificacionManual(String mensaje) {
        try {
            if (pushContext != null && mensaje != null) {
                pushContext.send(mensaje);
            }
        } catch (Exception e) {
            log.error("Error al emitir notificación manual en canal '{}'", CANAL_FACTURACION, e);
        }
    }
}
