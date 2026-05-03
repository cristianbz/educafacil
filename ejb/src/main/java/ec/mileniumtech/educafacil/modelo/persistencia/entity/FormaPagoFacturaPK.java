package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class FormaPagoFacturaPK implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Column(name = "srfp_id", nullable = false)
    private Integer srfpId;
    
    @Column(name = "fact_id", nullable = false)
    private Integer factId;

    // Constructores
    public FormaPagoFacturaPK() {}
    
    public FormaPagoFacturaPK(Integer srfpId, Integer factId) {
        this.srfpId = srfpId;
        this.factId = factId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormaPagoFacturaPK that = (FormaPagoFacturaPK) o;
        return java.util.Objects.equals(srfpId, that.srfpId) &&
               java.util.Objects.equals(factId, that.factId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(srfpId, factId);
    }
}
