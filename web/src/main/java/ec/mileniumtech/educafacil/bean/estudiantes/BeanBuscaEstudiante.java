/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] christian Apr 23, 2022
 *
 */
@Named
@ViewScoped
public class BeanBuscaEstudiante implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String cedula;
	
	@Getter
	@Setter
	private String apellidos;
	
	@Getter
	@Setter
	private List<Estudiante> listaEstudiante;
	
	@Getter
	@Setter
	private int codigoCliente;
	
	@Getter
	@Setter
	private Matricula matriculaSeleccionada;
	
	@Getter
	@Setter
	private Estudiante estudiante;
	
	@Getter
	@Setter
	private String codigoCargo;
	
	@Getter
	@Setter
	private String codigoNivelEstudio;
	
//	@Getter
//	@Setter
//	private List<Matricula> listaMatricula;
	
	@Getter
	@Setter //1 fichaEstudiante 2 Pagos
	private int tipoBusqueda;
}
