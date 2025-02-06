/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumTipoCapacitacion {
	CURSO("TCAPA01","CURSO"),
	TALLER("TCAPA02","TALLER"),
	SEMINARIO("TCAPA03","SEMINARIO");
	
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	/**
	 * @param codigo
	 * @param label
	 */
	private EnumTipoCapacitacion(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	
}

