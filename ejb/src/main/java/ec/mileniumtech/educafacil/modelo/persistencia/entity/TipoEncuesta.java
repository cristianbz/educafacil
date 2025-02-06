package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.List;

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
/**
 * Entity implementation class for Entity: TipoEncuesta
 *
 */
@Entity
@Getter
@Setter
@Table(name="tipoEncuesta")
@NamedQueries({
	@NamedQuery(name = TipoEncuesta.CARGAR_TIPOS_ENCUESTAS, query = "SELECT TE FROM TipoEncuesta TE ORDER BY TE.objetoEvaluacion.objeNombre,TE.tipeDescripcion"),
	@NamedQuery(name = TipoEncuesta.CARGAR_TIPOS_ENCUESTAS_POR_OE, query = "SELECT TE FROM TipoEncuesta TE WHERE TE.objetoEvaluacion.objeId=:codigo ORDER BY TE.objetoEvaluacion.objeNombre,TE.tipeDescripcion")

})

public class TipoEncuesta implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_TIPOS_ENCUESTAS="cargarTiposEncuestas";
	public static final String CARGAR_TIPOS_ENCUESTAS_POR_OE="cargarTiposEncuestasPorOe";
	
	@Id
	@SequenceGenerator(name="tipoencuestaseq", sequenceName="tipoencuesta_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tipoencuestaseq")
	@Column(name="tipe_id")
	private Integer tipeId;
	
	@Column(name="tipe_descripcion")
	private String tipeDescripcion;

	@Column(name="tipe_estado")
	private Boolean tipeEstado;
	
	@OneToMany (mappedBy="tipoEncuesta", fetch=FetchType.LAZY)
	private List<TipoEncuestaPregunta> tipoEncuestaPregunta;
	
	@OneToMany (mappedBy="tipoEncuesta", fetch=FetchType.LAZY)
	private List<EvaluacionCurso> evaluacionCursos;
	
	@ManyToOne
	@JoinColumn(name="obje_id",updatable = true, insertable = true)
	private ObjetoEvaluacion objetoEvaluacion;
	
}

