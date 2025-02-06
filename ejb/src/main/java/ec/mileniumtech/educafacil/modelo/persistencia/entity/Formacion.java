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
@Table(name="formacion")
@NamedQueries({
	@NamedQuery(name = Formacion.LISTADO_FORMACIONES,query = "SELECT F FROM Formacion F WHERE F.instructor.instId=:codigoInstructor")
})
public class Formacion implements Serializable {


	private static final long serialVersionUID = 1L;
	public static final String LISTADO_FORMACIONES="listadoFormaciones";

	@Id
	@SequenceGenerator(name="formacionSeq", sequenceName="formacion_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="formacionSeq")
	@Column(name="form_id")
	private int formId;
	
	@Column(name="form_nivel_estudio")
	private String formNivelEstudio;
	
	@Column(name="form_titulo_academico")
	private String formTituloAcademico;
	
	@Column(name="form_institucion")
	private String formInstitucion;
	
	@Column(name="form_fecha_inicio")
	private Date formFechaInicio;
	
	@Column(name="form_fecha_fin")
	private Date formFechaFin;
	
	@ManyToOne
	@JoinColumn(name="inst_id",updatable = false, insertable = false)
	private Instructor instructor;
}
