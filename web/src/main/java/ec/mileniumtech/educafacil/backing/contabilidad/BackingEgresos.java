/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanEgresos;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Proveedor;
import ec.mileniumtech.educafacil.service.ContabilidadDataService;
import ec.mileniumtech.educafacil.service.SistemaDataService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosEgresos;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BackingEgresos implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(BackingEgresos.class);

	@EJB
	@Getter
	private SistemaDataService sistemaDataService;

	@EJB
	@Getter
	private ContabilidadDataService contabilidadDataService;

	@Inject
	@Getter
	private BeanEgresos beanEgresos;

	@Inject
	@Getter
	private BeanLogin beanLogin;

	@Inject
	@Getter
	private MensajesBacking mensajesBacking;

	@PostConstruct
	public void init() {
	
			getBeanEgresos().setListaProveedores(new ArrayList<>());
			getBeanEgresos().setListaTipoEgreso(new ArrayList<>());
			getBeanEgresos().setListaTipoEgreso(sistemaDataService.catalogosPorTipo("TPEGR"));
			getBeanEgresos().setListaEgresosRegistrados(new ArrayList<>());
			getBeanEgresos().setListaEgresosRegistrados(contabilidadDataService.listaEgresos());
			getBeanEgresos().setListaProveedores(contabilidadDataService.listaProveedores().stream().sorted((p1,p2)->p1.getProvNombre().compareTo(p2.getProvNombre())).collect(Collectors.toList()));
			
			getBeanEgresos().setNuevoProveedor(new Proveedor());


	}

	public void nuevoRegistroEgreso() {
		getBeanEgresos().setEgreso(new Egresos());
		getBeanEgresos().getEgreso().setEgreFecha(new Date());
		Mensaje.verDialogo("dlgRegEgreso");
	}

	public void registrarEgreso() {
	
		if (Double.parseDouble(getBeanEgresos().getEgreso().getEgreValor().toString()) > 0.00) {
			getBeanEgresos().getEgreso().setCatalogo(getBeanEgresos().getEgresoSeleccionado());
			getBeanEgresos().getEgreso().setProveedor(getBeanEgresos().getProveedorSeleccionado());
			getBeanEgresos().getEgreso().setEgreFechaRegistro(new Date());
			getBeanEgresos().getEgreso().setEgreEstado(EnumEstadosEgresos.REGISTRADO.getCodigo());
			contabilidadDataService.agregarActualizarEgreso(getBeanEgresos().getEgreso());
			getBeanEgresos().setListaEgresosRegistrados(new ArrayList<>());
			getBeanEgresos().setListaEgresosRegistrados(contabilidadDataService.listaEgresos());
			Mensaje.ocultarDialogo("dlgRegEgreso");
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.informacion"));

		}

	}

	public void registrarProveedor() {
	
			if (contabilidadDataService.validaProveedor(getBeanEgresos().getNuevoProveedor().getProvRuc())==null) {
				contabilidadDataService.agregarActualizarProveedor(getBeanEgresos().getNuevoProveedor());
				getBeanEgresos().getListaProveedores().add(getBeanEgresos().getNuevoProveedor());
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregaProveedor"));
				Mensaje.ocultarDialogo("dlgRegProveedor");
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.proveedor"));
			}
	
	}

	public void nuevoProveedor() {
		getBeanEgresos().setNuevoProveedor(new Proveedor());
		Mensaje.verDialogo("dlgRegProveedor");
	}
	
	public void dialogoBuscaEgresos() {
		getBeanEgresos().setFechaInicial(null);
		getBeanEgresos().setFechaFinal(null);
		Mensaje.verDialogo("dlgBuscaEgresos");
	}
	
	public void buscarEgresos() {
	
			getBeanEgresos().setListaEgresosRegistrados(contabilidadDataService.listaEgresosFechas(getBeanEgresos().getFechaInicial(), getBeanEgresos().getFechaFinal()));
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.cargarInfo"));
			Mensaje.ocultarDialogo("dlgBuscaEgresos");

	}
}
