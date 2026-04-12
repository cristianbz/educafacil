/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumParaQueCurso {
	TRABAJO("PAQ01","PARA MEJORAR SU PUESTO DE TRABAJO"),
	NEGOCIO("PAQ02","PARA TENER SU NEGOCIO"),
	EMPLEO("PAQ03","BUSCAR EMPLEO"),
	HOBBY("PAQ04","POR HOBBY"),
	ESTUDIOS("PAQ05","POR ESTUDIOS");
	
	
	@Getter
	private final String codigo;
	@Getter
	private final String label;

	private EnumParaQueCurso(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	public static EnumParaQueCurso[] listaValores() {
		return values();
	}
}
