/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de sesión para manejar los datos de la empresa en la vista.
 * 
 * @author christian
 *
 */
@Named
@ViewScoped
public class BeanEmpresa implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Getter
    @Setter
    private EmpresaMatriz empresa;
    
    @Getter
    @Setter
    private List<EmpresaMatriz> listaEmpresas;

    @PostConstruct
    public void init() {
        setEmpresa(new EmpresaMatriz());
    }
}
