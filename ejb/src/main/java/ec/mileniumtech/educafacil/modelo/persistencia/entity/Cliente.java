package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clie_id")
    private Integer id;

    @NotNull(message = "{cliente.tipoIdentificacion.required}")
    @Column(name = "clie_tipo_identificacion", nullable = false)
    private Integer tipoIdentificacion;

    @NotBlank(message = "{cliente.numeroIdentificacion.required}")
    @Column(name = "clie_numero_identificacion", nullable = false)
    private String numeroIdentificacion;

    @NotBlank(message = "{cliente.nombresCompletos.required}")
    @Column(name = "clie_nombres_completos", length = 100, nullable = false)
    private String nombresCompletos;

    @NotBlank(message = "{cliente.correo.required}")
    @Email(message = "{cliente.correo.email}")
    @Column(name = "clie_correo", length = 50, nullable = false)
    private String correo;

    @NotBlank(message = "{cliente.telefono.required}")
    @Column(name = "clie_telefono", length = 20, nullable = false)
    private String telefono;

    @NotBlank(message = "{cliente.direccion.required}")
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
    
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<NotaCredito> notaCredito;
}