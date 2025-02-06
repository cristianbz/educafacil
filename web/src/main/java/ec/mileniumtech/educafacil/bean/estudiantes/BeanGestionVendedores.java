/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.SeguimientoClientes;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Vendedor;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosContactoCliente;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [Christian Báez] 14 feb. 2024
 *
 */

@ViewScoped

public class BeanGestionVendedores implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private BigDecimal totalSeguimiento;
	@Getter
	@Setter
	private BigDecimal totalAbandonado;
	@Getter
	@Setter
	private BigDecimal totalMatriculado;
	@Getter
	@Setter
	private BigDecimal totalCandidato;
	@Getter
	@Setter
	private BigDecimal totalProximaOcasion;
	@Getter
	@Setter
	private BigDecimal totalMatriculados;
	@Getter
	@Setter
	private BigDecimal totalDesertados;
	@Getter
	@Setter
	private BigDecimal totalGastoCampania;
	
	
	@Getter
	@Setter
	private Vendedor vendedor;
	
	@Getter
	@Setter
	private Campania cursoSeleccionado;
	
	@Getter
	@Setter
	private Campania cursoSeleccionadoReporte;
	
	@Getter
	@Setter
	private Vendedor vendedorSeleccionado;
	
	@Getter
	@Setter
	private Vendedor vendedorSeleccionadoReporte;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listaSeguimientoClientes;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listaSeguimientoClientesSinVendedores;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listaSeguimientoClientesSelect;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listaSeguimientoClientesEstado;
	
	@Getter
	@Setter
	private List<Vendedor> listaVendedores;
	
	@Getter
	@Setter
	private List<Vendedor> listaVendedoresReporte;
	
	@Getter
	@Setter
	private List<Campania> listaCursos;
	
	@Getter
	@Setter
	private List<Campania> listaCursosReporte;
	
	@Getter
	@Setter
	private boolean campaniasFinalizadas;
	
	@Getter
	@Setter
	private String[] estadosSeleccionados;	
	
	@Getter
	@Setter
	private BarChartDataSet barCharDataSetVendedores;

	@Getter
	@Setter
	@RequestScoped
	private BarChartModel barModel;
	
	@Getter
	@Setter
	private ChartData chartDataVendedores;
	
	
	@Setter
	private EnumEstadosContactoCliente[] enumEstadosContacto;
	
	public EnumEstadosContactoCliente[] getEnumEstadosContacto() {
		return EnumEstadosContactoCliente.listaValores();
	}
}
