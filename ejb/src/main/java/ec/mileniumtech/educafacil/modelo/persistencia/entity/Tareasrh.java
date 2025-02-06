package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="tareasrh")
public class Tareasrh implements Serializable {
	@Id
	@SequenceGenerator(name="tareasrhTrhuIdSeq", sequenceName="tareasrh_trhu_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tareasrhTrhuIdSeq")
	@Column(name="trhu_id")
	private int trhuId;
	
	@ManyToOne
	@JoinColumn(name="rehu_id",updatable = false, insertable = false)
	private Recursohumano recursohumano;
	
	@Column(name="trhu_actividad")
	private String trhuActividad;
	
	@Column(name="trhu_estado")
	private String trhuEstado;
	
	@Column(name="trhu_fecha_entrega")
	private Date trhuFechaEntrega;
	
	@Column(name="trhu_fecha_asignacion")
	private Date trhuFechaAsignacion;
	
	@Column(name="trhu_fecha_finalizacion")
	private Date trhuFechaFinalizacion;
}
