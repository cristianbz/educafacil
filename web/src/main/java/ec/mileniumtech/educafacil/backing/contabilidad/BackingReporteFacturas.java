package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import org.apache.log4j.Logger;

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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Backing bean para gestionar el reporte de facturas.
 */
@Named("backingReporteFacturas")
@ViewScoped
public class BackingReporteFacturas implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(BackingReporteFacturas.class);

    @EJB
    @Getter
    private FacturaDaoImpl facturaServicio;
    
    @EJB
    private NotificacionService notificacionService;

    @Inject
    @Getter
    private BeanReporteFacturas beanReporteFacturas;

    /**
     * Realiza la búsqueda de facturas en base a los filtros.
     */
    public void buscarFacturas() {
        try {
            beanReporteFacturas.setListaFacturas(facturaServicio.buscarFacturasPorFiltros(
                beanReporteFacturas.getFechaInicio(),
                beanReporteFacturas.getFechaFin(),
                beanReporteFacturas.getIdentificacion(),
                beanReporteFacturas.getNumeroAutorizacion()
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
        if (doc != null && doc.getXmlAutorizadoSri() != null) {
            return DefaultStreamedContent.builder()
                    .name("Factura_" + factura.getNumero() + ".xml")
                    .contentType("text/xml")
                    .stream(() -> new ByteArrayInputStream(doc.getXmlAutorizadoSri()))
                    .build();
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
        if (doc != null && doc.getPdfRide() != null) {
            return DefaultStreamedContent.builder()
                    .name("RIDE_" + factura.getNumero() + ".pdf")
                    .contentType("application/pdf")
                    .stream(() -> new ByteArrayInputStream(doc.getPdfRide()))
                    .build();
        } else {
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
            if (doc == null || doc.getXmlAutorizadoSri() == null || doc.getPdfRide() == null) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "La factura no tiene el XML o PDF autorizado para enviar por correo.");
                return;
            }

            String destinatario = factura.getCliente().getCorreo();
            if (destinatario == null || destinatario.trim().isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El cliente no tiene un correo electrónico registrado.");
                return;
            }

            notificacionService.enviarComprobante(
                destinatario, 
                doc.getXmlAutorizadoSri(), 
                doc.getPdfRide(), 
                factura.getNumero()
            );

            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Factura enviada correctamente a: " + destinatario);
        } catch (Exception e) {
            log.error("Error al enviar correo", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo enviar el correo: " + e.getMessage());
        }
    }
    public void nuevoReporte() {
    	beanReporteFacturas.setListaFacturas(null);
    	beanReporteFacturas.setNumeroAutorizacion(null);
    }
}
