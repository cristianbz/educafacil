package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: Area
 *
 */
@Entity
@Getter
@Setter
@Table(name="area")
@NamedQueries({
	@NamedQuery(name=Area.LISTA_DE_AREAS,query = "SELECT A FROM Area A ")
})
public class Area implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String LISTA_DE_AREAS="listaDeAreas";

	@Id
	@SequenceGenerator(name="areaSeq", sequenceName="area_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="areaSeq")
	@Column(name="area_id")
	private int areaId;
	
	@Column(name="area_nombre")
	private String areaNombre;
   
	
	@OneToMany(mappedBy="area", fetch=FetchType.LAZY)
	private List<OfertaCapacitacion> ofertaCapacitacion;
}
