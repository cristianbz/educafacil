package ec.mileniumtech.educafacil.backing.facelectronica;

import lombok.Getter;
import lombok.Setter;

/**
* @author christian May 21, 2025
*/
/**
 * 
 */
@Getter
@Setter
public class ErrorResponse {
	private String mensaje;

	public ErrorResponse(String mensaje) {
		super();
		this.mensaje = mensaje;
	}
	
}

