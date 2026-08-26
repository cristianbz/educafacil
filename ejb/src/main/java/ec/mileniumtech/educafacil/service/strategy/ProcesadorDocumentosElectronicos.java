package ec.mileniumtech.educafacil.service.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import ec.mileniumtech.educafacil.dao.ConfiguracionesDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.service.AwsS3Service;
import ec.mileniumtech.educafacil.service.NotificacionService;
import ec.mileniumtech.educafacil.service.SriWebServiceService;
import ec.mileniumtech.educafacil.service.XadesSignatureService;
import ec.mileniumtech.educafacil.service.sri.autorizacion.Autorizacion;
import ec.mileniumtech.educafacil.service.sri.autorizacion.RespuestaComprobante;
import ec.mileniumtech.educafacil.service.sri.recepcion.RespuestaSolicitud;
import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.utilitarios.ValidacionUtil;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CriptografiaUtil;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadoDocumentoElectronico;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
@LocalBean
public class ProcesadorDocumentosElectronicos {

    private static final Logger log = LogManager.getLogger(ProcesadorDocumentosElectronicos.class);

    /**
     * Número máximo de consultas de autorización que se ejecutan luego de recibir "RECIBIDA".
     * Si el SRI está saturado, la autorización puede tardar varios segundos en resolverse.
     */
    private static final int MAX_INTENTOS_AUTORIZACION = 6;

    /**
     * Backoff progresivo (ms) entre consultas de autorización:
     * 3s, 5s, 10s, 20s, 30s.
     */
    private static final long[] BACKOFF_AUTORIZACION_MS = {3_000L, 5_000L, 10_000L, 20_000L, 30_000L};

    @EJB
    private XadesSignatureService xadesSignatureService;

    @EJB
    private SriWebServiceService sriWebServiceService;

    @EJB
    private NotificacionService notificacionService;

    @EJB
    private AwsS3Service awsS3Service;

    @EJB
    private ConfiguracionesDao configuracionesDao;

