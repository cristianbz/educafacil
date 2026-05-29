package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "documentoelectronico")
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
    
    @Column(name = "doel_url_pdf", length = 500)
    private String urlPdf;
    
    @Column(name = "doel_url_xml", length = 500)
    private String urlXml;
    
    @Transient 
    private XMLGregorianCalendar fechaAutorizacion;

    // Este campo es el que JPA guarda en la base de datos
    @Column(name = "doel_fecha_autorizacion", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime fechaAutorizacionDb;
    
//    @Column(name = "doel_fecha_autorizacion")
//    private LocalDateTime fechaAutorizacionDb;

    // Getters y Setters
//    public void setFechaAutorizacion(XMLGregorianCalendar fecha) {
//        this.fechaAutorizacion = fecha;
//        // Convertimos XMLGregorianCalendar a LocalDateTime
//        if (fecha != null) {
//            this.fechaAutorizacionDb = fecha.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
//        }
//    }
}