/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumMedioContacto {
	CORREOELECTRONICO("MEC01","CORREO"),
	LLAMADATELEFONICA("MEC02","LLAMADA"),
	WHATSAPP("MEC03","WHATSAPP"),
	VISITAINSITU("MEC04","VISITA IN SITU"),
	OTRO("MEC05","OTRO");
	
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	private EnumMedioContacto(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	public static EnumMedioContacto[] listaValores() {
		return values();
	}
}

