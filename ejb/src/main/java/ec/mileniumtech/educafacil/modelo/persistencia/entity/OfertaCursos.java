package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
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
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: OfertaCurso
 *
 */
@Entity
@Table(name = "ofertacursos")
@Getter
@Setter
@NamedQueries({
	@NamedQuery(name = OfertaCursos.OFERTA_CURSOS_DISPONIBLES_POR_CURSO,query = "SELECT OC FROM OfertaCursos OC WHERE OC.ofertaCapacitacion.ofcaId=:ofertaCapacitacion AND OC.ocurEstado='OCUR01' AND OC.ocurPorDefecto=false"),
	@NamedQuery(name = OfertaCursos.OFERTA_CURSOS_DISPONIBLES_ACTIVOS,query = "SELECT OC FROM OfertaCursos OC WHERE OC.ocurEstado='OCUR01' AND OC.ocurPorDefecto=false"),
	@NamedQuery(name = OfertaCursos.OFERTA_CURSOS_ACTIVOS_CERRADOS,query = "SELECT OC FROM OfertaCursos OC WHERE OC.ocurEstado IN ('OCUR01','OCUR02') AND OC.ocurPorDefecto=false ORDER BY OC.ofertaCapacitacion.curso.cursNombre"),
	@NamedQuery(name = OfertaCursos.OFERTA_CURSOS_POR_DEFECTO,query = "SELECT OC FROM OfertaCursos OC WHERE OC.ocurPorDefecto=true"),
	@NamedQuery(name = OfertaCursos.OFERTA_CURSOS_POR_CURSO,query = "SELECT OC FROM OfertaCursos OC WHERE OC.ofertaCapacitacion.curso.cursId=:cursoId AND OC.ocurPorDefecto=false")
})
public class OfertaCursos implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String OFERTA_CURSOS_DISPONIBLES_POR_CURSO="ofertaCursosDisponiblesPorCurso";
	public static final String OFERTA_CURSOS_DISPONIBLES_ACTIVOS="ofertaCursosDisponiblesActivos";
	public static final String OFERTA_CURSOS_ACTIVOS_CERRADOS="ofertaCursosActivosCerrados";
	public static final String OFERTA_CURSOS_POR_DEFECTO="ofertaCursosPorDefecto";
	public static final String OFERTA_CURSOS_POR_CURSO="ofertaCursosPorCurso";
	
	@Id
	@SequenceGenerator(name="ofertacursosSeq", sequenceName="ofertacursos_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ofertacursosSeq")
	@Column(name="ocur_id")
	private int ocurId;
	
	@Column(name = "ocur_fecha_inicio")
	private Date ocurFechaInicio;
   
	@Column(name = "ocur_fecha_fin")
	private Date ocurFechaFin;
	
	@Column(name = "ocur_valor")
	private double ocurValor;
	
	@Column(name = "ocur_descuento")
	private double ocurDescuento;
	
	@Column(name = "ocur_tipo")
	private String ocurTipo;
	
	@Column(name = "ocur_estado")
	private String ocurEstado;
	
	@Column(name="ocur_duracion")
	private int ocurDuracion;
	
	@Column(name="ocur_por_defecto")
	private boolean ocurPorDefecto;
	
	@Column(name = "ocur_horario")
	private String ocurHorario;	
	
	@Column(name = "ocur_grupo_whatsapp")
	private String ocurGrupoWhatsapp;	
	
	@OneToMany (mappedBy="ofertacursos", fetch=FetchType.LAZY)
	private List<EvaluacionCurso> evaluacionCurso;
	
	@ManyToOne()
	@JoinColumn(name="ofca_id")
	private OfertaCapacitacion ofertaCapacitacion;
	
	@ManyToOne
	@JoinColumn(name="inst_id",updatable = true, insertable = true)
	private Instructor instructor;
}
