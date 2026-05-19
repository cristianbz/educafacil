package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad para los detalles del Comprobante de Retención.
 */
@Entity
@Getter
@Setter
@Table(name = "detalle_retencion")
public class DetalleRetencion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "detalleRetencionSeq", sequenceName = "detalle_retencion_deret_id_seq", allocationSize = 1, schema = "cap")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detalleRetencionSeq")
    @Column(name = "deret_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rete_id", nullable = false)
    private Retencion retencion;

    @Column(name = "deret_codigo_impuesto", length = 5, nullable = false)
    private String codigoImpuesto; // 1: Renta, 2: IVA, 6: ISD

    @Column(name = "deret_codigo_retencion", length = 10, nullable = false)
    private String codigoRetencion; // Códigos SRI (ej: 312, 1, etc.)

    @Column(name = "deret_base_imponible", precision = 14, scale = 2, nullable = false)
    private BigDecimal baseImponible;

    @Column(name = "deret_porcentaje", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentaje;

    @Column(name = "deret_valor_retenido", precision = 14, scale = 2, nullable = false)
    private BigDecimal valorRetenido;

    // Campos informativos del documento sustento
    @Column(name = "deret_cod_doc_sustento", length = 5)
    private String codigoDocSustento; // 01: Factura

    @Column(name = "deret_num_doc_sustento", length = 20)
    private String numeroDocSustento;

    @Temporal(TemporalType.DATE)
    @Column(name = "deret_fecha_sustento")
    private Date fechaSustento;

    public DetalleRetencion() {
    }
}
