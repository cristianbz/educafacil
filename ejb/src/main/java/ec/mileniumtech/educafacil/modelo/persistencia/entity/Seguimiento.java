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
 * Entity implementation class for Entity: Seguimiento
 *
 */
@Entity
@Getter
@Setter
@Table(name="seguimiento")
@NamedQueries({
	@NamedQuery(name=Seguimiento.BUSCAR_POR_MATRICULA,query="SELECT S FROM Seguimiento S WHERE S.matricula.matrId=:matricula")
})
public class Seguimiento implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String BUSCAR_POR_MATRICULA="buscarPorMatricula";

	@Id
	@SequenceGenerator(name="seguimientoSeq", sequenceName="seguimiento_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seguimientoSeq")
	@Column(name="segm_id")
	private int segmId;
	
	@Column(name="segm_fecha_registro")
	private Date segmFechaRegistro;
	
	@Column(name="segm_tipo_tarea")
	private String segmTipoTarea;
	
	@Column(name="segm_tarea")
	private String segmTarea;
	
	@Column(name="segm_mensaje_respuesta_tarea")
	private String segmMensajeRespuestaTarea;
	
	@Column(name="segm_fecha_ejecucion_tarea")
	private Date segmFechaEjecucionTarea;
	
	@Column(name="segm_hora_tarea")
	private Date segmHoraTarea;
	
	@Column(name="segm_culminado")
	private boolean segmCulminado;
	
	@Column(name="segm_notas_tarea")
	private String segmNotasTarea;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="matr_id")
	private Matricula matricula;
   
}
