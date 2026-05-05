package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean para almacenar el estado del reporte de facturas.
 */
@Named("beanReporteFacturas")
@ViewScoped
@Getter
@Setter
public class BeanReporteFacturas implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String identificacion;
    private String comprobanteTipo; // "Factura" por defecto en la imagen
    private String numeroAutorizacion;
    
    private List<Factura> listaFacturas;
    
    @PostConstruct
    public void init() {
        fechaInicio = LocalDate.now();
        fechaFin = LocalDate.now();
        comprobanteTipo = "Factura";
        listaFacturas = new ArrayList<>();
    }
}
