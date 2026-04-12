/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumModalidadCurso {
	PRESENCIAL("MODA01","PRESENCIAL"),
	SEMIPRESENCIAL("MODA02","SEMIPRESENCIAL"),
	VIRTUAL("MODA03","VIRTUAL"),
	VIDEOCURSO("MODA04","VIDEOCURSO");
	
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	/**
	 * @param codigo
	 * @param label
	 */
	private EnumModalidadCurso(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	public static EnumModalidadCurso[] listaValores() {
		return values();
	}
}

