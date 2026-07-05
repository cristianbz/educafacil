package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @SequenceGenerator(name="facturaSeq", sequenceName="factura_fact_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="facturaSeq")
    @Column(name = "fact_id")
    private Integer id;

    @NotNull(message = "{factura.cliente.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clie_id", nullable = false)
    private Cliente cliente;

    // Asumiendo que existe una entidad PuntoEmision ya creada
    @NotNull(message = "{factura.puntoEmision.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puem_id", nullable = false)
    private PuntoEmision puntoEmision;

    @NotBlank(message = "{factura.numero.required}")
    @Column(name = "fact_numero", length = 20, nullable = false)
    private String numero;

    @NotNull(message = "{factura.fechaEmision.required}")
    @Column(name = "fact_fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull(message = "{factura.subtotal.required}")
    @Column(name = "fact_subtotal", precision = 6, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @NotNull(message = "{factura.descuentoTotal.required}")
    @Column(name = "fact_descuento_total", precision = 6, scale = 2, nullable = false)
    private BigDecimal descuentoTotal;

    @NotNull(message = "{factura.totalImpuestos.required}")
    @Column(name = "fact_total_impuestos", precision = 6, scale = 2, nullable = false)
    private BigDecimal totalImpuestos;

    @NotNull(message = "{factura.total.required}")
    @Column(name = "fact_total", precision = 6, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "fact_notas")
    private String notas;
    
    @Transient
    private List<InfoAdicionalDto> listaInfoAdicional;
    
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
    
    @OneToMany(mappedBy = "factura", fetch = FetchType.LAZY)
    private List<NotaCredito> notaCredito;
}