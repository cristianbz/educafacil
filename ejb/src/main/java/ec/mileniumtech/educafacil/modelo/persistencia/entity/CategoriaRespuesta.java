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
@Entity
@Getter
@Setter
@Table(name="categoriaRespuesta")
@NamedQueries({
	@NamedQuery(name = CategoriaRespuesta.CARGAR_CATEGORIA, query = "SELECT CR FROM CategoriaRespuesta CR ORDER BY CR.catrNombre"),
	@NamedQuery(name = CategoriaRespuesta.BUSCAR_CATEGORIA, query = "SELECT CR FROM CategoriaRespuesta CR WHERE CR.catrId=:codigo ORDER BY CR.catrNombre")
})

public class CategoriaRespuesta implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_CATEGORIA="cargarCategoria";
	public static final String BUSCAR_CATEGORIA="buscarCategoria";
	
	@Id
	@SequenceGenerator(name="categoriarespuestaSeq", sequenceName="categoriarespuesta_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="categoriarespuestaSeq")
	@Column(name="catr_id")
	private Integer catrId;
	
	@Column(name="catr_nombre")
	private String catrNombre;

	@Column(name="catr_estado")
	private Boolean catrEstado;
	
	@OneToMany(mappedBy="categoriaRespuesta", fetch=FetchType.LAZY)
	private List<Respuestas> respuestas;
	
	@OneToMany(mappedBy="categoriaRespuesta", fetch=FetchType.LAZY)
	private List<Pregunta> pregunta;
	
}