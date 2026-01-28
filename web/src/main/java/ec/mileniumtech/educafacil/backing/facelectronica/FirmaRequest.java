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
public class FirmaRequest {
	private byte[] p12Data;
    private String password;
    private byte[] documento;
}

