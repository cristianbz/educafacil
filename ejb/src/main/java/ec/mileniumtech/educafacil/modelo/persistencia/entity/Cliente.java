package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cliente", schema = "cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clie_id")
    private Integer id;

    @Column(name = "clie_tipo_identificacion", nullable = false)
    private Integer tipoIdentificacion;

    @Column(name = "clie_numero_identificacion", nullable = false)
    private String numeroIdentificacion;

    @Column(name = "clie_nombres_completos", length = 100, nullable = false)
    private String nombresCompletos;

    @Column(name = "clie_correo", length = 50, nullable = false)
    private String correo;

    @Column(name = "clie_telefono", length = 20, nullable = false)
    private String telefono;

    @Column(name = "clie_direccion", length = 100, nullable = false)
    private String direccion;

    /**
     * El tipo bit(1) de PostgreSQL se puede mapear como Boolean.
     * Hibernate se encarga de la conversión.
     */
    @Column(name = "clie_estado", nullable = false, columnDefinition = "bit(1)")
    private Boolean estado;
    
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Factura> facturas;
}