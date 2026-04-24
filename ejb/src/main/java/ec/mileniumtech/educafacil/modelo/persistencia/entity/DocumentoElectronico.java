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
    @Column(name = "doel_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id", nullable = false)
    private Factura factura;

    @Column(name = "doel_clave_acceso", length = 49, nullable = false)
    private String claveAcceso;

    @Column(name = "doel_estado", nullable = false)
    private String estado;

    @Lob
    @Column(name = "doel_xml_autorizado_sri", nullable = false)
    private byte[] xmlAutorizadoSri;

    @Lob
    @Column(name = "doel_xml_firmado", nullable = false)
    private byte[] xmlFirmado;

    @Lob
    @Column(name = "doel_pdf_ride", nullable = false)
    private byte[] pdfRide;

    @Column(name = "doel_mensaje_sri", nullable = false)
    private String mensajeSri;

    @Column(name = "doel_numero_autorizacion", length = 49, nullable = false)
    private String numeroAutorizacion;
}