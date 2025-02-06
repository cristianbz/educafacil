/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumNivelEstudio {
	SINESTUDIOS("NIVEL01","SINESTUDIOS"),
	PRIMARIA("NIVEL02","PRIMARIA"),
	SECUNDARIA("NIVEL03","SECUNDARIA"),
	UNIVERSIDAD("NIVEL04","UNIVERSIDAD"),
	MAESTRIA("NIVEL05","MAESTRIA"),
	DOCTORADO("NIVEL06","DOCTORADO"),
	TECNICO("NIVEL07","TECNICO"),
	TECNOLOGIA("NIVEL08","TECNOLOGIA");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	/**
	 * @param codigo
	 * @param label
	 */
	private EnumNivelEstudio(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	public static EnumNivelEstudio[] listaValores() {
		return values();
	}
}

