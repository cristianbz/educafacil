package ec.mileniumtech.educafacil.service.sri.validacion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Valida el XML sin firmar contra los XSD oficiales del SRI colocados en
 * {@code /sri/xsd/}.
 * <p>
 * La retención se genera con el modelo 1.0.0 ({@code impuestos}); el XSD
 * disponible es 2.0.0 ({@code docsSustento}). En ese caso no se aplica XSD
 * para no rechazar comprobantes estructurales vigentes.
 */
@Singleton
@LocalBean
public class SriXmlSchemaValidator {

    private static final Logger log = LogManager.getLogger(SriXmlSchemaValidator.class);

    static final String XSD_FACTURA = "/sri/xsd/factura_V2.1.0.xsd";
    static final String XSD_NOTA_CREDITO = "/sri/xsd/NOTA_CREDITO-1.1.0.xsd";
    static final String XSD_RETENCION = "/sri/xsd/ComprobanteRetencion_V1.0.0.xsd";
    static final String XSD_NOTA_DEBITO = "/sri/xsd/NOTA_DEBITO-1.0.0.xsd";
    static final String XSD_GUIA_REMISION = "/sri/xsd/GuiaRemision_V1.1.0.xsd";

    private final Map<String, Schema> esquemas = new ConcurrentHashMap<>();

