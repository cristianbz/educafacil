/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de sesión para manejar el listado de facturas y su estado SRI.
 * 
 * @author christian
 *
 */
@Named
@ViewScoped
public class BeanFacturacion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Getter
    @Setter
    private List<Pagos> listaPagos;
    
    @Getter
    @Setter
    private Pagos pagoSeleccionado;

    @PostConstruct
    public void init() {
        // Inicialización si es necesaria
    }
}
