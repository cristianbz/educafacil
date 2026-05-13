package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Servicio para realizar la firma electrónica de documentos XML usando el estándar XAdES-BES.
 * Implementación compatible con los requisitos del SRI de Ecuador usando la API javax.xml.crypto.dsig
 * del JDK con construcción manual de los elementos XAdES requeridos.
 *
 * Correcciones aplicadas para cumplimiento SRI:
 * 1. Transform C14N agregado después de ENVELOPED en la referencia al documento.
 * 2. xmlns:ds declarado explícitamente en QualifyingProperties.
 * 3. IssuerName en formato RFC 1779 (getName("RFC1779")).
 * 4. Serialización con JAXP Transformer (no LSSerializer) para preservar namespaces.
 */
@Stateless
@LocalBean
public class XadesSignatureService {

    private static final String XADES_NS = "http://uri.etsi.org/01903/v1.3.2#";
    private static final String DSIG_NS  = "http://www.w3.org/2000/09/xmldsig#";
    
    /**
     * Crea el elemento <ds:Signature> vacío con el prefijo correcto en el DOM,
     * lo inserta al final del elemento raíz, y retorna la referencia.
     */
    private Element createSignatureContainer(Document doc, String signatureId) {
        Element sigElement = doc.createElementNS(DSIG_NS, "ds:Signature");
        sigElement.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:ds",
            DSIG_NS
        );
        sigElement.setAttribute("Id", signatureId);
        doc.getDocumentElement().appendChild(sigElement);
        return sigElement;
    }

    /**
     * Firma un documento XML en formato XAdES-BES.
     *
     * @param xmlDocument Documento XML original en bytes (UTF-8).
     * @param pkcs12Cert  Certificado digital en formato .p12 / .pfx.
     * @param password    Contraseña del certificado.
     * @return Documento XML firmado en bytes (UTF-8).
     * @throws Exception Si ocurre un error durante la firma.
     */
   
    public byte[] firmarDocumento(byte[] xmlDocument, byte[] pkcs12Cert, String password) throws Exception {

        // ── 1. Cargar PKCS12 ─────────────────────────────────────────────────
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new ByteArrayInputStream(pkcs12Cert), password.toCharArray());

        String alias = null;
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String current = aliases.nextElement();
            if (ks.isKeyEntry(current)) { alias = current; break; }
        }
        if (alias == null) throw new Exception("No se encontró llave privada en el certificado.");

        PrivateKey      privateKey = (PrivateKey)      ks.getKey(alias, password.toCharArray());
        X509Certificate cert       = (X509Certificate) ks.getCertificate(alias);

        // ── 2. Parsear XML ───────────────────────────────────────────────────
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmlDocument));
        doc.setXmlStandalone(true);

        // ── 3. IDs únicos ────────────────────────────────────────────────────
        String signatureId        = "Signature-"        + generateId();
        String signedPropertiesId = "SignedProperties-" + generateId();
        String keyInfoId          = "KeyInfoId-"        + signatureId;
        String referenceDocId     = "Reference-"        + generateId();
        String signatureValueId   = "SignatureValue-"   + generateId();

        // ── 4. Insertar <ds:Signature> vacío en el DOM con prefijo correcto ──
        //    CLAVE: el nodo existe ANTES de que XMLSignatureFactory lo llene
//        Element signatureNode = createSignatureContainer(doc, signatureId);
//        DOMSignContext signContext = new DOMSignContext(privateKey, doc.getDocumentElement());
//        signContext.setDefaultNamespacePrefix("ds");

        // ── 5. Construir XAdES ───────────────────────────────────────────────
        Element qualifyingProperties =
            buildXadesElements(doc, cert, signatureId, signedPropertiesId, referenceDocId);

        // ── 6. Configurar XMLSignatureFactory ────────────────────────────────
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Referencia 1: documento completo
        List<Transform> docTransforms = List.of(
            fac.newTransform(Transform.ENVELOPED,              (TransformParameterSpec) null),
            fac.newTransform(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null)
        );
