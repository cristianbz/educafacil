package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
@Getter
@Setter
public class BeanRetencion implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Retencion> listaRetenciones;
    private Retencion retencionSeleccionada;
    private Retencion nuevaRetencion;
    private Egresos egresoSustento;
    private DetalleRetencion detalleNuevo;

    /** Catálogo de códigos SRI filtrado por tipo de impuesto seleccionado. */
    private List<CodigoSriRetencion> listaCodigosSri = new ArrayList<>();

    /** Objeto CodigoSriRetencion seleccionado en el autoComplete. */
    private CodigoSriRetencion codigoSriSeleccionado;

    // Filtros de búsqueda
    private java.util.Date fechaInicio;
    private java.util.Date fechaFin;

    public BeanRetencion() {
    }
}
