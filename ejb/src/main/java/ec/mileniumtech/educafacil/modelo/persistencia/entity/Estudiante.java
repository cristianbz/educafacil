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
 * Entity implementation class for Entity: Estudiante
 *
 */
@Entity
@Getter
@Setter
@Table(name="estudiante")
@NamedQueries({
	@NamedQuery(name=Estudiante.BUSCA_POR_APELLIDO,query="SELECT E FROM Estudiante E WHERE LOWER(E.persona.persApellidos) LIKE :apellidos"),
	@NamedQuery(name=Estudiante.BUSCA_POR_CEDULA,query="SELECT E FROM Estudiante E WHERE E.persona.persDocumentoIdentidad = :cedula"),
})
public class Estudiante implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String BUSCA_POR_APELLIDO="buscaPorApellido";
	public static final String BUSCA_POR_CEDULA="buscaPorCedula";

	@Id
	@SequenceGenerator(name="estudianteSeq", sequenceName="estudiante_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="estudianteSeq")
	@Column(name="estu_id")
	private int estuId;
	
	@Column(name="estu_telefono_trabajo")
	private String estuTelefonoTrabajo;
	
	@Column(name="estu_cargo_ocupa")
	private String estuCargoOcupa;
	
	@Column(name="estu_nivel_estudio")
	private String estuNivelEstudio;
	
	@Column(name="estu_ultimo_curso")
	private String estuUltimoCurso;
	
	@Column(name="estu_direccion_trabajo")
	private String estuDireccionTrabajo;
	
	@Column(name="estu_ingresos_mensuales")
	private String estuIngresosMensuales;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="pers_id")
	private Persona persona;
	
	@OneToMany(mappedBy="estudiante", fetch=FetchType.LAZY)
	private List<Matricula> matriculas;
   
}
