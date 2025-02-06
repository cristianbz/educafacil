package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="recursohumano")
@NamedQueries({
	@NamedQuery(name=Recursohumano.BUSCAR_POR_TIPO,query="SELECT rh FROM Recursohumano rh WHERE rh.rehuTipo=:tipo"),
})
public class Recursohumano implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String BUSCAR_POR_TIPO="buscarPorTipo";	

	@Id
	@SequenceGenerator(name="recursohumanoRehuIdSeq", sequenceName="recursohumano_rehu_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="recursohumanoRehuIdSeq")
	@Column(name="rehu_id")
	private int rehuId;
	
	@Column(name="rehu_tipo")
	private String rehuTipo;
	
	@ManyToOne
	@JoinColumn(name="pers_id",updatable = false, insertable = false)
	private Persona persona;
	
	@OneToMany(mappedBy="recursohumano", fetch=FetchType.LAZY)
	private List<Tareasrh> tareasrh;
}
