package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "documentoelectronico", schema = "cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoElectronico {

    @Id
    @SequenceGenerator(name="documentoElectronicoSeq", sequenceName="documentoelectronico_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="documentoElectronicoSeq")
    @Column(name = "doel_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id", nullable = false)
    private Factura factura;

    @Column(name = "doel_clave_acceso", length = 49)
    private String claveAcceso;

    @Column(name = "doel_estado")
    private String estado;

    @Column(name = "doel_xml_autorizado_sri", columnDefinition ="bytea")
    private byte[] xmlAutorizadoSri;

    @Column(name = "doel_xml_firmado", columnDefinition ="bytea")
    private byte[] xmlFirmado;

    @Column(name = "doel_pdf_ride", columnDefinition ="bytea")
    private byte[] pdfRide;

    @Column(name = "doel_mensaje_sri")
    private String mensajeSri;

    @Column(name = "doel_numero_autorizacion", length = 49)
    private String numeroAutorizacion;
}