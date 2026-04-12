/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumEstadosEgresos {
	REGISTRADO("EGRESO01","REGISTRADO"),
	ANULADO("INSMAT02","ANULADO"),
	ELIMINADO("INSMAT03","ELIMINADO");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	
	private EnumEstadosEgresos(String codigo,String label) {
		this.codigo=codigo;
		this.label=label;
	}
	
	public static EnumEstadosEgresos[] listaValores() {
		return values();
	}

}

