/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jun 15, 2024
*
*/
@Getter
@Setter
public class DtoFlujoDinero {
	private double anio;
	private double mes;
	private Date fecha;
	private double valor;
}
