package ec.mileniumtech.educafacil.service.sri.validacion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.modelo.sri.Factura;
import ec.mileniumtech.educafacil.modelo.sri.Factura.CampoAdicional;
import ec.mileniumtech.educafacil.modelo.sri.Factura.Detalle;
import ec.mileniumtech.educafacil.modelo.sri.Factura.Impuesto;
import ec.mileniumtech.educafacil.modelo.sri.Factura.InfoFactura;
import ec.mileniumtech.educafacil.modelo.sri.Factura.InfoTributaria;
import ec.mileniumtech.educafacil.modelo.sri.Factura.PagoSRI;
import ec.mileniumtech.educafacil.modelo.sri.Factura.TotalImpuesto;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;

class SriComprobanteValidadorTest {

    private SriComprobanteValidador validador;
    private ClaveAccesoGenerator claveAccesoGenerator;
    private SriXmlSchemaValidator schemaValidator;

    @BeforeEach
    void setUp() {
        claveAccesoGenerator = new ClaveAccesoGenerator();
        schemaValidator = new SriXmlSchemaValidator();
        validador = new SriComprobanteValidador();
        validador.setClaveAccesoGenerator(claveAccesoGenerator);
        validador.setSchemaValidator(schemaValidator);
    }

    // =========================================================================
    // Factura
    // =========================================================================

    @Test
    void facturaValidaPasaXsdYReglas() {
        Factura factura = facturaValida();
        String xml = xmlFactura(factura);
        assertDoesNotThrow(() -> validador.validar("01", factura, xml));
    }

