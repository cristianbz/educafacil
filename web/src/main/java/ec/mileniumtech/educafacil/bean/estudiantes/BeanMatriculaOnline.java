/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumCargoTrabajo;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadoCivil;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumIngresosMensuales;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumMedioInformacion;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumModalidadCurso;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumNivelEstudio;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumParaQueCurso;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumUbicacionDomicilio;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] christian 24 feb. 2022
 *
 */
@Named
@ViewScoped
public class BeanMatriculaOnline implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private Persona persona;
	@Getter
	@Setter
	private Catalogo provincia;
	@Getter
	@Setter
	private Integer codigoCiudad;
	@Getter
	@Setter
	private Matricula matricula;
	@Getter
	@Setter
	private Estudiante estudiante;
	@Getter
	@Setter
	private List<Catalogo> listaProvincias;
	
	@Getter
	@Setter
	private List<Catalogo> listaCantones;
	@Getter
	@Setter
	private UsuarioRol usuarioRol;
	@Getter
	@Setter
	private Rol rol;
	@Setter
	private EnumCargoTrabajo[] enumCargoTrabajo;
	@Setter
	private EnumNivelEstudio[] enumNivelEstudio;
	@Setter
	private EnumMedioInformacion[] enumMedioInformacion;
	@Setter
	private EnumIngresosMensuales[] enumIngresos;
	@Setter
	private EnumUbicacionDomicilio[] enumUbicaDom;
	@Setter
	private EnumEstadoCivil[] enumEstadoCivil;
	@Getter
	@Setter
	private String codigoCargo;
	@Getter
	@Setter
	private String codigoSector;
	@Getter
	@Setter
	private String codigoNivelEstudio;
	@Setter
	@Getter
	private String codigoModalidadCurso;
	@Setter
	@Getter
	private String codigoMedioInformacion;
	@Setter
	@Getter
	private String codigoIngresosMensuales;
	@Setter
	@Getter
	private boolean mostrarDatosPersona;
	@Setter
	@Getter
	private boolean deshabilitaMatricula;
	
	@Setter
	@Getter
	private List<OfertaCursos> listaOfertaCursos;
	
	@Setter
	@Getter
	private OfertaCursos cursoSeleccionado;
	
	
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
	public EnumUbicacionDomicilio[] getEnumUbicacionDomicilio() {
		return EnumUbicacionDomicilio.listaValores();
	}
	
	@Setter
	private EnumParaQueCurso[] enumParaQueCurso;
	
	public EnumParaQueCurso[] getEnumParaQueCurso() {
		return EnumParaQueCurso.listaValores();
	}
	public EnumEstadoCivil[] getEnumEstadoCivil() {
		return EnumEstadoCivil.listaValores();
	}
}
