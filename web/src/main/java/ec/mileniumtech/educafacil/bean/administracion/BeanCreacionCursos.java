/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named("beanCreacionCursos")
@ViewScoped
public class BeanCreacionCursos implements Serializable {

	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private List<Curso> listaCursos;
	@Getter
	@Setter
	private Curso curso;
	@Getter
	@Setter
	private List<Area> listaAreas;
	@Getter
	@Setter
	private List<Especialidad> listaEspecialidades;
	@Getter
	@Setter
	private int codigoCurso;
	@Getter
	@Setter
	private int codigoArea;
	@Getter
	@Setter
	private int codigoEspecialidad;
	
	@Getter
	@Setter
	private boolean asignarOferta;
	@Getter
	@Setter
	private boolean cursoActivo;
	@Getter
	@Setter
	private OfertaCapacitacion ofertaCapacitacion;
	@Getter
	@Setter
	private List<OfertaCapacitacion> listaOfertaCapacitacion;
	
	@PostConstruct
	public void init() {
		//setCurso(new Curso());
	}
}
