/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jun 15, 2024
*
*/
@Getter
@Setter
public class DtoEncuestas {
	private String pregunta;
	private String respuesta;
	private String codigoPregResp;
}
