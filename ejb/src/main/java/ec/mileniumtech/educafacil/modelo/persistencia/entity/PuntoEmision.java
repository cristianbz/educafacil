package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;

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
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: Puntoemision
 *
 */
@Entity
@Getter
@Setter
@Table(name = "puntoemision")
public class PuntoEmision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
	@SequenceGenerator(name="puntoemisionSeq", sequenceName="puntoemision_puem_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="puntoemisionSeq")
    @Column(name = "puem_id", nullable = false)
    private Integer id;

    @Column(name = "puem_codigo", length = 10)
    private String codigo;

    @Column(name = "puem_sec_factura")
    private Integer secuencialFactura;

    @Column(name = "puem_sec_notacredito")
    private Integer secuencialNotaCredito;

    @Column(name = "puem_sec_notadebito")
    private Integer secuencialNotaDebito;

    @Column(name = "puem_sec_retencion")
    private Integer secuencialRetencion;

    @Column(name = "puem_sec_liq_compra")
    private Integer secuencialLiquidacionCompra;

    @Column(name = "puem_sec_guia_remision")
    private Integer secuencialGuiaRemision;

    @Column(name = "puem_informacion_adicional", length = 250)
    private String informacionAdicional;

    @Column(name = "puem_estado", columnDefinition = "BIT(1)")
    private Boolean estado;

    // Relación con la entidad Establecimiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esta_id", referencedColumnName = "esta_id", 
                insertable = false, updatable = false)
    private Establecimiento establecimientos;

    // Constructor por defecto
    public PuntoEmision() {
    }

    // Constructor con parámetros principales
    public PuntoEmision(Integer id, String codigo, Boolean estado) {
        this.id = id;
        this.codigo = codigo;
        this.estado = estado;
    }
   
}
