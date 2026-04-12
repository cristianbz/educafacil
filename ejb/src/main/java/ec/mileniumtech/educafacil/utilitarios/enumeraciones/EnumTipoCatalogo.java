/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
*@author christian  Jul 7, 2024
*
*/
public enum EnumTipoCatalogo {
	TIPOCAPACITACION(1,"Tipo Curso","TCAPA"),
	TIPONIVELESTUDIO(2,"Tipo Nivel Estudio","TPNES"),
	TIPOSEGUIMIENTO(3,"Tipo Seguimiento","TSEGM"),
	TIPOPAGO(4,"Tipo Pago","TPAGO");
	
	@Getter
	private final int codigo;
	@Getter
	private final String label;
	@Getter
	private final String nemotecnico;

	private EnumTipoCatalogo(int codigo, String label, String nemotecnico) {
		this.codigo = codigo;
		this.label = label;
		this.nemotecnico = nemotecnico;
	}

	
}

