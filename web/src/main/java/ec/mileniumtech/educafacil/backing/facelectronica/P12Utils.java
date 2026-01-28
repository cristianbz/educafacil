package ec.mileniumtech.educafacil.backing.facelectronica;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;

/**
* @author christian May 20, 2025
*/
/**
 * 
 */
@ApplicationScoped
public class P12Utils {
	 public KeyStore loadP12(byte[] p12Data, String password) throws Exception {
	        KeyStore ks = KeyStore.getInstance("PKCS12");
	        try (InputStream is = new ByteArrayInputStream(p12Data)) {
	            ks.load(is, password.toCharArray());
	        }
	        return ks;
	    }

	    public PrivateKey getPrivateKey(KeyStore ks, String password) throws Exception {
	        Enumeration<String> aliases = ks.aliases();
	        String alias = aliases.nextElement(); // Tomamos el primer alias
	        return (PrivateKey) ks.getKey(alias, password.toCharArray());
	    }

	    public X509Certificate getCertificate(KeyStore ks) throws Exception {
	        Enumeration<String> aliases = ks.aliases();
	        String alias = aliases.nextElement();
	        return (X509Certificate) ks.getCertificate(alias);
	    }

	    public boolean validateCertificate(X509Certificate cert) throws Exception {
	        // Validar fecha de expiración
	        cert.checkValidity();
	        
	        // Aquí puedes añadir más validaciones como:
	        // - Verificar contra lista de certificados revocados (CRL/OCSP)
	        // - Verificar el emisor del certificado
	        // - Verificar el propósito del certificado (firma digital)
	        
	        return true;
	    }
}

