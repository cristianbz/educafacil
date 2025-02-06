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
@Table(name="curso")
@NamedQueries({
	@NamedQuery(name = Curso.CARGAR_CURSOS,query = "SELECT C FROM Curso C ORDER BY C.cursNombre"),
	@NamedQuery(name = Curso.OFERTA_CURSOS_ACTIVOS,query = "SELECT C FROM Curso C JOIN C.ofertaCapacitacion OCA JOIN OCA.ofertacursos OCU WHERE OCU.ocurEstado='OCUR01' AND OCU.ocurPorDefecto=false ORDER BY C.cursNombre")
})
public class Curso implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_CURSOS="cargarCursos";
	public static final String OFERTA_CURSOS_ACTIVOS="ofertaCursosActivos";

	@Id
	@SequenceGenerator(name="cursoSeq", sequenceName="curso_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="cursoSeq")
	@Column(name="curs_id")
	private int cursId;
	
	@Column(name="curs_nombre")
	private String cursNombre;
   
	@OneToMany(mappedBy="curso", fetch=FetchType.LAZY)
	private List<OfertaCapacitacion> ofertaCapacitacion;
	
	@OneToMany(mappedBy="curso", fetch=FetchType.LAZY)
	private List<SeguimientoClientes> seguimientoClientes;
	
	@OneToMany(mappedBy="curso", fetch=FetchType.LAZY)
	private List<Campania> campania;
}
