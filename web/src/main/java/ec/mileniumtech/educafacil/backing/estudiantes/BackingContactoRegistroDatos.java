/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;



import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanContactoRegistroDatos;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.SeguimientoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Seguimiento;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumRol;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumTipoCatalogo;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * @author [ Christian Baez ] cbaez Sep 17, 2020
 *
 */
@Named
@ViewScoped
public class BackingContactoRegistroDatos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingContactoRegistroDatos.class);
	@Inject
	@Getter
	private BeanContactoRegistroDatos beanContactoRegistroDatos;
	@Getter
	@Inject
	private MensajesBacking mensajesBacking;
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicioImpl;
	
	@EJB
	@Getter
	private SeguimientoDaoImpl seguimientoServicioImpl;
	
	@EJB
	@Getter
	private CatalogoDaoImpl catalogoServicioImpl;
	
	@EJB
	@Getter
	private UsuarioDaoImpl usuarioServicioImpl;
	
	
	/**
	 * Carga las oportunidades de ventas
	 */
	public void cargarOportunidades() {
		try {
			getBeanContactoRegistroDatos().setLitaOportunidades(new ArrayList<>());
			getBeanContactoRegistroDatos().setLitaOportunidades(getMatriculaServicioImpl().listaOportunidades());
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarOportunidades"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarOportunidades " + ": ").append(e.getMessage()));
		}
	}
	
	
	@PostConstruct
	public void init() {
		cargarOportunidades();
		getBeanContactoRegistroDatos().setMostrarClientes(true);
		getBeanContactoRegistroDatos().setListaTipoSeguimiento(new ArrayList<>());
		try {
			getBeanContactoRegistroDatos().setListaVendedores(getUsuarioServicioImpl().consultarUsuariosPorIdRol(EnumRol.ADMINISTRADOR.getCodigo()));			
			getBeanContactoRegistroDatos().setListaTipoSeguimiento(getCatalogoServicioImpl().catalogosPorTipo(EnumTipoCatalogo.TIPOSEGUIMIENTO.getNemotecnico()));
		} catch (DaoException e) { 
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.catalogo"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "init " + ": ").append(e.getMessage()));
		}
	}

	/**
	 * Muestra el cuadro de dialogo de grabar
	 */
	public void mostrarListaTareas() {
		getBeanContactoRegistroDatos().setActividades(true);
		getBeanContactoRegistroDatos().setMostrarClientes(false);
		cargarActividadesClientePotencial();
	}
	/**
	 * Cancelar 
	 */
	public void cancelar() {
		
		getBeanContactoRegistroDatos().setOportunidadSeleccionado(null);
	}
	/**
	 * Muestra el dialogo de asignacion de actividades
	 */
	public void mostrarDialogoGrabar() {
		Mensaje.verDialogo("dlggrabar");								
	}
	/**
	 * Graba una tarea de seguimiento a un cliente potencial
	 */
	public void asignarTarea() {
		try {
			getBeanContactoRegistroDatos().getSeguimiento().setSegmCulminado(false);
			getBeanContactoRegistroDatos().getSeguimiento().setSegmFechaRegistro(new Date());
			getBeanContactoRegistroDatos().getSeguimiento().setSegmTipoTarea(getBeanContactoRegistroDatos().getCodigoTarea());
			getBeanContactoRegistroDatos().getSeguimiento().setMatricula(getBeanContactoRegistroDatos().getOportunidadSeleccionado());
			getSeguimientoServicioImpl().agregarActualizarSeguimiento(getBeanContactoRegistroDatos().getSeguimiento());
			getBeanContactoRegistroDatos().setSeguimiento(new Seguimiento());
			getBeanContactoRegistroDatos().setCodigoTarea("");
			getBeanContactoRegistroDatos().setCodigoUsuario(0);
			cargarActividadesClientePotencial();
		}catch(DaoException e) { 
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabarseguimiento"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "asignarTarea " + ": ").append(e.getMessage()));
		} catch (EntidadDuplicadaException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.entidadDuplicada"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "asignarTarea " + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Carga las actividades del cliente potencial seleccionado
	 */
	public void cargarActividadesClientePotencial() {
		try {
			getBeanContactoRegistroDatos().setListadoSeguimiento(new ArrayList<>());
			getBeanContactoRegistroDatos().setListadoSeguimiento(getSeguimientoServicioImpl().listaSeguimientoMatricula(getBeanContactoRegistroDatos().getOportunidadSeleccionado().getMatrId()));
			getBeanContactoRegistroDatos().setListadoSeguimiento(getBeanContactoRegistroDatos().getListadoSeguimiento().stream().sorted((s1,s2)->s2.getSegmFechaEjecucionTarea().compareTo(s1.getSegmFechaEjecucionTarea())).collect(Collectors.toList()));
		}catch(DaoException e) { 
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargaractividades"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarActividadesClientePotencial " + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Actualiza un seguimiento del cliente potencial
	 */
	public void actualizarSeguimientoClientePotencial() {
		try {
			getSeguimientoServicioImpl().agregarActualizarSeguimiento(getBeanContactoRegistroDatos().getSeguimiento());
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.procesoexito"));	
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.actualizar"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "actualizarSeguimientoClientePotencial " + ": ").append(e.getMessage()));
		}catch(EntidadDuplicadaException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.entidadDuplicada"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "actualizarSeguimientoClientePotencial " + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Cancela el ingreso de nuevos seguimientos al cliente
	 */
	public void cancelarIngresoSeguimiento() {
		getBeanContactoRegistroDatos().setListadoSeguimiento(new ArrayList<>());
		getBeanContactoRegistroDatos().setOportunidadSeleccionado(new Matricula());
		getBeanContactoRegistroDatos().setSeguimiento(new Seguimiento());
		getBeanContactoRegistroDatos().setCodigoTarea("");
		getBeanContactoRegistroDatos().setCodigoUsuario(0);		
		getBeanContactoRegistroDatos().setMostrarClientes(true);
		getBeanContactoRegistroDatos().setActividades(false);
	}
}
