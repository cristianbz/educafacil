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
 * Entity implementation class for Entity: Especialidad
 *
 */
@Entity
@Getter
@Setter
@Table(name="especialidad")
@NamedQueries({
	@NamedQuery(name = Especialidad.LISTA_DE_ESPECIALIDAD,query = "SELECT E FROM Especialidad E")
})
public class Especialidad implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String LISTA_DE_ESPECIALIDAD="listaDeEspecialidad";
	
	@Id
	@SequenceGenerator(name="especialidadSeq", sequenceName="especialidad_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="especialidadSeq")
	@Column(name="espe_id")
	private int espeId;
	
	@Column(name="espe_nombre")
	private String espeNombre;
   
	@OneToMany(mappedBy="especialidad", fetch=FetchType.LAZY)
	private List<OfertaCapacitacion> ofertaCapacitacion;
}
