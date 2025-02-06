package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
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

/**
 * Entity implementation class for Entity: Rol
 *
 */
@Entity
@Getter
@Setter
@Table(name="rol")
@NamedQueries({
	@NamedQuery(name = Rol.CARGAR_ROL, query = "SELECT R FROM Rol R ORDER BY R.rolId ASC")
	
})

public class Rol implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String CARGAR_ROL="cargarRol";
	
	@Id
	@SequenceGenerator(name="rolseq", sequenceName="rol_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="rolseq")
	@Column(name="rol_id")
	private Integer rolId;
	
	@Column(name="rol_nombre")
	private String rolNombre;
	
	@Column(name="rol_descripcion")
	private String rolDescripcion;
	
	@Column(name="rol_estado")
	private Boolean rolEstado;
	
	@OneToMany(mappedBy="rol", fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	private List<UsuarioRol> usuarioRol;

}
