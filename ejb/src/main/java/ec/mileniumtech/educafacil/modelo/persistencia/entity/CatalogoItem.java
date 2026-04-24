package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "catalogoitems", schema = "cap")
@Data // Genera getters, setters, toString, etc. (requiere Lombok)
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cait_id")
    private Integer id;

    @Column(name = "cait_codigo", length = 8, nullable = false)
    private String codigo;

    @Column(name = "cait_nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "cait_tipo", length = 8, nullable = false)
    private String tipo;

    @Column(name = "cait_descripcion", nullable = false)
    private String descripcion;

    @Column(name = "cait_precio", precision = 6, scale = 2, nullable = false)
    private BigDecimal precio;
    
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<DetalleFactura> detallesFactura;
}