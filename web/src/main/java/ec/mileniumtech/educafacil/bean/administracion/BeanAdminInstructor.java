/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
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
public class BeanAdminInstructor implements Serializable{


	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private List<Capacitacion> listaCapacitaciones;
	@Getter
	@Setter
	private List<Formacion> listaFormaciones;
	@Getter
	@Setter
	private List<Instructor> listaInstructores;
	@Getter
	@Setter
	private boolean mostrarDatosInstructor;
	@Getter
	@Setter
	private Instructor instructor;
	@Getter
	@Setter
	private Persona persona;
	@Getter
	@Setter
	private List<Catalogo> listaFormacion;
	@Getter
	@Setter
	private List<Catalogo> listaCapacitacion;
	@Getter
	@Setter
	private String cedula;
}
