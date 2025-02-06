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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
@Table(name="detalleEvaluaCurso")
//@NamedQueries({
//	@NamedQuery(name = TipoEncuesta.CARGAR_TIPOS_ENCUESTAS, query = "SELECT TE FROM TipoEncuesta TE ORDER BY TE.objetoEvaluacion.objeNombre,TE.tipeDescripcion")
//})

public class DetalleEvaluaCurso implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_DETALLE_EVALUACION="cargarTiposEncuestas";
	
	@Id
	@SequenceGenerator(name="detalleevaluacursoseq", sequenceName="detalleevaluacurso_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="detalleevaluacursoseq")
	@Column(name="devc_id")
	private Integer devcId;
	
	@Column(name="devc_fecha_registro")
	private Date devcFechaRegistro;
	

	@ManyToOne
	@JoinColumn(name = "preg_id",updatable = true, insertable = true)
	private Pregunta pregunta;

	@ManyToOne
	@JoinColumn(name = "resp_id",updatable = true, insertable = true)
	private Respuestas respuestas;
	
	@ManyToOne
	@JoinColumn(name="evcu_id",updatable = true, insertable = true)
	private EvaluacionCurso evaluacionCurso;
	
	@ManyToOne
	@JoinColumn(name="matr_id",updatable = true, insertable = true)
	private Matricula matricula;
}