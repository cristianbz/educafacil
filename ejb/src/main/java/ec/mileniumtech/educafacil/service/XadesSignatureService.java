package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.impl.DirectKeyingDataProvider;

/**
 * Servicio para realizar la firma electrónica de documentos XML usando el estándar XAdES-BES.
 */
@Stateless
@LocalBean
public class XadesSignatureService {

    /**
     * Firma un documento XML en formato XAdES-BES.
     * 
     * @param xmlDocument Documento XML original en bytes.
     * @param pkcs12Cert  Certificado digital en formato .p12 o .pfx.
     * @param password    Contraseña del certificado.
     * @return Documento XML firmado en bytes.
     * @throws Exception Si ocurre un error durante la firma.
     */
    public byte[] firmarDocumento(byte[] xmlDocument, byte[] pkcs12Cert, String password) throws Exception {
        // Cargar el almacén de llaves PKCS12
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new ByteArrayInputStream(pkcs12Cert), password.toCharArray());

        String alias = null;
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String currentAlias = aliases.nextElement();
            if (ks.isKeyEntry(currentAlias)) {
                alias = currentAlias;
                break;
            }
        }

        if (alias == null) {
            throw new Exception("No se encontró una llave privada en el certificado proporcionado.");
        }

        PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

        // Configurar el proveedor de datos de firmado
        KeyingDataProvider kdp = new DirectKeyingDataProvider(cert, key);
        XadesBesSigningProfile profile = new XadesBesSigningProfile(kdp);
        XadesSigner signer = profile.newSigner();

        // Parsear el documento XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlDocument));

        // Realizar la firma
        // Nota: En el SRI de Ecuador, se suele firmar todo el documento envolviendo el contenido.
        signer.sign(new xades4j.production.SignedDataObjects(new xades4j.production.DataObjectReference("")), doc.getDocumentElement());

        // Serializar el documento firmado a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(baos));

        return baos.toByteArray();
    }
}
