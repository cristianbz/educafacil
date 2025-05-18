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
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity implementation class for Entity: SeguimientoClientes
 *
 */
@Entity
@Getter
@Setter
@Table(name="seguimientoclientes")
@NamedQueries({
	@NamedQuery(name=SeguimientoClientes.LISTA_SEGUIMIENTO,query="SELECT SC FROM SeguimientoClientes SC ORDER BY SC.segcFechaSeguimiento DESC"),
	@NamedQuery(name=SeguimientoClientes.LISTA_SEGUIMIENTO_CAMPANIA,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.campania.campId=:campania ORDER BY SC.segcFechaSeguimiento DESC"),
	@NamedQuery(name=SeguimientoClientes.LISTA_SEGUIMIENTO_CURSO,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.curso.cursId = :curso ORDER BY SC.segcFechaSeguimiento DESC"),
	@NamedQuery(name=SeguimientoClientes.LISTA_SEGUIMIENTO_FECHAS,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.segcFechaSolicitud >=:fechaInicio AND SC.segcFechaSolicitud <=:fechaFin ORDER BY SC.segcFechaSolicitud DESC"),
	@NamedQuery(name=SeguimientoClientes.BUSCA_SEGUIMIENTO,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.segcId =:id"),
	@NamedQuery(name=SeguimientoClientes.VALIDA_NUMERO,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.segcTelefono =:telefono AND SC.curso.cursId = :curso AND SC.campania.campId=:campania"),
	@NamedQuery(name=SeguimientoClientes.PENDIENTE_LLAMADAS,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.segcProximaLlamada <= :proximallamada"),
	@NamedQuery(name=SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.campania.campId=:campaniaS AND SC.vendedor.vendId = NULL ORDER BY SC.segcFechaSeguimiento, SC.segcCliente ASC"),
	@NamedQuery(name=SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR_ASIGNADO,query="SELECT SC FROM SeguimientoClientes SC WHERE SC.campania.campId IS NOT NULL AND SC.campania.campEstado = TRUE AND SC.vendedor.vendId IS NULL ORDER BY SC.segcFechaSeguimiento ASC")
//	SELECT COUNT (segc_id) FROM cap.seguimientoclientes WHERE vend_id=1 AND segc_estado='CONTAC03'
})
public class SeguimientoClientes implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String LISTA_SEGUIMIENTO="listaSeguimiento"; 
	public static final String LISTA_SEGUIMIENTO_CAMPANIA="listaSeguimientoCampania"; 
	public static final String LISTA_SEGUIMIENTO_CURSO="listaSeguimientoCurso"; 
	public static final String LISTA_SEGUIMIENTO_FECHAS="listaSeguimientoFechas"; 
	public static final String BUSCA_SEGUIMIENTO="buscaSeguimiento";
	public static final String VALIDA_NUMERO="validaNumero";
	public static final String PENDIENTE_LLAMADAS="pendienteLlamadas";
	public static final String LISTA_SEGUIMIENTO_VENDEDOR="listaSeguimientoVendedor";
	public static final String LISTA_SEGUIMIENTO_VENDEDOR_ASIGNADO="listaSeguimientoVendedorAsignado";
	 
	
	public SeguimientoClientes() {
		super();
	}
	@Id
	@SequenceGenerator(name="seguimientoclientesSegcIdSeq", sequenceName="seguimientoclientes_segc_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seguimientoclientesSegcIdSeq")
	@Column(name="segc_id")
	private Integer segcId;
	
	@Column(name="segc_cliente")
	private String segcCliente;
	
	@Column(name="segc_telefono")
	private String segcTelefono;
	
	@Column(name="segc_medio_informacion")
	private String segcMedioInformacion;
	
	@Column(name="segc_estado")
	private String segcEstado;
	
	@Column(name="segc_correo")
	private String segcCorreo;
	
	@Column(name="segc_motivos_no_matricula")
	private String segcMotivosNoMatricula;
	
	@Column(name="segc_fecha_solicitud")
	private Date segcFechaSolicitud;
	
	@Column(name="segc_solicitud")
	private String segcSolicitud;

	@Column(name="segc_fecha_seguimiento")
	private Date segcFechaSeguimiento;
	
	@Column(name="segc_ultimo_seguimiento")
	private String segcUltimoSeguimiento;
	
	@Column(name="segc_motivo_curso")
	private String segcMotivoCurso;
	
	@Column(name="segc_ubicacion_llamada")
	private String segcUbicacionLlamada;
	
	@Column(name="segc_proxima_llamada")
	private Date segcProximaLlamada;
	
	@Column(name="segc_trabaja_en_area")
	private boolean segcTrabajaEnArea;
	
	@Column(name="segc_preg_resp")
	private String segcPregResp;
	
	@ManyToOne
	@JoinColumn(name="curs_id",updatable = true, insertable = true)
	private Curso curso;
	
	@OneToMany(mappedBy="seguimientoClientes", fetch=FetchType.LAZY)
	private List<DetalleSeguimiento> detalleSeguimiento;
	
	@ManyToOne
	@JoinColumn(name="camp_id",updatable = true, insertable = true)
	private Campania campania;
	
	@ManyToOne
	@JoinColumn(name="vend_id",updatable = true, insertable = true)
	private Vendedor vendedor;
	
}
