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
@Table(name = "perfil_accion")
@Getter
@Setter
public class PerfilAccion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pacc_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perf_id", nullable = false)
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acc_hija", nullable = false)
    private Accion accion;

    @Column(name = "pacc_estado", nullable = false)
    private Boolean estado;

    // Constructor vacío requerido por JPA
    public PerfilAccion() {
    }

}
