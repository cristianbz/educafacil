/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanFacturacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.service.IntegracionSriService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * Backing bean para el monitoreo y gestión de facturación electrónica.
 * 
 * @author christian
 *
 */
@Named("backingFacturacion")
@ViewScoped
public class BackingFacturacion implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(BackingFacturacion.class);

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @Inject
    @Getter
    private BeanFacturacion beanFacturacion;

    @EJB
    private AdministracionService administracionService;

    @EJB
    private IntegracionSriService integracionSriService;

    @PostConstruct
    public void init() {
        cargarPagos();
    }

    public void cargarPagos() {
        getBeanFacturacion().setListaPagos(administracionService.listarPagos());
    }

    /**
     * Reintenta procesar una factura electrónica que falló o no se envió.
     * @param pago
     */
    public void reintentarFacturacion(Pagos pago) {
        try {
            EmpresaMatriz empresa = administracionService.listarEmpresas().get(0);
            integracionSriService.procesarFacturaElectronica(pago, empresa);
            cargarPagos();
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), "Factura procesada correctamente.");
        } catch (Exception e) {
            log.error("Error al reintentar facturación", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), e.getMessage());
        }
    }

    /**
     * Descarga el XML autorizado.
     */
    public StreamedContent getXmlDownload(Pagos pago) {
        if (pago.getPagoXmlAutorizado() == null) return null;
        InputStream is = new ByteArrayInputStream(pago.getPagoXmlAutorizado());
        return DefaultStreamedContent.builder()
                .name("factura_" + pago.getPagoNumeroFactura() + ".xml")
                .contentType("text/xml")
                .stream(() -> is)
                .build();
    }

    /**
     * Descarga el PDF RIDE.
     */
    public StreamedContent getPdfDownload(Pagos pago) {
        if (pago.getPagoPdfRide() == null) return null;
        InputStream is = new ByteArrayInputStream(pago.getPagoPdfRide());
        return DefaultStreamedContent.builder()
                .name("factura_" + pago.getPagoNumeroFactura() + ".pdf")
                .contentType("application/pdf")
                .stream(() -> is)
                .build();
    }
}
