/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumEstadosMatricula {
	INSCRITO("INSMAT01","INSCRITO"),
	MATRICULADO("INSMAT02","MATRICULADO"),
	DESERTADO("INSMAT03","DESERTADO"),
	CULMINADO("INSMAT05","CULMINADO");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	
	private EnumEstadosMatricula(String codigo,String label) {
		this.codigo=codigo;
		this.label=label;
	}
	
	public static EnumEstadosMatricula[] listaValores() {
		return values();
	}
}

