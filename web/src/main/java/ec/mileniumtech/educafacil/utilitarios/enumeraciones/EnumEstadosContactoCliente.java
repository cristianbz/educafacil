/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumEstadosContactoCliente {
	ENSEGUIMIENTO("CONTAC01","EN SEGUIMIENTO"),
	ABANDONADO("CONTAC02","ABANDONADO"),
	MATRICULADO("CONTAC03","MATRICULADO"),
	CANDIDATO("CONTAC04","CANDIDATO"),
	PROXIMAOCASION("CONTAC05","PROXIMAOCASION");

	@Getter
	private final String codigo;
	@Getter
	private final String label;
	
	private EnumEstadosContactoCliente(String codigo,String label) {
		this.codigo=codigo;
		this.label=label;
	}
	public static EnumEstadosContactoCliente[] listaValores() {
		return values();
	}
}
