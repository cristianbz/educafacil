/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BeanFinalizarCurso implements Serializable{

	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private List<OfertaCursos> listaCursosAbiertos;
	@Getter
	@Setter
	private List<Matricula> listaMatriculadosCurso;
	@Getter
	@Setter
	private OfertaCursos ofertaCursosSeleccionado;
	@Getter
	@Setter
	private Matricula matriculaSeleccionada;
}

