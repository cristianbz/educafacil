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
 * Entity implementation class for Entity: Producto
 *
 */
@Entity
@Table(name = "producto")
@SequenceGenerator(name = "producto_seq", 
                  sequenceName = "cap.producto_seq", 
                  allocationSize = 1)
@Getter
@Setter
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @Column(name = "prod_id", nullable = false)
    private Integer id;

    @Column(name = "prod_codigo", length = 10)
    private String codigo;

    @Column(name = "prod_descripcion", length = 100)
    private String descripcion;

    @Column(name = "prod_valor_unitario", precision = 6, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "prod_estado", columnDefinition = "BIT(1)")
    private Boolean estado;

    @Column(name = "prod_tarifa_iva", length = 7)
    private String tarifaIva;

    @Column(name = "prod_tarifa_ice", length = 7)
    private String tarifaIce;

    @Column(name = "prod_aplica_ice", columnDefinition = "BIT(1)")
    private Boolean aplicaIce;

    @Column(name = "prod_aplica_iva_turismo", columnDefinition = "BIT(1)")
    private Boolean aplicaIvaTurismo;

    @Column(name = "prod_info_adicional", length = 100)
    private String informacionAdicional;

    // Constructor por defecto
    public Producto() {
    }

    // Constructor con parámetros principales
    public Producto(String codigo, String descripcion, BigDecimal valorUnitario, Boolean estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.valorUnitario = valorUnitario;
        this.estado = estado;
    }
   
}
