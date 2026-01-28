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
public class ValidacionResponse {
	private boolean valido;

	public ValidacionResponse(boolean valido) {
		super();
		this.valido = valido;
	}
	
}

