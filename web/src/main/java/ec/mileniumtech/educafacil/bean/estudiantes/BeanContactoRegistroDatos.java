/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.bean.estudiantes;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Seguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] cbaez Sep 17, 2020
 *
 */
@Named
@ViewScoped
public class BeanContactoRegistroDatos implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private List<Matricula> litaOportunidades;
	@Getter
	@Setter
	private Matricula oportunidadSeleccionado;

	@Getter
	@Setter
	private List<Seguimiento> listadoSeguimiento;
	@Getter
	@Setter
	private List<Catalogo> listaTipoSeguimiento;
	@Getter
	@Setter
	private boolean actividades;
	@Getter
	@Setter
	private boolean mostrarClientes;
	@Getter
	@Setter
	private String codigoTarea;
	@Getter
	@Setter
	private List<Usuario> listaVendedores;
	@Getter
	@Setter
	private Seguimiento seguimiento;
	@Getter
	@Setter
	private int codigoUsuario;
	
	@PostConstruct
	public void init() {
		setOportunidadSeleccionado(new Matricula());
		setSeguimiento(new Seguimiento());
	}

}
