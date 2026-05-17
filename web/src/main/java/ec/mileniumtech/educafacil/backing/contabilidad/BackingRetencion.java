package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
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
     * Carga los códigos SRI correspondientes al tipo de impuesto por defecto (RENTA).
     *
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
        String periodo = String.format("%02d/%d",
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.YEAR));
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
        getBeanRetencion().setCodigoSriSeleccionado(null);

        // Cargar códigos SRI para el tipo de impuesto por defecto (RENTA = "1")
        cargarCodigosSri("1");
    }

    /**
     * Se invoca via p:ajax al cambiar el Tipo de Impuesto en el selectOneMenu.
     * Limpia el código SRI seleccionado y recarga el catálogo filtrado.
     */
    public void onTipoImpuestoChange() {
        String tipoImpuesto = getBeanRetencion().getDetalleNuevo().getCodigoImpuesto();
        // Limpiar selección anterior
        getBeanRetencion().setCodigoSriSeleccionado(null);
        getBeanRetencion().getDetalleNuevo().setCodigoRetencion(null);
        getBeanRetencion().getDetalleNuevo().setPorcentaje(BigDecimal.ZERO);
        getBeanRetencion().getDetalleNuevo().setValorRetenido(BigDecimal.ZERO);
        cargarCodigosSri(tipoImpuesto);
    }

    /**
     * Carga el catálogo de códigos SRI para el tipo de impuesto dado.
     *
     * @param tipoImpuesto "1", "2" o "6"
     */
    private void cargarCodigosSri(String tipoImpuesto) {
        if (tipoImpuesto != null && !tipoImpuesto.isBlank()) {
            List<CodigoSriRetencion> codigos = retencionService.listarCodigosPorTipoImpuesto(tipoImpuesto);
            getBeanRetencion().setListaCodigosSri(codigos);
        } else {
            getBeanRetencion().setListaCodigosSri(new ArrayList<>());
        }
    }

    /**
     * Método de autoComplete para el p:autoComplete del campo "Cód. SRI Retención".
     * Filtra por el tipo de impuesto actual y el texto escrito.
     *
     * @param query Texto escrito por el usuario en el autoComplete.
     * @return Lista de CodigoSriRetencion que coincidan.
     */
    public List<CodigoSriRetencion> completarCodigoSri(String query) {
        String tipoImpuesto = getBeanRetencion().getDetalleNuevo().getCodigoImpuesto();
        if (tipoImpuesto == null || tipoImpuesto.isBlank()) {
            return new ArrayList<>();
        }
        return retencionService.buscarCodigosSri(tipoImpuesto, query);
    }

    /**
     * Se invoca al seleccionar un código SRI en el autoComplete.
     * Copia el porcentaje precargado del catálogo al detalle y recalcula el valor retenido.
     */
    public void onCodigoSriSelect() {
        CodigoSriRetencion seleccionado = getBeanRetencion().getCodigoSriSeleccionado();
        if (seleccionado != null) {
            DetalleRetencion det = getBeanRetencion().getDetalleNuevo();
            det.setCodigoRetencion(seleccionado.getCodigo());
            if (seleccionado.getPorcentaje() != null) {
                det.setPorcentaje(seleccionado.getPorcentaje());
                calcularValorRetenido();
            }
        }
    }

    public void calcularValorRetenido() {
        DetalleRetencion det = getBeanRetencion().getDetalleNuevo();
        if (det.getBaseImponible() != null && det.getPorcentaje() != null) {
            BigDecimal valorRetenido = det.getBaseImponible()
                    .multiply(det.getPorcentaje().divide(new BigDecimal(100)));
            det.setValorRetenido(valorRetenido);
        }
    }

    public void agregarDetalle() {
        DetalleRetencion det = getBeanRetencion().getDetalleNuevo();
        if (det.getCodigoRetencion() == null || det.getCodigoRetencion().isBlank()) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Atención", "Debe seleccionar un código SRI de retención.");
            return;
        }
        if (det.getValorRetenido() == null || det.getValorRetenido().compareTo(BigDecimal.ZERO) <= 0) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Atención", "El valor retenido debe ser mayor a 0.");
            return;
        }
        getBeanRetencion().getNuevaRetencion().getDetalles().add(det);
        prepararNuevoDetalle();
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
}
