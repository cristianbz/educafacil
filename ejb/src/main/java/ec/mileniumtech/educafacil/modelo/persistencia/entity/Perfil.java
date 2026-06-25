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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "perfil")
@Getter
@Setter
public class Perfil implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfil_seq_gen")
    @SequenceGenerator(
        name = "perfil_seq_gen", 
        sequenceName = "perfil_seq", 
        allocationSize = 1
    )
    @Column(name = "perf_id", nullable = false)
    private Integer id;

    @Column(name = "perf_nombre", length = 50)
    private String nombre;

    @Column(name = "perf_descripcion", length = 200)
    private String descripcion;

    @Column(name = "perf_estado")
    private Boolean estado;

    @Column(name = "perf_icono", length = 20)
    private String icono;
    
    @OneToMany (mappedBy="perfil", fetch=FetchType.LAZY)
	private List<PerfilAccion> perfilAccion;
    
    @OneToMany (mappedBy="perfil", fetch=FetchType.LAZY)
	private List<RolPerfil> rolPerfil;
    // Constructor vacío requerido por JPA
    public Perfil() {
    }

}
