/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumCargoTrabajo;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumModalidadCurso;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumNivelEstudio;
import jakarta.enterprise.context.RequestScoped;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] cbaez Nov 9, 2020
 *
 */

@RequestScoped
public class BeanFichaEstudiante implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private List<Estudiante> listaEstudiante;
	
	@Getter
	@Setter
	private List<Matricula> listaMatricula;
	@Getter
	@Setter
	private Matricula matriculaSeleccionada;

	@Getter
	@Setter
	private Estudiante estudiante;
	
	@Getter
	@Setter
	private String cedula;
	
	@Getter
	@Setter
	private String apellidos;
	
	@Getter
	@Setter
	private int codigoCliente;
	
	@Getter
	@Setter
	private String codigoCargo;
	
	@Getter
	@Setter
	private String codigoNivelEstudio;
	
	@Setter
	private EnumCargoTrabajo[] enumCargoTrabajo;
	
	@Setter
	private EnumNivelEstudio[] enumNivelEstudio;
	
	@Setter
	@Getter
	private String codigoModalidadCurso;
	
	@Setter
	private EnumModalidadCurso[] enumModalidadCurso;
	
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
}
