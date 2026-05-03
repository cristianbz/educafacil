package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "factura", schema = "cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @SequenceGenerator(name="facturaSeq", sequenceName="factura_fact_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="facturaSeq")
    @Column(name = "fact_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clie_id", nullable = false)
    private Cliente cliente;

    // Asumiendo que existe una entidad PuntoEmision ya creada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puem_id", nullable = false)
    private PuntoEmision puntoEmision;

    @Column(name = "fact_numero", length = 20, nullable = false)
    private String numero;

    @Column(name = "fact_fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fact_subtotal", precision = 6, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "fact_descuento_total", precision = 6, scale = 2, nullable = false)
    private BigDecimal descuentoTotal;

    @Column(name = "fact_total_impuestos", precision = 6, scale = 2, nullable = false)
    private BigDecimal totalImpuestos;

    @Column(name = "fact_total", precision = 6, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "fact_notas")
    private String notas;
    
 // Relación con los detalles (Maestro-Detalle)
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles;

    // Relación con los pagos
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<PagosFacturados> pagos;

    // Relación con el documento electrónico (SRI)
    @OneToOne(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentoElectronico documentoElectronico;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormaPagoFactura> formaPagoFacturas;
}