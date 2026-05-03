package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "formapago_factura")
public class FormaPagoFactura implements Serializable{

	private static final long serialVersionUID = 1L;
	@EmbeddedId
    private FormaPagoFacturaPK id;
    
    @Column(name = "fpfa_valor", nullable = false, precision = 6, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "fpfac_plazo")
    private String plazo;
    
    @Column(name = "fpfa_tiempo")
    private String tiempo;

    // Constructores
    public FormaPagoFactura() {}

}
