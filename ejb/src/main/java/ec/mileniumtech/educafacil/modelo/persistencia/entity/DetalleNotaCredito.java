package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity
@Table(name = "detallenotacredito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleNotaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "denc_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cait_id", nullable = false)
    private CatalogoItem item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nocr_id", nullable = false)
    private NotaCredito notaCredito;
    @Column(name = "denc_descripcion", length = 150, nullable = false)
    private String descripcion;
    @Column(name = "denc_cantidad", nullable = false)
    private Integer cantidad;
    @Column(name = "denc_precio_unitario", precision = 5, scale = 2, nullable = false)
    private BigDecimal precioUnitario;
    @Column(name = "denc_descuento", precision = 5, scale = 2)
    private BigDecimal descuento;
    
    @Transient
    private BigDecimal impuestoIva;
}
