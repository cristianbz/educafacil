/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jun 15, 2024
*
*/
@Getter
@Setter
public class ObjetosMenuDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rol_id;
	private String rol_nombre;
	private Boolean rol_estado;
	private Boolean rper_estado;
	private String per_id;
	private String per_nombre;
	private String per_icono;
	private Boolean per_estado;
	private String acc_nombre;
	private String acc_descripcion;
	private String acc_ruta;
	private Boolean acc_estado;
	private String acc_padre;
	private String acc_hija;
	private String acc_icono;
	
	public ObjetosMenuDto() {
		super();
	}

	/**
	 * @param rol_id
	 * @param rol_nombre
	 * @param rol_estado
	 * @param rper_estado
	 * @param per_id
	 * @param per_nombre
	 * @param per_estado
	 * @param acc_nombre
	 * @param acc_descripcion
	 * @param acc_ruta
	 * @param acc_estado
	 * @param acc_padre
	 * @param acc_hija
	 */
	public ObjetosMenuDto(String rol_id, String rol_nombre, Boolean rol_estado, Boolean rper_estado, String per_id,
			String per_nombre, Boolean per_estado, String acc_nombre, String acc_descripcion, String acc_ruta,
			Boolean acc_estado, String acc_padre, String acc_hija,String acc_icono) {
		super();
		this.rol_id = rol_id;
		this.rol_nombre = rol_nombre;
		this.rol_estado = rol_estado;
		this.rper_estado = rper_estado;
		this.per_id = per_id;
		this.per_nombre = per_nombre;
		this.per_estado = per_estado;
		this.acc_nombre = acc_nombre;
		this.acc_descripcion = acc_descripcion;
		this.acc_ruta = acc_ruta;
		this.acc_estado = acc_estado;
		this.acc_padre = acc_padre;
		this.acc_hija = acc_hija;
		this.acc_icono = acc_icono;
	}
}
