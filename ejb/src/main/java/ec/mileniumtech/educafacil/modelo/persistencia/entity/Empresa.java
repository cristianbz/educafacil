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
 * Entity implementation class for Entity: Empresa
 *
 */
@Entity
@Getter
@Setter
@Table(name = "empresa")
@NamedQueries({
	@NamedQuery(name =Empresa.EMPRESAS_ACTIVAS,query = "SELECT E FROM Empresa E WHERE E.emprEstado=true" )
})
public class Empresa implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String EMPRESAS_ACTIVAS="empresasActivas";

	@Id
	@SequenceGenerator(name="empresaSeq", sequenceName="empresa_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="empresaSeq")
	@Column(name="empr_id")
	private int emprId;
   
	@Column(name="empr_ruc")
	private String emprRuc;
	
	@Column(name="empr_nombre")
	private String emprNombre;
	
	@Column(name="empr_telefono")
	private String emprTelefono;
	
	@Column(name="empr_mail")
	private String emprMail;
	
	@Column(name="empr_direccion")
	private String emprDireccion;
	
	@Column(name="empr_estado")
	private boolean emprEstado;
	
	@OneToMany(mappedBy="empresa", fetch=FetchType.LAZY)
	private List<Matricula> matriculas;
}
