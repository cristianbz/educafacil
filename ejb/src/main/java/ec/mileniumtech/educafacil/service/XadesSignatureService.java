package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Servicio para realizar la firma electrónica de documentos XML usando el estándar XAdES-BES.
 * Implementación compatible con los requisitos del SRI de Ecuador usando la API javax.xml.crypto.dsig
 * del JDK con construcción manual de los elementos XAdES requeridos.
 */
@Stateless
@LocalBean
public class XadesSignatureService {

    private static final String XADES_NS = "http://uri.etsi.org/01903/v1.3.2#";
    private static final String DSIG_NS  = "http://www.w3.org/2000/09/xmldsig#";

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
        // 1. Cargar el almacén de llaves PKCS12
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

        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

        // 2. Parsear el documento XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlDocument));

        // 3. Generar IDs únicos para los elementos de la firma
        String signatureId = "Signature" + generateId();
        String signedPropertiesId = "SignedProperties" + generateId();
        String keyInfoId = "KeyInfoId-" + signatureId;
        String referenceDocId = "Reference-" + generateId();

        // 4. Construir elementos XAdES manualmente
        Element qualifyingProperties = buildXadesElements(doc, cert, signatureId, signedPropertiesId);

        // 5. Crear la firma XML usando la API estándar del JDK
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Referencia 1: Documento completo (firma enveloped)
        List<Transform> docTransforms = new ArrayList<>();
        docTransforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        Reference refDoc = fac.newReference(
                "",
                fac.newDigestMethod(DigestMethod.SHA1, null),
                docTransforms,
                null,
                referenceDocId);

        // Referencia 2: SignedProperties (requerido por XAdES-BES)
        Reference refSignedProps = fac.newReference(
                "#" + signedPropertiesId,
                fac.newDigestMethod(DigestMethod.SHA1, null),
                null,
                "http://uri.etsi.org/01903#SignedProperties",
                null);

        // SignedInfo con C14N y RSA-SHA1
        SignedInfo signedInfo = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                List.of(refDoc, refSignedProps));

        // KeyInfo con certificado X509
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        X509Data x509Data = kif.newX509Data(Collections.singletonList(cert));
        KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(x509Data), keyInfoId);

        // XMLObject que contiene el QualifyingProperties
        XMLObject xmlObject = fac.newXMLObject(
                Collections.singletonList(new javax.xml.crypto.dom.DOMStructure(qualifyingProperties)),
                null, null, null);

        // Crear y ejecutar la firma
        XMLSignature signature = fac.newXMLSignature(
                signedInfo,
                keyInfo,
                Collections.singletonList(xmlObject),
                signatureId,
                null);

        DOMSignContext signContext = new DOMSignContext(privateKey, doc.getDocumentElement());
        // Registrar el ID del SignedProperties para la resolución de la referencia URI
        Element signedPropsElement = (Element) qualifyingProperties.getElementsByTagNameNS(XADES_NS, "SignedProperties").item(0);
        signContext.setIdAttributeNS(signedPropsElement, null, "Id");

        signature.sign(signContext);

        // 6. Serializar el documento firmado a bytes
        DOMImplementationLS domImplLS = (DOMImplementationLS) doc.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        lsOutput.setByteStream(baos);
        serializer.write(doc, lsOutput);

        return baos.toByteArray();
    }

    /**
     * Construye los elementos XAdES QualifyingProperties para la firma XAdES-BES.
     */
    private Element buildXadesElements(Document doc, X509Certificate cert,
            String signatureId, String signedPropertiesId) throws Exception {

        // QualifyingProperties
        Element qualifyingProperties = doc.createElementNS(XADES_NS, "etsi:QualifyingProperties");
        qualifyingProperties.setAttribute("Target", "#" + signatureId);
        qualifyingProperties.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:etsi", XADES_NS);

        // SignedProperties
        Element signedProperties = doc.createElementNS(XADES_NS, "etsi:SignedProperties");
        signedProperties.setAttribute("Id", signedPropertiesId);
        qualifyingProperties.appendChild(signedProperties);

        // SignedSignatureProperties
        Element signedSigProps = doc.createElementNS(XADES_NS, "etsi:SignedSignatureProperties");
        signedProperties.appendChild(signedSigProps);

        // SigningTime
        Element signingTime = doc.createElementNS(XADES_NS, "etsi:SigningTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        signingTime.setTextContent(sdf.format(new Date()));
        signedSigProps.appendChild(signingTime);

        // SigningCertificate
        Element signingCertificate = doc.createElementNS(XADES_NS, "etsi:SigningCertificate");
        Element certEl = doc.createElementNS(XADES_NS, "etsi:Cert");

        // CertDigest
        Element certDigest = doc.createElementNS(XADES_NS, "etsi:CertDigest");

        Element digestMethodEl = doc.createElementNS(DSIG_NS, "ds:DigestMethod");
        digestMethodEl.setAttribute("Algorithm", DigestMethod.SHA1);
        certDigest.appendChild(digestMethodEl);

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] certDigestBytes = md.digest(cert.getEncoded());
        Element digestValueEl = doc.createElementNS(DSIG_NS, "ds:DigestValue");
        digestValueEl.setTextContent(Base64.getEncoder().encodeToString(certDigestBytes));
        certDigest.appendChild(digestValueEl);

        certEl.appendChild(certDigest);

        // IssuerSerial
        Element issuerSerial = doc.createElementNS(XADES_NS, "etsi:IssuerSerial");

        Element issuerName = doc.createElementNS(DSIG_NS, "ds:X509IssuerName");
        issuerName.setTextContent(cert.getIssuerX500Principal().getName());
        issuerSerial.appendChild(issuerName);

        Element serialNumber = doc.createElementNS(DSIG_NS, "ds:X509SerialNumber");
        serialNumber.setTextContent(cert.getSerialNumber().toString());
        issuerSerial.appendChild(serialNumber);

        certEl.appendChild(issuerSerial);
        signingCertificate.appendChild(certEl);
        signedSigProps.appendChild(signingCertificate);

        // SignedDataObjectProperties (vacío, pero algunos validadores lo requieren)
        Element signedDataObjProps = doc.createElementNS(XADES_NS, "etsi:SignedDataObjectProperties");
        Element dataObjFormat = doc.createElementNS(XADES_NS, "etsi:DataObjectFormat");
        dataObjFormat.setAttribute("ObjectReference", "#" + "Reference-doc");

        Element mimeType = doc.createElementNS(XADES_NS, "etsi:MimeType");
        mimeType.setTextContent("text/xml");
        dataObjFormat.appendChild(mimeType);
        signedDataObjProps.appendChild(dataObjFormat);
        signedProperties.appendChild(signedDataObjProps);

        return qualifyingProperties;
    }

    private String generateId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
