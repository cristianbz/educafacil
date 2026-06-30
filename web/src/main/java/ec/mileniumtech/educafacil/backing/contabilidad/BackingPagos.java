package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cuota;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.service.PagosService;
import ec.mileniumtech.educafacil.service.facade.ContabilidadFacade;
import ec.mileniumtech.educafacil.service.facade.InstructorFacade;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadoCuota;
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
    private InstructorFacade sistemaDataService;

    @EJB
    @Getter
    private ContabilidadFacade contabilidadDataService;

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
    private MatriculaFacade matriculaDataService;
    
    @Inject
    @Getter
    private BeanPagos beanPagos;

    @Inject
    @Getter
    private BeanLogin beanLogin;

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @Getter
    @Setter
    private Integer numeroCuotas;

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
        getBeanPagos().setListaCuotasSeleccionadas(new ArrayList<>());
        numeroCuotas = 3;
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

    public void agregarServicio() {
        Double valor = getBeanPagos().getDetallePagos().getDepaValor();
        if (getBeanPagos().getServicioSeleccionado() == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Seleccione un servicio");
            return;
        }
        if (valor != null && valor > 0.00) {
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
            double totalPagado = getBeanPagos().getMatricula().getTotalPagadoCurso(); 
            getBeanPagos().getPago().setPagoFecha(new Date());            
            getBeanPagos().getPago().setDetallePagos(getBeanPagos().getListaDetallePagos());
            getBeanPagos().getPago().setPagoUsuarioIngreso(getBeanLogin().getUsuario().getUsuaUsuario());

            pagosService.registrarPagoYFacturar(getBeanPagos().getPago());

            getBeanPagos().setListaDetallePagosRealizados(new ArrayList<>());
            getBeanPagos().setListaDetallePagosRealizados(getContabilidadDataService().buscaPagosPorMatricula(getBeanPagos().getPago().getMatricula().getMatrId()));

            getBeanPagos().getMatricula().setTotalPagadoCurso(totalPagado + getBeanPagos().getPago().getDetallePagos().stream().mapToDouble(p -> p.getDepaValor()).sum());

            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));

            if (getBeanPagos().getPago().getDetallePagos() != null && !getBeanPagos().getPago().getDetallePagos().isEmpty()) {
                imprimirTicketTermicoPorPago(getBeanPagos().getPago());
                Mensaje.actualizarComponente("form:ticketTermicoPanel");
                PrimeFaces.current().executeScript("imprimirTicket()");
            }

            Mensaje.ocultarDialogo("dlgGrabar");
            Mensaje.ocultarDialogo("dlgRegistroPago");
            getBeanPagos().setListaDetallePagos(new ArrayList<>());
            getBeanPagos().setPago(new Pagos());
        } catch (Exception e) {
            log.error("Error al registrar pago y factura", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error en facturación: " + e.getMessage());
        }
    }

    public void imprimirTicketTermico(DetallePagos detalle) {
        if (detalle != null) {
            imprimirTicketTermicoPorPago(detalle.getPagos());
        }
    }

    public void imprimirTicketTermicoPorPago(Pagos pago) {
        try {
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

                if (getBeanPagos().getListaDetallePagosRealizados() != null && !getBeanPagos().getListaDetallePagosRealizados().isEmpty()) {
                    this.ticketDetalles = getBeanPagos().getListaDetallePagosRealizados().stream()
                            .filter(d -> d.getPagos() != null && d.getPagos().getPagoId() != null && d.getPagos().getPagoId().equals(pago.getPagoId()))
                            .collect(Collectors.toList());
                }
                
                if (this.ticketDetalles == null || this.ticketDetalles.isEmpty()) {
                    this.ticketDetalles = pago.getDetallePagos();
                }
                
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

    public void pagarCuotasSeleccionadas() {
        try {
            List<Cuota> seleccionadas = getBeanPagos().getListaCuotasSeleccionadas();
            if (seleccionadas == null || seleccionadas.isEmpty()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Selección requerida", "Seleccione al menos una cuota para pagar");
                return;
            }
            
            // 1. Crear la cabecera del Pago para la transacción contable
            Pagos nuevoPago = new Pagos();
            nuevoPago.setPagoFecha(new Date());
            nuevoPago.setMatricula(getBeanPagos().getMatricula());
            nuevoPago.setPagoUsuarioIngreso(getBeanLogin().getUsuario().getUsuaUsuario());
            
            // Generar número secuencial temporal o de factura según tu estándar
            SimpleDateFormat sdfSecuencial = new SimpleDateFormat("yyyyMMddHHmmss");
            nuevoPago.setPagoNumeroFactura("CUO-" + sdfSecuencial.format(new Date()));
            
            List<DetallePagos> detallesParaTicket = new ArrayList<>();
            int cuotasProcesadas = 0;
            double totalPagadoTransaccion = 0.0;
            getBeanPagos().setServicioSeleccionado(
            	    getBeanPagos().getListaServiciosPago().stream()
            	        .filter(c -> "TPAGO02".equals(c.getCataCodigoIndice()))
            	        .findFirst()
            	        .orElse(null)
            	);
            for (Cuota cuota : seleccionadas) {
                if (!EnumEstadoCuota.PAGADO.getCodigo().equals(cuota.getCuoEstado())) {
                    
                    // 2. Crear el detalle del pago correspondiente a la cuota
                    DetallePagos det = new DetallePagos();
                    det.setPagos(nuevoPago);
                    det.setDepaFormaPago(getBeanPagos().getFormaPago() != null ? getBeanPagos().getFormaPago() : "FORMP01");
                    det.setDepaObservacion("Pago de Cuota N° " + cuota.getCuoNumero());
                    det.setDepaValor(cuota.getCuoValor());
                    det.setDepaEstado(true);
                    det.setDepaFechaInserto(new Date());
                    det.setDepaUsuarioInserto(getBeanLogin().getUsuario().getUsuaUsuario());
                    det.setCatalogo(getBeanPagos().getServicioSeleccionado());
                    
                    // Nota: Si det.setCatalogo exige un Objeto Catalogo, debes recuperarlo de tu lista de servicios, 
                    // aquí lo dejamos como null o mapeado según la lógica de negocio para evitar fallos de casteo.
                    detallesParaTicket.add(det);

                    // 3. Vincular cuota al detalle y actualizar estados
                    cuota.setCuoEstado(EnumEstadoCuota.PAGADO.getCodigo());
                    cuota.setCuoFechaPago(new Date());
                    cuota.setDetallePagos(det); 
                    
                    totalPagadoTransaccion += cuota.getCuoValor();
                    cuotasProcesadas++;
                }
            }
            
            if (cuotasProcesadas == 0) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Las cuotas seleccionadas ya se encontraban pagadas.");
                getBeanPagos().setListaCuotasSeleccionadas(new ArrayList<>());
                return;
            }

            // 4. Inyectar detalles al Objeto de pago principal y persistir vía Service transaccional
            nuevoPago.setDetallePagos(detallesParaTicket);
            pagosService.registrarPagoYFacturar(nuevoPago);

            // 5. Actualizar en cascada las cuotas mutadas en la base de datos
            for (Cuota cuota : seleccionadas) {
                if (EnumEstadoCuota.PAGADO.getCodigo().equals(cuota.getCuoEstado())) {
                    getContabilidadDataService().actualizarCuota(cuota);
                }
            }

            // 6. Actualizar el acumulado histórico en la matrícula del alumno
            double totalAnterior = getBeanPagos().getMatricula().getTotalPagadoCurso();
            getBeanPagos().getMatricula().setTotalPagadoCurso(totalAnterior + totalPagadoTransaccion);

            // 7. Disparar impresión de Ticket Térmico en caliente
            imprimirTicketTermicoPorPago(nuevoPago);
            Mensaje.actualizarComponente("form:ticketTermicoPanel");
            PrimeFaces.current().executeScript("imprimirTicket()");

            // Limpieza y refresco visual
            getBeanPagos().setListaCuotasSeleccionadas(new ArrayList<>());
            cargarCuotasEstudiante();
            
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, 
                getMensajesBacking().getPropiedad("info"), 
                cuotasProcesadas + " cuota(s) pagada(s) y facturada(s) correctamente");
                
        } catch (Exception e) {
            log.error("Error al pagar cuotas", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error al pagar cuotas: " + e.getMessage());
        }
    }

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

    public void cargarOfertaCursosActivos() {
            getBeanPagos().setListaOfertaCursos(new ArrayList<>());
            getBeanPagos().setListaOfertaCursos(matriculaDataService.listaOfertaCursosActivos());
            getBeanPagos().setListaOfertaCursos(getBeanPagos().getListaOfertaCursos().stream().sorted((a1,a2) -> a1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(a2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
    }
    
    public void buscarMatriculadosCurso() {
            getBeanPagos().setListaCursosMatriculados(new ArrayList<Matricula>());
            List<Matricula> matriculados = matriculaDataService.listaMatriculadosPorOfertaCurso(getBeanPagos().getCursoSeleccionado().getOcurId());
            if (matriculados != null) {
                for (Matricula mat : matriculados) {
                    List<Cuota> cuotas = getContabilidadDataService().listarCuotasPorMatricula(mat.getMatrId());
                    if (cuotas == null || cuotas.isEmpty()) {
                        generarCuotasAuto(mat);
                    }
                }
                getBeanPagos().setListaCursosMatriculados(matriculados);
            }
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

    private void generarCuotasAuto(Matricula matricula) {
        try {
            double valorCurso = matricula.getOfertaCursos().getOcurValor();
            double descuento = matricula.getOfertaCursos().getOcurDescuento();
            double valorTotal = valorCurso - descuento;
            if (valorTotal <= 0) valorTotal = valorCurso;

            int numCuotas = getNumeroCuotas() != null && getNumeroCuotas() > 0 ? getNumeroCuotas() : 3;
            double valorCuota = Math.round((valorTotal / numCuotas) * 100.0) / 100.0;

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            for (int i = 1; i <= numCuotas; i++) {
                Cuota cuota = new Cuota();
                cuota.setCuoNumero(i);
                cuota.setCuoValor(i == numCuotas ? valorTotal - (valorCuota * (numCuotas - 1)) : valorCuota);
                cuota.setCuoEstado(EnumEstadoCuota.PENDIENTE.getCodigo());
                cuota.setMatricula(matricula);

                if (i == 1) {
                    cuota.setCuoFechaLimite(cal.getTime());
                } else {
                    cal.add(Calendar.MONTH, 1);
                    cuota.setCuoFechaLimite(cal.getTime());
                }

                getContabilidadDataService().guardarCuota(cuota);
            }
        } catch (Exception e) {
            log.error("Error al generar cuotas automaticas", e);
        }
    }

    public void generarCuotasManual() {
        Matricula mat = getBeanPagos().getMatricula();
        if (mat == null || mat.getMatrId() == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Seleccione un estudiante primero");
            return;
        }
        generarCuotasAuto(mat);
        cargarCuotasEstudiante();
        Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), "Cuotas generadas correctamente");
    }

    public void mostrarDialogoRegPago() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        getBeanPagos().getPago().setPagoNumeroFactura(sdf.format(new Date()));
        Mensaje.verDialogo("dlgRegistroPago");
    }

    public void cargarCuotasPendientes() {
        if (getBeanPagos().getMatricula() != null && getBeanPagos().getMatricula().getMatrId() != null) {
            List<Cuota> cuotas = getContabilidadDataService().listarCuotasPendientesPorMatricula(getBeanPagos().getMatricula().getMatrId());
            getBeanPagos().setListaCuotas(cuotas);
        }
    }

    public void cargarCuotasEstudiante() {
        if (getBeanPagos().getMatricula() != null && getBeanPagos().getMatricula().getMatrId() != null) {
            List<Cuota> cuotas = getContabilidadDataService().listarCuotasPorMatricula(getBeanPagos().getMatricula().getMatrId());
            getBeanPagos().setListaCuotas(cuotas);
        }
    }
    
    public void mostrarDialogoResumenPago() {
            getBeanPagos().setListaDetallePagosRealizados(new ArrayList<DetallePagos>());
            getBeanPagos().setListaDetallePagosRealizados(getContabilidadDataService().buscaPagosPorMatricula(getBeanPagos().getMatricula().getMatrId()));
            Mensaje.verDialogo("dlgresumenPagos");
    }

    public void mostrarDialogoCuotas() {
        getBeanPagos().setListaCuotasSeleccionadas(new ArrayList<>());
        finalizarYActualizarCuotas();
        cargarCuotasEstudiante();
        Mensaje.verDialogo("dlgCuotas");
    }

    public void cerrarDialogoGrabar() {
        Mensaje.ocultarDialogo("dlgRegistroPago");
        getBeanPagos().setMatricula(null);
    }

    public void cerrarDialogoCuotas() {
        Mensaje.ocultarDialogo("dlgCuotas");
        getBeanPagos().setListaCuotas(null);
        getBeanPagos().setListaCuotasSeleccionadas(new ArrayList<>());
    }

    public void guardarCambiosCuotas() {
        try {
            List<Cuota> cuotas = getBeanPagos().getListaCuotas();
            if (cuotas != null) {
                for (Cuota c : cuotas) {
                    if (EnumEstadoCuota.PENDIENTE.getCodigo().equals(c.getCuoEstado()) || EnumEstadoCuota.VENCIDO.getCodigo().equals(c.getCuoEstado())) {
                        getContabilidadDataService().actualizarCuota(c);
                    }
                }
            }
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), "Cambios guardados correctamente");
        } catch (Exception e) {
            log.error("Error al guardar cambios en cuotas", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error al guardar cambios");
        }
    }

    public void eliminarCuota(Cuota cuota) {
        try {
            if (cuota == null || cuota.getCuoId() == null) return;
            if (EnumEstadoCuota.PAGADO.getCodigo().equals(cuota.getCuoEstado())) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar una cuota que ya tiene un pago registrado");
                return;
            }
            if (cuota.getDetallePagos() != null && cuota.getDetallePagos().getDepaId() != null) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar una cuota vinculada a un pago");
                return;
            }
            getContabilidadDataService().eliminarCuota(cuota);
            cargarCuotasEstudiante();
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), "Cuota eliminada correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar cuota", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error al eliminar cuota");
        }
    }
    
    public void cargarCursosActivosCerrados() {
            if(getBeanPagos().isCursosFinalizados())
                getBeanPagos().setListaOfertaCursos(matriculaDataService.listaOfertaCursosActivosCerrados());
            else {
                getBeanPagos().setListaOfertaCursos(matriculaDataService.listaOfertaCursosActivos());
                getBeanPagos().setListaOfertaCursos(getBeanPagos().getListaOfertaCursos().stream().sorted((a1,a2) -> a1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(a2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
            }
    }

    public String obtenerLabelEstadoCuota(String codigo) {
        if (codigo == null) return "";
        for (EnumEstadoCuota e : EnumEstadoCuota.listaValores()) {
            if (e.getCodigo().equals(codigo)) return e.getLabel();
        }
        return codigo;
    }

    public String obtenerStyleCuota(String codigo) {
        if (EnumEstadoCuota.PAGADO.getCodigo().equals(codigo)) return "color: green; font-weight: bold;";
        if (EnumEstadoCuota.VENCIDO.getCodigo().equals(codigo)) return "color: red; font-weight: bold;";
        return "color: orange; font-weight: bold;";
    }

    public long contarCuotasPendientes(Matricula mat) {
        if (mat == null || mat.getMatrId() == null) return 0;
        try {
            List<Cuota> cuotas = getContabilidadDataService().listarCuotasPendientesPorMatricula(mat.getMatrId());
            return cuotas != null ? cuotas.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public double getTotalLineasPago() {
        if (getBeanPagos().getListaDetallePagos() == null) return 0;
        return getBeanPagos().getListaDetallePagos().stream().mapToDouble(DetallePagos::getDepaValor).sum();
    }

    public double getTotalCuotasSeleccionadas() {
        if (getBeanPagos().getListaCuotasSeleccionadas() == null) return 0;
        return getBeanPagos().getListaCuotasSeleccionadas().stream().mapToDouble(Cuota::getCuoValor).sum();
    }
    
    public void generarReporte() {
        Map<String, Object> params = new HashMap<>();
        params.put("parametroCurso", getBeanPagos().getNombreCurso());
        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(getBeanPagos().getListaCursosMatriculados());
        try {
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

    public void finalizarYActualizarCuotas() {
        if (getBeanPagos().getListaCuotas() != null) {
            for (Cuota c : getBeanPagos().getListaCuotas()) {
                if (EnumEstadoCuota.PENDIENTE.getCodigo().equals(c.getCuoEstado()) && c.getCuoFechaLimite() != null && c.getCuoFechaLimite().before(new Date())) {
                    c.setCuoEstado(EnumEstadoCuota.VENCIDO.getCodigo());
                    getContabilidadDataService().actualizarCuota(c);
                }
            }
        }
    }
}