/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumEstadosOfertaCurso {
	INICIADO("OCUR01","INICIADO"),
	FINALIZADO("OCUR02","FINALIZADO"),
	PORDEFECTO("OCUR03","PORDEFECTO"),
	ANULADO("OCUR04","ANULADO");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	/**
	 * @param codigo
	 * @param label
	 */
	private EnumEstadosOfertaCurso(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
}

