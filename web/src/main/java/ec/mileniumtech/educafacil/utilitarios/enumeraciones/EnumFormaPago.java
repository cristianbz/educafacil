/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumFormaPago {
	EFECTIVO("FORMP01","EFECTIVO"),
	TRANSFERENCIA("FORMP02","TRANSFERENCIA"),
	TARJETA("FORMP03","TARJETA");
	
	
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	private EnumFormaPago(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	public static EnumFormaPago[] listaValores() {
		return values();
	}
}

