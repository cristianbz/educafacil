package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
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
 * Entity implementation class for Entity: Proveedor
 *
 */
@Entity
@Getter
@Setter
@Table(name="proveedor")
@NamedQueries({
	@NamedQuery(name = Proveedor.LISTA_PROVEEDORES,query = "SELECT P FROM Proveedor P"),
	@NamedQuery(name = Proveedor.RUC_PROVEEDOR,query = "SELECT P FROM Proveedor P WHERE P.provRuc=:ruc")
})
public class Proveedor implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String LISTA_PROVEEDORES="listaProveedores";
	public static final String RUC_PROVEEDOR="rucProveedor";

	@Id
	@SequenceGenerator(name="proveedorSeq", sequenceName="proveedor_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="proveedorSeq")
	@Column(name="prov_id")
	private Integer provId;
	
	@Column(name="prov_ruc")
	private String provRuc;
	
	@Column(name="prov_nombre")
	private String provNombre;
	
	@Column(name="prov_direccion")
	private String provDireccion;
	
	@Column(name="prov_correo")
	private String provCorreo;
	
	@Column(name="prov_telefono")
	private String provTelefono;
	
	@Column(name="prov_estado")
	private boolean provEstado;
	
	@OneToMany(mappedBy="proveedor", fetch=FetchType.EAGER,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	private List<DocumentacionProveedor> documentacionProveedors;
   
	@OneToMany(mappedBy="proveedor")
	private List<Egresos> egresos;
}
