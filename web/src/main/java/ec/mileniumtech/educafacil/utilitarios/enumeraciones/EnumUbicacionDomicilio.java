/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumUbicacionDomicilio {
	NORTE("UDOM01","NORTE"),
	CENTRONORTE("UDOM02","CENTRO NORTE"),
	CENTROSUR("UDOM03","CENTRO SUR"),
	CENTRO("UDOM04","CENTRO"),
	SUR("UDOM05","SUR"),
	PERIFERIA("UDOM06","PERIFERIA"),
	FUERACIUDAD("UDOM07","FUERACIUDAD");
	@Getter
	private final String codigo;
	@Getter
	private final String label;
	private EnumUbicacionDomicilio(String codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	public static EnumUbicacionDomicilio[] listaValores() {
		return values();
	}
}

