/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jun 15, 2024
*
*/
@Entity
@Table(name="configuraciones")
@Getter
@Setter
@NamedQueries({
	@NamedQuery(query = "SELECT C FROM Configuraciones C",name = Configuraciones.LISTA_CONFIGURACIONES)
})
public class Configuraciones implements Serializable {

	
	private static final long serialVersionUID = 1L;
	public static final String LISTA_CONFIGURACIONES="listaConfiguraciones";

	@Id
	@SequenceGenerator(name="configuracionesSeq", sequenceName="configuraciones_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="configuracionesSeq")
	@Column(name="conf_id")
	private int confId;
	
	@Column(name="conf_servidor_smtp")
	private String confServidorSmtp;
	
	@Column(name="conf_usuario_correo")
	private String confUsuarioCorreo;
	
	@Column(name="conf_clave_correo")
	private String confClaveCorreo;
	
	@Column(name="conf_enviado_mail_desde")
	private String confEnviadoMailDesde;
	
	@Column(name="conf_empresa")
	private String confEmpresa;
	
	@Column(name="conf_enviarmail")
	private boolean confEnviarmail;
	
	@Column(name="conf_representante_legal")
	private String confRepresentanteLegal;
	
	@Column(name="conf_api_whatsapp")
	private String confApiWhatsapp;
	
	@Column(name="conf_apfacebook_id")
	private String confApfacebookId;
	
	@Column(name="conf_apfacebook_clave")
	private String confApfacebookClave;
	
	@Column(name="conf_apfacebook_uriredirect")
	private String confApfacebookUriredirect;
	
}
