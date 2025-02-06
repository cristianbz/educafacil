package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cbaez [Christian Baez] Nov 18, 2020
 * @version 1.0.0
 *
 */
@Entity
@Getter
@Setter
@Table(name="capacitacion")
@NamedQueries({
	@NamedQuery(name = Capacitacion.LISTADO_CAPACITACIONES,query = "SELECT C FROM Capacitacion C WHERE C.instructor.instId=:codigoInstructor")
})
public class Capacitacion implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String LISTADO_CAPACITACIONES="listadoCapacitaciones";
	
	@Id
	@SequenceGenerator(name="capacitacionSeq", sequenceName="capacitacion_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="capacitacionSeq")
	@Column(name="capa_id")
	private int capaId;
	
	@Column(name="capa_tipo")
	private String capaTipo;
	
	@Column(name="capa_nombre")
	private String capaNombre;
	
	@Column(name="capa_institucion")
	private String capaInstitucion;
	
	@Column(name="capa_fecha_inicio")
	private Date capaFechaInicio;
	
	@Column(name="capa_fecha_fin")
	private Date capaFechaFin;
	
	@ManyToOne
	@JoinColumn(name="inst_id",updatable = false, insertable = false)
	private Instructor instructor;
}