    /**
     * Valida el XML contra el XSD del tipo de documento. Si el tipo no tiene
     * esquema aplicable, no hace nada.
     *
     * @param codigoDocumento {@code 01} factura, {@code 04} nota de crédito, {@code 07} retención
     * @param xml             XML sin firmar
     */
    @Lock(LockType.READ)
    public void validar(String codigoDocumento, String xml) {
        String rutaXsd = resolverRutaXsd(codigoDocumento);
        if (rutaXsd == null) {
            return;
        }
        Schema schema = esquemas.computeIfAbsent(rutaXsd, this::cargarSchema);
        List<String> errores = new ArrayList<>();
        try {
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new CollectingErrorHandler(errores));
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (Exception e) {
            if (errores.isEmpty()) {
                errores.add(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            }
        }
        if (!errores.isEmpty()) {
            throw new BusinessException(
                    "El XML no cumple el esquema del SRI: " + String.join(" | ", errores),
                    "BIZ-SRI-XML-XSD");
        }
    }

    /**
     * Comprueba que el XML firmado sea parseable y contenga la firma XAdES.
     *
     * @param xmlFirmado bytes UTF-8 del XML firmado
     */
    public void validarXmlFirmado(byte[] xmlFirmado) {
        if (xmlFirmado == null || xmlFirmado.length == 0) {
            throw new BusinessException("El XML firmado está vacío.", "BIZ-SRI-XML-FIRMA");
        }
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            var builder = factory.newSAXParser().getXMLReader();
            FirmaCheckHandler handler = new FirmaCheckHandler();
            builder.setContentHandler(handler);
            builder.parse(new org.xml.sax.InputSource(new ByteArrayInputStream(xmlFirmado)));
            if (!"comprobante".equals(handler.idRaiz)) {
                throw new BusinessException(
                        "El XML firmado no tiene id=\"comprobante\" en el elemento raíz.",
                        "BIZ-SRI-XML-FIRMA");
            }
            if (!handler.tieneFirma) {
                throw new BusinessException(
                        "El XML firmado no contiene el nodo ds:Signature requerido por el SRI.",
                        "BIZ-SRI-XML-FIRMA");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(
                    "El XML firmado no es un documento XML bien formado: " + e.getMessage(),
                    "BIZ-SRI-XML-FIRMA", e);
        }
    }

    String resolverRutaXsd(String codigoDocumento) {
        if ("01".equals(codigoDocumento)) {
            return XSD_FACTURA;
        }
        if ("04".equals(codigoDocumento)) {
            return XSD_NOTA_CREDITO;
        }
        if ("05".equals(codigoDocumento)) {
            return XSD_NOTA_DEBITO;
        }
        if ("06".equals(codigoDocumento)) {
            return XSD_GUIA_REMISION;
        }
        if ("07".equals(codigoDocumento)) {
            return XSD_RETENCION;
        }
        throw new BusinessException("Tipo de documento no soportado para validación XSD: " + codigoDocumento,
                "BIZ-SRI-XML-TIPO");
    }

    private Schema cargarSchema(String rutaClasspath) {
        InputStream xsd = getClass().getResourceAsStream(rutaClasspath);
        if (xsd == null) {
            throw new SystemException("No se encontró el XSD del SRI en el classpath: " + rutaClasspath,
                    "SYS-SRI-XSD-MISSING");
        }
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "all");
                factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "all");
            } catch (Exception e) {
                log.warn("No se pudo configurar ACCESS_EXTERNAL_SCHEMA en SchemaFactory: {}", e.getMessage());
            }
            factory.setResourceResolver(new ClasspathXsdResolver());

            InputStream dsigXsd = getClass().getResourceAsStream("/sri/xsd/xmldsig-core-schema.xsd");
            if (dsigXsd != null) {
                return factory.newSchema(new Source[] {
                        new StreamSource(dsigXsd),
                        new StreamSource(xsd)
                });
            }
            return factory.newSchema(new StreamSource(xsd));
        } catch (Exception e) {
            log.error("Error al cargar XSD del SRI {}: {}", rutaClasspath, e.getMessage(), e);
            throw new SystemException("No se pudo cargar el XSD del SRI: " + rutaClasspath,
                    "SYS-SRI-XSD-LOAD", e);
        }
    }

    private static final class CollectingErrorHandler implements ErrorHandler {
        private final List<String> errores;

        private CollectingErrorHandler(List<String> errores) {
            this.errores = errores;
        }

        @Override
        public void warning(SAXParseException exception) {
            // advertencias del schema no bloquean el envío
        }

        @Override
        public void error(SAXParseException exception) {
            errores.add(formatear(exception));
        }

        @Override
        public void fatalError(SAXParseException exception) {
            errores.add(formatear(exception));
        }

        private static String formatear(SAXParseException e) {
            return "línea " + e.getLineNumber() + ": " + e.getMessage();
        }
    }

    private static final class FirmaCheckHandler extends org.xml.sax.helpers.DefaultHandler {
        private boolean raiz = true;
        private String idRaiz;
        private boolean tieneFirma;

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) {
            if (raiz) {
                idRaiz = attributes.getValue("id");
                raiz = false;
            }
            if ("Signature".equals(localName)
                    || "Signature".equals(qName)
                    || (qName != null && qName.endsWith(":Signature"))) {
                tieneFirma = true;
            }
        }
    }

    private static final class ClasspathXsdResolver implements LSResourceResolver {
        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId,
                                       String systemId, String baseURI) {
            String fileName = extraerNombre(systemId);
            if (fileName == null && "http://www.w3.org/2000/09/xmldsig#".equals(namespaceURI)) {
                fileName = "xmldsig-core-schema.xsd";
            }
            if (fileName == null) {
                return null;
            }
            InputStream in = SriXmlSchemaValidator.class.getResourceAsStream("/sri/xsd/" + fileName);
            if (in == null) {
                return null;
            }
            SimpleLSInput input = new SimpleLSInput();
            input.setPublicId(publicId);
            input.setSystemId(systemId);
            input.setBaseURI(baseURI);
            input.setByteStream(in);
            input.setEncoding(StandardCharsets.UTF_8.name());
            return input;
        }

        private static String extraerNombre(String systemId) {
            if (systemId == null || systemId.isBlank()) {
                return null;
            }
            int slash = Math.max(systemId.lastIndexOf('/'), systemId.lastIndexOf('\\'));
            return slash >= 0 ? systemId.substring(slash + 1) : systemId;
        }
    }

    private static final class SimpleLSInput implements LSInput {
        private Reader characterStream;
        private InputStream byteStream;
        private String stringData;
        private String systemId;
        private String publicId;
        private String baseURI;
        private String encoding;
        private boolean certifiedText;

        @Override
        public Reader getCharacterStream() {
            return characterStream;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            this.characterStream = characterStream;
        }

        @Override
        public InputStream getByteStream() {
            return byteStream;
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            this.byteStream = byteStream;
        }

        @Override
        public String getStringData() {
            return stringData;
        }

        @Override
        public void setStringData(String stringData) {
            this.stringData = stringData;
        }

        @Override
        public String getSystemId() {
            return systemId;
        }

        @Override
        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        @Override
        public String getBaseURI() {
            return baseURI;
        }

        @Override
        public void setBaseURI(String baseURI) {
            this.baseURI = baseURI;
        }

        @Override
        public String getEncoding() {
            return encoding;
        }

        @Override
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        @Override
        public boolean getCertifiedText() {
            return certifiedText;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            this.certifiedText = certifiedText;
        }
    }
}
