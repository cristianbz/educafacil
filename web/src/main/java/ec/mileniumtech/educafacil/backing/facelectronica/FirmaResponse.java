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
public class FirmaResponse {
	private byte[] firma;

	public FirmaResponse(byte[] firma) {
		super();
		this.firma = firma;
	}

}

