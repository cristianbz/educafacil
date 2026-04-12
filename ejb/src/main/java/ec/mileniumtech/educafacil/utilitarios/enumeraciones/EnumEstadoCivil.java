/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumEstadoCivil {
	CASADO("ESTCIV01","CASADO"),
	DIVORCIADO("ESTCIV02","DIVORCIADO"),
	SOLTERO("ESTCIV03","SOLTERO"),
	UNIONLIBRE("ESTCIV04","UNION LIBRE"),
	VIUDO("ESTCIV05","VIUDO");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	
	private EnumEstadoCivil(String codigo,String label) {
		this.codigo=codigo;
		this.label=label;
	}
	
	public static EnumEstadoCivil[] listaValores() {
		return values();
	}
}
