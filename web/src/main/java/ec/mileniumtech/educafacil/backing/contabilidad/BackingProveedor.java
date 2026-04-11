/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanProveedor;
import ec.mileniumtech.educafacil.dao.impl.ProveedorDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentacionProveedor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Proveedor;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BackingProveedor implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingProveedor.class);
	
	@Getter
	@Inject
	private BeanProveedor beanProveedor;
	
	@Getter
	@Inject
	private MensajesBacking mensajesBacking;
	
	@Getter
	@EJB
	private ProveedorDaoImpl proveedorServicioImpl;
	
	@Getter
	@Setter
	private boolean mostrarPanelNuevo;
	
	@PostConstruct
	public void init() {

			getBeanProveedor().setProveedor(new Proveedor());
			getBeanProveedor().setDocumentacionProveedor(new DocumentacionProveedor());
			getBeanProveedor().setListaProveedores(new ArrayList<>());
			getBeanProveedor().setListaProveedores(getProveedorServicioImpl().listaProveedores());
			setMostrarPanelNuevo(false);

	}
	
	/**
	 * Crea un nuevo proveedor
	 */
	public void nuevoProveedor() {
		setMostrarPanelNuevo(true);
		getBeanProveedor().setProveedor(new Proveedor());
		getBeanProveedor().setDocumentacionProveedor(new DocumentacionProveedor());
		}
	/**
	 * Muestra el cuadro dialogo grabar proveedor
	 */
	public void mostrarDialogoGrabarProveedor() {
		Mensaje.verDialogo("dlgGrabaProveedor");

	}
	/**
	 * Agrega / modifica un proveedor
	 */
	public void grabarProveedor() {

			List<DocumentacionProveedor> documentacion=new ArrayList<>();
			getBeanProveedor().getDocumentacionProveedor().setProveedor(getBeanProveedor().getProveedor());
			documentacion.add(getBeanProveedor().getDocumentacionProveedor());
			getBeanProveedor().getProveedor().setDocumentacionProveedors(documentacion);		
			getProveedorServicioImpl().agregarActualizarProveedor(getBeanProveedor().getProveedor());
			getBeanProveedor().setListaProveedores(new ArrayList<>());
			getBeanProveedor().setListaProveedores(getProveedorServicioImpl().listaProveedores());
			setMostrarPanelNuevo(false);

	}
	/**
	 * Edita un proveedor
	 */
	public void editarProveedor() {
		setMostrarPanelNuevo(true);
		getBeanProveedor().setDocumentacionProveedor(getBeanProveedor().getProveedor().getDocumentacionProveedors().get(0));
	}
}
