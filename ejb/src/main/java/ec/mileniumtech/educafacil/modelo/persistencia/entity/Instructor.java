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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: Instructor
 *
 */
@Entity
@Table(name = "instructor")
@Getter
@Setter
@NamedQueries({
	@NamedQuery(name = Instructor.LISTADO_INSTRUCTORES,query = "SELECT I FROM Instructor I ORDER BY I.persona.persApellidos")
})
public class Instructor implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String LISTADO_INSTRUCTORES="listadoInstructores";
	@Id
	@SequenceGenerator(name="instructorSeq", sequenceName="instructor_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="instructorSeq")
	@Column(name="inst_id")
	private int instId;
	
	@Column(name="inst_activo")
	private boolean instActivo;
	
	@Column(name="inst_fecha_ingreso")
	private Date instFechaIngreso;
	
	@Column(name="inst_fecha_salida")
	private Date instFechaSalida;
	

	// === DATOS ACADÉMICOS Y COMPETENCIA PROFESIONAL ===
	@Column(name="inst_experiencia_profesional", columnDefinition="TEXT")
	private String instExperienciaProfesional;

	@Column(name="inst_experiencia_docente", columnDefinition="TEXT")
	private String instExperienciaDocente;

	// === HABILIDADES PEDAGÓGICAS Y EVALUACIÓN DE CALIDAD ===
	@Column(name="inst_metodologias", columnDefinition="TEXT")
	private String instMetodologias;

	@Column(name="inst_modalidades_entrega", columnDefinition="TEXT")
	private String instModalidadesEntrega;

	@Column(name="inst_portafolio_materiales", columnDefinition="TEXT")
	private String instPortafolioMateriales;

	@Column(name="inst_evaluaciones_referencias", columnDefinition="TEXT")
	private String instEvaluacionesReferencias;

	// === INFORMACIÓN ADMINISTRATIVA Y DE CONTACTO ===
	@Column(name="inst_linkedin")
	private String instLinkedin;

	@Column(name="inst_disponibilidad_horaria", columnDefinition="TEXT")
	private String instDisponibilidadHoraria;

	@Column(name="inst_disponibilidad_geografica", columnDefinition="TEXT")
	private String instDisponibilidadGeografica;

	@Column(name="inst_regimen_fiscal")
	private String instRegimenFiscal;

	@Column(name="inst_datos_bancarios", columnDefinition="TEXT")
	private String instDatosBancarios;

	// === APTITUDES BLANDAS ===
	@Column(name="inst_habilidades_comunicacion", columnDefinition="TEXT")
	private String instHabilidadesComunicacion;

	@Column(name="inst_adaptabilidad", columnDefinition="TEXT")
	private String instAdaptabilidad;

	@Column(name="inst_notas_entrevista", columnDefinition="TEXT")
	private String instNotasEntrevista;

	@ManyToOne
	@JoinColumn(name="pers_id",insertable = true,updatable = true)
	private Persona persona;

	@OneToMany(mappedBy="instructor", fetch=FetchType.LAZY)
	private List<OfertaCursos> ofertaCursos;
	
	@OneToMany(mappedBy="instructor", fetch=FetchType.LAZY)
	private List<Capacitacion> capacitaciones;
	
	@OneToMany(mappedBy="instructor", fetch=FetchType.LAZY)
	private List<Formacion> formaciones;
	
	@OneToMany(mappedBy="instructor", fetch=FetchType.LAZY)
	private List<PlanificacionCurso> planificacionCurso;
}
