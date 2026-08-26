package ec.mileniumtech.educafacil.service;

import java.net.URL;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.service.sri.autorizacion.Autorizacion;
import ec.mileniumtech.educafacil.service.sri.autorizacion.AutorizacionComprobantesOffline;
import ec.mileniumtech.educafacil.service.sri.autorizacion.AutorizacionComprobantesOfflineService;
import ec.mileniumtech.educafacil.service.sri.autorizacion.RespuestaComprobante;
import ec.mileniumtech.educafacil.service.sri.recepcion.RecepcionComprobantesOffline;
import ec.mileniumtech.educafacil.service.sri.recepcion.RecepcionComprobantesOfflineService;
import ec.mileniumtech.educafacil.service.sri.recepcion.RespuestaSolicitud;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.xml.ws.BindingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para el consumo de los Web Services del SRI en sus ambientes de pruebas y producción.
 */
@Stateless
@LocalBean
public class SriWebServiceService {

    private static final Logger log = LogManager.getLogger(SriWebServiceService.class);

    /**
     * Tiempo máximo (ms) para establecer la conexión TCP con los servidores del SRI.
     * Configurable con la propiedad de sistema {@code educafacil.sri.connect.timeout}.
     * 15 segundos da margen cuando el SRI está bajo carga sin bloquear el hilo indefinidamente.
     */
    private static final int CONNECT_TIMEOUT_MS =
            Integer.getInteger("educafacil.sri.connect.timeout", 15_000);

    /**
     * Tiempo máximo (ms) de espera de respuesta del WS de RECEPCIÓN luego de enviar el comprobante.
     * Configurable con {@code educafacil.sri.envio.request.timeout}. El envío es una operación pesada:
     * firma XAdES + transmisión del XML; se deja un margen amplio para servidores saturados.
     */
    private static final int REQUEST_TIMEOUT_ENVIO_MS =
            Integer.getInteger("educafacil.sri.envio.request.timeout", 45_000);

    /**
     * Tiempo máximo (ms) de espera de respuesta del WS de AUTORIZACIÓN por clave de acceso.
     * Configurable con {@code educafacil.sri.autorizacion.request.timeout}.
     * La autorización suele ser la llamada más lenta cuando el SRI tiene problemas de carga,
     * por eso usa el timeout más alto. El reintento con backoff lo gestiona el procesador.
     */
    private static final int REQUEST_TIMEOUT_AUTORIZACION_MS =
            Integer.getInteger("educafacil.sri.autorizacion.request.timeout", 60_000);

    /**
     * Envía un comprobante firmado al SRI para su recepción.
     * 
     * @param xmlFirmado   XML firmado en bytes.
     * @param esProduccion true si se debe enviar al ambiente de producción, false para pruebas.
     * @return Respuesta de la solicitud de recepción.
     * @throws Exception Si ocurre un error en la comunicación o URL.
     */
    public RespuestaSolicitud enviarComprobante(byte[] xmlFirmado, boolean esProduccion, Configuraciones configuracion) throws Exception {
        String urlWsdl = esProduccion ? configuracion.getConfWsRecepcionProduccion() : configuracion.getConf_wsRecepcionPruebas();
        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(new URL(urlWsdl));
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();

        // Configurar timeouts (configurables, con valores por defecto tolerantes a carga)
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", CONNECT_TIMEOUT_MS);
        bp.getRequestContext().put("com.sun.xml.ws.request.timeout", REQUEST_TIMEOUT_ENVIO_MS);

        return port.validarComprobante(xmlFirmado);
    }

    /**
     * Consulta el estado de autorización de un comprobante mediante su clave de acceso.
     * 
     * @param claveAcceso  Clave de acceso de 49 dígitos.
     * @param esProduccion true si se debe consultar en el ambiente de producción, false para pruebas.
     * @return Respuesta con los detalles de la autorización.
     * @throws Exception Si ocurre un error en la comunicación o URL.
     */
    public RespuestaComprobante autorizarComprobante(String claveAcceso, boolean esProduccion, Configuraciones configuracion) throws Exception {
        String urlWsdl = esProduccion ? configuracion.getConfWsAutorizacionProduccion() : configuracion.getConf_wsAutorizacionPruebas();
        AutorizacionComprobantesOfflineService service = new AutorizacionComprobantesOfflineService(new URL(urlWsdl));
        AutorizacionComprobantesOffline port = service.getAutorizacionComprobantesOfflinePort();
        // Configurar timeouts (configurables; la autorización es la llamada más lenta del SRI)
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", CONNECT_TIMEOUT_MS);
        bp.getRequestContext().put("com.sun.xml.ws.request.timeout", REQUEST_TIMEOUT_AUTORIZACION_MS);
        RespuestaComprobante respuesta = port.autorizacionComprobante(claveAcceso);

        if (log.isDebugEnabled() && respuesta.getAutorizaciones() != null
                && !respuesta.getAutorizaciones().getAutorizacion().isEmpty()) {
            Autorizacion aut = respuesta.getAutorizaciones().getAutorizacion().get(0);
            log.debug("Respuesta SRI para claveAcceso={}: estado={}, numAutorizacion={}",
                    claveAcceso, aut.getEstado(), aut.getNumeroAutorizacion());
        }

        return respuesta;
    }
}
