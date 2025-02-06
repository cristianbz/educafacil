package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * @author christian [Christian Baez] 15 ene. 2022
 * @version 1.0.0
 *
 */
@Entity
@Getter
@Setter
@Table(name="campania")
@NamedQueries({
	@NamedQuery(name=Campania.CAMPANIAS_ACTIVAS,query = "SELECT C FROM Campania C WHERE C.campEstado = TRUE "),
	@NamedQuery(name=Campania.CAMPANIAS_TODAS,query = "SELECT C FROM Campania C "),
	@NamedQuery(name=Campania.CAMPANIA_CURSO,query = "SELECT C FROM Campania C WHERE C.campEstado = TRUE AND C.curso.cursId=:curso AND C.campFechaHasta>=current_date"),
	@NamedQuery(name=Campania.CAMPANIA_CURSO_ACTIVAS,query = "SELECT C FROM Campania c WHERE c.campEstado = TRUE AND c.campFechaHasta >= current_date")

//	@NamedQuery(name=Campania.CAMPANIA_CURSO_ACTIVAS,query = "SELECT C FROM Campania C WHERE C.campEstado = TRUE AND C.curso.cursId=:curso AND C.campFechaHasta>=current_date")
})
public class Campania  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final String CAMPANIAS_ACTIVAS="campaniasActivas";
	public static final String CAMPANIAS_TODAS="campaniasTodas";
	public static final String CAMPANIA_CURSO="campaniaCurso";
	public static final String CAMPANIA_CURSO_ACTIVAS="campaniaCursoActivas";
	
	@Id
	@SequenceGenerator(name="campaniaCampIdSeq", sequenceName="campania_camp_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="campaniaCampIdSeq")
	@Column(name="camp_id")
	private Integer campId;
	
	@Column(name="camp_descripcion")
	private String campDescripcion;
	
	@Column(name="camp_identificador")
	private String campIdentificador;

	@Column(name="camp_fecha_desde")
	private Date campFechaDesde;
	
	@Column(name="camp_fecha_hasta")
	private Date campFechaHasta;
	
	@Column(name="camp_costo")
	private BigDecimal campCosto;
	
	@Column(name="camp_tipo_contenido")
	private String campTipoContenido;
	
	@Column(name="camp_estado")
	private boolean campEstado;
	
	@Transient
	private String fechasCampania;
	
	@OneToMany(mappedBy="campania", fetch=FetchType.LAZY)
	private List<SeguimientoClientes> seguimientoClientes;
	
	@ManyToOne
	@JoinColumn(name="curs_id",updatable = true, insertable = true)
	private Curso curso;
}
