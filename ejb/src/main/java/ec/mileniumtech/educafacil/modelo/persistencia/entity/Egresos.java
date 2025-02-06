package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
/**
 * Entity implementation class for Entity: Egresos
 *
 */

@Entity
@Table(name="egresos")
@Getter
@Setter
@NamedQueries({
	@NamedQuery(name = Egresos.CARGA_EGRESOS,query = "SELECT E FROM Egresos E WHERE E.egreFecha = current_date ORDER BY E.egreId"),
	@NamedQuery(name = Egresos.CARGA_EGRESOS_POR_FECHA,query = "SELECT E FROM Egresos E WHERE E.egreFecha BETWEEN :fechauno AND :fechados ORDER BY E.egreId")
	
})
public class Egresos implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String CARGA_EGRESOS="cargaEgresos";
	public static final String CARGA_EGRESOS_POR_FECHA="cargaEgresosPorFecha";

	@Id
	@SequenceGenerator(name="egresosSeq", sequenceName="egresos_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="egresosSeq")
	@Column(name="egre_id")
	private Integer egreId;
   
	@Column(name="egre_fecha")
	private Date egreFecha;
	
	@Column(name="egre_fecha_registro")
	private Date egreFechaRegistro;
	
	@Column(name="egre_valor")
	private Double egreValor;
	
	@Column(name="egre_detalle")
	private String egreDetalle;
	
	@Column(name="egre_num_factura")
	private String egreNumFactura;
	
	@Column(name="egre_estado")
	private String egreEstado;
	
	@ManyToOne
	@JoinColumn(name="cata_id")
	private Catalogo catalogo;
	
	@ManyToOne
	@JoinColumn(name="prov_id")
	private Proveedor proveedor;
}
