/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentacionProveedor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Proveedor;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BeanProveedor implements Serializable {
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private List<Proveedor> listaProveedores;
	
	@Getter
	@Setter
	private Proveedor proveedor;
	
	@Getter
	@Setter
	private DocumentacionProveedor documentacionProveedor;

}
