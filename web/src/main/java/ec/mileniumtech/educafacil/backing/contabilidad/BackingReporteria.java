/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import ec.mileniumtech.educafacil.bean.contabilidad.BeanReporteria;
import ec.mileniumtech.educafacil.dao.impl.EgresoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PagosDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.fechas.FechaFormato;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
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
public class BackingReporteria implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingReporteria.class);
	
	@EJB
	@Getter
	private EgresoDaoImpl egresoServicio;
	
	@EJB
	@Getter
	private PagosDaoImpl pagosServicio;
	
	@Inject
	@Getter
	private BeanReporteria beanReporteria;
	
	@PostConstruct
	public void init() {
		getBeanReporteria().setModelGraficoE(new HorizontalBarChartModel());
		getBeanReporteria().getModelGraficoE().setOptions(new BarChartOptions());
		getBeanReporteria().setModelGraficoI(new HorizontalBarChartModel());
		getBeanReporteria().getModelGraficoI().setOptions(new BarChartOptions());
		getBeanReporteria().setModelPieFechas(new PieChartModel());
		
	}
	public void dialogoReporteFechas() {
		getBeanReporteria().setFechaInicial(null);
		getBeanReporteria().setFechaFinal(null);
		Mensaje.verDialogo("dlgReporteFechas");
	}

	public void buscaReporteFlujoFechas() {
		String fechas="Desde: ".concat(FechaFormato.cambiarFormato(getBeanReporteria().getFechaInicial(),"yyyy-MM-dd")).concat(" Hasta: ").concat(FechaFormato.cambiarFormato(getBeanReporteria().getFechaFinal(),"yyyy-MM-dd")); 
		getBeanReporteria().setFechasReporte(fechas);
		graficaEgresos();
        graficaIngresos();        
        createGraficaPieTotalIngresoEgresos();
        Mensaje.ocultarDialogo("dlgReporteFechas");
	}
	
	public void graficaEgresos() {
try {
			double totalEgresos=0;
			getBeanReporteria().setListaEgresos(new ArrayList<DtoFlujoDinero>());
			getBeanReporteria().setListaIngresos(new ArrayList<DtoFlujoDinero>());
			getBeanReporteria().setListaEgresos(getEgresoServicio().buscaEgresosReporteria(getBeanReporteria().getFechaInicial(), getBeanReporteria().getFechaFinal()));
			
			getBeanReporteria().setModelGraficoE(new HorizontalBarChartModel());
			
	        ChartData data = new ChartData();
			

	        BarChartDataSet barDataSet = new BarChartDataSet();
	        barDataSet.setLabel("EGRESOS");

	        List<Number> values = new ArrayList<>();
	        if(getBeanReporteria().getListaEgresos()!=null && getBeanReporteria().getListaEgresos().size()>0) {
		        for (DtoFlujoDinero egresos : getBeanReporteria().getListaEgresos()) {
					values.add(egresos.getValor());
					totalEgresos = totalEgresos + egresos.getValor();
					
				}
	        }
	        getBeanReporteria().setTotalEgresos(totalEgresos);
	        barDataSet.setData(values);
	        
	        List<String> borderColor = new ArrayList<>();	        
	        borderColor.add("rgb(255, 99, 132)");

	        List<String> bgColor = new ArrayList<>();
	        
//	        bgColor.add("rgba(75, 192, 192, 0.2)");
	        bgColor.add("rgb(255, 99, 132)");
	        barDataSet.setBackgroundColor(bgColor);
	        
			barDataSet.setBorderColor(borderColor);
			barDataSet.setBorderWidth(1);

	        data.addChartDataSet(barDataSet);
	        
	        List<String> labels = new ArrayList<>();
	        if(getBeanReporteria().getListaEgresos()!=null && getBeanReporteria().getListaEgresos().size()>0) {
	        for (DtoFlujoDinero egresos : getBeanReporteria().getListaEgresos()) {
				labels.add(FechaFormato.cambiarFormato(egresos.getFecha(),"yyyy-MM-dd"));
				
			}
	        }
	        data.setLabels(labels);
	        getBeanReporteria().getModelGraficoE().setData(data);
	        
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
	        title.setText("Resumen de Egresos");
	        title.setFontColor("BLUE");
	        options.setTitle(title);
	        getBeanReporteria().getModelGraficoE().setOptions(options);

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void graficaIngresos() {
		try {
			double totalIngresos=0;
			getBeanReporteria().setListaIngresos(new ArrayList<DtoFlujoDinero>());
			getBeanReporteria().setListaIngresos(new ArrayList<DtoFlujoDinero>());
			getBeanReporteria().setListaIngresos(getPagosServicio().buscaIngresosReporteria(getBeanReporteria().getFechaInicial(), getBeanReporteria().getFechaFinal()));
			
			getBeanReporteria().setModelGraficoI(new HorizontalBarChartModel());
			
	        ChartData data = new ChartData();
			

	        BarChartDataSet barDataSet = new BarChartDataSet();
	        barDataSet.setLabel("INGRESOS");

	        List<Number> values = new ArrayList<>();
	        for (DtoFlujoDinero ingresos : getBeanReporteria().getListaIngresos()) {
				values.add(ingresos.getValor());
				totalIngresos = totalIngresos + ingresos.getValor(); 
			}
	        getBeanReporteria().setTotalIngresos(totalIngresos);
	        barDataSet.setData(values);
	        
	        List<String> colorBorde = new ArrayList<>();	        
	        colorBorde.add("rgb(54, 162, 235)");

	        List<String> bgColor = new ArrayList<>();
	        
	        bgColor.add("rgb(54, 162, 235)");
//	        bgColor.add("rgba(54, 162, 235, 0.2)");
	        barDataSet.setBackgroundColor(bgColor);
	        
			barDataSet.setBorderColor(colorBorde);
			barDataSet.setBorderWidth(1);

	        data.addChartDataSet(barDataSet);
	        
	        List<String> labels = new ArrayList<>();
	        for (DtoFlujoDinero ingresos : getBeanReporteria().getListaIngresos()) {
				labels.add(FechaFormato.cambiarFormato(ingresos.getFecha(),"yyyy-MM-dd"));
				
			}

	        data.setLabels(labels);
	        getBeanReporteria().getModelGraficoI().setData(data);
	        
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
	        title.setText("Resumen de Ingresos");
	        title.setFontColor("BLUE");
	        options.setTitle(title);
	        getBeanReporteria().getModelGraficoI().setOptions(options);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createGraficaPieTotalIngresoEgresos() {
		getBeanReporteria().setModelPieFechas(new PieChartModel());
        
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        
        List<Number> values = new ArrayList<>();
        values.add(getBeanReporteria().getTotalEgresos());
        values.add(getBeanReporteria().getTotalIngresos());        
        dataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");        
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();
        labels.add("Egresos");
        labels.add("Ingresos");
        data.setLabels(labels);
        getBeanReporteria().getModelPieFechas().setData(data);
        
    }
}
