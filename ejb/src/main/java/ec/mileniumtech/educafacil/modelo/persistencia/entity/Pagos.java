package ec.mileniumtech.educafacil.modelo.persistencia.entity;
import java.io.Serializable;
import java.util.Date;
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
/**
 * Entity implementation class for Entity: Pagos
 *
 */
@Entity
@Table(name = "pagos")
@Getter
@Setter

@NamedQueries({
	@NamedQuery(name = Pagos.BUSCAR_DETALLEPAGOS,query = "SELECT DP FROM Pagos P, DetallePagos DP WHERE DP.pagos.pagoId = P.pagoId AND P.matricula.matrId=:codigoMatricula")
})
public class Pagos implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String BUSCAR_DETALLEPAGOS="buscarDetallepagos";

	@Id
	@SequenceGenerator(name="pagosSeq", sequenceName="pagos_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pagosSeq")
	@Column(name="pago_id")
	private Integer pagoId;
	
	@Column(name="pago_fecha")
	private Date pagoFecha;
   
	@Column(name="pago_numero_factura")
	private String pagoNumeroFactura;
	
	@Column(name="pago_observacion")
	private String pagoObservacion;
	
	@Column(name="pago_usuario_ingreso")
	private String pagoUsuarioIngreso;
		
	@ManyToOne
	@JoinColumn(name="matr_id")
	private Matricula matricula;
	
	
	@OneToMany(mappedBy="pagos", fetch=FetchType.LAZY)
	private List<DetallePagos> detallePagos;
}
