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
 * Entity implementation class for Entity: Usuario
 *
 */
@Entity

@Table(name="usuario")
@NamedQueries({
	@NamedQuery(name=Usuario.BUSCAR_USUARIO_POR_USUARIO, query="SELECT u FROM Usuario u WHERE u.usuaUsuario=:usuario"),
	@NamedQuery(name=Usuario.BUSCAR_USUARIO_POR_NRO_IDENTIFICACION, query="SELECT u FROM Usuario u WHERE u.persona.persDocumentoIdentidad=:nrodocumento")
})

public class Usuario implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String BUSCAR_USUARIO_POR_USUARIO="UsuarioIg.buscarPorUsuario";
	public static final String BUSCAR_USUARIO_POR_NRO_IDENTIFICACION="UsuarioIg.buscarPorNroIdentificacion";
	
	@Id
	@SequenceGenerator(name="usuarioSeq", sequenceName="usuario_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="usuarioSeq")	
	@Column(name="usua_id")
	@Getter
	@Setter
	private Integer usuaId;
	
	@Getter
	@Setter
	@Column(name="usua_usuario")
	private String usuaUsuario;
	
	@Getter
	@Setter
	@Column(name="usua_clave")
	private String usuaClave;
	
	@Getter
	@Setter
	@Column(name="usua_fecha_registro")
	private Date usuaFechaRegistro;
	
	@Getter
	@Setter
	@Column(name="usua_fecha_inicio")
	private Date usuaFechaInicio;
	
	@Getter
	@Setter
	@Column(name="usua_fecha_caducidad")
	private Date usuaFechaCaducidad;
	
	@Getter
	@Setter
	@Column(name="usua_estado")
	private boolean usuaEstado;
	
	@Getter
	@Setter
	@Column(name="usua_token")
	private Integer usuaToken;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pers_id",updatable = true, insertable = true)
	private Persona persona;
	

//	@OneToMany(mappedBy="persona", fetch=FetchType.LAZY)
//	private List<Usuario> usuarios;

	
	@OneToMany(mappedBy="usuario", fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	private List<UsuarioRol> usuarioRol;
}
