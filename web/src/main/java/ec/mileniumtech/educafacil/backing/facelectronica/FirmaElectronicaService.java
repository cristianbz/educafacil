package ec.mileniumtech.educafacil.backing.facelectronica;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

import jakarta.inject.Inject;

/**
* @author christian May 20, 2025
*/
/**
 * 
 */
public class FirmaElectronicaService {
	@Inject
    private P12Utils p12Utils;
	public boolean validarP12(byte[] p12Data, String password) throws Exception {
        KeyStore ks = p12Utils.loadP12(p12Data, password);
        X509Certificate cert = p12Utils.getCertificate(ks);
        return p12Utils.validateCertificate(cert);
    }

    public byte[] firmarDocumento(byte[] p12Data, String password, byte[] documento) throws Exception {
        KeyStore ks = p12Utils.loadP12(p12Data, password);
        PrivateKey privateKey = p12Utils.getPrivateKey(ks, password);
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(documento);
        
        return signature.sign();
    }

    public boolean verificarFirma(byte[] p12Data, String password, byte[] documento, byte[] firma) throws Exception {
        KeyStore ks = p12Utils.loadP12(p12Data, password);
        X509Certificate cert = p12Utils.getCertificate(ks);
        PublicKey publicKey = cert.getPublicKey();
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(documento);
        
        return signature.verify(firma);
    }
}

