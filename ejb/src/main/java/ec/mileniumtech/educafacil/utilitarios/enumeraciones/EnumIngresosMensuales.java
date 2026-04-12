/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumIngresosMensuales {
	BASICO("INGR01","entre $100 y $420"),
	INTERMEDIO("INGR02","entre $421 y $800"),
	ALTO("INGR03","entre $801 y $1500"),
	MUYALTO("INGR04","mayor a $1500");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	
	private EnumIngresosMensuales(String codigo,String label) {
		this.codigo=codigo;
		this.label=label;
	}
	
	public static EnumIngresosMensuales[] listaValores() {
		return values();
	}
}

