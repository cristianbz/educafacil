package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
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
import ec.mileniumtech.educafacil.dao.impl.FacturaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentoElectronico;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.service.NotificacionService;
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
    private FacturaDaoImpl facturaServicio;
    
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
            beanReporteFacturas.setListaFacturas(facturaServicio.buscarFacturasPorFiltros(
                beanReporteFacturas.getFechaInicio(),
                beanReporteFacturas.getFechaFin(),
                beanReporteFacturas.getIdentificacion(),
                beanReporteFacturas.getNumeroAutorizacion(),
                beanReporteFacturas.getEstadoAutorizacion()
            ));
            
            if (beanReporteFacturas.getListaFacturas().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Información", "No se encontraron resultados");
            }
        } catch (Exception e) {
            log.error("Error al buscar facturas", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al buscar las facturas");
        }
    }
    
    /**
     * Limpia los filtros de búsqueda.
     */
    public void limpiarFiltros() {
        beanReporteFacturas.init();
    }

    /**
     * Genera la descarga del archivo XML de la factura.
     */
    public StreamedContent descargarXml(Factura factura) {
    	DocumentoElectronico doc = factura.getDocumentoElectronico();
    	// Prioridad 1: Descargar desde S3 via pre-signed URL
    	if (doc.getUrlXml() != null && !doc.getUrlXml().isEmpty()) {
    		try {
    			String presignedUrl = awsS3Service.generarUrlDescarga(doc.getUrlXml());
    			// ESCAPE Y EJECUCIÓN: Mandamos a abrir la ventana inmediatamente con la URL fresca
    			String script = String.format("window.open('%s', '_blank');", presignedUrl);
    			PrimeFaces.current().executeScript(script);
    			return null;
    		} catch (Exception e) {
    			log.error("Error al generar pre-signed URL para XML", e);
    			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo generar el enlace de descarga: " + e.getMessage());
    			return null;
    		}
    	} else {
    		Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El XML no está disponible para esta factura.");
    		return null;
    	}
    }

    /**
     * Genera la descarga del archivo RIDE (PDF) de la factura.
     */
    public StreamedContent descargarRide(Factura factura) {
        DocumentoElectronico doc = factura.getDocumentoElectronico();
        if (doc == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El RIDE no está disponible para esta factura.");
            return null;
        }
        // Prioridad 1: Descargar desde S3 via pre-signed URL
        if (doc.getUrlPdf() != null && !doc.getUrlPdf().isEmpty()) {
            try {
                String presignedUrl = awsS3Service.generarUrlDescarga(doc.getUrlPdf());
             // ESCAPE Y EJECUCIÓN: Mandamos a abrir la ventana inmediatamente con la URL fresca
                String script = String.format("window.open('%s', '_blank');", presignedUrl);
                PrimeFaces.current().executeScript(script);
                return null;
            } catch (Exception e) {
                log.error("Error al generar pre-signed URL para RIDE", e);
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo generar el enlace de descarga: " + e.getMessage());
                return null;
            }
        }else {
        	Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El RIDE no está disponible para esta factura.");
          return null;
        }
    }
    
    /**
     * Anula la factura seleccionada.
     */
    public void anularFactura(Factura factura) {
        try {
            // Implementación de anulación (ej. cambiar estado en DocumentoElectronico o Factura)
        	factura.getDocumentoElectronico().setEstado("ANULADA");
        	facturaServicio.actualizar(factura);
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Info", "Proceso de anulación iniciado para: " + factura.getNumero());
        } catch (Exception e) {
            log.error("Error al anular factura", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo anular la factura.");
        }
    }

    /**
     * Envía la factura por correo electrónico.
     */
    public void enviarCorreo(Factura factura) {
        try {
            DocumentoElectronico doc = factura.getDocumentoElectronico();
            if (doc == null || doc.getUrlXml() == null || doc.getUrlPdf() == null) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "La factura no tiene el XML o PDF autorizado para enviar por correo.");
                return;
            }

            String destinatario = factura.getCliente().getCorreo();
            if (destinatario == null || destinatario.trim().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El cliente no tiene un correo electrónico registrado.");
                return;
            }

            byte[] xmlBytes = descargarBytes(awsS3Service.generarUrlDescarga(doc.getUrlXml()));
            byte[] pdfBytes = descargarBytes(awsS3Service.generarUrlDescarga(doc.getUrlPdf()));

            notificacionService.enviarComprobante(
                destinatario, 
                xmlBytes,
                pdfBytes, 
                factura.getNumero()
            );

            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Factura enviada correctamente a: " + destinatario);
        } catch (Exception e) {
            log.error("Error al enviar correo", e);
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
            if (beanReporteFacturas.getListaFacturas() == null || beanReporteFacturas.getListaFacturas().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "No hay facturas para exportar.");
                return null;
            }


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String csv = "FECHA_EMISION COMPROBANTE NUMERO_COMPROBANTE IDENTIFICACION_RECEPTOR RAZON_SOCIAL CLAVE_ACCESO VALOR_TOTAL\n" +
                    beanReporteFacturas.getListaFacturas().stream()
                        .map(fac -> {
                            String claveAcceso = "";
                            if (fac.getDocumentoElectronico() != null) {
                                claveAcceso = fac.getDocumentoElectronico().getNumeroAutorizacion() != null
                                    ? fac.getDocumentoElectronico().getNumeroAutorizacion()
                                    : fac.getDocumentoElectronico().getClaveAcceso() != null
                                        ? fac.getDocumentoElectronico().getClaveAcceso()
                                        : "";
                            }
                            return escaped(fac.getFechaEmision() != null ? fac.getFechaEmision().format(formatter) : "") + ' '
                                + escaped("Factura") + ' '
                                + escaped(fac.getNumero() != null ? fac.getNumero() : "") + ' '
                                + escaped(fac.getCliente() != null && fac.getCliente().getNumeroIdentificacion() != null ? fac.getCliente().getNumeroIdentificacion() : "") + ' '
                                + escaped(fac.getCliente() != null && fac.getCliente().getNombresCompletos() != null ? fac.getCliente().getNombresCompletos() : "") + ' '
                                + escaped(claveAcceso) + ' '
                                + escaped(fac.getTotal() != null ? fac.getTotal().toString() : "") + '\n';
                        })
                        .collect(Collectors.joining());

                byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

            return DefaultStreamedContent.builder()
                    .name("ReporteFacturas.csv")
                    .contentType("text/csv")
                    .stream(() -> new ByteArrayInputStream(bytes))
                    .build();

        } catch (Exception e) {
            log.error("Error al generar CSV de reporte de facturas", e);
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
    	beanReporteFacturas.setListaFacturas(null);
    	beanReporteFacturas.setNumeroAutorizacion(null);
    }
}
