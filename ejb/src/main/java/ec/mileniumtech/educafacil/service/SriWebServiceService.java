package ec.mileniumtech.educafacil.service;

import java.net.URL;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.service.sri.autorizacion.AutorizacionComprobantesOffline;
import ec.mileniumtech.educafacil.service.sri.autorizacion.AutorizacionComprobantesOfflineService;
import ec.mileniumtech.educafacil.service.sri.autorizacion.RespuestaComprobante;
import ec.mileniumtech.educafacil.service.sri.recepcion.RecepcionComprobantesOffline;
import ec.mileniumtech.educafacil.service.sri.recepcion.RecepcionComprobantesOfflineService;
import ec.mileniumtech.educafacil.service.sri.recepcion.RespuestaSolicitud;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Servicio para el consumo de los Web Services del SRI en sus ambientes de pruebas y producción.
 */
@Stateless
@LocalBean
public class SriWebServiceService {

//    private static final String WS_RECEPCION_PRUEBAS = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
//    private static final String WS_AUTORIZACION_PRUEBAS = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
//    
//    private static final String WS_RECEPCION_PRODUCCION = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
//    private static final String WS_AUTORIZACION_PRODUCCION = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

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
        return port.autorizacionComprobante(claveAcceso);
    }
}
