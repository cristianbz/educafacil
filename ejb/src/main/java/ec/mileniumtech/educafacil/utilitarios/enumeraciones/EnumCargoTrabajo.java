/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumCargoTrabajo {
	OPERATIVO("CARG01","OPERATIVO"),
	JEFE("CARG02","JEFE"),
	GERENTE("CARG03","GERENTE"),
	NOTRABAJA("CARG04","NO TRABAJA");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	/**
	 * @param codigo
	 * @param label
	 */
	private EnumCargoTrabajo(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	public static EnumCargoTrabajo[] listaValores() {
		return values();
	}
}

