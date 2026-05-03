package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pagosfacturados", schema = "cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagosFacturados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pafa_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id", nullable = false)
    private Factura factura;

    @Column(name = "pafa_fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "pafa_metodo", length = 8, nullable = false)
    private String metodo;

    @Column(name = "pafa_monto", precision = 5, scale = 2, nullable = false)
    private BigDecimal monto;
    
    @Column(name = "pafa_descuento", precision = 5, scale = 2, nullable = false)
    private BigDecimal descuento;
    
    @Column(name = "pafa_valor_impuesto", precision = 5, scale = 2, nullable = false)
    private BigDecimal valorImpuestos;
    
    @Transient
    private Integer impuestoIva;

    @Column(name = "pafa_referencia", length = 50, nullable = false)
    private String referencia;
}