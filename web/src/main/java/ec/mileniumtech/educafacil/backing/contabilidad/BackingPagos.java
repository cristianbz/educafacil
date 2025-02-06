/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.backing.estudiantes.ComponenteBuscaEstudiante;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanPagos;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PagosDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumFormaPago;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumTipoCatalogo;
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
public class BackingPagos implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingPagos.class);

	@Inject
	@Getter
	private ComponenteBuscaEstudiante componenteBuscaEstudiante;

	@EJB
	@Getter
	private CatalogoDaoImpl catalogoServicio;

	@EJB
	@Getter
	private PagosDaoImpl pagosServicio;

	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaServicios;
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicios;
	
	@Inject
	@Getter
	private BeanPagos beanPagos;

	@Inject
	@Getter
	private BeanLogin beanLogin;

	@Inject
	@Getter
	private MensajesBacking mensajesBacking;

	@PostConstruct
	public void init() {
		getBeanPagos().setListaDetallePagos(new ArrayList<>());
		getBeanPagos().setPago(new Pagos());
		getBeanPagos().setDetallePagos(new DetallePagos());
		getBeanPagos().setFormaPago(EnumFormaPago.EFECTIVO.getCodigo());
		getComponenteBuscaEstudiante().getBeanBuscaEstudiante().setTipoBusqueda(2);
		getBeanPagos().setFechaRegistro(new Date());
		getBeanPagos().setMatricula(new Matricula());
		getBeanPagos().setCursosFinalizados(false);
		getBeanPagos().setCursoSeleccionado(new OfertaCursos());
		cargarOfertaCursosActivos();
		try {
			getBeanPagos().setListaServiciosPago(new ArrayList<>());
			getBeanPagos().setListaServiciosPago(getCatalogoServicio().catalogosPorTipo(EnumTipoCatalogo.TIPOPAGO.getNemotecnico()));
			getBeanPagos().setListaCursos(getOfertaServicios().listaOfertaCursosActivos());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Agrega un servicio a la factura
	 */
	public void agregarServicio() {
		if (Double.parseDouble(getBeanPagos().getDetallePagos().getDepaValor().toString()) > 0.00) {
			if(getBeanPagos().getMatricula().getMatrId()!=null) {
				getBeanPagos().getPago().setMatricula(getBeanPagos().getMatricula());
				getBeanPagos().getDetallePagos().setCatalogo(getBeanPagos().getServicioSeleccionado());
				getBeanPagos().getDetallePagos().setDepaFormaPago(getBeanPagos().getFormaPago());
				getBeanPagos().getDetallePagos().setPagos(getBeanPagos().getPago());
				getBeanPagos().getDetallePagos().setDepaEstado(true);
				getBeanPagos().getDetallePagos().setDepaFechaInserto(new Date());
				getBeanPagos().getDetallePagos().setDepaUsuarioInserto(getBeanLogin().getUsuario().getPersona().getPersApellidos().concat(getBeanLogin().getUsuario().getPersona().getPersNombres()));
				getBeanPagos().getListaDetallePagos().add(getBeanPagos().getDetallePagos());
				getBeanPagos().setDetallePagos(new DetallePagos());
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.selecionaEstudiante"));
			}
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.informacion"));
		}
	}
	public void quitarServicio() {
		if(getBeanPagos().getDetallePagosEliminar().getDepaId()==null) {
			getBeanPagos().getListaDetallePagos().remove(getBeanPagos().getDetallePagosEliminar());
		}
	}
	public void grabarPago() {
		try {
			double totalPagado= getBeanPagos().getMatricula().getTotalPagadoCurso(); 
			getBeanPagos().getPago().setPagoFecha(new Date());			
//			getBeanPagos().getPago().setMatricula(getBeanPagos().getListaCursosMatriculados().get(0));
			getBeanPagos().getPago().setDetallePagos(getBeanPagos().getListaDetallePagos());
			getBeanPagos().getPago().setPagoUsuarioIngreso(getBeanLogin().getUsuario().getUsuaUsuario());
			getPagosServicio().agregarPago(getBeanPagos().getPago());
			getBeanPagos().setListaDetallePagosRealizados(new ArrayList<>());
			getBeanPagos().setListaDetallePagosRealizados(getPagosServicio().buscaPagosPorMatricula(getBeanPagos().getPago().getMatricula().getMatrId()));
			
			getBeanPagos().getMatricula().setTotalPagadoCurso(totalPagado + getBeanPagos().getPago().getDetallePagos().stream().mapToDouble(p -> p.getDepaValor()).sum());
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));
			Mensaje.ocultarDialogo("dlgGrabar");
			Mensaje.ocultarDialogo("dlgRegistroPago");
			getBeanPagos().setListaDetallePagos(new ArrayList<>());
			getBeanPagos().setPago(new Pagos());
		}catch(Exception e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabar"));
			e.printStackTrace();
		}
	}
	public void mostrarDialogoGrabar() {
		if(getBeanPagos().getListaDetallePagos().size()>0 && getBeanPagos().getMatricula().getEstudiante()!=null) {
			Mensaje.verDialogo("dlgGrabar");
		}else
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.pagoEstudiante"));
	}
	
	public void mostrarSeleccionCurso() {
		getBeanPagos().setCursoSeleccionado(new OfertaCursos());
		Mensaje.verDialogo("dlgCursos");
	}
	/**
	 * Carga la oferta de cursos activos
	 */
	public void cargarOfertaCursosActivos() {
		try {
			
			getBeanPagos().setListaOfertaCursos(new ArrayList<>());
			getBeanPagos().setListaOfertaCursos(getOfertaServicios().listaOfertaCursosActivos());
			getBeanPagos().setListaOfertaCursos(getBeanPagos().getListaOfertaCursos().stream().sorted((a1,a2) -> a1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(a2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
		} catch (DaoException e) { 
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarcursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarOfertaCursosActivos" + ": ").append(e.getMessage()));
		}
	}
	
	public void buscarMatriculadosCurso() {
		try {
			getBeanPagos().setListaCursosMatriculados(new ArrayList<Matricula>());
			getBeanPagos().setListaCursosMatriculados(getMatriculaServicios().listaMatriculadosPorOfertaCurso(getBeanPagos().getCursoSeleccionado().getOcurId()));
			if(getBeanPagos().getCursoSeleccionado().getOcurHorario()!=null) {
				getBeanPagos().setNombreCurso(getBeanPagos().getCursoSeleccionado().getOfertaCapacitacion().getCurso().getCursNombre().concat("/").concat(getBeanPagos().getCursoSeleccionado().getOcurHorario()));
				getBeanPagos().setCursoSeleccionado(null);
				Mensaje.ocultarDialogo("dlgCursos");
			}else {
				if(getBeanPagos().getCursoSeleccionado().getOfertaCapacitacion()!=null) {
					getBeanPagos().setNombreCurso(getBeanPagos().getCursoSeleccionado().getOfertaCapacitacion().getCurso().getCursNombre());
					getBeanPagos().setCursoSeleccionado(null);
					Mensaje.ocultarDialogo("dlgCursos");
				}else 
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noExisteOfertaCursos"));
				
					
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void mostrarDialogoRegPago() {
		Mensaje.verDialogo("dlgRegistroPago");
		
	}
	
	public void mostrarDialogoResumenPago() {
		try {
			getBeanPagos().setListaDetallePagosRealizados(new ArrayList<DetallePagos>());
			getBeanPagos().setListaDetallePagosRealizados(getPagosServicio().buscaPagosPorMatricula(getBeanPagos().getMatricula().getMatrId()));
			Mensaje.verDialogo("dlgresumenPagos");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cerrarDialogoGrabar() {
		Mensaje.ocultarDialogo("dlgRegistroPago");
		getBeanPagos().setMatricula(null);
	}
	
	public void cargarCursosActivosCerrados() {
		try {
			if(getBeanPagos().isCursosFinalizados())
				getBeanPagos().setListaOfertaCursos(getOfertaServicios().listaOfertaCursosActivosCerrados());
			else {
				getBeanPagos().setListaOfertaCursos(getOfertaServicios().listaOfertaCursosActivos());
				getBeanPagos().setListaOfertaCursos(getBeanPagos().getListaOfertaCursos().stream().sorted((a1,a2) -> a1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(a2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
