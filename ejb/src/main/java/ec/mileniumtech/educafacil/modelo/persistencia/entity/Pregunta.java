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
 * Entity implementation class for Entity: Pregunta
 *
 */
@Entity
@Getter
@Setter
@Table(name="pregunta")
@NamedQueries({
	@NamedQuery(name = Pregunta.CARGAR_PREGUNTA, query = "SELECT P FROM Pregunta P ORDER BY P.categoriaRespuesta.catrNombre, P.pregDescripcion"),
	@NamedQuery(name = Pregunta.CARGAR_PREGUNTA_POR_CATEGORIA, query = "SELECT P FROM Pregunta P WHERE P.categoriaRespuesta.catrId=:codigo ORDER BY P.pregId")
})

public class Pregunta implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_PREGUNTA="cargarPregunta";
	public static final String CARGAR_PREGUNTA_POR_CATEGORIA="cargarPreguntaPorCategoria";
	
	@Id
	@SequenceGenerator(name="preguntaseq", sequenceName="pregunta_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="preguntaseq")
	@Column(name="preg_id")
	private Integer pregId;
	
	@Column(name="preg_descripcion")
	private String pregDescripcion;

	@Column(name="preg_estado")
	private Boolean pregEstado;
		
	@ManyToOne
	@JoinColumn(name="catr_id",updatable = true, insertable = true)
	private CategoriaRespuesta categoriaRespuesta;

	@OneToMany (mappedBy="pregunta", fetch=FetchType.LAZY)
	private List<TipoEncuestaPregunta> tipoEncuestaPregunta;
	
}

