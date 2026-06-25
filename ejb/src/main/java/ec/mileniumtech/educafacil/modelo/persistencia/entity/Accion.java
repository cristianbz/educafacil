package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accion")
@Getter
@Setter
public class Accion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "acc_hija", length = 10, nullable = false)
    private String id; // acc_hija es la Clave Primaria

    @Column(name = "acc_nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "acc_descripcion", length = 200, nullable = false)
    private String descripcion;

    @Column(name = "acc_ruta", length = 500, nullable = false)
    private String ruta;

    @Column(name = "acc_estado", nullable = false)
    private Boolean estado;

    @Column(name = "acc_icono", length = 20)
    private String icono;
    
    @OneToMany (mappedBy="accion", fetch=FetchType.LAZY)
	private List<PerfilAccion> perfilAccion;

    // Relación Reflexiva: Muchas acciones pueden pertenecer a una acción Padre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acc_padre", referencedColumnName = "acc_hija")
    private Accion padre;

    // Opcional: Lista de acciones hijas (útil si necesitas renderizar árboles o menús en PrimeFaces)
    @OneToMany(mappedBy = "padre", fetch = FetchType.LAZY)
    private List<Accion> subAcciones;

    // Constructor vacío requerido por JPA
    public Accion() {
    }

}
