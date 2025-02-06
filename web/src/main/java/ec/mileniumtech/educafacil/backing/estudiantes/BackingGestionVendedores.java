/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanGestionVendedores;
import ec.mileniumtech.educafacil.dao.impl.CampaniaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.SeguimientoClientesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.VendedorDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.SeguimientoClientes;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Vendedor;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosContactoCliente;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [Christian Báez] 14 feb. 2024
 *
 */
@ViewScoped
@Named
public class BackingGestionVendedores implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingGestionVendedores.class);
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@Getter
	@Inject
	private BeanGestionVendedores beanGestionVendedores;
	
	@Getter
	@Inject
	private CampaniaDaoImpl campaniaServicioImpl;
	
	@Getter
	@Inject
	private VendedorDaoImpl vendedorServicioImpl;
	
	@Getter
	@Inject
	private SeguimientoClientesDaoImpl seguimientoClientesServicioImpl;
	
	@Setter
	@Getter
	private boolean mostrarPanelReportesVentas;
	
	@Setter
	@Getter
	private boolean mostrarPanelVendedorCliente;
	
	@Setter
	@Getter
	private boolean mostrarPanelTotalSeguimiento;
	
	@Setter
	@Getter
	private boolean mostrarPanelTotalAbandonado;
	
	@Setter
	@Getter
	private boolean mostrarPanelTotalMatriculado;
	
	@Setter
	@Getter
	private boolean mostrarPanelTotalCandidato;
	
	@Setter
	@Getter
	private boolean mostrarPanelTotalProximaOcasion;
	
	@Setter
	@Getter
	private boolean mostrarTituloPanel;
	
	@PostConstruct
	public void init () {
		try {
			mostrarPanelReportesVentas=false;
			mostrarPanelVendedorCliente=true;
			mostrarPanelTotalSeguimiento=false;
			mostrarPanelTotalAbandonado=false;
			mostrarPanelTotalMatriculado=false;
			mostrarPanelTotalCandidato=false;
			mostrarPanelTotalProximaOcasion=false;
			mostrarTituloPanel=false;
			getBeanGestionVendedores().setListaCursos(new ArrayList<>());
			getBeanGestionVendedores().setListaVendedores(new ArrayList<>());
			getBeanGestionVendedores().setListaCursos(getCampaniaServicioImpl().listaCampaniasporCurso());
			getBeanGestionVendedores().setListaVendedores(getVendedorServicioImpl().listaDeVendedores());
			getBeanGestionVendedores().setListaSeguimientoClientes(new ArrayList<>());
			getBeanGestionVendedores().setListaSeguimientoClientes(getSeguimientoClientesServicioImpl().listaSeguimientoVendedorAsignado());
			getBeanGestionVendedores().setListaCursosReporte(new ArrayList<>());
			getBeanGestionVendedores().setListaCursosReporte(getCampaniaServicioImpl().listaCampaniasporCurso());
			getBeanGestionVendedores().setListaSeguimientoClientesEstado(new ArrayList<>());
			getBeanGestionVendedores().setListaSeguimientoClientesEstado(getSeguimientoClientesServicioImpl().listaSeguimiento());
			getBeanGestionVendedores().setListaVendedoresReporte(new ArrayList<>());
			getBeanGestionVendedores().setListaVendedoresReporte(getVendedorServicioImpl().listaDeVendedores());;
			getBeanGestionVendedores().setVendedorSeleccionado(new Vendedor());
			getBeanGestionVendedores().setBarModel(new BarChartModel());
			getBeanGestionVendedores().setChartDataVendedores(new ChartData());
			cargarCampaniasPasadas();
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	public void asignarVendedor() {
		try {
			mostrarPanelReportesVentas=false;
			mostrarPanelVendedorCliente=true;
			mostrarPanelTotalSeguimiento=false;
			mostrarPanelTotalAbandonado=false;
			mostrarPanelTotalMatriculado=false;
			mostrarPanelTotalCandidato=false;
			mostrarPanelTotalProximaOcasion=false;
			mostrarTituloPanel=false;
			getBeanGestionVendedores().setListaVendedores(new ArrayList<>());
			if(getBeanGestionVendedores().getListaCursos().isEmpty()) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregarDatosCampanias"));
			}else if (getBeanGestionVendedores().getListaVendedores().isEmpty()){
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregarVendedor"));
			}else {
				getBeanGestionVendedores().setVendedorSeleccionado(new Vendedor());
				Mensaje.verDialogo("dlgAsignarVendedor");
				if(getBeanGestionVendedores().getListaCursos().size()==1) {
					getBeanGestionVendedores().setCursoSeleccionado(getBeanGestionVendedores().getListaCursos().get(0));
					getBeanGestionVendedores().setListaSeguimientoClientesSinVendedores(new ArrayList<>());
					getBeanGestionVendedores().setListaSeguimientoClientesSinVendedores((getSeguimientoClientesServicioImpl().listaSeguimientoCampaniaVendedor(getBeanGestionVendedores().getCursoSeleccionado().getCampId())));
					
				}
				
			}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cargarClientesPotenciales() {
		try {
			getBeanGestionVendedores().setListaSeguimientoClientesSinVendedores(new ArrayList<>());
			getBeanGestionVendedores().setListaSeguimientoClientesSinVendedores((getSeguimientoClientesServicioImpl().listaSeguimientoCampaniaVendedor(getBeanGestionVendedores().getCursoSeleccionado().getCampId())));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void guardarAsignacionVendedor() {
		try {
			if(getBeanGestionVendedores().getListaSeguimientoClientesSelect().isEmpty()) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.vendedorCliente"));
			}else {
				for (SeguimientoClientes seClientes : getBeanGestionVendedores().getListaSeguimientoClientesSelect()) {
					seClientes.setVendedor(getBeanGestionVendedores().getVendedorSeleccionado());
					getSeguimientoClientesServicioImpl().actualizarSeguimiento(seClientes);
				}
				Mensaje.ocultarDialogo("dlgAsignarVendedor");
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
				getBeanGestionVendedores().setListaSeguimientoClientes(new ArrayList<>());
				getBeanGestionVendedores().setListaSeguimientoClientes(getSeguimientoClientesServicioImpl().listaSeguimientoVendedorAsignado());
			}
			}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void consultarVentas() {
		mostrarPanelReportesVentas=false;
		mostrarPanelVendedorCliente=false;
		mostrarPanelTotalSeguimiento=false;
		mostrarPanelTotalAbandonado=false;
		mostrarPanelTotalMatriculado=false;
		mostrarPanelTotalCandidato=false;
		mostrarPanelTotalProximaOcasion=false;
		mostrarTituloPanel=false;
		getBeanGestionVendedores().setCampaniasFinalizadas(false);
		getBeanGestionVendedores().setEstadosSeleccionados(null);
		getBeanGestionVendedores().setListaCursosReporte(new ArrayList<>());
		getBeanGestionVendedores().setEstadosSeleccionados(null);
		cargarCampaniasPasadas();
		Mensaje.verDialogo("dlgConsultarVentas");
	}
	
	public void consultarVentasVendedores() {
		try {

			mostrarPanelReportesVentas=true;
			int codigoVend = getBeanGestionVendedores().getVendedorSeleccionadoReporte().getVendId();
			int codigoCamp = getBeanGestionVendedores().getCursoSeleccionadoReporte().getCampId();
			for (String estado : getBeanGestionVendedores().getEstadosSeleccionados()) {
				if(estado.equals(EnumEstadosContactoCliente.ENSEGUIMIENTO.getCodigo())) {
					getBeanGestionVendedores().setTotalSeguimiento(getSeguimientoClientesServicioImpl().totalDatosCRMVendedor(estado, codigoVend, codigoCamp));
					mostrarPanelTotalSeguimiento=true;
					mostrarTituloPanel=true;
				}else if(estado.equals(EnumEstadosContactoCliente.ABANDONADO.getCodigo())) {
					getBeanGestionVendedores().setTotalAbandonado(getSeguimientoClientesServicioImpl().totalDatosCRMVendedor(estado, codigoVend, codigoCamp));
					mostrarPanelTotalAbandonado=true;
					mostrarTituloPanel=true;
				}else if(estado.equals(EnumEstadosContactoCliente.MATRICULADO.getCodigo())) {
					getBeanGestionVendedores().setTotalMatriculado(getSeguimientoClientesServicioImpl().totalDatosCRMVendedor(estado, codigoVend, codigoCamp));
					mostrarPanelTotalMatriculado=true;
					mostrarTituloPanel=true;
				}else if(estado.equals(EnumEstadosContactoCliente.CANDIDATO.getCodigo())) {
					getBeanGestionVendedores().setTotalCandidato(getSeguimientoClientesServicioImpl().totalDatosCRMVendedor(estado, codigoVend, codigoCamp));
					mostrarPanelTotalCandidato=true;
					mostrarTituloPanel=true;
				}else {
					getBeanGestionVendedores().setTotalProximaOcasion(getSeguimientoClientesServicioImpl().totalDatosCRMVendedor(estado, codigoVend, codigoCamp));
					mostrarPanelTotalProximaOcasion=true;
					mostrarTituloPanel=true;
				}
			}
			graficoBarrasTotalVendedores();
		Mensaje.ocultarDialogo("dlgConsultarVentas");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cargarCampaniasPasadas() {
		try {

			if(getBeanGestionVendedores().isCampaniasFinalizadas()) {
				getBeanGestionVendedores().setListaCursosReporte(getCampaniaServicioImpl().listaTodasCampanias());
			}else {
				getBeanGestionVendedores().setListaCursosReporte(getCampaniaServicioImpl().listaCampaniasporCurso());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void graficoBarrasTotalVendedores() {
	    try {
	    	getBeanGestionVendedores().setBarCharDataSetVendedores(new BarChartDataSet());
	    	getBeanGestionVendedores().setChartDataVendedores(new ChartData());
    		getBeanGestionVendedores().getBarCharDataSetVendedores().setLabel("Matriculas");
    		List<String> backgroundColor1 = new ArrayList<>();
    		List<String> borderColor1 = new ArrayList<>();
    		
    		Map<String, BigDecimal> ventasPorVendedor = new HashMap<>();
	        int cursoReporte = getBeanGestionVendedores().getCursoSeleccionadoReporte().getCampId();
	        String codigoEstado = EnumEstadosContactoCliente.MATRICULADO.getCodigo();
	        for (Vendedor vendedor : getBeanGestionVendedores().getListaVendedoresReporte()) {
	            int vendedorReporte = vendedor.getVendId();
	            BigDecimal totalVentas = getSeguimientoClientesServicioImpl().totalDatosCRMVendedor(codigoEstado, vendedorReporte, cursoReporte);
	            String nombreVendedor = vendedor.getPersona().getPersNombres().concat(" ").concat(vendedor.getPersona().getPersApellidos()); 
	            ventasPorVendedor.put(nombreVendedor, totalVentas);
	            for (Vendedor vendedorColor : getBeanGestionVendedores().getListaVendedoresReporte()) {

		            int upper = 200;
		            int lower = 20;
		            int red1 = (int) (Math.random() * (upper - lower)) + lower;
		            int green1 = (int) (Math.random() * (upper - lower)) + lower;
		            int blue1 = (int) (Math.random() * (upper - lower)) + lower;
		    		backgroundColor1.add("rgba(" + red1 + ", " + green1 + ", " + blue1 + ", 0.7)"); 
		    	
		    		borderColor1.add("rgba(" + red1 + ", " + green1 + ", " + blue1 + ", 1.0)");
		            }
	        } 		
	        
	        getBeanGestionVendedores().getBarCharDataSetVendedores().setBackgroundColor(backgroundColor1);
    		getBeanGestionVendedores().getBarCharDataSetVendedores().setBorderColor(borderColor1);
    		getBeanGestionVendedores().getBarCharDataSetVendedores().setBorderWidth(1);
	        List<Number> valorVentas = new ArrayList<>();
	        List<String> vendedor = new ArrayList<>();


	        for (Map.Entry<String, BigDecimal> datos : ventasPorVendedor.entrySet()) {
	            String clave = datos.getKey();
	            BigDecimal valor = datos.getValue();
	            valorVentas.add(valor.doubleValue());
	            vendedor.add(clave);
	        }
	        getBeanGestionVendedores().getBarCharDataSetVendedores().setData(valorVentas);  
	        getBeanGestionVendedores().getChartDataVendedores().addChartDataSet(getBeanGestionVendedores().getBarCharDataSetVendedores());
	        
	     
	        getBeanGestionVendedores().getChartDataVendedores().setLabels(vendedor);
	        getBeanGestionVendedores().getBarModel().setData(getBeanGestionVendedores().getChartDataVendedores());
	        
	        BarChartOptions options = new BarChartOptions();
	        CartesianScales cScales = new CartesianScales();
	        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
	        linearAxes.setOffset(true);
//	        linearAxes.setBeginAtZero(true);
	        CartesianLinearTicks ticks = new CartesianLinearTicks();
	        linearAxes.setTicks(ticks);
	        cScales.addYAxesData(linearAxes);
	        options.setScales(cScales);
	
	        Title title = new Title();
	        title.setDisplay(true);
	        title.setText("Total Matriculas por Vendedor");
	        options.setTitle(title);
		    getBeanGestionVendedores().getBarModel().setOptions(options);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
}
