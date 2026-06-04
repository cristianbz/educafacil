/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.backing.estudiantes.ComponenteBuscaEstudiante;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanPagos;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.service.ContabilidadDataService;
import ec.mileniumtech.educafacil.service.MatriculaDataService;
import ec.mileniumtech.educafacil.service.PagosService;
import ec.mileniumtech.educafacil.service.SistemaDataService;
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
import lombok.Setter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BackingPagos implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(BackingPagos.class);
	private JasperReport jasperReport;
	@Getter
	private StreamedContent fileDownload;
	@Inject
	@Getter
	private ComponenteBuscaEstudiante componenteBuscaEstudiante;

	@EJB
	@Getter
	private SistemaDataService sistemaDataService;

	@EJB
	@Getter
	private ContabilidadDataService contabilidadDataService;

	@EJB
	private PagosService pagosService;

	@EJB
	private AdministracionService administracionService;

	@Getter
	@Setter
	private String ticketEmpresa;
	@Getter
	@Setter
	private String ticketDireccion;
	@Getter
	@Setter
	private String ticketRuc;
	@Getter
	@Setter
	private String ticketCliente;
	@Getter
	@Setter
	private String ticketFecha;
	@Getter
	@Setter
	private String ticketNumero;
	@Getter
	@Setter
	private String ticketServicio;
	@Getter
	@Setter
	private String ticketFormaPago;
	@Getter
	@Setter
	private String ticketValor;
	@Getter
	@Setter
	private String ticketObservacion;

	@Getter
	@Setter
	private List<DetallePagos> ticketDetalles;

	@EJB
	@Getter
	private MatriculaDataService matriculaDataService;
	
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
			getBeanPagos().setListaServiciosPago(getSistemaDataService().catalogosPorTipo(EnumTipoCatalogo.TIPOPAGO.getNemotecnico()));
			getBeanPagos().setListaCursos(matriculaDataService.listaOfertaCursosActivos());
			InputStream reportStream = getClass().getResourceAsStream("/reports/pagosAlumnosCurso.jasper");
            jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
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
			getBeanPagos().getPago().setDetallePagos(getBeanPagos().getListaDetallePagos());
			getBeanPagos().getPago().setPagoUsuarioIngreso(getBeanLogin().getUsuario().getUsuaUsuario());
			
			// Usar el nuevo servicio que orquesta pago + facturación
			pagosService.registrarPagoYFacturar(getBeanPagos().getPago());
			
			getBeanPagos().setListaDetallePagosRealizados(new ArrayList<>());
			getBeanPagos().setListaDetallePagosRealizados(getContabilidadDataService().buscaPagosPorMatricula(getBeanPagos().getPago().getMatricula().getMatrId()));
			
			getBeanPagos().getMatricula().setTotalPagadoCurso(totalPagado + getBeanPagos().getPago().getDetallePagos().stream().mapToDouble(p -> p.getDepaValor()).sum());
			
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));
			
			// --- IMPRESION TICKET TERMICO AUTOMATICO ---
			if (getBeanPagos().getPago().getDetallePagos() != null && !getBeanPagos().getPago().getDetallePagos().isEmpty()) {
				imprimirTicketTermicoPorPago(getBeanPagos().getPago());
				Mensaje.actualizarComponente("form:ticketTermicoPanel");
				PrimeFaces.current().executeScript("imprimirTicket()");
			}
			// ------------------------------------------

			Mensaje.ocultarDialogo("dlgGrabar");
			Mensaje.ocultarDialogo("dlgRegistroPago");
			getBeanPagos().setListaDetallePagos(new ArrayList<>());
			getBeanPagos().setPago(new Pagos());
		} catch (Exception e) {
			log.error("Error al registrar pago y factura", e);
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error en facturación: " + e.getMessage());
		}
	}

	/**
	 * Prepara la informacion para imprimir un ticket en impresora termica.
	 * 
	 * @param detalle El detalle de pago a imprimir
	 */
	public void imprimirTicketTermico(DetallePagos detalle) {
		if (detalle != null) {
			imprimirTicketTermicoPorPago(detalle.getPagos());
		}
	}

	/**
	 * Prepara la informacion para imprimir un ticket en impresora termica por objeto Pago.
	 * 
	 * @param pago El objeto Pago a imprimir
	 */
	public void imprimirTicketTermicoPorPago(Pagos pago) {
		try {
			// 1. Obtener la empresa matriz activa
			List<EmpresaMatriz> empresas = administracionService.listarEmpresas();
			if (empresas != null && !empresas.isEmpty()) {
				EmpresaMatriz emp = empresas.get(0);
				this.ticketEmpresa = emp.getEmpmNombreComercial() != null ? emp.getEmpmNombreComercial() : emp.getEmpmRazonSocial();
				this.ticketDireccion = emp.getEmpmDireccion();
				this.ticketRuc = emp.getEmpmRuc();
			} else {
				this.ticketEmpresa = "EDUCACIONAL";
				this.ticketDireccion = "Dirección General";
				this.ticketRuc = "9999999999999";
			}

			// 2. Datos del pago y cliente
			if (pago != null) {
				this.ticketNumero = pago.getPagoNumeroFactura();
				if (pago.getPagoFecha() != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					this.ticketFecha = sdf.format(pago.getPagoFecha());
				} else {
					this.ticketFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
				}
				
				if (pago.getMatricula() != null && pago.getMatricula().getEstudiante() != null && pago.getMatricula().getEstudiante().getPersona() != null) {
					this.ticketCliente = pago.getMatricula().getEstudiante().getPersona().getPersApellidos() + " " + pago.getMatricula().getEstudiante().getPersona().getPersNombres();
				} else {
					this.ticketCliente = "Consumidor Final";
				}

				// 3. Obtener todos los detalles de este pago especifico
				if (getBeanPagos().getListaDetallePagosRealizados() != null && !getBeanPagos().getListaDetallePagosRealizados().isEmpty()) {
					this.ticketDetalles = getBeanPagos().getListaDetallePagosRealizados().stream()
							.filter(d -> d.getPagos() != null && d.getPagos().getPagoId() != null && d.getPagos().getPagoId().equals(pago.getPagoId()))
							.collect(Collectors.toList());
				}
				
				// Si la lista sigue vacia (por ejemplo, recien insertado en registrarPago), usamos los detalles adjuntos al pago
				if (this.ticketDetalles == null || this.ticketDetalles.isEmpty()) {
					this.ticketDetalles = pago.getDetallePagos();
				}
				
				// Calcular el total cancelado de todos los servicios incluidos en el pago
				double total = 0.0;
				if (this.ticketDetalles != null) {
					total = this.ticketDetalles.stream().mapToDouble(DetallePagos::getDepaValor).sum();
				}
				this.ticketValor = String.format(java.util.Locale.US, "%.2f", total);
				
			} else {
				this.ticketNumero = "";
				this.ticketFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
				this.ticketCliente = "Consumidor Final";
				this.ticketDetalles = new ArrayList<>();
				this.ticketValor = "0.00";
			}

		} catch (Exception e) {
			log.error("Error al preparar ticket de impresion", e);
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error al preparar ticket: " + e.getMessage());
		}
	}

	/**
	 * Formatea un valor decimal para su correcta presentacion en el ticket termico
	 */
	public String formatearValor(Double valor) {
		if (valor == null) {
			return "0.00";
		}
		return String.format(java.util.Locale.US, "%.2f", valor);
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
	
			getBeanPagos().setListaOfertaCursos(new ArrayList<>());
			getBeanPagos().setListaOfertaCursos(matriculaDataService.listaOfertaCursosActivos());
			getBeanPagos().setListaOfertaCursos(getBeanPagos().getListaOfertaCursos().stream().sorted((a1,a2) -> a1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(a2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));

	}
	
	public void buscarMatriculadosCurso() {
		
			getBeanPagos().setListaCursosMatriculados(new ArrayList<Matricula>());
			getBeanPagos().setListaCursosMatriculados(matriculaDataService.listaMatriculadosPorOfertaCurso(getBeanPagos().getCursoSeleccionado().getOcurId()));
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
			
	
	}
	public void mostrarDialogoRegPago() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		getBeanPagos().getPago().setPagoNumeroFactura(sdf.format(new Date()));
		Mensaje.verDialogo("dlgRegistroPago");
		
	}
	
	public void mostrarDialogoResumenPago() {
	
			getBeanPagos().setListaDetallePagosRealizados(new ArrayList<DetallePagos>());
			getBeanPagos().setListaDetallePagosRealizados(getContabilidadDataService().buscaPagosPorMatricula(getBeanPagos().getMatricula().getMatrId()));
			Mensaje.verDialogo("dlgresumenPagos");

	}
	
	public void cerrarDialogoGrabar() {
		Mensaje.ocultarDialogo("dlgRegistroPago");
		getBeanPagos().setMatricula(null);
	}
	
	public void cargarCursosActivosCerrados() {
		
			if(getBeanPagos().isCursosFinalizados())
				getBeanPagos().setListaOfertaCursos(matriculaDataService.listaOfertaCursosActivosCerrados());
			else {
				getBeanPagos().setListaOfertaCursos(matriculaDataService.listaOfertaCursosActivos());
				getBeanPagos().setListaOfertaCursos(getBeanPagos().getListaOfertaCursos().stream().sorted((a1,a2) -> a1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(a2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
			}
	
	}
	
	public void generarReporte() {
		Map<String, Object> params = new HashMap<>();
        params.put("parametroCurso", getBeanPagos().getNombreCurso());
        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(getBeanPagos().getListaCursosMatriculados());
        try {
            // Llena directamente con reporte compilado (sin compile)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            
            DefaultStreamedContent.Builder builder = DefaultStreamedContent.builder()
                    .stream(() -> new ByteArrayInputStream(baos.toByteArray()))
                    .contentType("application/pdf")
                    .name("reportePagos.pdf");
                fileDownload = builder.build();
        } catch (Exception e) {
        	log.error("Error al generar reporte de pagos", e);
			throw new RuntimeException(e);
        }
	}
}
