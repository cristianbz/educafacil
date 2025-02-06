/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
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
@Getter
@Setter
public class BeanEgresos implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private List<Catalogo> listaTipoEgreso;
	private List<Proveedor> listaProveedores;
	private Catalogo egresoSeleccionado;
	private Proveedor proveedorSeleccionado;
	private List<Egresos> listaEgresosRegistrados;
	private Egresos egreso;
	private Proveedor nuevoProveedor;
	private Date fechaInicial;
	private Date fechaFinal;

}
