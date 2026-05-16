package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad para la cabecera del Comprobante de Retención.
 */
@Entity
@Getter
@Setter
@Table(name = "retencion", schema = "cap")
public class Retencion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "retencionSeq", sequenceName = "retencion_rete_id_seq", allocationSize = 1, schema = "cap")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "retencionSeq")
    @Column(name = "rete_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egre_id")
    private Egresos egreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puem_id")
    private PuntoEmision puntoEmision;

    @Column(name = "rete_numero", length = 20, nullable = false)
    private String numero;

    @Temporal(TemporalType.DATE)
    @Column(name = "rete_fecha_emision", nullable = false)
    private Date fechaEmision;

    @Column(name = "rete_ejercicio_fiscal", length = 10, nullable = false)
    private String ejercicioFiscal;

    // Campos para Facturación Electrónica SRI
    @Column(name = "rete_clave_acceso", length = 49)
    private String claveAcceso;

    @Column(name = "rete_estado", length = 30)
    private String estado;

    @Column(name = "rete_xml_firmado", columnDefinition = "bytea")
    private byte[] xmlFirmado;

    @Column(name = "rete_xml_autorizado", columnDefinition = "bytea")
    private byte[] xmlAutorizado;

    @Column(name = "rete_pdf_ride", columnDefinition = "bytea")
    private byte[] pdfRide;

    @Column(name = "rete_numero_autorizacion", length = 49)
    private String numeroAutorizacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rete_fecha_autorizacion")
    private Date fechaAutorizacion;

    @Column(name = "rete_mensaje_sri", columnDefinition = "text")
    private String mensajeSri;

    @OneToMany(mappedBy = "retencion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleRetencion> detalles;

    public Retencion() {
        this.estado = "PENDIENTE";
    }
}
