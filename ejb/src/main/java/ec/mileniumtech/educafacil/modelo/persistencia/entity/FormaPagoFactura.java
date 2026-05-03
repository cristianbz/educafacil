package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "formapago_factura")
public class FormaPagoFactura implements Serializable{

	private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="formapagofacturaSeq", sequenceName="formapago_factura_fpfa_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="formapagofacturaSeq")
    @Column(name = "fpfa_id")
    private Integer fpfaId;
    
    @Column(name = "fpfa_valor", nullable = false, precision = 6, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "fpfac_plazo")
    private String plazo;
    
    @Column(name = "fpfa_tiempo")
    private String tiempo;

    // Constructores
    public FormaPagoFactura() {}
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id", nullable = false)
    private Factura factura;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "srfp_id", nullable = false)
    private Sriformapago sriformapagos;

}
