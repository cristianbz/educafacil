package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: UsuarioRol
 *
 */
@Entity
@Getter
@Setter

@Table(name="usuario_rol")
@NamedQueries({
	@NamedQuery(name = UsuarioRol.CARGAR_Usuario_Rol, query = "SELECT UR FROM UsuarioRol UR ORDER BY UR.urolId ASC")
	
})

public class UsuarioRol implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_Usuario_Rol="cargarUsuarioRol";
	
	@Id
	@SequenceGenerator(name="usuarioRolSeq", sequenceName="usuario_rol_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="usuarioRolSeq")
	@Column(name="urol_id")
	private Integer urolId;
	
	@Column(name="urol_estado")
	private Boolean urolEstado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="usua_id",updatable = true, insertable = true)
	private Usuario usuario;
	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="rol_id",updatable = true, insertable = true)
	private Rol rol;
	
	
}
