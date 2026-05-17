package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Catálogo de códigos SRI para retenciones.
 * Almacena los códigos de retención agrupados por tipo de impuesto
 * (1=Renta, 2=IVA, 6=ISD) para que puedan ser mantenidos desde la BD.
 */
@Entity
@Getter
@Setter
@Table(name = "codigo_sri_retencion", schema = "cap")
public class CodigoSriRetencion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
        name = "codigoSriRetencionSeq",
        sequenceName = "codigo_sri_retencion_id_seq",
        allocationSize = 1,
        schema = "cap"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "codigoSriRetencionSeq")
    @Column(name = "csri_id")
    private Integer id;

    /**
     * Tipo de impuesto SRI: "1" = Renta, "2" = IVA, "6" = ISD.
     */
    @Column(name = "csri_tipo_impuesto", length = 5, nullable = false)
    private String tipoImpuesto;

    /**
     * Código de retención SRI (ej: "312", "303", "1", "2", "9", etc.).
     */
    @Column(name = "csri_codigo", length = 10, nullable = false)
    private String codigo;

    /**
     * Descripción del concepto de retención.
     */
    @Column(name = "csri_descripcion", length = 300, nullable = false)
    private String descripcion;

    /**
     * Porcentaje de retención asociado a este código.
     */
    @Column(name = "csri_porcentaje", precision = 5, scale = 2)
    private BigDecimal porcentaje;

    /**
     * Indica si el código está activo/vigente.
     */
    @Column(name = "csri_activo", nullable = false)
    private Boolean activo = true;

    public CodigoSriRetencion() {
    }

    /**
     * Etiqueta para mostrar en el combo: "código – descripción".
     */
    public String getEtiqueta() {
        return codigo + " – " + descripcion;
    }
}
