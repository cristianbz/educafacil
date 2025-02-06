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
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: Matricula
 *
 */
@Entity
@Getter
@Setter
@Table(name="matricula")
@NamedQueries({
	@NamedQuery(name = Matricula.INSCRIPCION_MATRICULA_CULMINACION_ALUMNO,query = "SELECT M FROM Matricula M WHERE M.matrEstado=:codigoEstado AND M.estudiante.persona.persId=:codigoPersona"),
	@NamedQuery(name = Matricula.BUSCAR_INSCRIPCION,query = "SELECT M FROM Matricula M WHERE M.matrEstado=:codigoEstado AND M.matrFechaInscripcion>=:fechaInicio AND M.matrFechaInscripcion<=:fechaFin"),
	@NamedQuery(name = Matricula.BUSCAR_MATRICULA_PORFECHA,query = "SELECT M FROM Matricula M WHERE M.matrEstado=:codigoEstado AND M.matrFechaMatricula>=:fechaInicio AND M.matrFechaMatricula<=:fechaFin"),
	@NamedQuery(name = Matricula.BUSCAR_MATRICULADOS_O_ENCURSO_POR_OFERTA,query = "SELECT M FROM Matricula M WHERE M.matrEstado ='INSMAT02' AND M.ofertaCursos.ocurId=:codigoOferta"),
	@NamedQuery(name = Matricula.BUSCAR_OPORTUNIDADES,query = "SELECT M FROM Matricula M WHERE M.matrEstado ='INSMAT01'"),
	@NamedQuery(name = Matricula.BUSCAR_MATRICULAS_ALUMNO,query = "SELECT M FROM Matricula M WHERE M.estudiante.estuId=:codigoEstudiante AND M.ofertaCursos.ocurPorDefecto=false"),
	@NamedQuery(name = Matricula.BUSCAR_POR_CURSO_ESTADO,query = "SELECT M FROM Matricula M WHERE M.ofertaCursos.ofertaCapacitacion.curso.cursId=:codigoCurso AND M.matrEstado=:codigoEstado"),
	@NamedQuery(name = Matricula.BUSCAR_MATRICULA_ESTUDIANTE_CURSO,query = "SELECT M FROM Matricula M WHERE M.ofertaCursos.ocurId=:codigoOferta AND M.estudiante.estuId=:codigoEstudiante AND M.matrEstado ='INSMAT02'"),
	@NamedQuery(name = Matricula.BUSCAR_MATRICULA_POR_OFERTA_CURSO,query = "SELECT M FROM Matricula M WHERE M.ofertaCursos.ocurId=:codigoOferta AND M.ofertaCursos.ocurEstado IN('OCUR01','OCUR02') ORDER BY M.estudiante.persona.persApellidos"),
	@NamedQuery(name = Matricula.BUSCAR_MATRICULAS_ALUMNO_ACTIVAS,query = "SELECT M FROM Matricula M WHERE M.estudiante.estuId=:codigoEstudiante AND M.ofertaCursos.ocurPorDefecto=false AND M.matrEstado='INSMAT02'")
})
public class Matricula implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String INSCRIPCION_MATRICULA_CULMINACION_ALUMNO="inscripcionMatriculaCulminacionAlumno";
	public static final String BUSCAR_INSCRIPCION="buscarInscripcion";
	public static final String BUSCAR_MATRICULA_PORFECHA="buscarMatriculaOEncurso";
	public static final String BUSCAR_MATRICULADOS_O_ENCURSO_POR_OFERTA="buscarMatriculadosOEncursoPorOferta";
	public static final String BUSCAR_OPORTUNIDADES="buscarOportunidades";
	public static final String BUSCAR_MATRICULAS_ALUMNO="buscarMatriculasAlumno";
	public static final String BUSCAR_POR_CURSO_ESTADO ="buscarPorCursoEstado";
	public static final String BUSCAR_MATRICULA_ESTUDIANTE_CURSO ="buscarMatriculaEstudianteCurso";
	public static final String BUSCAR_MATRICULA_POR_OFERTA_CURSO ="buscarMatriculaPorOfertaCurso";
	public static final String BUSCAR_MATRICULAS_ALUMNO_ACTIVAS="buscarMatriculasAlumnoActivas";
	@Id
	@SequenceGenerator(name="matriculaSeq", sequenceName="matricula_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="matriculaSeq")
	@Column(name="matr_id")
	private Integer matrId;
   
	@Column(name="matr_estado")
	private String matrEstado;
	
	@Column(name="matr_fecha_registro")
	private Date matrFechaRegistro;
	
	@Column(name="matr_motivo_abandono")
	private String matrMotivoAbandono;
	
	@Column(name="matr_facturacion_empresa")
	private boolean matrFacturacionEmpresa;
	
	@Column(name="matr_fecha_matricula")
	private Date matrFechaMatricula;
	
	@Column(name="matr_fecha_inscripcion")
	private Date matrFechaInscripcion;
	
		
	@Column(name="matr_observacion")
	private String matrObservacion;
	
	@Column(name="matr_medio_informacion")
	private String matrMedioInformacion;
	
	@Column(name="matr_para_que_curso")
	private String matrParaQueCurso;
	
	@Column(name="matr_evaluaciones_realizadas")
	private String matrEvaluacionesRealizadas;
	
	@Column(name="matr_trabaja_en_area")
	private boolean matrTrabajaEnArea;
	
	@Transient
	private double totalPagadoCurso;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="estu_id")
	private Estudiante estudiante;
	
	@OneToMany(mappedBy="matricula", fetch=FetchType.LAZY)
	private List<Pagos> pagos;
	
//	@ManyToOne(optional = false)
//	@JoinColumn(name="mein_id")
//	private MedioInformacion medioInformacion;
	
	@ManyToOne
	@JoinColumn(name="ocur_id",updatable = true, insertable = true)
	private OfertaCursos ofertaCursos;
	
	@ManyToOne
	@JoinColumn(name ="empr_id")
	private Empresa empresa;
	
	@OneToMany(mappedBy="matricula", fetch=FetchType.LAZY)
	private List<Seguimiento> seguimientos;
}
