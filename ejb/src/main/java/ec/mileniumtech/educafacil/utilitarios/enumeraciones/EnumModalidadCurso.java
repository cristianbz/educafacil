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
	PRESENCIAL("TMOES01","PRESENCIAL"),
	SEMIPRESENCIAL("TMOES02","SEMIPRESENCIAL"),
	VIRTUAL("TMOES03","VIRTUAL"),
	VIDEOCURSO("TMOES04","VIDEOCURSO"),
	HIBRIDO("TMOES05","HIBRIDO");
	
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

