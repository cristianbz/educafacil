package ec.mileniumtech.educafacil.service.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.service.AwsS3Service;
import ec.mileniumtech.educafacil.service.NotificacionService;
import ec.mileniumtech.educafacil.service.SriWebServiceService;
import ec.mileniumtech.educafacil.service.XadesSignatureService;
import ec.mileniumtech.educafacil.service.sri.autorizacion.Autorizacion;
import ec.mileniumtech.educafacil.service.sri.autorizacion.RespuestaComprobante;
import ec.mileniumtech.educafacil.service.sri.recepcion.RespuestaSolicitud;
import ec.mileniumtech.educafacil.utilitarios.ValidacionUtil;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CriptografiaUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class ProcesadorDocumentosElectronicos {

    @EJB
    private XadesSignatureService xadesSignatureService;

    @EJB
    private SriWebServiceService sriWebServiceService;

    @EJB
    private NotificacionService notificacionService;

    @EJB
    private AwsS3Service awsS3Service;

    public void procesar(Object entidad, DocumentoElectronicoStrategy strategy) throws Exception {
        SriProcessingContext context = new SriProcessingContext();

        EmpresaMatriz empresa = null;

        try {
            empresa = resolverEmpresa(entidad);
        } catch (Exception e) {
            throw new Exception("La entidad no tiene una empresa matriz asociada.", e);
        }

        Object jaxbObject = strategy.construirJaxb(entidad, empresa, context);
        Configuraciones configuraciones = context.getConfiguraciones();

        String xmlString = strategy.generarXml(jaxbObject);
        context.setXmlString(xmlString);
        byte[] pkcs12 = empresa.getEmpmCertificado();
        String password = CriptografiaUtil.desencriptar(empresa.getEmpmPasswordCertificado());

        if (pkcs12 == null || password == null) {
            throw new Exception("Certificado o contraseña no configurados en la empresa.");
        }

        byte[] xmlFirmado = xadesSignatureService.firmarDocumento(
                xmlString.getBytes(StandardCharsets.UTF_8), pkcs12, password);
        context.setXmlFirmado(xmlFirmado);

        boolean esProduccion = empresa.getEmpmAmbiente() == 2;
        String urlWsdl = esProduccion
                ? configuraciones.getConfWsRecepcionProduccion()
                : configuraciones.getConf_wsRecepcionPruebas();

        if (!ValidacionUtil.verificarConexion(urlWsdl, 5000)) {
            throw new Exception("No se pudo establecer comunicación con los servidores del SRI. Verifique su conexión a internet.");
        }

        RespuestaSolicitud respuestaEnvio;
        try {
            respuestaEnvio = sriWebServiceService.enviarComprobante(xmlFirmado, esProduccion, configuraciones);
        } catch (Exception e) {
            throw new Exception("Error al comunicar con el SRI: " + e.getMessage());
        }

        if ("RECIBIDA".equals(respuestaEnvio.getEstado())) {
            Thread.sleep(3000);
            RespuestaComprobante respuestaAut = sriWebServiceService.autorizarComprobante(
                    context.getClaveAcceso(), esProduccion, configuraciones);

            if (!respuestaAut.getAutorizaciones().getAutorizacion().isEmpty()) {
                Autorizacion aut = respuestaAut.getAutorizaciones().getAutorizacion().get(0);
                context.setEstadoAutorizacion(aut.getEstado());
                context.setNumeroAutorizacion(aut.getNumeroAutorizacion());

                if ("AUTORIZADO".equals(aut.getEstado())) {
                    context.setAutorizado(true);
                    context.setFechaAutorizacion(convertir(aut.getFechaAutorizacion()));
                    byte[] pdfContent = strategy.generarRide(jaxbObject, empresa, context);
                    context.setPdfContent(pdfContent);

                    try {
                        String identifier = strategy.getEntityIdentifier(entidad);

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
                        System.err.println("[AwsS3] Error al subir documentos a S3: " + e3.getMessage());
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
            }
        } else {
            context.setEstadoAutorizacion("RECHAZADO");
            if (respuestaEnvio.getComprobantes() != null
                    && !respuestaEnvio.getComprobantes().getComprobante().isEmpty()) {
                context.setMensajeSri(respuestaEnvio.getComprobantes().getComprobante().get(0)
                        .getMensajes().getMensaje().get(0).getMensaje());
            }
            strategy.actualizarEntidad(entidad, context);
            strategy.persistir(entidad);
            throw new Exception("Error en envío al SRI: " + context.getMensajeSri());
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

    private OffsetDateTime convertir(XMLGregorianCalendar xmlDate) {
        if (xmlDate == null) return null;
        if (xmlDate.getYear() == DatatypeConstants.FIELD_UNDEFINED) return null;
        return xmlDate.toGregorianCalendar().toZonedDateTime().toOffsetDateTime();
    }
}
