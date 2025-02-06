/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.dto.pdf;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 6, 2024
*
*/
public class InscripcionMatriculaDto implements Serializable{

	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private String logotipo;
	@Getter
	@Setter
	private String nombres;
	@Getter
	@Setter
	private String apellidos;
	@Getter
	@Setter
	private String curso;
	@Getter
	@Setter
	private Date fechaMatriculaInscripcion;
	@Getter
	@Setter
	private Date fechaInicioCurso;
	@Getter
	@Setter
	private Date fechaFinCurso;
}

