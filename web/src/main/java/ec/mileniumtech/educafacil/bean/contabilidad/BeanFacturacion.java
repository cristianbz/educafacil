package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean para almacenar el estado de la pantalla de facturación.
 */
@Named("beanFacturacion")
@ViewScoped
@Getter
@Setter
public class BeanFacturacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Factura> listaFacturas;
    private Factura facturaSeleccionada;
    private String filtroNumero;
    private String filtroCliente;

    // Campos para Nueva Factura
    private Factura nuevaFactura;
    private Cliente clienteSeleccionado;
    private List<Cliente> listaClientesBusqueda;
    private String identificacionBusqueda;
    
    private List<CatalogoItem> listaItems;
    private CatalogoItem itemSeleccionado;
    private List<DetalleFactura> listaDetallesNueva;
    private DetalleFactura detalleNuevo;
    private boolean mostrarFormularioNuevoCliente;
    private CatalogoItem nuevoItem;

    @jakarta.annotation.PostConstruct
    public void init() {
        listaFacturas = new java.util.ArrayList<>();
        listaDetallesNueva = new java.util.ArrayList<>();
        listaItems = new java.util.ArrayList<>();
        nuevaFactura = new ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura();
        nuevaFactura.setTotal(java.math.BigDecimal.ZERO);
        detalleNuevo = new ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura();
        clienteSeleccionado = new ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente();
        nuevoItem = new ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem();
    }
}
