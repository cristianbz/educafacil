package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name="vendedor")
@NamedQueries({
	@NamedQuery(name=Vendedor.BUSCAR_VENDEDOR,query="SELECT V FROM Vendedor V ORDER BY V.persona.persApellidos")		
})

public class Vendedor implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String BUSCAR_VENDEDOR= "buscaVendedor"; 
	
	@Id
	@SequenceGenerator(name="vendedorSeq", sequenceName="vendedor_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="vendedorSeq")
	@Column(name="vend_id")
	private Integer vendId;
	
	@Column(name="vend_fecha_salida")
	private Date vendFechaSalida;
	
	@Column(name="vend_fecha_ingreso")
	private Date vendFechaIngreso;
	
	@Column(name="vend_estado")
	private Boolean vendEstado;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="pers_id")
	private Persona persona;
	
	@OneToMany(mappedBy="vendedor", fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	private List<SeguimientoClientes> seguimientoClientes;
	
	
}
