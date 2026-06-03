package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "notacredito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaCredito {
    @Id
    @SequenceGenerator(name="notaCreditoSeq", sequenceName="notacredito_nocr_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="notaCreditoSeq")
    @Column(name = "nocr_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id", nullable = false)
    private Factura factura; // Relación con la factura original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clie_id", nullable = false)
    private Cliente cliente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puem_id", nullable = false)
    private PuntoEmision puntoEmision;
    @Column(name = "nocr_numero", length = 20, nullable = false)
    private String numero; // Ejemplo: 001-001-000000001
    @Column(name = "nocr_fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    
    @Column(name = "nocr_motivo", length = 300, nullable = false)
    private String motivo; // Requerido por el SRI
    @Column(name = "nocr_subtotal", precision = 6, scale = 2, nullable = false)
    private BigDecimal subtotal;
    @Column(name = "nocr_total_impuestos", precision = 6, scale = 2, nullable = false)
    private BigDecimal totalImpuestos;
    @Column(name = "nocr_total", precision = 6, scale = 2, nullable = false)
    private BigDecimal total;
    // Campos de SRI embebidos (similar a Retencion) para no alterar DocumentoElectronico
    @Column(name = "nocr_clave_acceso", length = 49)
    private String claveAcceso;
    @Column(name = "nocr_estado")
    private String estado;
    @Column(name = "nocr_mensaje_sri")
    private String mensajeSri;
    @Column(name = "nocr_numero_autorizacion", length = 49)
    private String numeroAutorizacion;
    
    @Column(name = "nocr_url_pdf", length = 500)
    private String urlPdf;
    
    @Column(name = "nocr_url_xml", length = 500)
    private String urlXml;
    @Column(name = "nocr_fecha_autorizacion", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime fechaAutorizacionDb;
    @Transient 
    private XMLGregorianCalendar fechaAutorizacion;
    @Transient
    private List<InfoAdicionalDto> listaInfoAdicional;
    
    // Relación con los detalles (Maestro-Detalle)
    @OneToMany(mappedBy = "notaCredito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleNotaCredito> detalles;
}