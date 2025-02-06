package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
/**
 * Entity implementation class for Entity: ObjetoEvaluacion
 *
 */
@Entity
@Getter
@Setter
@Table(name="objetoEvaluacion")
@NamedQueries({
	@NamedQuery(name = ObjetoEvaluacion.CARGAR_OBJETO_EVALUACION, query = "SELECT OE FROM ObjetoEvaluacion OE ORDER BY OE.objeNombre")
})


public class ObjetoEvaluacion implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_OBJETO_EVALUACION="cargarObjetoEvaluacion";
	
	@Id
	@SequenceGenerator(name="objetoevaluacionseq", sequenceName="objetoevaluacion_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="objetoevaluacionseq")
	@Column(name="obje_id")
	private Integer objeId;
	
	@Column(name="obje_nombre")
	private String objeNombre;

	@Column(name="obje_estado")
	private Boolean objeEstado;
	
	@OneToMany (mappedBy="objetoEvaluacion", fetch=FetchType.LAZY)
	private List<TipoEncuesta> tipoEncuesta;

	
}

