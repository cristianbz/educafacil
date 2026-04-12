/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumMedioInformacion {
	CURSOEMPRESARIAL("MED01","CURSO EMPRESARIAL"),
	FACEBOOK("MED02","FACEBOOK"),
	PAGINAWEB("MED03","PAGINA WEB"),
	RADIO("MED04","RADIO"),
	RECOMENDACION("MED05","RECOMENDACION"),
	WHATSAPP("MED06","WHATSAPP"),
	INSTAGRAM("MED07","INSTAGRAM"),
	TICTOK("MED08","TICTOK");
	
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	private EnumMedioInformacion(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	public static EnumMedioInformacion[] listaValores() {
		return values();
	}
}