    public void procesar(Object entidad, DocumentoElectronicoStrategy strategy) throws Exception {
        SriProcessingContext context = new SriProcessingContext();

        EmpresaMatriz empresa = null;

        try {
            empresa = resolverEmpresa(entidad);
        } catch (Exception e) {
            throw new BusinessException("La entidad no tiene una empresa matriz asociada.", "BIZ-SRI-NO-EMPRESA", e);
        }

        Object jaxbObject = strategy.construirJaxb(entidad, empresa, context);
        Configuraciones configuraciones = context.getConfiguraciones();

        String xmlString = strategy.generarXml(jaxbObject);
        context.setXmlString(xmlString);
        byte[] pkcs12 = empresa.getEmpmCertificado();
        String password = CriptografiaUtil.desencriptar(empresa.getEmpmPasswordCertificado());

        if (pkcs12 == null || password == null) {
            throw new BusinessException("Certificado o contraseña no configurados en la empresa.", "BIZ-SRI-NO-CERT");
        }

        byte[] xmlFirmado = xadesSignatureService.firmarDocumento(
                xmlString.getBytes(StandardCharsets.UTF_8), pkcs12, password);
        context.setXmlFirmado(xmlFirmado);

        // Persistencia temprana (hotfix): guardar clave de acceso y XML firmado apenas están
        // disponibles, para que una caída o saturación del SRI no deje el documento sin
        // referencia y para evitar re-envíos duplicados.
        try {
            strategy.persistirProgreso(entidad, context);
        } catch (Exception e) {
            log.warn("No se pudo persistir el progreso temprano del documento (claveAcceso/xmlFirmado).", e);
        }

        boolean esProduccion = empresa.getEmpmAmbiente() == 2;
        String urlWsdl = esProduccion
                ? configuraciones.getConfWsRecepcionProduccion()
                : configuraciones.getConf_wsRecepcionPruebas();

        if (!ValidacionUtil.verificarConexion(urlWsdl, 5000)) {
            throw new SystemException("No se pudo establecer comunicación con los servidores del SRI. Verifique su conexión a internet.", "SYS-SRI-NO-CONN");
        }

        RespuestaSolicitud respuestaEnvio;
        try {
            respuestaEnvio = sriWebServiceService.enviarComprobante(xmlFirmado, esProduccion, configuraciones);
        } catch (Exception e) {
        	log.error("Error al comunicar con el SRI en la recepción del comprobante. URL wsdl: {}", urlWsdl, e);
            throw new SystemException("Error al comunicar con el SRI.", "SYS-SRI-COMM-ERR", e);
        }

        if ("RECIBIDA".equals(respuestaEnvio.getEstado())) {
            // Paso 1b: reintentos con backoff en lugar de una única consulta tras sleep fijo.
            RespuestaComprobante respuestaAut = consultarAutorizacionConReintentos(
                    context.getClaveAcceso(), esProduccion, configuraciones);

            if (respuestaAut != null && !respuestaAut.getAutorizaciones().getAutorizacion().isEmpty()) {
                Autorizacion aut = respuestaAut.getAutorizaciones().getAutorizacion().get(0);
                context.setEstadoAutorizacion(aut.getEstado());
                context.setNumeroAutorizacion(aut.getNumeroAutorizacion());

                if (EnumEstadoDocumentoElectronico.AUTORIZADO.getLabel().equals(aut.getEstado())) {
                    context.setAutorizado(true);
                    context.setFechaAutorizacion(convertir(aut.getFechaAutorizacion()));
                    byte[] pdfContent = strategy.generarRide(jaxbObject, empresa, context);
                    context.setPdfContent(pdfContent);

                    String identifier = null;
                    try {
                        identifier = strategy.getEntityIdentifier(entidad);

                        if (pdfContent != null) {
                        	 String documento = resolverDocumento(entidad);
                             String ambiente = empresa.getEmpmAmbiente() == 2 ? "produccion" : "pruebas";
                            String clavePdf = awsS3Service.construirClavePdf(identifier,documento,ambiente);
                            awsS3Service.subirArchivo(pdfContent, clavePdf, "application/pdf");
                            context.setUrlPdf(clavePdf);
                        }
                        String documento = resolverDocumento(entidad);
                        String ambiente = empresa.getEmpmAmbiente() == 2 ? "produccion" : "pruebas";
                        String claveXml = awsS3Service.construirClaveXml(identifier,documento,ambiente);
                        awsS3Service.subirArchivo(xmlFirmado, claveXml, "text/xml");
                        context.setUrlXml(claveXml);

                    } catch (Exception e3) {
                        log.error("Error al subir documentos a S3 para identifier: {}", identifier, e3);
                    }

                    if (pdfContent != null) {
                        String destinatario = obtenerDestinatario(entidad);
                        if (destinatario != null) {
                            notificacionService.enviarComprobante(
                                    destinatario, xmlFirmado, pdfContent,
                                    context.getClaveAcceso().substring(Math.max(0, context.getClaveAcceso().length() - 9)));
                        }
                    }
                } else {
                    if (!aut.getMensajes().getMensaje().isEmpty()) {
                        context.setMensajeSri(aut.getMensajes().getMensaje().get(0).getMensaje());
                    }
                }
            } else {
                // El SRI recibió el comprobante pero la autorización no se resolvió tras
                // los reintentos. Se deja en ENVIADO para reconciliación posterior.
                context.setEstadoAutorizacion(EnumEstadoDocumentoElectronico.ENVIADO.getLabel());
                context.setMensajeSri("El comprobante fue RECIBIDO por el SRI, pero la autorización aún no se ha resuelto. Se consultará nuevamente.");
                log.warn("Comprobante RECIBIDO por el SRI sin autorización resuelta tras {} reintentos. ClaveAcceso: {}",
                        MAX_INTENTOS_AUTORIZACION, context.getClaveAcceso());
            }
        } else {
            String mensajeErr = "";
            if (respuestaEnvio.getComprobantes() != null
                    && !respuestaEnvio.getComprobantes().getComprobante().isEmpty()
                    && respuestaEnvio.getComprobantes().getComprobante().get(0).getMensajes() != null
                    && !respuestaEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().isEmpty()) {
                mensajeErr = respuestaEnvio.getComprobantes().getComprobante().get(0)
                        .getMensajes().getMensaje().get(0).getMensaje();
            }
            context.setMensajeSri(mensajeErr);

            // Si el comprobante o secuencial ya fue registrado en el SRI previamente,
            // intentamos verificar su autorización en lugar de marcar como fallo inmediato.
            if (mensajeErr != null && (mensajeErr.toUpperCase().contains("SECUENCIAL REGISTRADO") 
                    || mensajeErr.toUpperCase().contains("CLAVE ACCESO REGISTRADA")
                    || mensajeErr.toUpperCase().contains("CLAVE DE ACCESO REGISTRADA"))) {
                
                log.info("Secuencial o clave ya registrada en el SRI para clave {}. Consultando autorización...", context.getClaveAcceso());
                RespuestaComprobante respuestaAut = consultarAutorizacionConReintentos(
                        context.getClaveAcceso(), esProduccion, configuraciones);

                if (respuestaAut != null && !respuestaAut.getAutorizaciones().getAutorizacion().isEmpty()) {
                    Autorizacion aut = respuestaAut.getAutorizaciones().getAutorizacion().get(0);
                    context.setEstadoAutorizacion(aut.getEstado());
                    context.setNumeroAutorizacion(aut.getNumeroAutorizacion());
                    if (EnumEstadoDocumentoElectronico.AUTORIZADO.getLabel().equals(aut.getEstado())) {
                        context.setAutorizado(true);
                        context.setFechaAutorizacion(convertir(aut.getFechaAutorizacion()));
                        byte[] pdfContent = strategy.generarRide(jaxbObject, empresa, context);
                        context.setPdfContent(pdfContent);

                        String identifier = null;
                        try {
                            identifier = strategy.getEntityIdentifier(entidad);
                            if (pdfContent != null) {
                                String documento = resolverDocumento(entidad);
                                String ambiente = empresa.getEmpmAmbiente() == 2 ? "produccion" : "pruebas";
                                String clavePdf = awsS3Service.construirClavePdf(identifier, documento, ambiente);
                                awsS3Service.subirArchivo(pdfContent, clavePdf, "application/pdf");
                                context.setUrlPdf(clavePdf);
                            }
                            String documento = resolverDocumento(entidad);
                            String ambiente = empresa.getEmpmAmbiente() == 2 ? "produccion" : "pruebas";
                            String claveXml = awsS3Service.construirClaveXml(identifier, documento, ambiente);
                            awsS3Service.subirArchivo(xmlFirmado, claveXml, "text/xml");
                            context.setUrlXml(claveXml);
                        } catch (Exception e3) {
                            log.error("Error al subir documentos a S3 para identifier: {}", identifier, e3);
                        }

                        if (pdfContent != null) {
                            String destinatario = obtenerDestinatario(entidad);
                            if (destinatario != null) {
                                notificacionService.enviarComprobante(
                                        destinatario, xmlFirmado, pdfContent,
                                        context.getClaveAcceso().substring(Math.max(0, context.getClaveAcceso().length() - 9)));
                            }
                        }
                        strategy.actualizarEntidad(entidad, context);
                        strategy.persistir(entidad);
                        return;
                    }
                }
            }

            context.setEstadoAutorizacion(EnumEstadoDocumentoElectronico.RECHAZADO.getLabel());
            strategy.actualizarEntidad(entidad, context);
            strategy.persistir(entidad);
            throw new SystemException("Error en envío al SRI: " + context.getMensajeSri(), "SYS-SRI-SEND-ERR");
        }

        strategy.actualizarEntidad(entidad, context);
        strategy.persistir(entidad);
    }

