package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
/**
 * Entity implementation class for Entity: Catalogo
 *
 */
@Entity
@Table(name="catalogo")
@NamedQueries({
	@NamedQuery(name="Catalogo.findAll", query="SELECT c FROM Catalogo c"),
//	@NamedQuery(name="Catalogo.findByCodigoIndice", query="SELECT c FROM Catalogo c WHERE c.cataCodigoIndice = :cataCodigoIndice"),
//	@NamedQuery(name=Catalogo.BUSCAR_PARROQUIA, query="SELECT c FROM Catalogo c WHERE c.cataCodigoIndice like :codigoParroquia"),
	@NamedQuery(name = Catalogo.BUSCAR_POR_TIPO_CATALOGO, query = "SELECT c FROM Catalogo c WHERE c.Catalogotipo.catiCodigoIndice = :tipoCatalogo AND c.cataEstado = True order by c.cataDescripcion" ),
	@NamedQuery(name = Catalogo.BUSCAR_POR_PADRE, query = "SELECT c FROM Catalogo c WHERE c.cataIdPadre = :padre AND c.cataEstado = True order by c.cataDescripcion" )
//	@NamedQuery(name = "Catalogo.consultaHijosfindByCodigoIndice", query = "SELECT c FROM Catalogo c WHERE c.cataIdPadre IN (SELECT cat.cataId FROM Catalogo cat WHERE  cat.cataCodigoIndice= :cataCodigoIndice AND cat.cataEstado = True) and c.cataIdPadre<> c.cataId order by c.cataDescripcion" )
})
@Getter
@Setter
public class Catalogo implements Serializable {

	
	private static final long serialVersionUID = 1L;
//	public static final String BUSCAR_PARROQUIA="Catalogo.findByParroquia";
	public static final String BUSCAR_POR_TIPO_CATALOGO="Catalogo.buscarPorTipoCatalogo";
	public static final String BUSCAR_POR_PADRE="Catalogo.buscarPorPadre";
	@Id
	@SequenceGenerator(name="catalogoSeq", sequenceName="catalogo_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="catalogoSeq")
	@Column(name="cata_id")
	private int cataId;
   
	@Column(name="cata_descripcion")
	private String cataDescripcion;

	@Column(name="cata_estado")
	private boolean cataEstado;
	
	@Column(name="cata_codigo_indice")
	private String cataCodigoIndice;


	 @OneToMany(mappedBy = "cataIdPadre")
    private List<Catalogo> catalogsList;
	    
    @JoinColumn(name = "cata_id_padre", referencedColumnName = "cata_id")
    @ManyToOne
    private Catalogo cataIdPadre;
	
	
	//bi-directional many-to-one association to TipoCatalogo
	@ManyToOne
	@JoinColumn(name="cati_id")
	private Catalogotipo Catalogotipo;

	@OneToMany(mappedBy="catalogo")
	private List<DetallePagos> detallePagos;
	
	@OneToMany(mappedBy="catalogo")
	private List<Egresos> egresos;


}
