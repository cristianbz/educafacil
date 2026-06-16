/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.optionconfig.title.Title;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanAdminCampanias;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.service.facade.MarketingFacade;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosContactoCliente;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
@ViewScoped
@Named
public class BackingAdminCampanias implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(BackingAdminCampanias.class);
	
	@EJB
	@Getter
	private MarketingFacade marketingDataService;

	@EJB
	@Getter
	private MatriculaFacade matriculaDataService;
	
	@Inject
	@Getter
	private BeanAdminCampanias beanAdminCampanias;
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking; 
	
	@PostConstruct
	public void init() {
	
			getBeanAdminCampanias().setListaCampanias(new ArrayList<>());
			getBeanAdminCampanias().setListaCampanias(marketingDataService.listaTodasCampanias());
			getBeanAdminCampanias().setModelGrafico(new HorizontalBarChartModel());
			getBeanAdminCampanias().getModelGrafico().setOptions(new BarChartOptions());
			getBeanAdminCampanias().setMostrarGrafica(true);
			getBeanAdminCampanias().setListaCursos(new ArrayList<>());
			getBeanAdminCampanias().setListaCursos(matriculaDataService.listaCursos());
			getBeanAdminCampanias().setCampaniaSeleccionada(new Campania());			

	}
	
	public void mostrarDialogoCampania() {
		if(getBeanAdminCampanias().getCampaniaSeleccionada().getCurso()==null)
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.campaniaNoEditable"));
		else {
			getBeanAdminCampanias().setCodigoTipo(getBeanAdminCampanias().getCampaniaSeleccionada().getCampTipoContenido());
			getBeanAdminCampanias().setCursoSeleccionado(getBeanAdminCampanias().getCampaniaSeleccionada().getCurso().getCursId());
		}
		
		Mensaje.verDialogo("dlgCampania");
		
	}
	
	public void grabar() {
	
			Curso curso = new Curso();
			curso.setCursId(getBeanAdminCampanias().getCursoSeleccionado());
			getBeanAdminCampanias().getCampaniaSeleccionada().setCurso(curso);
			getBeanAdminCampanias().getCampaniaSeleccionada().setCampTipoContenido(getBeanAdminCampanias().getCodigoTipo());
			marketingDataService.agregarActualizarCampania(getBeanAdminCampanias().getCampaniaSeleccionada());
			getBeanAdminCampanias().setListaCampanias(new ArrayList<>());
			getBeanAdminCampanias().setListaCampanias(marketingDataService.listaTodasCampanias());
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));	
			Mensaje.ocultarDialogo("dlgCampania");
	
	}
	
	public void nuevaCampania() {
		getBeanAdminCampanias().setCodigoTipo("");
		getBeanAdminCampanias().setCampaniaSeleccionada(new Campania());
		Mensaje.verDialogo("dlgCampania");
	}
	
	public void resumenCampania() {
		
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String f1 = simpleDateFormat.format(getBeanAdminCampanias().getCampaniaSeleccionada().getCampFechaDesde());
			String f2 =simpleDateFormat.format(getBeanAdminCampanias().getCampaniaSeleccionada().getCampFechaHasta());
			String fechas =f1.concat(" al ").concat(f2);
			getBeanAdminCampanias().setMostrarGrafica(true);
			getBeanAdminCampanias().setProspectos(marketingDataService.alcanceCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId()));
			getBeanAdminCampanias().setProspectosSeguimiento(marketingDataService.prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.ENSEGUIMIENTO.getCodigo()));
			getBeanAdminCampanias().setProspectosAbandonados(marketingDataService.prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.ABANDONADO.getCodigo()));
			getBeanAdminCampanias().setProspectosMatriculados(marketingDataService.prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.MATRICULADO.getCodigo()));
			getBeanAdminCampanias().setProspectosCandidatos(marketingDataService.prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.CANDIDATO.getCodigo()));
			getBeanAdminCampanias().setProspectosProximaOcasion(marketingDataService.prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.PROXIMAOCASION.getCodigo()));
			
			getBeanAdminCampanias().setModelGrafico(new HorizontalBarChartModel());
			
	        ChartData data = new ChartData();

	        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
	        hbarDataSet.setLabel("CAMPAÑA: " + getBeanAdminCampanias().getCampaniaSeleccionada().getCampDescripcion().concat(" -- FECHA ").concat(fechas));

	        List<Number> values = new ArrayList<>();
	        values.add(getBeanAdminCampanias().getProspectos());
	        values.add(getBeanAdminCampanias().getProspectosSeguimiento());
	        values.add(getBeanAdminCampanias().getProspectosCandidatos());
	        values.add(getBeanAdminCampanias().getProspectosMatriculados());
	        values.add(getBeanAdminCampanias().getProspectosAbandonados());
	        values.add(getBeanAdminCampanias().getProspectosProximaOcasion());
	        
	        hbarDataSet.setData(values);

	        List<String> bgColor = new ArrayList<>();	        
	        bgColor.add("rgba(255, 159, 64, 0.2)");
			bgColor.add("rgba(255, 205, 86, 0.2)");
			bgColor.add("rgba(75, 192, 192, 0.2)");
			bgColor.add("rgba(31, 168, 85,0.2)");	        
			bgColor.add("rgba(255, 99, 132, 0.2)");
			bgColor.add("rgba(255, 171, 104, 0.2)");
	        hbarDataSet.setBackgroundColor(bgColor);

	        List<String> borderColor = new ArrayList<>();	        
	        borderColor.add("rgb(255, 159, 64)");
			borderColor.add("rgb(255, 205, 86)");
			borderColor.add("rgb(31, 168, 85)");
			borderColor.add("rgb(75, 192, 192)");
			borderColor.add("rgb(255, 99, 132)");
			borderColor.add("rgba(255, 171, 104, 0.2)");
	        
	        hbarDataSet.setBorderColor(borderColor);
	        hbarDataSet.setBorderWidth(1);

	        data.addChartDataSet(hbarDataSet);

	        List<String> labels = new ArrayList<>();
	        labels.add("Alcance");
	        labels.add("En seguimiento");
	        labels.add("Candidatos");
	        labels.add("Matriculados");
	        labels.add("Abandonados");
	        labels.add("Proxima Ocasión");
	        data.setLabels(labels);
	        getBeanAdminCampanias().getModelGrafico().setData(data);
	        

	        //Options
	        BarChartOptions options = new BarChartOptions();
	        CartesianScales cScales = new CartesianScales();
	        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
	        linearAxes.setOffset(true);
	        CartesianLinearTicks ticks = new CartesianLinearTicks();
//	        ticks.setBeginAtZero(true);
	        linearAxes.setTicks(ticks);
	        cScales.addXAxesData(linearAxes);
	        options.setScales(cScales);

	        Title title = new Title();
	        title.setDisplay(true);
	        title.setText("Rendimiento de la campaña");
	        options.setTitle(title);
	        getBeanAdminCampanias().getModelGrafico().setOptions(options);
	        

	}
    public void createHorizontalBarModel() {
        
    }
}

