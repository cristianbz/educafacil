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
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.NamedQueries;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: OfertaCapacitacion
 *
 */
@Entity
@Getter
@Setter
@Table(name="ofertacapacitacion")
@NamedQueries({
	@NamedQuery(name = OfertaCapacitacion.OFERTA_CAPACITACION,query = "SELECT OC FROM OfertaCapacitacion OC WHERE OC.area.areaId=:area AND OC.curso.cursId=:curso AND OC.especialidad.espeId=:especialidad AND OC.ofcaEstado=true"),
	@NamedQuery(name=OfertaCapacitacion.LISTA_ESPECIALIDAD_POR_AREA,query = "SELECT DISTINCT OC.especialidad FROM OfertaCapacitacion OC WHERE OC.area.areaId=:area"),
	@NamedQuery(name=OfertaCapacitacion.LISTA_CURSOS_POR_AREA_ESPECIALIDAD,query = "SELECT DISTINCT OC.curso FROM OfertaCapacitacion OC WHERE OC.area.areaId=:area AND OC.especialidad.espeId=:especialidad"),
	@NamedQuery(name = OfertaCapacitacion.BUSCAR_POR_CURSO,query = "SELECT OC FROM OfertaCapacitacion OC WHERE OC.curso.cursId=:curso AND OC.ofcaEstado=true"),
	@NamedQuery(name = OfertaCapacitacion.CARGAR_TODAS_OFERTAS,query = "SELECT OC FROM OfertaCapacitacion OC ORDER BY OC.curso.cursNombre")
})
public class OfertaCapacitacion implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String OFERTA_CAPACITACION="ofertaCapacitacion";
	public static final String LISTA_ESPECIALIDAD_POR_AREA="listaEspecialidadPorArea";
	public static final String LISTA_CURSOS_POR_AREA_ESPECIALIDAD="listaCursosPorAreaEspecialidad";
	public static final String BUSCAR_POR_CURSO="buscarPorCurso";
	public static final String CARGAR_TODAS_OFERTAS="cargarTodasOfertas";

	@Id
	@SequenceGenerator(name="ofertaCapacitacionSeq", sequenceName="oferta_capacitacion_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ofertaCapacitacionSeq")
	@Column(name="ofca_id")
	private int ofcaId;
	
	@Column(name="ofca_estado")
	private boolean ofcaEstado;
	
	@ManyToOne
	@JoinColumn(name="area_id",updatable = true, insertable = true)
	private Area area;
	
	@ManyToOne
	@JoinColumn(name="espe_id",updatable = true, insertable = true)
	private Especialidad especialidad;
	
	@ManyToOne()
	@JoinColumn(name="curs_id",updatable = true, insertable = true)
	private Curso curso;
   
	@OneToMany(mappedBy="ofertaCapacitacion", fetch=FetchType.LAZY)
	private List<OfertaCursos> ofertacursos;
}

