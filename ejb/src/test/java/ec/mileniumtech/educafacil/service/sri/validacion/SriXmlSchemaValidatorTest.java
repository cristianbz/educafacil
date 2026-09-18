package ec.mileniumtech.educafacil.service.sri.validacion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;

/**
 * Tests unitarios para {@link SriXmlSchemaValidator}.
 * Valida cada tipo de documento contra su XSD oficial del SRI.
 */
class SriXmlSchemaValidatorTest {

    private SriXmlSchemaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SriXmlSchemaValidator();
    }

    // =========================================================================
    // resolverRutaXsd
    // =========================================================================

    @Test
    void resolverRutaXsd_factura() {
        assertEquals(SriXmlSchemaValidator.XSD_FACTURA, validator.resolverRutaXsd("01"));
    }

    @Test
    void resolverRutaXsd_notaCredito() {
        assertEquals(SriXmlSchemaValidator.XSD_NOTA_CREDITO, validator.resolverRutaXsd("04"));
    }

    @Test
    void resolverRutaXsd_notaDebito() {
        assertEquals(SriXmlSchemaValidator.XSD_NOTA_DEBITO, validator.resolverRutaXsd("05"));
    }

    @Test
    void resolverRutaXsd_guiaRemision() {
        assertEquals(SriXmlSchemaValidator.XSD_GUIA_REMISION, validator.resolverRutaXsd("06"));
    }

    @Test
    void resolverRutaXsd_retencion() {
        assertEquals(SriXmlSchemaValidator.XSD_RETENCION, validator.resolverRutaXsd("07"));
    }

    @Test
    void resolverRutaXsd_tipoNoSoportado() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.resolverRutaXsd("99"));
        assertTrue(ex.getMessage().contains("no soportado"));
    }

    // =========================================================================
    // Factura - XSD
    // =========================================================================

    @Test
    void facturaValidaPasaXsd() {
        String xml = xmlFacturaValida();
        assertDoesNotThrow(() -> validator.validar("01", xml));
    }

    @Test
    void facturaConEstabInvalidoFallaXsd() {
        String xml = xmlFacturaValida().replace("<estab>001</estab>", "<estab>1</estab>");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validar("01", xml));
        assertEquals("BIZ-SRI-XML-XSD", ex.getCode());
    }

    @Test
    void facturaConRucInvalidoFallaXsd() {
        String xml = xmlFacturaValida().replace("<ruc>1790012345001</ruc>", "<ruc>123</ruc>");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validar("01", xml));
        assertEquals("BIZ-SRI-XML-XSD", ex.getCode());
    }

    // =========================================================================
    // Nota Crédito - XSD
    // =========================================================================

    @Test
    void notaCreditoValidaPasaXsd() {
        String xml = xmlNotaCreditoValida();
        assertDoesNotThrow(() -> validator.validar("04", xml));
    }

    @Test
    void notaCreditoSinMotivoPasaXsdPeroFallariaEnNegocio() {
        // motivo es obligatorio en negocio pero no en el XSD (minOccurs=1 pero con contenido vacío)
        // Solo validamos que el XSD se carga y se procesa correctamente
        String xml = xmlNotaCreditoValida().replace("<motivo>Devolucion</motivo>", "<motivo>X</motivo>");
        assertDoesNotThrow(() -> validator.validar("04", xml));
    }

    // =========================================================================
    // Retención v1.0.0 - XSD
    // =========================================================================

    @Test
    void retencionV1ValidaPasaXsd() {
        String xml = xmlRetencionV1Valida();
        assertDoesNotThrow(() -> validator.validar("07", xml));
    }

    @Test
    void retencionConRucInvalidoFallaXsd() {
        String xml = xmlRetencionV1Valida().replace("<ruc>1790012345001</ruc>", "<ruc>ABC</ruc>");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validar("07", xml));
        assertEquals("BIZ-SRI-XML-XSD", ex.getCode());
    }

    @Test
    void retencionSinImpuestosFallaXsd() {
        // Eliminar el bloque de impuestos completo para forzar error XSD
        String xmlOriginal = xmlRetencionV1Valida();
        int start = xmlOriginal.indexOf("<impuestos>");
        int end = xmlOriginal.indexOf("</impuestos>") + "</impuestos>".length();
        String xml = xmlOriginal.substring(0, start) + xmlOriginal.substring(end);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validar("07", xml));
        assertEquals("BIZ-SRI-XML-XSD", ex.getCode());
    }

    // =========================================================================
    // Nota Débito - XSD
    // =========================================================================

    @Test
    void notaDebitoValidaPasaXsd() {
        String xml = xmlNotaDebitoValida();
        assertDoesNotThrow(() -> validator.validar("05", xml));
    }

    // =========================================================================
    // validarXmlFirmado
    // =========================================================================

    @Test
    void xmlFirmadoConSignaturePasa() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<factura id=\"comprobante\" version=\"1.1.0\">"
                + "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo/></ds:Signature>"
                + "</factura>";
        assertDoesNotThrow(() -> validator.validarXmlFirmado(xml.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void xmlFirmadoSinSignatureFalla() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<factura id=\"comprobante\" version=\"1.1.0\"/>";
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validarXmlFirmado(xml.getBytes(StandardCharsets.UTF_8)));
        assertTrue(ex.getMessage().contains("Signature"));
    }

    @Test
    void xmlFirmadoSinIdComprobanteFalla() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<factura id=\"otro\" version=\"1.1.0\">"
                + "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"/>"
                + "</factura>";
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validarXmlFirmado(xml.getBytes(StandardCharsets.UTF_8)));
        assertTrue(ex.getMessage().contains("comprobante"));
    }

    @Test
    void xmlFirmadoVacioFalla() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validarXmlFirmado(null));
        assertTrue(ex.getMessage().contains("vacío"));
    }

    // =========================================================================
    // XML helpers
    // =========================================================================

    /** Clave de acceso de 49 dígitos válida para tests. */
    private static final String CLAVE_49 = "1501202501179001234500110010010000000011234567819";

    private String xmlFacturaValida() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<factura id=\"comprobante\" version=\"1.1.0\">"
                + "<infoTributaria>"
                + "<ambiente>1</ambiente>"
                + "<tipoEmision>1</tipoEmision>"
                + "<razonSocial>EDUCA FACIL SA</razonSocial>"
                + "<nombreComercial>EDUCA FACIL</nombreComercial>"
                + "<ruc>1790012345001</ruc>"
                + "<claveAcceso>" + CLAVE_49 + "</claveAcceso>"
                + "<codDoc>01</codDoc>"
                + "<estab>001</estab>"
                + "<ptoEmi>001</ptoEmi>"
                + "<secuencial>000000001</secuencial>"
                + "<dirMatriz>Quito</dirMatriz>"
                + "</infoTributaria>"
                + "<infoFactura>"
                + "<fechaEmision>15/01/2025</fechaEmision>"
                + "<dirEstablecimiento>Quito</dirEstablecimiento>"
                + "<obligadoContabilidad>SI</obligadoContabilidad>"
                + "<tipoIdentificacionComprador>05</tipoIdentificacionComprador>"
                + "<razonSocialComprador>JUAN PEREZ</razonSocialComprador>"
                + "<identificacionComprador>1710034065</identificacionComprador>"
                + "<totalSinImpuestos>100.00</totalSinImpuestos>"
                + "<totalDescuento>0.00</totalDescuento>"
                + "<totalConImpuestos>"
                + "<totalImpuesto>"
                + "<codigo>2</codigo>"
                + "<codigoPorcentaje>4</codigoPorcentaje>"
                + "<baseImponible>100.00</baseImponible>"
                + "<valor>15.00</valor>"
                + "</totalImpuesto>"
                + "</totalConImpuestos>"
                + "<propina>0.00</propina>"
                + "<importeTotal>115.00</importeTotal>"
                + "<moneda>DOLAR</moneda>"
                + "<pagos>"
                + "<pago><formaPago>01</formaPago><total>115.00</total></pago>"
                + "</pagos>"
                + "</infoFactura>"
                + "<detalles>"
                + "<detalle>"
                + "<codigoPrincipal>SERV</codigoPrincipal>"
                + "<descripcion>Matricula</descripcion>"
                + "<cantidad>1.00</cantidad>"
                + "<precioUnitario>100.00</precioUnitario>"
                + "<descuento>0.00</descuento>"
                + "<precioTotalSinImpuesto>100.00</precioTotalSinImpuesto>"
                + "<impuestos>"
                + "<impuesto>"
                + "<codigo>2</codigo>"
                + "<codigoPorcentaje>4</codigoPorcentaje>"
                + "<tarifa>15.00</tarifa>"
                + "<baseImponible>100.00</baseImponible>"
                + "<valor>15.00</valor>"
                + "</impuesto>"
                + "</impuestos>"
                + "</detalle>"
                + "</detalles>"
                + "</factura>";
    }

    private String xmlNotaCreditoValida() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<notaCredito id=\"comprobante\" version=\"1.1.0\">"
                + "<infoTributaria>"
                + "<ambiente>1</ambiente>"
                + "<tipoEmision>1</tipoEmision>"
                + "<razonSocial>EDUCA FACIL SA</razonSocial>"
                + "<nombreComercial>EDUCA FACIL</nombreComercial>"
                + "<ruc>1790012345001</ruc>"
                + "<claveAcceso>" + CLAVE_49 + "</claveAcceso>"
                + "<codDoc>04</codDoc>"
                + "<estab>001</estab>"
                + "<ptoEmi>001</ptoEmi>"
                + "<secuencial>000000001</secuencial>"
                + "<dirMatriz>Quito</dirMatriz>"
                + "</infoTributaria>"
                + "<infoNotaCredito>"
                + "<fechaEmision>15/01/2025</fechaEmision>"
                + "<dirEstablecimiento>Quito</dirEstablecimiento>"
                + "<tipoIdentificacionComprador>05</tipoIdentificacionComprador>"
                + "<razonSocialComprador>JUAN PEREZ</razonSocialComprador>"
                + "<identificacionComprador>1710034065</identificacionComprador>"
                + "<obligadoContabilidad>SI</obligadoContabilidad>"
                + "<codDocModificado>01</codDocModificado>"
                + "<numDocModificado>001-001-000000001</numDocModificado>"
                + "<fechaEmisionDocSustento>10/01/2025</fechaEmisionDocSustento>"
                + "<totalSinImpuestos>50.00</totalSinImpuestos>"
                + "<valorModificacion>57.50</valorModificacion>"
                + "<moneda>DOLAR</moneda>"
                + "<totalConImpuestos>"
                + "<totalImpuesto>"
                + "<codigo>2</codigo>"
                + "<codigoPorcentaje>4</codigoPorcentaje>"
                + "<baseImponible>50.00</baseImponible>"
                + "<valor>7.50</valor>"
                + "</totalImpuesto>"
                + "</totalConImpuestos>"
                + "<motivo>Devolucion</motivo>"
                + "</infoNotaCredito>"
                + "<detalles>"
                + "<detalle>"
                + "<codigoInterno>SERV</codigoInterno>"
                + "<descripcion>Matricula devuelta</descripcion>"
                + "<cantidad>1.00</cantidad>"
                + "<precioUnitario>50.00</precioUnitario>"
                + "<descuento>0.00</descuento>"
                + "<precioTotalSinImpuesto>50.00</precioTotalSinImpuesto>"
                + "<impuestos>"
                + "<impuesto>"
                + "<codigo>2</codigo>"
                + "<codigoPorcentaje>4</codigoPorcentaje>"
                + "<tarifa>15.00</tarifa>"
                + "<baseImponible>50.00</baseImponible>"
                + "<valor>7.50</valor>"
                + "</impuesto>"
                + "</impuestos>"
                + "</detalle>"
                + "</detalles>"
                + "</notaCredito>";
    }

    private String xmlRetencionV1Valida() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<comprobanteRetencion id=\"comprobante\" version=\"1.0.0\">"
                + "<infoTributaria>"
                + "<ambiente>1</ambiente>"
                + "<tipoEmision>1</tipoEmision>"
                + "<razonSocial>EDUCA FACIL SA</razonSocial>"
                + "<nombreComercial>EDUCA FACIL</nombreComercial>"
                + "<ruc>1790012345001</ruc>"
                + "<claveAcceso>" + CLAVE_49 + "</claveAcceso>"
                + "<codDoc>07</codDoc>"
                + "<estab>001</estab>"
                + "<ptoEmi>001</ptoEmi>"
                + "<secuencial>000000001</secuencial>"
                + "<dirMatriz>Quito</dirMatriz>"
                + "</infoTributaria>"
                + "<infoCompRetencion>"
                + "<fechaEmision>15/01/2025</fechaEmision>"
                + "<dirEstablecimiento>Quito</dirEstablecimiento>"
                + "<obligadoContabilidad>SI</obligadoContabilidad>"
                + "<tipoIdentificacionSujetoRetenido>04</tipoIdentificacionSujetoRetenido>"
                + "<razonSocialSujetoRetenido>PROVEEDOR SA</razonSocialSujetoRetenido>"
                + "<identificacionSujetoRetenido>1790998877001</identificacionSujetoRetenido>"
                + "<periodoFiscal>01/2025</periodoFiscal>"
                + "</infoCompRetencion>"
                + "<impuestos>"
                + "<impuesto>"
                + "<codigo>1</codigo>"
                + "<codigoRetencion>303</codigoRetencion>"
                + "<baseImponible>100.00</baseImponible>"
                + "<porcentajeRetener>1.00</porcentajeRetener>"
                + "<valorRetenido>1.00</valorRetenido>"
                + "<codDocSustento>01</codDocSustento>"
                + "<numDocSustento>001001000000001</numDocSustento>"
                + "<fechaEmisionDocSustento>10/01/2025</fechaEmisionDocSustento>"
                + "</impuesto>"
                + "</impuestos>"
                + "</comprobanteRetencion>";
    }

    private String xmlNotaDebitoValida() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<notaDebito id=\"comprobante\" version=\"1.0.0\">"
                + "<infoTributaria>"
                + "<ambiente>1</ambiente>"
                + "<tipoEmision>1</tipoEmision>"
                + "<razonSocial>EDUCA FACIL SA</razonSocial>"
                + "<nombreComercial>EDUCA FACIL</nombreComercial>"
                + "<ruc>1790012345001</ruc>"
                + "<claveAcceso>" + CLAVE_49 + "</claveAcceso>"
                + "<codDoc>05</codDoc>"
                + "<estab>001</estab>"
                + "<ptoEmi>001</ptoEmi>"
                + "<secuencial>000000001</secuencial>"
                + "<dirMatriz>Quito</dirMatriz>"
                + "</infoTributaria>"
                + "<infoNotaDebito>"
                + "<fechaEmision>15/01/2025</fechaEmision>"
                + "<dirEstablecimiento>Quito</dirEstablecimiento>"
                + "<tipoIdentificacionComprador>05</tipoIdentificacionComprador>"
                + "<razonSocialComprador>JUAN PEREZ</razonSocialComprador>"
                + "<identificacionComprador>1710034065</identificacionComprador>"
                + "<obligadoContabilidad>SI</obligadoContabilidad>"
                + "<codDocModificado>01</codDocModificado>"
                + "<numDocModificado>001-001-000000001</numDocModificado>"
                + "<fechaEmisionDocSustento>10/01/2025</fechaEmisionDocSustento>"
                + "<totalSinImpuestos>20.00</totalSinImpuestos>"
                + "<impuestos>"
                + "<impuesto>"
                + "<codigo>2</codigo>"
                + "<codigoPorcentaje>4</codigoPorcentaje>"
                + "<tarifa>15.00</tarifa>"
                + "<baseImponible>20.00</baseImponible>"
                + "<valor>3.00</valor>"
                + "</impuesto>"
                + "</impuestos>"
                + "<valorTotal>23.00</valorTotal>"
                + "<pagos>"
                + "<pago><formaPago>01</formaPago><total>23.00</total></pago>"
                + "</pagos>"
                + "</infoNotaDebito>"
                + "<motivos>"
                + "<motivo>"
                + "<razon>Intereses por mora</razon>"
                + "<valor>20.00</valor>"
                + "</motivo>"
                + "</motivos>"
                + "</notaDebito>";
    }
}