    @Test
    void facturaConRucCortoSeRechaza() {
        Factura factura = facturaValida();
        factura.getInfoTributaria().setRuc("1790012345");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("01", factura, xmlFactura(factura)));
        assertTrue(ex.getMessage().contains("RUC"));
        assertTrue("BIZ-SRI-XML-INVALIDO".equals(ex.getCode()));
    }

    @Test
    void facturaSinDetallesSeRechaza() {
        Factura factura = facturaValida();
        factura.getDetallesList().clear();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("01", factura, xmlFactura(factura)));
        assertTrue(ex.getMessage().contains("detalle"));
    }

    @Test
    void xmlConEstabInvalidoFallaXsd() {
        Factura factura = facturaValida();
        String xml = xmlFactura(factura).replace("<estab>001</estab>", "<estab>1</estab>");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("01", factura, xml));
        assertTrue("BIZ-SRI-XML-XSD".equals(ex.getCode()));
    }

    @Test
    void facturaConCampoAdicionalVacioSeRechaza() {
        Factura factura = facturaValida();
        CampoAdicional ca = new CampoAdicional();
        ca.setNombre("Direccion");
        ca.setValor("");
        factura.getInfoAdicionalList().add(ca);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("01", factura, xmlFactura(factura)));
        assertTrue(ex.getMessage().contains("Los campos adicionales deben tener nombre y valor."));
    }

    @Test
    void xmlFirmadoSinSignatureSeRechaza() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><factura id=\"comprobante\" version=\"1.1.0\"/>";
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validarXmlFirmado(xml.getBytes(StandardCharsets.UTF_8)));
        assertTrue(ex.getMessage().contains("Signature"));
    }

    // =========================================================================
    // Retención
    // =========================================================================

    @Test
    void retencionValidaPasaValidacion() {
        ComprobanteRetencion ret = retencionValida();
        String xml = xmlRetencion(ret);
        assertDoesNotThrow(() -> validador.validar("07", ret, xml));
    }

    @Test
    void retencionConPeriodoFiscalInvalidoSeRechaza() {
        ComprobanteRetencion ret = retencionValida();
        ret.getInfoCompRetencion().setPeriodoFiscal("2025/01"); // formato incorrecto
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("07", ret, xmlRetencion(ret)));
        assertTrue(ex.getMessage().contains("periodoFiscal"));
    }

    @Test
    void retencionSinImpuestosSeRechaza() {
        ComprobanteRetencion ret = retencionValida();
        ret.getImpuestosList().clear();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("07", ret, xmlRetencion(ret)));
        assertTrue(ex.getMessage().contains("impuesto"));
    }

    @Test
    void retencionSinSujetoRetenidoSeRechaza() {
        ComprobanteRetencion ret = retencionValida();
        ret.getInfoCompRetencion().setIdentificacionSujetoRetenido(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("07", ret, xmlRetencion(ret)));
        assertTrue(ex.getMessage().contains("identificacionSujetoRetenido"));
    }

    // =========================================================================
    // Nota Crédito
    // =========================================================================

    @Test
    void notaCreditoValidaPasaValidacion() {
        NotaCredito nc = notaCreditoValida();
        String xml = xmlNotaCredito(nc);
        assertDoesNotThrow(() -> validador.validar("04", nc, xml));
    }

    @Test
    void notaCreditoSinMotivoSeRechaza() {
        NotaCredito nc = notaCreditoValida();
        nc.getInfoNotaCredito().setMotivo(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("04", nc, xmlNotaCredito(nc)));
        assertTrue(ex.getMessage().contains("motivo"));
    }

    @Test
    void notaCreditoConNumDocModificadoInvalidoSeRechaza() {
        NotaCredito nc = notaCreditoValida();
        nc.getInfoNotaCredito().setNumDocModificado("001001000000001"); // falta guiones
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validador.validar("04", nc, xmlNotaCredito(nc)));
        assertTrue(ex.getMessage().contains("numDocModificado"));
    }

    // =========================================================================
    // Helpers - Factura
    // =========================================================================

    private Factura facturaValida() {
        String clave = claveAccesoGenerator.generarClaveAcceso(
                LocalDate.of(2025, 1, 15), "01", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");

        Factura factura = new Factura();
        InfoTributaria trib = new InfoTributaria();
        trib.setAmbiente("1");
        trib.setTipoEmision("1");
        trib.setRazonSocial("EDUCA FACIL SA");
        trib.setNombreComercial("EDUCA FACIL");
        trib.setRuc("1790012345001");
        trib.setClaveAcceso(clave);
        trib.setCodDoc("01");
        trib.setEstab("001");
        trib.setPtoEmi("001");
        trib.setSecuencial("000000001");
        trib.setDirMatriz("Quito");
        factura.setInfoTributaria(trib);

        InfoFactura info = new InfoFactura();
        info.setFechaEmision("15/01/2025");
        info.setDirEstablecimiento("Quito");
        info.setObligadoContabilidad("SI");
        info.setTipoIdentificacionComprador("05");
        info.setRazonSocialComprador("JUAN PEREZ");
        info.setIdentificacionComprador("1710034065");
        info.setTotalSinImpuestos(new BigDecimal("100.00"));
        info.setTotalDescuento(new BigDecimal("0.00"));
        info.setImporteTotal(new BigDecimal("115.00"));

        TotalImpuesto ti = new TotalImpuesto();
        ti.setCodigo("2");
        ti.setCodigoPorcentaje("4");
        ti.setBaseImponible(new BigDecimal("100.00"));
        ti.setValor(new BigDecimal("15.00"));
        info.getTotalConImpuestosList().add(ti);

        PagoSRI pago = new PagoSRI();
        pago.setFormaPago("01");
        pago.setTotal(new BigDecimal("115.00"));
        info.getPagosList().add(pago);
        factura.setInfoFactura(info);

        Detalle det = new Detalle();
        det.setCodigoPrincipal("SERV");
        det.setDescripcion("Matricula");
        det.setCantidad(new BigDecimal("1.00"));
        det.setPrecioUnitario(new BigDecimal("100.00"));
        det.setDescuento(new BigDecimal("0.00"));
        det.setPrecioTotalSinImpuesto(new BigDecimal("100.00"));
        Impuesto imp = new Impuesto();
        imp.setCodigo("2");
        imp.setCodigoPorcentaje("4");
        imp.setTarifa(new BigDecimal("15").setScale(2, RoundingMode.HALF_UP).toPlainString());
        imp.setBaseImponible(new BigDecimal("100.00"));
        imp.setValor(new BigDecimal("15.00"));
        det.getImpuestosList().add(imp);
        factura.getDetallesList().add(det);

        CampoAdicional email = new CampoAdicional();
        email.setNombre("Email");
        email.setValor("cliente@test.com");
        factura.getInfoAdicionalList().add(email);
        return factura;
    }

    private String xmlFactura(Factura f) {
        InfoTributaria t = f.getInfoTributaria();
        InfoFactura i = f.getInfoFactura();
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<factura id=\"comprobante\" version=\"1.1.0\">");
        sb.append("<infoTributaria>");
        sb.append("<ambiente>").append(t.getAmbiente()).append("</ambiente>");
        sb.append("<tipoEmision>").append(t.getTipoEmision()).append("</tipoEmision>");
        sb.append("<razonSocial>").append(t.getRazonSocial()).append("</razonSocial>");
        sb.append("<nombreComercial>").append(t.getNombreComercial()).append("</nombreComercial>");
        sb.append("<ruc>").append(t.getRuc()).append("</ruc>");
        sb.append("<claveAcceso>").append(t.getClaveAcceso()).append("</claveAcceso>");
        sb.append("<codDoc>").append(t.getCodDoc()).append("</codDoc>");
        sb.append("<estab>").append(t.getEstab()).append("</estab>");
        sb.append("<ptoEmi>").append(t.getPtoEmi()).append("</ptoEmi>");
        sb.append("<secuencial>").append(t.getSecuencial()).append("</secuencial>");
        sb.append("<dirMatriz>").append(t.getDirMatriz()).append("</dirMatriz>");
        sb.append("</infoTributaria>");
        sb.append("<infoFactura>");
        sb.append("<fechaEmision>").append(i.getFechaEmision()).append("</fechaEmision>");
        sb.append("<dirEstablecimiento>").append(i.getDirEstablecimiento()).append("</dirEstablecimiento>");
        sb.append("<obligadoContabilidad>").append(i.getObligadoContabilidad()).append("</obligadoContabilidad>");
        sb.append("<tipoIdentificacionComprador>").append(i.getTipoIdentificacionComprador()).append("</tipoIdentificacionComprador>");
        sb.append("<razonSocialComprador>").append(i.getRazonSocialComprador()).append("</razonSocialComprador>");
        sb.append("<identificacionComprador>").append(i.getIdentificacionComprador()).append("</identificacionComprador>");
        sb.append("<totalSinImpuestos>").append(i.getTotalSinImpuestos()).append("</totalSinImpuestos>");
        sb.append("<totalDescuento>").append(i.getTotalDescuento()).append("</totalDescuento>");
        sb.append("<totalConImpuestos>");
        for (TotalImpuesto ti : i.getTotalConImpuestosList()) {
            sb.append("<totalImpuesto>");
            sb.append("<codigo>").append(ti.getCodigo()).append("</codigo>");
            sb.append("<codigoPorcentaje>").append(ti.getCodigoPorcentaje()).append("</codigoPorcentaje>");
            sb.append("<baseImponible>").append(ti.getBaseImponible()).append("</baseImponible>");
            sb.append("<valor>").append(ti.getValor()).append("</valor>");
            sb.append("</totalImpuesto>");
        }
        sb.append("</totalConImpuestos>");
        sb.append("<propina>0.00</propina>");
        sb.append("<importeTotal>").append(i.getImporteTotal()).append("</importeTotal>");
        sb.append("<moneda>DOLAR</moneda>");
        sb.append("<pagos>");
        for (PagoSRI p : i.getPagosList()) {
            sb.append("<pago><formaPago>").append(p.getFormaPago()).append("</formaPago>");
            sb.append("<total>").append(p.getTotal()).append("</total></pago>");
        }
        sb.append("</pagos>");
        sb.append("</infoFactura>");
        sb.append("<detalles>");
        for (Detalle d : new ArrayList<>(f.getDetallesList())) {
            sb.append("<detalle>");
            sb.append("<codigoPrincipal>").append(d.getCodigoPrincipal()).append("</codigoPrincipal>");
            sb.append("<descripcion>").append(d.getDescripcion()).append("</descripcion>");
            sb.append("<cantidad>").append(d.getCantidad()).append("</cantidad>");
            sb.append("<precioUnitario>").append(d.getPrecioUnitario()).append("</precioUnitario>");
            sb.append("<descuento>").append(d.getDescuento()).append("</descuento>");
            sb.append("<precioTotalSinImpuesto>").append(d.getPrecioTotalSinImpuesto()).append("</precioTotalSinImpuesto>");
            sb.append("<impuestos>");
            for (Impuesto imp : d.getImpuestosList()) {
                sb.append("<impuesto>");
                sb.append("<codigo>").append(imp.getCodigo()).append("</codigo>");
                sb.append("<codigoPorcentaje>").append(imp.getCodigoPorcentaje()).append("</codigoPorcentaje>");
                sb.append("<tarifa>").append(imp.getTarifa()).append("</tarifa>");
                sb.append("<baseImponible>").append(imp.getBaseImponible()).append("</baseImponible>");
                sb.append("<valor>").append(imp.getValor()).append("</valor>");
                sb.append("</impuesto>");
            }
            sb.append("</impuestos></detalle>");
        }
        sb.append("</detalles>");
        if (!f.getInfoAdicionalList().isEmpty()) {
            sb.append("<infoAdicional>");
            for (CampoAdicional c : f.getInfoAdicionalList()) {
                sb.append("<campoAdicional nombre=\"").append(c.getNombre()).append("\">")
                        .append(c.getValor()).append("</campoAdicional>");
            }
            sb.append("</infoAdicional>");
        }
        sb.append("</factura>");
        return sb.toString();
    }

    // =========================================================================
    // Helpers - Retención
    // =========================================================================

    private ComprobanteRetencion retencionValida() {
        String clave = claveAccesoGenerator.generarClaveAcceso(
                LocalDate.of(2025, 1, 15), "07", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");

        ComprobanteRetencion ret = new ComprobanteRetencion();
        ComprobanteRetencion.InfoTributaria trib = new ComprobanteRetencion.InfoTributaria();
        trib.setAmbiente("1");
        trib.setTipoEmision("1");
        trib.setRazonSocial("EDUCA FACIL SA");
        trib.setNombreComercial("EDUCA FACIL");
        trib.setRuc("1790012345001");
        trib.setClaveAcceso(clave);
        trib.setCodDoc("07");
        trib.setEstab("001");
        trib.setPtoEmi("001");
        trib.setSecuencial("000000001");
        trib.setDirMatriz("Quito");
        ret.setInfoTributaria(trib);

        ComprobanteRetencion.InfoCompRetencion info = new ComprobanteRetencion.InfoCompRetencion();
        info.setFechaEmision("15/01/2025");
        info.setDirEstablecimiento("Quito");
        info.setObligadoContabilidad("SI");
        info.setTipoIdentificacionSujetoRetenido("04");
        info.setRazonSocialSujetoRetenido("PROVEEDOR SA");
        info.setIdentificacionSujetoRetenido("1790998877001");
        info.setPeriodoFiscal("01/2025");
        ret.setInfoCompRetencion(info);

        ComprobanteRetencion.Impuesto imp = new ComprobanteRetencion.Impuesto();
        imp.setCodigo("1");
        imp.setCodigoRetencion("303");
        imp.setBaseImponible(new BigDecimal("100.00"));
        imp.setPorcentajeRetener(new BigDecimal("1.00"));
        imp.setValorRetenido(new BigDecimal("1.00"));
        imp.setCodDocSustento("01");
        imp.setNumDocSustento("001001000000001");
        imp.setFechaEmisionDocSustento("10/01/2025");
        ret.getImpuestosList().add(imp);

        ComprobanteRetencion.CampoAdicional ca = new ComprobanteRetencion.CampoAdicional();
        ca.setNombre("Email");
        ca.setValor("proveedor@test.com");
        ret.getInfoAdicionalList().add(ca);

        return ret;
    }

    private String xmlRetencion(ComprobanteRetencion ret) {
        ComprobanteRetencion.InfoTributaria t = ret.getInfoTributaria();
        ComprobanteRetencion.InfoCompRetencion i = ret.getInfoCompRetencion();
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<comprobanteRetencion id=\"comprobante\" version=\"1.0.0\">");
        sb.append("<infoTributaria>");
        sb.append("<ambiente>").append(t.getAmbiente()).append("</ambiente>");
        sb.append("<tipoEmision>").append(t.getTipoEmision()).append("</tipoEmision>");
        sb.append("<razonSocial>").append(t.getRazonSocial()).append("</razonSocial>");
        sb.append("<nombreComercial>").append(t.getNombreComercial()).append("</nombreComercial>");
        sb.append("<ruc>").append(t.getRuc()).append("</ruc>");
        sb.append("<claveAcceso>").append(t.getClaveAcceso()).append("</claveAcceso>");
        sb.append("<codDoc>").append(t.getCodDoc()).append("</codDoc>");
        sb.append("<estab>").append(t.getEstab()).append("</estab>");
        sb.append("<ptoEmi>").append(t.getPtoEmi()).append("</ptoEmi>");
        sb.append("<secuencial>").append(t.getSecuencial()).append("</secuencial>");
        sb.append("<dirMatriz>").append(t.getDirMatriz()).append("</dirMatriz>");
        sb.append("</infoTributaria>");
        sb.append("<infoCompRetencion>");
        sb.append("<fechaEmision>").append(i.getFechaEmision()).append("</fechaEmision>");
        if (i.getDirEstablecimiento() != null) {
            sb.append("<dirEstablecimiento>").append(i.getDirEstablecimiento()).append("</dirEstablecimiento>");
        }
        if (i.getContribuyenteEspecial() != null) {
            sb.append("<contribuyenteEspecial>").append(i.getContribuyenteEspecial()).append("</contribuyenteEspecial>");
        }
        if (i.getObligadoContabilidad() != null) {
            sb.append("<obligadoContabilidad>").append(i.getObligadoContabilidad()).append("</obligadoContabilidad>");
        }
        sb.append("<tipoIdentificacionSujetoRetenido>").append(i.getTipoIdentificacionSujetoRetenido()).append("</tipoIdentificacionSujetoRetenido>");
        sb.append("<razonSocialSujetoRetenido>").append(i.getRazonSocialSujetoRetenido()).append("</razonSocialSujetoRetenido>");
        sb.append("<identificacionSujetoRetenido>").append(i.getIdentificacionSujetoRetenido()).append("</identificacionSujetoRetenido>");
        sb.append("<periodoFiscal>").append(i.getPeriodoFiscal()).append("</periodoFiscal>");
        sb.append("</infoCompRetencion>");
        sb.append("<impuestos>");
        for (ComprobanteRetencion.Impuesto imp : ret.getImpuestosList()) {
            sb.append("<impuesto>");
            sb.append("<codigo>").append(imp.getCodigo()).append("</codigo>");
            sb.append("<codigoRetencion>").append(imp.getCodigoRetencion()).append("</codigoRetencion>");
            sb.append("<baseImponible>").append(imp.getBaseImponible()).append("</baseImponible>");
            sb.append("<porcentajeRetener>").append(imp.getPorcentajeRetener()).append("</porcentajeRetener>");
            sb.append("<valorRetenido>").append(imp.getValorRetenido()).append("</valorRetenido>");
            if (imp.getCodDocSustento() != null) {
                sb.append("<codDocSustento>").append(imp.getCodDocSustento()).append("</codDocSustento>");
            }
            if (imp.getNumDocSustento() != null) {
                sb.append("<numDocSustento>").append(imp.getNumDocSustento()).append("</numDocSustento>");
            }
            if (imp.getFechaEmisionDocSustento() != null) {
                sb.append("<fechaEmisionDocSustento>").append(imp.getFechaEmisionDocSustento()).append("</fechaEmisionDocSustento>");
            }
            sb.append("</impuesto>");
        }
        sb.append("</impuestos>");
        if (!ret.getInfoAdicionalList().isEmpty()) {
            sb.append("<infoAdicional>");
            for (ComprobanteRetencion.CampoAdicional c : ret.getInfoAdicionalList()) {
                sb.append("<campoAdicional nombre=\"").append(c.getNombre()).append("\">")
                        .append(c.getValor()).append("</campoAdicional>");
            }
            sb.append("</infoAdicional>");
        }
        sb.append("</comprobanteRetencion>");
        return sb.toString();
    }

    // =========================================================================
    // Helpers - Nota Crédito
    // =========================================================================

    private NotaCredito notaCreditoValida() {
        String clave = claveAccesoGenerator.generarClaveAcceso(
                LocalDate.of(2025, 1, 15), "04", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");

        NotaCredito nc = new NotaCredito();
        NotaCredito.InfoTributaria trib = new NotaCredito.InfoTributaria();
        trib.setAmbiente("1");
        trib.setTipoEmision("1");
        trib.setRazonSocial("EDUCA FACIL SA");
        trib.setNombreComercial("EDUCA FACIL");
        trib.setRuc("1790012345001");
        trib.setClaveAcceso(clave);
        trib.setCodDoc("04");
        trib.setEstab("001");
        trib.setPtoEmi("001");
        trib.setSecuencial("000000001");
        trib.setDirMatriz("Quito");
        nc.setInfoTributaria(trib);

        NotaCredito.InfoNotaCredito info = new NotaCredito.InfoNotaCredito();
        info.setFechaEmision("15/01/2025");
        info.setDirEstablecimiento("Quito");
        info.setTipoIdentificacionComprador("05");
        info.setRazonSocialComprador("JUAN PEREZ");
        info.setIdentificacionComprador("1710034065");
        info.setObligadoContabilidad("SI");
        info.setCodDocModificado("01");
        info.setNumDocModificado("001-001-000000001");
        info.setFechaEmisionDocSustento("10/01/2025");
        info.setTotalSinImpuestos(new BigDecimal("50.00"));
        info.setValorModificacion(new BigDecimal("57.50"));
        info.setMoneda("DOLAR");
        info.setMotivo("Devolucion parcial");

        NotaCredito.TotalImpuesto ti = new NotaCredito.TotalImpuesto();
        ti.setCodigo("2");
        ti.setCodigoPorcentaje("4");
        ti.setBaseImponible(new BigDecimal("50.00"));
        ti.setValor(new BigDecimal("7.50"));
        info.getTotalConImpuestosList().add(ti);
        nc.setInfoNotaCredito(info);

        NotaCredito.Detalle det = new NotaCredito.Detalle();
        det.setCodigoInterno("SERV");
        det.setDescripcion("Matricula devuelta");
        det.setCantidad(new BigDecimal("1.00"));
        det.setPrecioUnitario(new BigDecimal("50.00"));
        det.setDescuento(new BigDecimal("0.00"));
        det.setPrecioTotalSinImpuesto(new BigDecimal("50.00"));

        NotaCredito.Impuesto impNc = new NotaCredito.Impuesto();
        impNc.setCodigo("2");
        impNc.setCodigoPorcentaje("4");
        impNc.setTarifa("15.00");
        impNc.setBaseImponible(new BigDecimal("50.00"));
        impNc.setValor(new BigDecimal("7.50"));
        det.getImpuestosList().add(impNc);
        nc.getDetallesList().add(det);

        return nc;
    }

    private String xmlNotaCredito(NotaCredito nc) {
        NotaCredito.InfoTributaria t = nc.getInfoTributaria();
        NotaCredito.InfoNotaCredito i = nc.getInfoNotaCredito();
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<notaCredito id=\"comprobante\" version=\"1.1.0\">");
        sb.append("<infoTributaria>");
        sb.append("<ambiente>").append(t.getAmbiente()).append("</ambiente>");
        sb.append("<tipoEmision>").append(t.getTipoEmision()).append("</tipoEmision>");
        sb.append("<razonSocial>").append(t.getRazonSocial()).append("</razonSocial>");
        sb.append("<nombreComercial>").append(t.getNombreComercial()).append("</nombreComercial>");
        sb.append("<ruc>").append(t.getRuc()).append("</ruc>");
        sb.append("<claveAcceso>").append(t.getClaveAcceso()).append("</claveAcceso>");
        sb.append("<codDoc>").append(t.getCodDoc()).append("</codDoc>");
        sb.append("<estab>").append(t.getEstab()).append("</estab>");
        sb.append("<ptoEmi>").append(t.getPtoEmi()).append("</ptoEmi>");
        sb.append("<secuencial>").append(t.getSecuencial()).append("</secuencial>");
        sb.append("<dirMatriz>").append(t.getDirMatriz()).append("</dirMatriz>");
        sb.append("</infoTributaria>");
        sb.append("<infoNotaCredito>");
        sb.append("<fechaEmision>").append(i.getFechaEmision()).append("</fechaEmision>");
        sb.append("<dirEstablecimiento>").append(i.getDirEstablecimiento()).append("</dirEstablecimiento>");
        sb.append("<tipoIdentificacionComprador>").append(i.getTipoIdentificacionComprador()).append("</tipoIdentificacionComprador>");
        sb.append("<razonSocialComprador>").append(i.getRazonSocialComprador()).append("</razonSocialComprador>");
        sb.append("<identificacionComprador>").append(i.getIdentificacionComprador()).append("</identificacionComprador>");
        sb.append("<obligadoContabilidad>").append(i.getObligadoContabilidad()).append("</obligadoContabilidad>");
        sb.append("<codDocModificado>").append(i.getCodDocModificado()).append("</codDocModificado>");
        sb.append("<numDocModificado>").append(i.getNumDocModificado()).append("</numDocModificado>");
        sb.append("<fechaEmisionDocSustento>").append(i.getFechaEmisionDocSustento()).append("</fechaEmisionDocSustento>");
        sb.append("<totalSinImpuestos>").append(i.getTotalSinImpuestos()).append("</totalSinImpuestos>");
        sb.append("<valorModificacion>").append(i.getValorModificacion()).append("</valorModificacion>");
        sb.append("<moneda>").append(i.getMoneda()).append("</moneda>");
        sb.append("<totalConImpuestos>");
        for (NotaCredito.TotalImpuesto ti : i.getTotalConImpuestosList()) {
            sb.append("<totalImpuesto>");
            sb.append("<codigo>").append(ti.getCodigo()).append("</codigo>");
            sb.append("<codigoPorcentaje>").append(ti.getCodigoPorcentaje()).append("</codigoPorcentaje>");
            sb.append("<baseImponible>").append(ti.getBaseImponible()).append("</baseImponible>");
            sb.append("<valor>").append(ti.getValor()).append("</valor>");
            sb.append("</totalImpuesto>");
        }
        sb.append("</totalConImpuestos>");
        sb.append("<motivo>").append(i.getMotivo()).append("</motivo>");
        sb.append("</infoNotaCredito>");
        sb.append("<detalles>");
        for (NotaCredito.Detalle d : nc.getDetallesList()) {
            sb.append("<detalle>");
            sb.append("<codigoInterno>").append(d.getCodigoInterno()).append("</codigoInterno>");
            sb.append("<descripcion>").append(d.getDescripcion()).append("</descripcion>");
            sb.append("<cantidad>").append(d.getCantidad()).append("</cantidad>");
            sb.append("<precioUnitario>").append(d.getPrecioUnitario()).append("</precioUnitario>");
            sb.append("<descuento>").append(d.getDescuento()).append("</descuento>");
            sb.append("<precioTotalSinImpuesto>").append(d.getPrecioTotalSinImpuesto()).append("</precioTotalSinImpuesto>");
            sb.append("<impuestos>");
            for (NotaCredito.Impuesto imp : d.getImpuestosList()) {
                sb.append("<impuesto>");
                sb.append("<codigo>").append(imp.getCodigo()).append("</codigo>");
                sb.append("<codigoPorcentaje>").append(imp.getCodigoPorcentaje()).append("</codigoPorcentaje>");
                sb.append("<tarifa>").append(imp.getTarifa()).append("</tarifa>");
                sb.append("<baseImponible>").append(imp.getBaseImponible()).append("</baseImponible>");
                sb.append("<valor>").append(imp.getValor()).append("</valor>");
                sb.append("</impuesto>");
            }
            sb.append("</impuestos></detalle>");
        }
        sb.append("</detalles>");
        sb.append("</notaCredito>");
        return sb.toString();
    }
}
