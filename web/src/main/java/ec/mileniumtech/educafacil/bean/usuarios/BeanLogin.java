/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.usuarios;

import java.io.Serializable;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@SessionScoped
public class BeanLogin implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private boolean panelDocumento;
	@Getter
	@Setter
	private boolean panelValida;
	@Getter
	@Setter
	private boolean panelCambiaClave;
	@Getter
	@Setter
	private String documentoIdentidad;
	@Getter
	@Setter
	private String clave;
	@Getter
	@Setter
	private Usuario usuario;
	@Getter
	@Setter
	private String correo;
	@Getter
	@Setter
	private String cedula;
	@Getter
	@Setter
	private Configuraciones configuraciones;
	BeanLogin(){
		usuario=new Usuario();	
		configuraciones=new Configuraciones();
	}
	
}
