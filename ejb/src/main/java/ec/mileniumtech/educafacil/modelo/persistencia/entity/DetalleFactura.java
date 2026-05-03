package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "detallefactura", schema = "cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defa_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cait_id", nullable = false)
    private CatalogoItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id", nullable = false)
    private Factura factura;

    @Column(name = "defa_descripcion", length = 150, nullable = false)
    private String descripcion;

    @Column(name = "defa_cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "defa_precio_unitario", precision = 5, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "defa_descuento", precision = 5, scale = 2)
    private BigDecimal descuento;
    
    @Transient
    private Integer impuestoIva;
}