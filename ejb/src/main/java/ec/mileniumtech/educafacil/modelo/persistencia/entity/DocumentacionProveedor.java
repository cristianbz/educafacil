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
 * Entity implementation class for Entity: DocumentacionProveedor
 *
 */
@Entity
@Getter
@Setter
@Table(name="documentacionproveedor")
@NamedQueries({
	@NamedQuery(name = DocumentacionProveedor.DOCUMENTACION_POR_PROVEEDOR,query = "SELECT D FROM DocumentacionProveedor D WHERE D.proveedor.provId=:codigoProveedor AND D.docpEstado=TRUE")
})
public class DocumentacionProveedor implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String DOCUMENTACION_POR_PROVEEDOR="documentacionPorProveedor";

	@Id
	@SequenceGenerator(name="documentacionProveedorSeq", sequenceName="documentacion_proveedor_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="documentacionProveedorSeq")
	@Column(name="docp_id")
	private int docpId;
   
	@Column(name="docp_codigo_autorizacion")
	private String docpCodigoAutorizacion;
	
	@Column(name="docp_fecha_autorizacion_desde")
	private Date docpFechaAutorizacionDesde;
	
	@Column(name="docp_fecha_autorizacion_hasta")
	private Date docpFechaAutorizacionHasta;
	
	@Column(name="docp_retencion_iva")
	private double docpRetencionIva;
	
	@Column(name="docp_retencion_fuente")
	private double docpRetencionFuente;
	
	@Column(name="docp_tipo_facturacion")
	private String docpTipoFacturacion;
	
	@Column(name="docp_estado")
	private boolean docpEstado;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="prov_id")
	private Proveedor proveedor;
}
