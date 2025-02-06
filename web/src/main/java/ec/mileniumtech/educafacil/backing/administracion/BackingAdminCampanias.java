/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanAdminCampanias;
import ec.mileniumtech.educafacil.dao.impl.CampaniaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.SeguimientoClientesDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosContactoCliente;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
*@author christian  Jul 7, 2024
*
*/
@ViewScoped
@Named
public class BackingAdminCampanias implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingAdminCampanias.class);
	
	@EJB
	@Getter
	private CampaniaDaoImpl campaniaDao;
	
	@EJB
	@Getter
	private CursoDaoImpl cursoDaoImpl;
	
	@EJB
	@Getter
	private SeguimientoClientesDaoImpl seguimientoDao;
	
	@Inject
	@Getter
	private BeanAdminCampanias beanAdminCampanias;
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking; 
	
	@PostConstruct
	public void init() {
		try {
			getBeanAdminCampanias().setListaCampanias(new ArrayList<>());
			getBeanAdminCampanias().setListaCampanias(getCampaniaDao().listaTodasCampanias());
			getBeanAdminCampanias().setModelGrafico(new HorizontalBarChartModel());
			getBeanAdminCampanias().getModelGrafico().setOptions(new BarChartOptions());
			getBeanAdminCampanias().setMostrarGrafica(true);
			getBeanAdminCampanias().setListaCursos(new ArrayList<>());
			getBeanAdminCampanias().setListaCursos(getCursoDaoImpl().listaCursos());
			getBeanAdminCampanias().setCampaniaSeleccionada(new Campania());			
		}catch(Exception e) {
			e.printStackTrace();
		}
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
		try {
			Curso curso = new Curso();
			curso.setCursId(getBeanAdminCampanias().getCursoSeleccionado());
			getBeanAdminCampanias().getCampaniaSeleccionada().setCurso(curso);
			getBeanAdminCampanias().getCampaniaSeleccionada().setCampTipoContenido(getBeanAdminCampanias().getCodigoTipo());
			getCampaniaDao().agregarActualizarCampania(getBeanAdminCampanias().getCampaniaSeleccionada());
			getBeanAdminCampanias().setListaCampanias(new ArrayList<>());
			getBeanAdminCampanias().setListaCampanias(getCampaniaDao().listaTodasCampanias());
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));	
			Mensaje.ocultarDialogo("dlgCampania");
		}catch(Exception e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabar"));
			e.printStackTrace();
		}
	}
	
	public void nuevaCampania() {
		getBeanAdminCampanias().setCodigoTipo("");
		getBeanAdminCampanias().setCampaniaSeleccionada(new Campania());
		Mensaje.verDialogo("dlgCampania");
	}
	
	public void resumenCampania() {
		try {
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String f1 = simpleDateFormat.format(getBeanAdminCampanias().getCampaniaSeleccionada().getCampFechaDesde());
			String f2 =simpleDateFormat.format(getBeanAdminCampanias().getCampaniaSeleccionada().getCampFechaHasta());
			String fechas =f1.concat(" al ").concat(f2);
			getBeanAdminCampanias().setMostrarGrafica(true);
			getBeanAdminCampanias().setProspectos(getSeguimientoDao().alcanceCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId()));
			getBeanAdminCampanias().setProspectosSeguimiento(getSeguimientoDao().prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.ENSEGUIMIENTO.getCodigo()));
			getBeanAdminCampanias().setProspectosAbandonados(getSeguimientoDao().prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.ABANDONADO.getCodigo()));
			getBeanAdminCampanias().setProspectosMatriculados(getSeguimientoDao().prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.MATRICULADO.getCodigo()));
			getBeanAdminCampanias().setProspectosCandidatos(getSeguimientoDao().prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.CANDIDATO.getCodigo()));
			getBeanAdminCampanias().setProspectosProximaOcasion(getSeguimientoDao().prospectosCampania(getBeanAdminCampanias().getCampaniaSeleccionada().getCampId(), EnumEstadosContactoCliente.PROXIMAOCASION.getCodigo()));
			
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
	        
		}catch(Exception e) {
			e.printStackTrace();
			
		}
	}
    public void createHorizontalBarModel() {
        
    }
}

