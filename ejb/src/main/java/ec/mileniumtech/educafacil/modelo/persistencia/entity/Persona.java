package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: Persona
 *
 */
@Entity
@Getter
@Setter
@Table(name="persona")
@NamedQueries({
	@NamedQuery(name=Persona.BUSCAR_POR_CEDULA,query="SELECT p FROM Persona p WHERE p.persDocumentoIdentidad=:cedula"),
	@NamedQuery(name=Persona.BUSCAR_POR_CEDULA_CORREO,query="SELECT p FROM Persona p WHERE p.persDocumentoIdentidad=:cedula AND p.persCorreoElectronico = :correo"),
	@NamedQuery(name=Persona.BUSCAR_POR_APELLIDOS,query="SELECT p FROM Persona p WHERE lower(p.persApellidos)=:apellidos"),
	@NamedQuery(name=Persona.BUSCAR_POR_ID,query="SELECT p FROM Persona p WHERE p.persId=:id"),
})
public class Persona implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String BUSCAR_POR_CEDULA="buscarPorCedula";
	public static final String BUSCAR_POR_APELLIDOS="buscarPorApellidos";
	public static final String BUSCAR_POR_CEDULA_CORREO="buscarPorCedulaCorreo";
	public static final String BUSCAR_POR_ID="buscarPorId";

	@Id
	@SequenceGenerator(name="personaSeq", sequenceName="persona_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="personaSeq")
	@Column(name="pers_id")
	private int persId;
   
	@Column(name="pers_nombres")
	private String persNombres;
	
	@Column(name="pers_apellidos")
	private String persApellidos;
	
	@Column(name="pers_documento_identidad")
	private String persDocumentoIdentidad;
	
	@Column(name="pers_telefono_mobil")
	private String persTelefonoMobil;
	
	@Column(name="pers_telefono_casa")
	private String persTelefonoCasa;
	
	@Column(name="pers_correo_electronico")
	private String persCorreoElectronico;
	
	@Column(name="pers_domicilio")
	private String persDomicilio;
	
	@Column(name="pers_nacionalidad")
	private String persNacionalidad;
	
	@Column(name="pers_estado_civil")
	private String persEstadoCivil;
	
	@Column(name="pers_cargas_familiares")
	private boolean persCargasFamiliares;
	
	@Column(name="pers_fecha_nacimiento")
	private Date persFechaNacimiento;
	
	@Column(name="pers_provincia")
	private Integer persProvincia;
	
	@Column(name="pers_ciudad")
	private Integer persCiudad;
	
	@Column(name="pers_sector")
	private String persSector;
	
	@OneToMany(mappedBy="persona", fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	private List<Estudiante> estudiantes;
	
	@OneToMany(mappedBy="persona", fetch=FetchType.LAZY)
	private List<Usuario> usuarios;
	
	@OneToMany(mappedBy="persona", fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	private List<Instructor> instructors;
	
	@OneToMany(mappedBy="persona", fetch=FetchType.LAZY)
	private List<Recursohumano> recursohumano;
	
	@OneToMany(mappedBy="persona", fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	private List<Vendedor> vendedor;
}
