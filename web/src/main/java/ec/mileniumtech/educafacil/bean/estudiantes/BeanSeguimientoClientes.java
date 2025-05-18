/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.file.UploadedFile;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.SeguimientoClientes;
import ec.mileniumtech.educafacil.utilitarios.dto.registrodatos.FormFacebookAdsRecord;
import ec.mileniumtech.educafacil.utilitarios.dto.registrodatos.PreguntasFormFacesbookRecord;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosContactoCliente;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumMedioContacto;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumMedioInformacion;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumParaQueCurso;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumUbicacionDomicilio;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] christian 30 dic. 2021
 *
 */
@Named
@ViewScoped
public class BeanSeguimientoClientes implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private FormFacebookAdsRecord leadsFormulario;
	
	@Getter
	@Setter
	private PreguntasFormFacesbookRecord preguntasFormulario;
	
	@Getter
	@Setter
	private UploadedFile archivoExcel;
	
	@Getter
	@Setter
	private UploadedFile archivoFormulario;
	
	@Getter
	@Setter
	private boolean activaUploadFormulario;
	
	@Getter
	@Setter
	private boolean camposAdicionales;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listadoSeguimientoExcel;
	
	@Getter
	@Setter
	private List<FormFacebookAdsRecord> listadoLeadsForm;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listadoSeguimiento;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listadoSeguimientoSeleccionado;
	
	@Getter
	@Setter
	private List<FilterMeta> filtro;
	
	@Getter
	@Setter
	private boolean esCampania;
	
	@Getter
	@Setter
	private boolean correoDefecto;
	@Getter
	@Setter
	private List<String> trazabilidad;
	@Getter
	@Setter
	private ArrayList<DetalleSeguimiento> trazabilidadObj;
	@Getter
	@Setter
	private Campania campaniaSeleccionada;
	
	@Getter
	@Setter
	private Curso cursoSeleccionado;
	
	@Getter
	@Setter
	private SeguimientoClientes seguimientoClientes;
	
	@Getter
	@Setter
	private DetalleSeguimiento detalleSeguimiento;
	
	@Getter
	@Setter
	private List<Curso> listaCursos;
	
	@Getter
	@Setter
	private Integer codigoCurso;
	
	@Getter
	@Setter
	private Integer numeroPreguntas;
	
	@Getter
	@Setter
	private String codigoUbicacionLlamada;
	
	@Getter
	@Setter
	private String codigoMedioInformacion;
	
	@Getter
	@Setter
	private String codigoMedioContacto;
	
	@Getter
	@Setter
	private boolean seguimientoInicial;
	
	@Getter
	@Setter
	private boolean medioContactoLlamada;
	
	@Getter
	@Setter
	private boolean medioContactoVisita;
	
	@Getter
	@Setter
	private boolean motivoNoMatricula;
	
	@Getter
	@Setter
	private boolean habilitaCargaFormFaces;
	
	@Getter
	@Setter
	private List<Campania> listaCampanias;
	
	@Getter
	@Setter
	private List<Campania> listaCampaniasTodas;
	
	@Getter
	@Setter
	private List<String> motivosNoInteres;
	
	@Getter
	@Setter

    private String[] motivosNoInteresSeleccionados;
	
	@Getter
	@Setter
	private String codigoEstadoContacto;
	
	@Getter
	@Setter
	private String codigoDeseoCurso;
	
	@Getter
	@Setter
	private int tipoCargaInformacion;
	
	@Getter
	@Setter
	private Date fechaInicio;
	
	@Getter
	@Setter
	private Date fechaFin;
	
	@Getter
	@Setter
	private Date proximaLlamada;
	
	@Getter
	@Setter
	private List<DetalleSeguimiento> listaDetalle;
	
	@Getter
	@Setter
	private SeguimientoClientes seguimientoSeleccionado;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listaPorLlamar;
	
	@Getter
	@Setter
	private List<SeguimientoClientes> listaLlamarAhora;
	
	@Getter
	@Setter
	private String contenidoMensaje;
	
	@Getter
	@Setter
	private boolean nohabilitaGrabar;
	
	@Getter
	@Setter
	private boolean contestoLlamada;
	
	@Getter
	@Setter
	private boolean seguirIngresandoClientes;
	
	@Setter
	private EnumMedioInformacion[] enumMedioInformacion;
	
	public EnumMedioInformacion[] getEnumMedioInformacion() {
		return EnumMedioInformacion.listaValores();
	}
	
	@Setter
	private EnumMedioContacto[] enumMedioContacto;
	
	public EnumMedioContacto[] getEnumMedioContacto() {
		return EnumMedioContacto.listaValores();
	}
	
	@Setter
	private EnumEstadosContactoCliente[] enumEstadosContacto;
	
	public EnumEstadosContactoCliente[] getEnumEstadosContacto() {
		return EnumEstadosContactoCliente.listaValores();
	}
	
	@Setter
	private EnumParaQueCurso[] enumParaQueCurso;
	
	public EnumParaQueCurso[] getEnumParaQueCurso() {
		return EnumParaQueCurso.listaValores();
	}
	
	@Setter
	private EnumUbicacionDomicilio[] enumUbicacionLlamada;
	
	public EnumUbicacionDomicilio[] getEnumUbicacionLlamada() {
		return EnumUbicacionDomicilio.listaValores();
	}
	
}
