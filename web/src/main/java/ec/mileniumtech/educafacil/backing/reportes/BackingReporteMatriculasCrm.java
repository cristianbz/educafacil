package ec.mileniumtech.educafacil.backing.reportes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;

import ec.mileniumtech.educafacil.bean.reportes.BeanReporteMatriculasCrm;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import ec.mileniumtech.educafacil.service.facade.MarketingFacade;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosContactoCliente;
import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean para el reporte CRM de Matrículas
 */
@Named
@ViewScoped
@Getter
@Setter
public class BackingReporteMatriculasCrm implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingReporteMatriculasCrm.class);

    @Inject
    private BeanReporteMatriculasCrm beanReporte;

    @EJB
    private MarketingFacade marketingFacade;

    @PostConstruct
    public void init() {
        if (beanReporte != null) {
            // Inicialización garantizada de modelos para que PrimeFaces no arroje NPE
            HorizontalBarChartModel m1 = new HorizontalBarChartModel();
            m1.setData(new ChartData());
            m1.setOptions(obtenerOpcionesGenericas());
            beanReporte.setBarmodel(m1);

            HorizontalBarChartModel m2 = new HorizontalBarChartModel();
            m2.setData(new ChartData());
            m2.setOptions(obtenerOpcionesGenericas());
            beanReporte.setBarmodelVenta(m2);

            HorizontalBarChartModel m3 = new HorizontalBarChartModel();
            m3.setData(new ChartData());
            m3.setOptions(obtenerOpcionesGenericas());
            beanReporte.setBarmodelAbandona(m3);

            try {
                cargarTotales();
                generarGraficoInteresados();
                generarGraficoVentas();
                generarGraficoAbandonos();
            } catch (Exception e) {
                log.error("Error inicializando BackingReporteMatriculasCrm", e);
            }
        }
    }

    private void cargarTotales() {
        BigDecimal seguimiento = marketingFacade.totalDatosCRM(EnumEstadosContactoCliente.ENSEGUIMIENTO.getCodigo());
        BigDecimal candidato = marketingFacade.totalDatosCRM(EnumEstadosContactoCliente.CANDIDATO.getCodigo());
        BigDecimal matriculado = marketingFacade.totalDatosCRM(EnumEstadosContactoCliente.MATRICULADO.getCodigo());
        BigDecimal abandonado = marketingFacade.totalDatosCRM(EnumEstadosContactoCliente.ABANDONADO.getCodigo());
        BigDecimal proxima = marketingFacade.totalDatosCRM(EnumEstadosContactoCliente.PROXIMAOCASION.getCodigo());
        BigDecimal gasto = marketingFacade.totalGastoCampanias();

        beanReporte.setTotalSeguimiento(seguimiento != null ? seguimiento : BigDecimal.ZERO);
        beanReporte.setTotalCandidato(candidato != null ? candidato : BigDecimal.ZERO);
        beanReporte.setTotalMatriculado(matriculado != null ? matriculado : BigDecimal.ZERO);
        beanReporte.setTotalAbandonado(abandonado != null ? abandonado : BigDecimal.ZERO);
        beanReporte.setTotalProximaOcasion(proxima != null ? proxima : BigDecimal.ZERO);
        beanReporte.setTotalGastoCampania(gasto != null ? gasto : BigDecimal.ZERO);
    }

    private void generarGraficoInteresados() {
        HorizontalBarChartModel barmodel = new HorizontalBarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Interesados");

        List<DtoMatriculasCurso> lista = marketingFacade.listaInteresadosCursoCRM();
        beanReporte.setListaInteresadosCurso(lista);
        
        cargarDatosYColoresGrafico(lista, data, barDataSet);
        
        barmodel.setData(data);
        barmodel.setOptions(obtenerOpcionesGenericas());
        beanReporte.setBarmodel(barmodel);
    }

    private void generarGraficoVentas() {
        HorizontalBarChartModel barmodel = new HorizontalBarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Ventas");

        List<DtoMatriculasCurso> lista = marketingFacade.listaEstadosContactoCursoCRM(EnumEstadosContactoCliente.MATRICULADO.getCodigo());
        beanReporte.setListaVentasCurso(lista);
        
        cargarDatosYColoresGrafico(lista, data, barDataSet);
        
        barmodel.setData(data);
        barmodel.setOptions(obtenerOpcionesGenericas());
        beanReporte.setBarmodelVenta(barmodel);
    }

    private void generarGraficoAbandonos() {
        HorizontalBarChartModel barmodel = new HorizontalBarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Abandonos");

        List<DtoMatriculasCurso> lista = marketingFacade.listaEstadosContactoCursoCRM(EnumEstadosContactoCliente.ABANDONADO.getCodigo());
        beanReporte.setListaAbandonaCurso(lista);
        
        cargarDatosYColoresGrafico(lista, data, barDataSet);
        
        barmodel.setData(data);
        barmodel.setOptions(obtenerOpcionesGenericas());
        beanReporte.setBarmodelAbandona(barmodel);
    }

    private void cargarDatosYColoresGrafico(List<DtoMatriculasCurso> lista, ChartData data, BarChartDataSet barDataSet) {
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> borderColors = new ArrayList<>();
        
        String[] palette = {
            "rgba(59, 130, 246, 0.7)", "rgba(16, 185, 129, 0.7)", "rgba(245, 158, 11, 0.7)", 
            "rgba(239, 68, 68, 0.7)", "rgba(168, 85, 247, 0.7)", "rgba(99, 102, 241, 0.7)", 
            "rgba(236, 72, 153, 0.7)", "rgba(20, 184, 166, 0.7)"
        };
        String[] borderPalette = {
            "rgba(59, 130, 246, 1.0)", "rgba(16, 185, 129, 1.0)", "rgba(245, 158, 11, 1.0)", 
            "rgba(239, 68, 68, 1.0)", "rgba(168, 85, 247, 1.0)", "rgba(99, 102, 241, 1.0)", 
            "rgba(236, 72, 153, 1.0)", "rgba(20, 184, 166, 1.0)"
        };

        if (lista != null && !lista.isEmpty()) {
            int idx = 0;
            for (DtoMatriculasCurso dto : lista) {
                labels.add(dto.getCurso() != null ? dto.getCurso() : "Sin Nombre");
                values.add(dto.getCantidad());
                
                bgColors.add(palette[idx % palette.length]);
                borderColors.add(borderPalette[idx % borderPalette.length]);
                idx++;
            }
        } else {
            labels.add("Sin datos");
            values.add(0);
            bgColors.add("rgba(203, 213, 225, 0.7)");
            borderColors.add("rgba(203, 213, 225, 1.0)");
        }
        
        barDataSet.setData(values);
        barDataSet.setBackgroundColor(bgColors);
        barDataSet.setBorderColor(borderColors);
        barDataSet.setBorderWidth(1);
        data.setLabels(labels);
        data.addChartDataSet(barDataSet);
    }

    private BarChartOptions obtenerOpcionesGenericas() {
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes); 
        
        options.setScales(cScales);
        return options;
    }
}
