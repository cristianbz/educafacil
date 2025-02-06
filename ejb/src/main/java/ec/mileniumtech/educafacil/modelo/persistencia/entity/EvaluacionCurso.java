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

@Entity
@Getter
@Setter
@Table(name="evaluacionCurso")
@NamedQueries({
	@NamedQuery(name = EvaluacionCurso.CARGAR_EVALUACION_CURSO, query = "SELECT EC FROM EvaluacionCurso EC WHERE EC.evcuEstado=TRUE ORDER BY EC.ofertacursos.ocurFechaInicio"),
	@NamedQuery(name = EvaluacionCurso.CARGAR_ENCUESTAS_POR_CURSO_OBJETOEVALUACION, query = "SELECT EC FROM EvaluacionCurso EC WHERE EC.ofertacursos.ocurId=:codigo AND EC.tipoEncuesta.objetoEvaluacion.objeId=:codigoobj AND EC.evcuEstado=TRUE ORDER BY EC.evcuId"),
	@NamedQuery(name = EvaluacionCurso.CARGAR_ENCUESTAS_POR_CURSO_ACTIVO, query = "SELECT EC FROM EvaluacionCurso EC WHERE EC.ofertacursos.ocurId=:codigoOferta AND EC.evcuEstado=TRUE")
})
//AND EC.evcuEstado=TRUE 
public class EvaluacionCurso implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_EVALUACION_CURSO="cargarEvaluacionCurso";
	public static final String CARGAR_ENCUESTAS_POR_CURSO_OBJETOEVALUACION="cargarEncuestasPorCursoObjetoEvaluacion";
	public static final String CARGAR_ENCUESTAS_POR_CURSO_ACTIVO="cargarEncuestasPorCursoActivo";
			
	@Id
	@SequenceGenerator(name="evaluacioncursoseq", sequenceName="evaluacioncurso_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="evaluacioncursoseq")
	@Column(name="evcu_id")
	private Integer evcuId;
	
	@Column(name="evcu_fecha_maxima_evalua")
	private Date evcuFechaMaximaEvalua;
	
	@Column(name="evcu_estado")
	private boolean evcuEstado;
	
	@OneToMany (mappedBy="evaluacionCurso", fetch=FetchType.LAZY)
	private List<DetalleEvaluaCurso> detalleEvaluaCurso;
	
	@ManyToOne
	@JoinColumn(name="ocur_id",updatable = true, insertable = true)
	private OfertaCursos ofertacursos;
	
	@ManyToOne
	@JoinColumn(name="tipe_id",updatable = true, insertable = true)
	private TipoEncuesta tipoEncuesta;
	
}