    private EmpresaMatriz resolverEmpresa(Object entidad) {
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura) {
            ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura f = (ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura) entidad;
            return f.getPuntoEmision().getEstablecimientos().getEmpresaMatriz();
        }
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito) {
            ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito nc = (ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito) entidad;
            return nc.getPuntoEmision().getEstablecimientos().getEmpresaMatriz();
        }
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion) {
            ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion r = (ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion) entidad;
            return r.getPuntoEmision().getEstablecimientos().getEmpresaMatriz();
        }
        throw new IllegalArgumentException("Tipo de entidad no soportado: " + entidad.getClass().getName());
    }
    
    private String resolverDocumento(Object entidad) {
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura) {
            return "factura";
        }
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito) {
            return "notaCredito";
        }
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion) {
            return "retencion";
        }
        throw new IllegalArgumentException("Tipo de entidad no soportado: " + entidad.getClass().getName());
    }
    
    private String obtenerDestinatario(Object entidad) {
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura) {
            return ((ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura) entidad).getCliente().getCorreo();
        }
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito) {
            return ((ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito) entidad).getCliente().getCorreo();
        }
        if (entidad instanceof ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion) {
            ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion r = (ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion) entidad;
            if (r.getEgreso() != null && r.getEgreso().getProveedor() != null) {
                return r.getEgreso().getProveedor().getProvCorreo();
            }
        }
        return null;
    }

    /**
     * Consulta la autorización con reintentos y backoff progresivo.
     * <p>
     * Cada iteración que devuelve una autorización "definitiva" (AUTORIZADO,
     * RECHAZADO, DEVUELTA, NO AUTORIZADO) corta el ciclo de inmediato. Si el
     * servicio de autorización devuelve una respuesta sin autorizaciones (aún
     * procesando) o lanza un error de red/timeout, se reintenta con backoff.
     *
     * @return la primera respuesta con autorización definitiva, la última
     *         respuesta obtenida, o {@code null} si todos los intentos fallaron
     *         o devolvieron {@code null}.
     */
    private RespuestaComprobante consultarAutorizacionConReintentos(
            String claveAcceso, boolean esProduccion, Configuraciones configuraciones) {

        RespuestaComprobante respuesta = null;
        for (int intento = 1; intento <= MAX_INTENTOS_AUTORIZACION; intento++) {
            try {
                respuesta = sriWebServiceService.autorizarComprobante(claveAcceso, esProduccion, configuraciones);
                if (respuesta != null && tieneAutorizacionDefinitiva(respuesta)) {
                    String estado = respuesta.getAutorizaciones().getAutorizacion().get(0).getEstado();
                    log.info("Autorización resuelta (intento {}): {} para claveAcceso {}", intento, estado, claveAcceso);
                    return respuesta;
                }
                log.info("Intento {}/{}: el SRI aún no tiene autorización para la clave {}", intento, MAX_INTENTOS_AUTORIZACION, claveAcceso);
            } catch (Exception e) {
                log.warn("Intento {}/{}: error al consultar autorización clave {}: {}", intento, MAX_INTENTOS_AUTORIZACION, claveAcceso, e.getMessage());
            }

            if (intento < MAX_INTENTOS_AUTORIZACION) {
                long backoff = BACKOFF_AUTORIZACION_MS[Math.min(intento - 1, BACKOFF_AUTORIZACION_MS.length - 1)];
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Interrupción durante backoff de autorización para clave {}", claveAcceso);
                    break;
                }
            }
        }
        return respuesta;
    }

    /**
     * Determina si la respuesta del SRI contiene una resolución de autorización
     * definitiva (no requiere más reintentos).
     */
    private boolean tieneAutorizacionDefinitiva(RespuestaComprobante respuesta) {
        if (respuesta == null || respuesta.getAutorizaciones() == null
                || respuesta.getAutorizaciones().getAutorizacion() == null
                || respuesta.getAutorizaciones().getAutorizacion().isEmpty()) {
            return false;
        }
        String estado = respuesta.getAutorizaciones().getAutorizacion().get(0).getEstado();
        return EnumEstadoDocumentoElectronico.AUTORIZADO.getLabel().equals(estado)
                || EnumEstadoDocumentoElectronico.RECHAZADO.getLabel().equals(estado)
                || "DEVUELTA".equals(estado) || "NO AUTORIZADO".equals(estado);
    }

    private LocalDate convertir(XMLGregorianCalendar xmlDate) {
        if (xmlDate == null || xmlDate.getYear() == DatatypeConstants.FIELD_UNDEFINED) {
            return null;
        }
        // Convertimos directamente a LocalDate usando LocalDate.of
        return LocalDate.of(
            xmlDate.getYear(), 
            xmlDate.getMonth(), 
            xmlDate.getDay()
        );
    }

    /**
     * Reconciliación de comprobantes enviados al SRI cuya autorización no se resolvió
     * de forma síncrona (quedaron en estado ENVIADO/EN_PROCESO/PENDIENTE).
     * <p>
     * NO re-envía ni re-firma el comprobante: solo consulta la autorización por la
     * claveAcceso ya persistida y actualiza el estado (AUTORIZADO/RECHAZADO) más los
     * metadatos asociados (número de autorización, fecha, mensaje SRI).
     *
     * @param entidad entidad de negocio (Factura, NotaCredito, Retencion) ya persistida
     * @param claveAcceso clave de acceso del comprobante a reconciliar
     * @param strategy estrategia del tipo de documento
     */
    public void reconciliar(Object entidad, String claveAcceso, DocumentoElectronicoStrategy strategy) {
        try {
            EmpresaMatriz empresa = resolverEmpresa(entidad);
            Configuraciones configuraciones = configuracionesDao.findAll().get(0);
            boolean esProduccion = empresa.getEmpmAmbiente() == 2;

            RespuestaComprobante respuesta = consultarAutorizacionConReintentos(
                    claveAcceso, esProduccion, configuraciones);

            if (respuesta == null || respuesta.getAutorizaciones() == null
                    || respuesta.getAutorizaciones().getAutorizacion().isEmpty()) {
                log.info("Reconciliación: el SRI aún no resuelve la autorización de la clave {}. Se mantiene el estado.", claveAcceso);
                return;
            }

            Autorizacion aut = respuesta.getAutorizaciones().getAutorizacion().get(0);
            SriProcessingContext context = new SriProcessingContext();
            context.setClaveAcceso(claveAcceso);
            context.setEstadoAutorizacion(aut.getEstado());
            context.setNumeroAutorizacion(aut.getNumeroAutorizacion());
            if (aut.getMensajes() != null && !aut.getMensajes().getMensaje().isEmpty()) {
                context.setMensajeSri(aut.getMensajes().getMensaje().get(0).getMensaje());
            }
            if (EnumEstadoDocumentoElectronico.AUTORIZADO.getLabel().equals(aut.getEstado())) {
                context.setAutorizado(true);
                context.setFechaAutorizacion(convertir(aut.getFechaAutorizacion()));
            }

            strategy.actualizarEntidad(entidad, context);
            strategy.persistir(entidad);
            log.info("Reconciliación clave {} resuelta: {}", claveAcceso, aut.getEstado());
        } catch (Exception e) {
            log.error("Error durante la reconciliación del comprobante con clave {}.", claveAcceso, e);
        }
    }
}
