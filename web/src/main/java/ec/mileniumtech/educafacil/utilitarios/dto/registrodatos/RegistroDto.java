/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.dto.registrodatos;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 7, 2024
*
*/
public class RegistroDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String reg_id;
	@Getter
	@Setter
	private String reg_estado;
	@Getter
	@Setter
	private String reg_seg_observacion;
	@Getter
	@Setter
	private String reg_descargado;
	/**
	 * @param reg_id
	 * @param reg_estado
	 */
	public RegistroDto(String reg_id, String reg_estado,String reg_seg_observacion,String reg_descargado) {
		super();
		this.reg_id = reg_id;
		this.reg_estado = reg_estado;
		this.reg_seg_observacion=reg_seg_observacion;
		this.reg_descargado=reg_descargado;
	}
	public RegistroDto() {
		super();
	}

}