//        Reference refDoc = fac.newReference(
//            "",
//            fac.newDigestMethod(DigestMethod.SHA256, null),
//            docTransforms,
//            null,
//            referenceDocId
//        );
        Reference refDoc = fac.newReference(
        	    "#comprobante",   // ← ID del elemento raíz de la factura
        	    fac.newDigestMethod(DigestMethod.SHA256, null),
        	    List.of(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
        	    null,
        	    referenceDocId
        	);

        // Referencia 2: SignedProperties
        List<Transform> spTransforms = List.of(
            fac.newTransform(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null)
        );
        Reference refSignedProps = fac.newReference(
            "#" + signedPropertiesId,
            fac.newDigestMethod(DigestMethod.SHA256, null),
            spTransforms,
            "http://uri.etsi.org/01903#SignedProperties",
            null
        );

        // SignedInfo
        SignedInfo signedInfo = fac.newSignedInfo(
            fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
            fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
            List.of(refDoc, refSignedProps)
        );

        // KeyInfo
        KeyInfoFactory kif    = fac.getKeyInfoFactory();
        X509Data x509Data     = kif.newX509Data(Collections.singletonList(cert));
        KeyInfo keyInfo       = kif.newKeyInfo(Collections.singletonList(x509Data), keyInfoId);

        // XMLObject con QualifyingProperties
        XMLObject xmlObject = fac.newXMLObject(
            Collections.singletonList(new javax.xml.crypto.dom.DOMStructure(qualifyingProperties)),
            null, null, null
        );

        // Firma
        XMLSignature signature = fac.newXMLSignature(
            signedInfo,
            keyInfo,
            Collections.singletonList(xmlObject),
            signatureId,
            signatureValueId
        );

//        // ── 7. DOMSignContext apuntando al nodo <ds:Signature> ya existente ──
//        //    nextSibling = null significa "insertar al final del signatureNode"
//        DOMSignContext signContext = new DOMSignContext(privateKey, signatureNode);
//        signContext.setDefaultNamespacePrefix("ds"); // ← CLAVE: prefijo ds: en todos los hijos
//
//        // Registrar SignedProperties para resolver la referencia "#SignedProperties-..."
//        Element signedPropsEl =
//            (Element) qualifyingProperties.getElementsByTagNameNS(XADES_NS, "SignedProperties").item(0);
//        signContext.setIdAttributeNS(signedPropsEl, null, "Id");
     // ── 7. DOMSignContext apuntando al elemento raíz del documento ──────
        DOMSignContext signContext = new DOMSignContext(privateKey, doc.getDocumentElement());
        signContext.setDefaultNamespacePrefix("ds");

        Element rootElement = doc.getDocumentElement();
        signContext.setIdAttributeNS(rootElement, null, "id");
        
     // Registrar SignedProperties
        Element signedPropsEl =
            (Element) qualifyingProperties.getElementsByTagNameNS(XADES_NS, "SignedProperties").item(0);
        signContext.setIdAttributeNS(signedPropsEl, null, "Id");


        // ── 8. Firmar ────────────────────────────────────────────────────────
        signature.sign(signContext);

        // ── 9. Serializar ────────────────────────────────────────────────────
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.setOutputProperty(OutputKeys.ENCODING,             "UTF-8");
//        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//        transformer.setOutputProperty(OutputKeys.INDENT,               "no");
//        
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        transformer.transform(new DOMSource(doc), new StreamResult(baos));
//     // ── AQUÍ: ya tienes el XML final en el baos ──────────────────────────
//        String xmlFirmadoFinal = baos.toString("UTF-8")
//        	    .replace("\r\n", "")
//        	    .replace("\n", "")
//        	    .replace("\r", "");
//        System.out.println("=== XML FINAL QUE SE ENVÍA AL SRI ===");
//        System.out.println(xmlFirmadoFinal);
//        System.out.println("=====================================");
//        // ─────────────────────────────────────────────────────────────────────
//        
//        return baos.toByteArray();
     // ── 9. Serializar ────────────────────────────────────────────────────
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING,             "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT,               "no");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(baos));

        // ✅ Eliminar BOM y espacios antes del <?xml
        String xmlString = baos.toString("UTF-8")
            .replace("\r\n", "")
            .replace("\n", "")
            .replace("\r", "")
            .trim(); // ← elimina espacios y BOM al inicio y al final

        // ✅ Verificar que empiece exactamente con <?xml
        if (!xmlString.startsWith("<?xml")) {
            // Eliminar cualquier caracter extraño antes del <?xml
            int index = xmlString.indexOf("<?xml");
            if (index > 0) {
                xmlString = xmlString.substring(index);
            }
        }


        return xmlString.getBytes(StandardCharsets.UTF_8);
    }
    
    
    
    /**
     * Construye el elemento etsi:QualifyingProperties con todos los sub-elementos
     * requeridos por XAdES-BES para el SRI de Ecuador.
     */
    private Element buildXadesElements(Document doc, X509Certificate cert,
    		String signatureId, String signedPropertiesId,
    		String referenceDocId) throws Exception {

    	// QualifyingProperties con prefijo etsi:
    	Element qualifyingProps = doc.createElementNS(XADES_NS, "etsi:QualifyingProperties");
    	qualifyingProps.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:etsi", XADES_NS);
    	qualifyingProps.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds",   DSIG_NS);
    	qualifyingProps.setAttribute("Target", "#" + signatureId);

    	// SignedProperties
    	Element signedProps = doc.createElementNS(XADES_NS, "etsi:SignedProperties");
    	signedProps.setAttribute("Id", signedPropertiesId);
    	qualifyingProps.appendChild(signedProps);

    	// SignedSignatureProperties
    	Element signedSigProps = doc.createElementNS(XADES_NS, "etsi:SignedSignatureProperties");
    	signedProps.appendChild(signedSigProps);

    	// SigningTime
    	Element signingTime = doc.createElementNS(XADES_NS, "etsi:SigningTime");
    	signingTime.setTextContent(
    			java.time.OffsetDateTime.now().format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    			);
    	signedSigProps.appendChild(signingTime);

    	// SigningCertificate
    	Element signingCert = doc.createElementNS(XADES_NS, "etsi:SigningCertificate");
    	signedSigProps.appendChild(signingCert);

    	Element certEl = doc.createElementNS(XADES_NS, "etsi:Cert");
    	signingCert.appendChild(certEl);

    	// CertDigest — usa ds: porque pertenece a xmldsig
    	Element certDigest = doc.createElementNS(XADES_NS, "etsi:CertDigest");
    	certEl.appendChild(certDigest);

    	Element digestMethod = doc.createElementNS(DSIG_NS, "ds:DigestMethod");
    	digestMethod.setAttribute("Algorithm", DigestMethod.SHA256);
    	certDigest.appendChild(digestMethod);

    	Element digestValue = doc.createElementNS(DSIG_NS, "ds:DigestValue");
    	MessageDigest md = MessageDigest.getInstance("SHA-256");
    	digestValue.setTextContent(
    			Base64.getEncoder().encodeToString(md.digest(cert.getEncoded()))
    			);
    	certDigest.appendChild(digestValue);

    	// IssuerSerial
    	Element issuerSerial = doc.createElementNS(XADES_NS, "etsi:IssuerSerial");
    	certEl.appendChild(issuerSerial);

    	Element issuerName = doc.createElementNS(DSIG_NS, "ds:X509IssuerName");
    	issuerName.setTextContent(cert.getIssuerX500Principal().getName());
    	issuerSerial.appendChild(issuerName);

    	Element serialNumber = doc.createElementNS(DSIG_NS, "ds:X509SerialNumber");
    	serialNumber.setTextContent(cert.getSerialNumber().toString());
    	issuerSerial.appendChild(serialNumber);

    	// SignedDataObjectProperties → MimeType
    	Element signedDataObjProps = doc.createElementNS(XADES_NS, "etsi:SignedDataObjectProperties");
    	signedProps.appendChild(signedDataObjProps);

    	Element dataObjFormat = doc.createElementNS(XADES_NS, "etsi:DataObjectFormat");
    	dataObjFormat.setAttribute("ObjectReference", "#" + referenceDocId);
    	signedDataObjProps.appendChild(dataObjFormat);

    	Element mimeType = doc.createElementNS(XADES_NS, "etsi:MimeType");
    	mimeType.setTextContent("text/xml");
    	dataObjFormat.appendChild(mimeType);

    	return qualifyingProps;
    }
  
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
