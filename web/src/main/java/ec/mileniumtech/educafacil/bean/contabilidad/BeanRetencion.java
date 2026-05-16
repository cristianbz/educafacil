package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.util.List;

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
    
    // Filtros de búsqueda
    private java.util.Date fechaInicio;
    private java.util.Date fechaFin;

    public BeanRetencion() {
    }
}
