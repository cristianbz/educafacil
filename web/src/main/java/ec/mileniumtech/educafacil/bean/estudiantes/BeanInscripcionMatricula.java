/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.MedioInformacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.utilitarios.dto.registrodatos.ClientesRegistradosDto;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumCargoTrabajo;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumIngresosMensuales;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumMedioInformacion;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumModalidadCurso;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumNivelEstudio;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] cbaez
 *
 */
@Named
@ViewScoped
public class BeanInscripcionMatricula implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	List<ClientesRegistradosDto> listaRegistrados;
	@Getter
	@Setter
	private Matricula matricula;
	@Getter
	@Setter
	private Estudiante estudiante;
	@Getter
	@Setter
	private Persona persona;
	@Getter
	@Setter
	private List<Area> listaAreas;
	@Getter
	@Setter
	private List<Especialidad> listaEspecialidad;
	@Getter
	@Setter
	private List<Curso> listaCurso;
	@Getter
	@Setter
	private List<MedioInformacion> listaMedioInformacion;
	@Getter
	@Setter
	private List<Empresa> listaEmpresas;
	@Getter
	@Setter
	private List<OfertaCursos> listaOfertaCursos;
	@Getter
	@Setter
	private List<Matricula> listaMatriculas;
	@Getter
	@Setter
	private List<Empresa> listaEmpresasFiltradas;
	@Getter
	@Setter
	private String codigoMedioInformacion;
	@Getter
	@Setter
	private int codigoArea;
	@Getter
	@Setter
	private int codigoEspecialidad;
	@Getter
	@Setter
	private int codigoCurso;
	@Getter
	@Setter
	private int codigoOfertaCapacitacion;
	@Getter
	@Setter
	private OfertaCursos ofertaCursosSeleccionado;
	@Getter
	@Setter
	private Empresa empresaSeleccionada;
	@Getter
	@Setter
	private Empresa empresa;
	@Getter
	@Setter
	private int codigoPersona;
	@Getter
	@Setter
	private List<Persona> listaPersonas;
	@Setter
	private EnumCargoTrabajo[] enumCargoTrabajo;
	@Setter
	private EnumNivelEstudio[] enumNivelEstudio;
	@Setter
	private EnumMedioInformacion[] enumMedioInformacion;
	@Setter
	private EnumIngresosMensuales[] enumIngresos;
	@Getter
	@Setter
	private String codigoCargo;
	@Getter
	@Setter
	private String codigoNivelEstudio;
	@Getter
	@Setter
	private String observacionRegistro;
	@Setter
	@Getter
	private String codigoIngresosMensuales;
	@Getter
	@Setter
	private Date fechaActual;
	@Getter
	@Setter
	private ClientesRegistradosDto clienteRegistradoSeleccionado;
	@Setter
	private EnumModalidadCurso[] enumModalidadCurso;
	@Setter
	@Getter
	private String codigoModalidadCurso;
	@PostConstruct
	public void init() {
		matricula=new Matricula();
		persona=new Persona();
		empresa=new Empresa();
		fechaActual=new Date();
		estudiante=new Estudiante();
	}

	public EnumCargoTrabajo[] getEnumCargoTrabajo() {
		return EnumCargoTrabajo.listaValores();
	}

	/**
	 * @return the enumNivelEstudio
	 */
	public EnumNivelEstudio[] getEnumNivelEstudio() {
		return EnumNivelEstudio.listaValores();
	}

	/**
	 * @return the enumModalidadCurso
	 */
	public EnumModalidadCurso[] getEnumModalidadCurso() {
		return EnumModalidadCurso.listaValores();
	}

	public EnumMedioInformacion[] getEnumMedioInformacion() {
		return EnumMedioInformacion.listaValores();
	}
	public EnumIngresosMensuales[] getEnumIngresos() {
		return EnumIngresosMensuales.listaValores();
	}
}
