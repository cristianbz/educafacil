/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.contabilidad;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
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
@Getter
@Setter
public class BeanPagos implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<Matricula> listaCursosMatriculados;
	private List<Catalogo> listaServiciosPago;
	private Estudiante estudiante;
	private Date fechaRegistro;
	private Pagos pago;
	private DetallePagos detallePagos;	
	private DetallePagos detallePagosEliminar;	
	private List<DetallePagos> listaDetallePagos;
	private List<DetallePagos> listaDetallePagosRealizados;
	private Catalogo servicioSeleccionado;
	private String formaPago;
	private Matricula matricula;
	private Curso cursoSelecionado;
	private List<OfertaCursos> listaCursos;
	private List<OfertaCursos> listaOfertaCursos;
	private OfertaCursos cursoSeleccionado;
	private String nombreCurso;
	private boolean cursosFinalizados;
}
