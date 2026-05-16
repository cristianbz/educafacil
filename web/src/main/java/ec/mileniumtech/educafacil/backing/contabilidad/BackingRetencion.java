package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.service.RetencionService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

@Named
@ViewScoped
public class BackingRetencion implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private RetencionService retencionService;

    @Inject
    @Getter
    private BeanRetencion beanRetencion;

    @Inject
    private MensajesBacking mensajesBacking;

    @PostConstruct
    public void init() {
        cargarRetenciones();
    }

    public void cargarRetenciones() {
        getBeanRetencion().setListaRetenciones(retencionService.listarRetenciones());
        getBeanRetencion().setDetalleNuevo(new DetalleRetencion());
    }

    /**
     * Prepara el diálogo para crear una nueva retención vinculada a un egreso.
     * @param egreso El egreso sustento.
     */
    public void prepararNuevaRetencion(Egresos egreso) {
        getBeanRetencion().setEgresoSustento(egreso);
        
        Retencion nueva = new Retencion();
        nueva.setEgreso(egreso);
        nueva.setFechaEmision(new Date());
        
        // Calcular ejercicio fiscal MM/YYYY
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(nueva.getFechaEmision());
        String periodo = String.format("%02d/%d", cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.YEAR));
        nueva.setEjercicioFiscal(periodo);
        
        nueva.setDetalles(new ArrayList<>());
        
        getBeanRetencion().setNuevaRetencion(nueva);
        prepararNuevoDetalle();
        
        Mensaje.verDialogo("dlgNuevaRetencion");
    }

    public void prepararNuevoDetalle() {
        DetalleRetencion det = new DetalleRetencion();
        det.setBaseImponible(getBeanRetencion().getEgresoSustento().getEgreValor());
        det.setCodigoDocSustento("01"); // Factura
        det.setNumeroDocSustento(getBeanRetencion().getEgresoSustento().getEgreNumFactura());
        det.setFechaSustento(getBeanRetencion().getEgresoSustento().getEgreFecha());
        det.setCodigoImpuesto("1"); // RENTA por defecto
        det.setPorcentaje(BigDecimal.ZERO);
        det.setValorRetenido(BigDecimal.ZERO);
        getBeanRetencion().setDetalleNuevo(det);
    }

    public void calcularValorRetenido() {
        DetalleRetencion det = getBeanRetencion().getDetalleNuevo();
        if (det.getBaseImponible() != null && det.getPorcentaje() != null) {
        	BigDecimal valorRetenido = det.getBaseImponible().multiply(det.getPorcentaje().divide(new BigDecimal(100)));
            det.setValorRetenido(valorRetenido);
        }
    }

    public void agregarDetalle() {
        DetalleRetencion det = getBeanRetencion().getDetalleNuevo();
        if (det.getValorRetenido().compareTo(BigDecimal.ZERO) > 0) {
            getBeanRetencion().getNuevaRetencion().getDetalles().add(det);
            System.out.println(det.getCodigoImpuesto());
            prepararNuevoDetalle();
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Atención", "El valor retenido debe ser mayor a 0.");
        }
    }

    public void eliminarDetalle(DetalleRetencion det) {
        getBeanRetencion().getNuevaRetencion().getDetalles().remove(det);
    }

    public void guardarRetencion() {
        try {
            if (getBeanRetencion().getNuevaRetencion().getDetalles().isEmpty()) {
                throw new Exception("Debe agregar al menos un detalle de retención.");
            }
            
            retencionService.guardarYEmitirRetencion(getBeanRetencion().getNuevaRetencion());
            cargarRetenciones();
            Mensaje.ocultarDialogo("dlgNuevaRetencion");
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Retención guardada y enviada al SRI.");
        } catch (Exception e) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void reemitir(Retencion r) {
        try {
            retencionService.reemitirRetencion(r.getId());
            cargarRetenciones();
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "SRI", "Proceso de re-emisión completado. Verifique el estado.");
        } catch (Exception e) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }
    
    /**
     * Genera la descarga del RIDE (PDF).
     */
    public StreamedContent descargarRide(Retencion r) {
        if (r != null && r.getPdfRide() != null) {
            return DefaultStreamedContent.builder()
                    .name("RIDE_" + r.getNumero() + ".pdf")
                    .contentType("application/pdf")
                    .stream(() -> new ByteArrayInputStream(r.getPdfRide()))
                    .build();
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El RIDE no está disponible.");
            return null;
        }
    }

    /**
     * Genera la descarga del XML autorizado.
     */
    public StreamedContent descargarXml(Retencion r) {
        if (r != null && r.getXmlAutorizado() != null) {
            return DefaultStreamedContent.builder()
                    .name("RET_" + r.getNumero() + ".xml")
                    .contentType("text/xml")
                    .stream(() -> new ByteArrayInputStream(r.getXmlAutorizado()))
                    .build();
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El XML no está disponible.");
            return null;
        }
    }
    public void valoresPrueba() {
    	System.out.println(getBeanRetencion().getDetalleNuevo().getCodigoImpuesto());
    	System.out.println(getBeanRetencion().getDetalleNuevo().getCodigoRetencion());
    }
}
