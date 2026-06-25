package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rol_perfil")
@Getter
@Setter
public class RolPerfil implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rper_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perf_id")
    private Perfil perfil;

    @Column(name = "rper_estado")
    private Boolean estado;

    // Constructor vacío requerido por la especificación de Jakarta Persistence
    public RolPerfil() {
    }
}
