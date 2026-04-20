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

@Entity
@Getter
@Setter
@Table(name = "empresamatriz")
@NamedQueries({
	@NamedQuery(name =EmpresaMatriz.EMPRESAMATRIZ_ACTIVAS,query = "SELECT E FROM EmpresaMatriz E WHERE E.empmEstado=true" )
})
public class EmpresaMatriz implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String EMPRESAMATRIZ_ACTIVAS="empresaMatrizActivas";

	@Id
	@SequenceGenerator(name="empresaMatrizSeq", sequenceName="empresamatriz_empm_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="empresaMatrizSeq")
	@Column(name="empm_id")
	private int empmId;
   
	@Column(name="empm_ruc")
	private String empmRuc;
	
	@Column(name="empm_nombre_comercial")
	private String empmNombreComercial;
	
	@Column(name="empm_razon_social")
	private String empmRazonSocial;
	
	@Column(name="empm_telefono")
	private String empmTelefono;
	
	@Column(name="empm_mail")
	private String empmMail;
	
	@Column(name="empm_direccion")
	private String empmDireccion;
	
	@Column(name="empm_estado")
	private boolean empmEstado;
	
	@Column(name="empm_logo", columnDefinition = "BYTEA")
	private byte[] empmLogo;
	
	@Column(name="empm_certificado", columnDefinition = "BYTEA")
	private byte[] empmCertificado;
	
	@Column(name="empm_password_certificado")
	private String empmPasswordCertificado;

	@Column(name="empm_obligado_contabilidad")
	private boolean empmObligadoContabilidad;

	@Column(name="empm_ambiente")
	private Integer empmAmbiente; // 1: Pruebas, 2: Produccion

	@Column(name="empm_tipo_emision")
	private Integer empmTipoEmision; // 1: Normal

	@Column(name="empm_contribuyente_especial")
	private String empmContribuyenteEspecial;

	@Column(name="empm_resolucion_agente")
	private String empmResolucionAgente;
	
	
	@OneToMany(mappedBy="empresaMatriz", fetch=FetchType.LAZY)
	private List<Puntoemision> puntoemsiones;
}
