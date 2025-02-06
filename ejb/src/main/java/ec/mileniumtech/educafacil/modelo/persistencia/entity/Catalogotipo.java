package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
/**
 * Entity implementation class for Entity: TipoCatalogo
 *
 */
@Entity
@Table(name="catalogotipo")
@NamedQuery(name="Catalogotipo.findAll", query="SELECT ct FROM Catalogotipo ct")
@Getter
@Setter
public class Catalogotipo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="catalogotipoSeq", sequenceName="catalogotipo_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="catalogotipoSeq")
	@Column(name="cati_id")
	private int catiId;
   
	@Column(name="cati_descripcion")
	private String catiDescripcion;
	
	@Column(name="cati_codigo_indice")
	private String catiCodigoIndice;
	
	@Column(name="cati_estado")
	private boolean catiEstado;
	
	//bi-directional many-to-one association to Catalogo
	@OneToMany(mappedBy="Catalogotipo")
	private List<Catalogo> catalogos;

	

}
