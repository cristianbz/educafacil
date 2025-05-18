/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] cbaez Jan 29, 2020
 *
 */
@Named
@ViewScoped
public class BeanListadoEstudiantes implements Serializable{

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private List<Matricula> listaMatriculas;
	@Getter
	private EnumEstadosMatricula[] listaEstadosMatricula;
	@Getter
	@Setter
	private String codigoEstadoMatricula;
	@Getter
	@Setter
	private Matricula matriculaSeleccionada;
	@Getter
	@Setter
	private Integer codigoCurso;
	@Getter
	@Setter
	private List<Curso> listaCursos;
	@Getter
	@Setter
	private Curso cursoSeleccionado;
	@Getter
	@Setter
	private List<OfertaCursos> listaOfertaCursos;
	@Getter
	@Setter
	private OfertaCursos ofertaSeleccionada;
	@Getter
	@Setter
	private int anioBusqueda;
	
	@PostConstruct
	public void iniciar() {
		listaEstadosMatricula=EnumEstadosMatricula.listaValores();
	}
}
