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
 * Entity implementation class for Entity: DetalleSeguimiento
 *
 */
@Entity
@Getter
@Setter
@Table(name="detalleseguimiento")
@NamedQueries({
	@NamedQuery(name =DetalleSeguimiento.LISTA_DETALLE,query = "SELECT D FROM DetalleSeguimiento D WHERE D.seguimientoClientes.segcId =:seguimiento" )
})
public class DetalleSeguimiento implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String LISTA_DETALLE ="detalleSeguimiento";

	public DetalleSeguimiento() {
		super();
	}
   
	@Id
	@SequenceGenerator(name="detalleseguimientoDsegIdSeq", sequenceName="detalleseguimiento_dseg_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="detalleseguimientoDsegIdSeq")
	@Column(name="dseg_id")
	private Integer dsegId;

	@Column(name="dseg_observacion")
	private String dsegObservacion;
	
	@Column(name="dseg_medio_contacto")
	private String dsegMedioContacto;
	
	@Column(name="dseg_estado")
	private String dsegEstado;
		
	@Column(name="dseg_fecha")
	private Date dsegFecha;
	
	@Column(name="dseg_proxima_llamada")
	private Date dsegProximaLlamada;
	
	@Column(name="dseg_contesto_llamada")
	private boolean dsegContestoLlamada;
	
	@ManyToOne
	@JoinColumn(name="segc_id",updatable = true, insertable = true)
	private SeguimientoClientes seguimientoClientes;
}
