/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumRol {
	ADMINISTRADOR(1,"Admininstrador"),
	VENTAS(2,"Ventas"),
	MARKETING(3,"Marketing"),
	CONTADOR(4,"Contador"),
	ESTUDIANTE(5,"Estudiante");
	@Getter
	private final int codigo;
	@Getter
	private final String label;
	/**
	 * @param codigo
	 * @param label
	 */
	private EnumRol(int codigo, String label) {
		this.codigo = codigo;
		this.label = label;
	}
	
	

}

