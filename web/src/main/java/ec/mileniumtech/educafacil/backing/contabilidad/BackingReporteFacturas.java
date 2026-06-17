package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.mileniumtech.educafacil.bean.contabilidad.BeanReporteFacturas;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteReporteDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.service.NotificacionService;
import ec.mileniumtech.educafacil.service.facade.FacturacionFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * Backing bean para gestionar el reporte de facturas.
 */
@Named("backingReporteFacturas")
@ViewScoped
public class BackingReporteFacturas implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingReporteFacturas.class);

    @EJB
    @Getter
    private FacturacionFacade facturaServicio;
    
    @EJB
    private NotificacionService notificacionService;

    @Inject
    @Getter
    private BeanReporteFacturas beanReporteFacturas;
    
    @EJB
    private ec.mileniumtech.educafacil.service.AwsS3Service awsS3Service;
    
    /**
     * Realiza la búsqueda de facturas en base a los filtros.
     */
    public void buscarFacturas() {
        try {
            beanReporteFacturas.setListaComprobantes(facturaServicio.buscarComprobantesPorFiltros(
                    beanReporteFacturas.getFechaInicio(),
                    beanReporteFacturas.getFechaFin(),
                    beanReporteFacturas.getIdentificacion(),
                    beanReporteFacturas.getNumeroAutorizacion(),
                    beanReporteFacturas.getEstadoAutorizacion(),
                    beanReporteFacturas.getComprobanteTipo()
                ));
            
            if (beanReporteFacturas.getListaComprobantes().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Información", "No se encontraron resultados");
            }
        } catch (Exception e) {
            log.error("Error al buscar comprobantes", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al buscar los comprobantes");
        }
    }
    
    /**
     * Limpia los filtros de búsqueda.
     */
    public void limpiarFiltros() {
        beanReporteFacturas.init();
    }

    /**
     * Genera la descarga del archivo RIDE (PDF) o XML del comprobante seleccionado
     */
    
    public StreamedContent descargarDocumento(ComprobanteReporteDto comprobante, String tipoArchivo) {
        // 1. Validaciones iniciales
        if (comprobante == null || comprobante.getEntityId() == null || comprobante.getEntityType() == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Datos del comprobante incompletos.");
            return null;
        }
        
        if (tipoArchivo == null || (!tipoArchivo.equalsIgnoreCase("pdf") && !tipoArchivo.equalsIgnoreCase("xml"))) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Tipo de archivo no soportado.");
            return null;
        }

        String tipoComprobante = comprobante.getTipoComprobante();
        String urlArchivo = null;
        boolean esPdf = tipoArchivo.equalsIgnoreCase("pdf");

        try {
            // 2. Switch centralizado: recupera la URL correcta basándose en el tipoArchivo solicitado
            switch (tipoComprobante) {
                case "Factura":
                    Factura factura = facturaServicio.buscarFacturaPorId(comprobante.getEntityId());
                    if (factura != null && factura.getDocumentoElectronico() != null) {
                        urlArchivo = esPdf ? factura.getDocumentoElectronico().getUrlPdf() 
                                           : factura.getDocumentoElectronico().getUrlXml();
                    }
                    break;
                    
                case "Nota de Credito":
                    NotaCredito notaCredito = facturaServicio.buscarNotaCreditoporId(comprobante.getEntityId());
                    if (notaCredito != null) {
                        urlArchivo = esPdf ? notaCredito.getUrlPdf() : notaCredito.getUrlXml();
                    }
                    break;

                case "Retencion":
                    Retencion retencion = facturaServicio.buscarRetencionporId(comprobante.getEntityId());
                    if (retencion != null) {
                        urlArchivo = esPdf ? retencion.getUrlPdf() : retencion.getUrlXml();
                    }
                    break;
                    
                default:
                    Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Tipo de comprobante no reconocido: " + tipoComprobante);
                    return null;
            }
        } catch (Exception e) {
            log.error("Error al buscar el comprobante tipo: " + tipoComprobante, e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Error al consultar la base de datos o procesar el documento.");
            return null;
        }

        // 3. Validación unificada de la URL obtenida
        if (urlArchivo == null || urlArchivo.trim().isEmpty()) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", 
                    String.format("El archivo %s no está disponible para este documento (%s).", tipoArchivo.toUpperCase(), tipoComprobante));
            return null;
        }

        // 4. Bloque único para AWS S3 y redirección con PrimeFaces
        try {
            String presignedUrl = awsS3Service.generarUrlDescarga(urlArchivo);
            String script = String.format("window.open('%s', '_blank');", presignedUrl);
            PrimeFaces.current().executeScript(script);
            return null;
        } catch (Exception e) {
            log.error("Error al generar pre-signed URL para " + tipoArchivo, e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo generar el enlace de descarga: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Anula la factura seleccionada.
     */  
    public void anularComprobante(ComprobanteReporteDto comprobante) {
        if (comprobante == null || comprobante.getEntityId() == null || comprobante.getEntityType() == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Datos del comprobante incompletos para proceder con la anulación.");
            return;
        }

        String tipoComprobante = comprobante.getTipoComprobante();
        String numeroDocumento = "";

        try {
            switch (tipoComprobante) {
                case "Factura":
                    Factura factura = facturaServicio.buscarFacturaPorId(comprobante.getEntityId());
                    if (factura != null && factura.getDocumentoElectronico() != null) {
                        factura.getDocumentoElectronico().setEstado("ANULADA");
                        facturaServicio.actualizar(factura); // Reemplaza por tu método real si varía
                        numeroDocumento = factura.getNumero(); // O factura.getDocumentoElectronico().getNumero()
                    } else {
                        mostrarMensajeNoEncontrado(tipoComprobante);
                        return;
                    }
                    break;
                    
                case "Nota de Credito":
                    NotaCredito notaCredito = facturaServicio.buscarNotaCreditoporId(comprobante.getEntityId());
                    // Si las notas de crédito tienen el DocumentoElectronico embebido o asociado:
                    if (notaCredito != null ) {
                        notaCredito.setEstado("ANULADA");
                        facturaServicio.actualizarNotaCredito(notaCredito); // Asegúrate de usar el método correcto de tu servicio
                        numeroDocumento = notaCredito.getNumero(); 
                    } else {
                        mostrarMensajeNoEncontrado(tipoComprobante);
                        return;
                    }
                    break;

                case "Retencion":
                    Retencion retencion = facturaServicio.buscarRetencionporId(comprobante.getEntityId());
                    if (retencion != null ) {
                        retencion.setEstado("ANULADA");
                        facturaServicio.actualizarRetencion(retencion); // Asegúrate de usar el método correcto de tu servicio
                        numeroDocumento = retencion.getNumero();
                    } else {
                        mostrarMensajeNoEncontrado(tipoComprobante);
                        return;
                    }
                    break;
                    
                default:
                    Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Tipo de comprobante no reconocido para anulación: " + tipoComprobante);
                    return;
            }

            // Mensaje de éxito unificado para cualquier documento
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Info", 
                String.format("Proceso de anulación completado para %s Nro: %s", tipoComprobante, numeroDocumento));

        } catch (Exception e) {
            log.error("Error al anular el comprobante tipo: " + tipoComprobante, e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo anular el documento seleccionado de tipo " + tipoComprobante);
        }
    }

    /**
     * Método de apoyo (Helper) para estandarizar el aviso de documento no encontrado.
     */
    private void mostrarMensajeNoEncontrado(String tipoComprobante) {
        Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", 
            String.format("No se encontró el registro o el documento electrónico para este/a %s.", tipoComprobante));
    }

    /**
     * Envía la factura por correo electrónico.
     */   
    public void enviarCorreo(ComprobanteReporteDto comprobante) {    	
        if (comprobante == null || comprobante.getEntityId() == null || comprobante.getEntityType() == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Datos del comprobante incompletos para realizar el envío.");
            return;
        }

        String tipoComprobante = comprobante.getTipoComprobante();
        
        // Variables temporales para unificar el envío al final
        String urlXml = null;
        String urlPdf = null;
        String destinatario = null;
        String numeroDocumento = null;

        try {
            // 1. Fase de extracción: Obtenemos los datos necesarios según el tipo de documento
            switch (tipoComprobante) {
                case "Factura":
                    Factura factura = facturaServicio.buscarFacturaPorId(comprobante.getEntityId());
                    if (factura != null && factura.getDocumentoElectronico() != null) {
                        urlXml = factura.getDocumentoElectronico().getUrlXml();
                        urlPdf = factura.getDocumentoElectronico().getUrlPdf();
                        numeroDocumento = factura.getNumero();
                        if (factura.getCliente() != null) {
                            destinatario = factura.getCliente().getCorreo();
                        }
                    }
                    break;
                    
                case "Nota de Credito":
                    NotaCredito notaCredito = facturaServicio.buscarNotaCreditoporId(comprobante.getEntityId());
                    if (notaCredito != null) {
                        urlXml = notaCredito.getUrlXml();
                        urlPdf = notaCredito.getUrlPdf();
                        numeroDocumento = notaCredito.getNumero();
                        // Adapta cómo obtienes el correo en tu modelo (ej. notaCredito.getCliente().getCorreo() o notaCredito.getFactura().getCliente().getCorreo())
                        if (notaCredito.getCliente() != null) { 
                            destinatario = notaCredito.getCliente().getCorreo();
                        }
                    }
                    break;

                case "Retencion":
                    Retencion retencion = facturaServicio.buscarRetencionporId(comprobante.getEntityId());
                    if (retencion != null) {
                        urlXml = retencion.getUrlXml();
                        urlPdf = retencion.getUrlPdf();
                        numeroDocumento = retencion.getNumero();
                        // Adapta si tu retención apunta a un Proveedor, Sujeto Pasivo o Cliente
                        if (retencion.getEgreso().getProveedor() != null) {
                            destinatario = retencion.getEgreso().getProveedor().getProvCorreo();
                        }
                    }
                    break;
                    
                default:
                    Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Tipo de comprobante no reconocido para envío por correo: " + tipoComprobante);
                    return;
            }

            // 2. Validación unificada de existencia del documento
            if (numeroDocumento == null) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", String.format("No se encontró el/la %s en el sistema.", tipoComprobante));
                return;
            }

            // 3. Validación unificada de archivos electrónicos autorizados
            if (urlXml == null || urlXml.trim().isEmpty() || urlPdf == null || urlPdf.trim().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", 
                    String.format("El/La %s no tiene el XML o PDF autorizado para enviar por correo.", tipoComprobante));
                return;
            }

            // 4. Validación unificada del correo electrónico
            if (destinatario == null || destinatario.trim().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El receptor del comprobante no tiene un correo electrónico registrado.");
                return;
            }

            // 5. Descarga de archivos e invocación del servicio de notificación (Cero duplicación)
            byte[] xmlBytes = descargarBytes(awsS3Service.generarUrlDescarga(urlXml));
            byte[] pdfBytes = descargarBytes(awsS3Service.generarUrlDescarga(urlPdf));

            notificacionService.enviarComprobante(
                destinatario, 
                xmlBytes,
                pdfBytes, 
                numeroDocumento
            );

            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", 
                String.format("%s Nro. %s enviado correctamente a: %s", tipoComprobante, numeroDocumento, destinatario));

        } catch (Exception e) {
            log.error("Error al enviar correo para el comprobante tipo: " + tipoComprobante, e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo enviar el correo: " + e.getMessage());
        }
    }
    
    /**
     * Descarga un archivo desde la url del bucket s3
     * @param urlStr
     * @return
     * @throws IOException
     */
    private byte[] descargarBytes(String urlStr) throws IOException {
        URL url = new URL(urlStr);

        try (InputStream is = url.openStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        }
    }
    
    public StreamedContent getCsvDownload() {
        try {
            if (beanReporteFacturas.getListaComprobantes() == null || beanReporteFacturas.getListaComprobantes().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "No hay comprobantes para exportar.");
                return null;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String csv = "FECHA_EMISION COMPROBANTE NUMERO_COMPROBANTE IDENTIFICACION_RECEPTOR RAZON_SOCIAL CLAVE_ACCESO VALOR_TOTAL\n" +
                beanReporteFacturas.getListaComprobantes().stream()
                    .map(dto -> {
                        String claveAcceso = dto.getNumeroAutorizacion() != null
                            ? dto.getNumeroAutorizacion()
                            : dto.getClaveAcceso() != null ? dto.getClaveAcceso() : "";
                        return escaped(dto.getFechaEmision() != null ? dto.getFechaEmision().format(formatter) : "") + ' '
                            + escaped(dto.getTipoComprobante() != null ? dto.getTipoComprobante() : "") + ' '
                            + escaped(dto.getNumero() != null ? dto.getNumero() : "") + ' '
                            + escaped(dto.getIdentificacion() != null ? dto.getIdentificacion() : "") + ' '
                            + escaped(dto.getRazonSocial() != null ? dto.getRazonSocial() : "") + ' '
                            + escaped(claveAcceso) + ' '
                            + escaped(dto.getTotal() != null ? dto.getTotal().toString() : "") + '\n';
                    })
                    .collect(Collectors.joining());

            byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

            return DefaultStreamedContent.builder()
                    .name("ReporteComprobantes.csv")
                    .contentType("text/csv")
                    .stream(() -> new ByteArrayInputStream(bytes))
                    .build();

        } catch (Exception e) {
            log.error("Error al generar CSV de reporte de comprobantes", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al generar el archivo CSV");
            return null;
        }
    }

    private String escaped(String value) {
        if (value == null) return "";
        if (value.contains(" ") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }


    public void nuevoReporte() {
    	beanReporteFacturas.setListaComprobantes(null);
    	beanReporteFacturas.setNumeroAutorizacion(null);
    }
}
