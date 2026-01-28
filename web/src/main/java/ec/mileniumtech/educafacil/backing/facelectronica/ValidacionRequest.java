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
public class ValidacionRequest {
	private byte[] p12Data;
    private String password;
}

