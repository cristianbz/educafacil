package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
@Table(name="sriformapago")
public class Sriformapago implements Serializable{

	private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="sriformapagoSeq", sequenceName="sriformapago_srfp_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sriformapagoSeq")
    @Column(name = "srfp_id")
    private Integer srfpId;
    
    @Column(name = "srfp_codigo_sri")
    private String srfpCodigoSri;
    
    @Column(name = "srfp_descripcion")
    private String srfpDescripcion;
    
    @OneToMany(mappedBy = "sriformapagos", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormaPagoFactura> formaPagoFacturas;
}
