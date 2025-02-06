package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;

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
 * Entity implementation class for Entity: Respuestas
 *
 */
@Entity
@Getter
@Setter
@Table(name="respuestas")
@NamedQueries({
	@NamedQuery(name = Respuestas.CARGAR_RESPUESTAS, query = "SELECT R FROM Respuestas R ORDER BY R.respOrden"),
	@NamedQuery(name = Respuestas.CARGAR_RESPUESTAS_POR_CATEGORIA, query = "SELECT R FROM Respuestas R WHERE R.categoriaRespuesta.catrId=:codigo ORDER BY R.respOrden")
})
public class Respuestas  implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_RESPUESTAS="cargarRespuestas";
	public static final String CARGAR_RESPUESTAS_POR_CATEGORIA="cargarRespuestasPorCategoria";
	
	@Id
	@SequenceGenerator(name="respuestasseq", sequenceName="respuestas_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="respuestasseq")
	@Column(name="resp_id")
	private Integer respId;
	
	@Column(name="resp_descripcion")
	private String respDescripcion;

	@Column(name="resp_orden")
	private int respOrden;
	
	@Column(name="resp_estado")
	private boolean respEstado;
	
	@ManyToOne
	@JoinColumn(name="catr_id",updatable = true, insertable = true)
	private CategoriaRespuesta categoriaRespuesta;

	
	
}
