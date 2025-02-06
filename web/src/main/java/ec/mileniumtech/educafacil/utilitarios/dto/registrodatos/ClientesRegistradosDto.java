/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.dto.registrodatos;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 7, 2024
*
*/
public class ClientesRegistradosDto implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private int per_id;
	@Getter
	@Setter
	private int cur_id;
	@Getter
	@Setter
    private int are_id;
	@Getter
	@Setter
    private int reg_id;
	@Getter
	@Setter
    private String per_apellidos;
	@Getter
	@Setter
    private String per_nombres;
	@Getter
	@Setter
    private String per_telefono_mobil;
	@Getter
	@Setter
    private String per_correo_electronico;
	@Getter
	@Setter
    private String per_cedula;
	@Getter
	@Setter
    private String cur_nombre;
	@Getter
	@Setter
    private Date per_fecha_nacimiento;
	@Getter
	@Setter
    private String per_domicilio;
	@Getter
	@Setter
    private String per_direccion_trabajo;
	@Getter
	@Setter
    private String per_cargo_ocupa;
	@Getter
	@Setter
    private String per_nivel_estudio;
	@Getter
	@Setter
	private String reg_observacion;
	@Getter
	@Setter
	private String reg_seg_observacion;
	@Getter
	@Setter
	private String reg_estado;
	@Getter
	@Setter
    private Date reg_fecha_creacion;
	@Getter
	@Setter
	private String reg_medio_informacion;
}

