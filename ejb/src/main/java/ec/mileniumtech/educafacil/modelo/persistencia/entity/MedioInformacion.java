/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jun 15, 2024
*
*/
@Entity
@Table(name="medioinformacion")
@Getter
@Setter
@NamedQueries({
	@NamedQuery(name = MedioInformacion.LISTADO_MEDIOS_INFORMACION,query = "SELECT M FROM MedioInformacion M")
})
public class MedioInformacion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LISTADO_MEDIOS_INFORMACION="listadoMediosInformacion";

	@Id
	@SequenceGenerator(name="medioinformacionSeq", sequenceName="medioinformacion_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="medioinformacionSeq")
	@Column(name="mein_id")
	private int meinId;
	
	@Column(name="mein_nombre")
	private String meinNombre;
}
