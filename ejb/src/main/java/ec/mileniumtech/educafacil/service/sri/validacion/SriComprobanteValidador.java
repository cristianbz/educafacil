package ec.mileniumtech.educafacil.service.sri.validacion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion;
import ec.mileniumtech.educafacil.modelo.sri.Factura;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Validación previa al envío al SRI: reglas de negocio más XSD oficial.
 */
@Stateless
@LocalBean
public class SriComprobanteValidador {

    static final int MAX_MENSAJE = 2000;
    private static final BigDecimal TOLERANCIA = new BigDecimal("0.01");
    private static final Pattern FECHA_SRI = Pattern.compile("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/20[0-9]{2}");
    private static final Pattern NUM_DOC = Pattern.compile("[0-9]{3}-[0-9]{3}-[0-9]{9}");
    private static final Pattern PERIODO_FISCAL = Pattern.compile("(0[1-9]|1[012])/20[0-9]{2}");

    @EJB
    private SriXmlSchemaValidator schemaValidator;

    @EJB
    private ClaveAccesoGenerator claveAccesoGenerator;

    public void validar(String codigoDocumento, Object jaxbObject, String xml) {
        List<String> errores = new ArrayList<>();
        if (xml == null || xml.isBlank()) {
            errores.add("El XML generado está vacío.");
        } else if (!xml.contains("<?xml")) {
            errores.add("El XML no incluye declaración XML.");
        }

        if (jaxbObject instanceof Factura factura) {
            validarFactura(factura, errores);
        } else if (jaxbObject instanceof NotaCredito notaCredito) {
            validarNotaCredito(notaCredito, errores);
        } else if (jaxbObject instanceof ComprobanteRetencion retencion) {
            validarRetencion(retencion, errores);
        } else {
            errores.add("Tipo de comprobante JAXB no soportado.");
        }

        if (!errores.isEmpty()) {
            lanzar(errores);
        }
        schemaValidator.validar(codigoDocumento, xml);
    }

    public void validarXmlFirmado(byte[] xmlFirmado) {
        schemaValidator.validarXmlFirmado(xmlFirmado);
    }

    void validarFactura(Factura factura, List<String> errores) {
        if (factura.getInfoTributaria() == null || factura.getInfoFactura() == null) {
            errores.add("Faltan infoTributaria o infoFactura.");
            return;
        }
        validarInfoTributaria(factura.getInfoTributaria().getClaveAcceso(),
                factura.getInfoTributaria().getRuc(),
                factura.getInfoTributaria().getEstab(),
                factura.getInfoTributaria().getPtoEmi(),
                factura.getInfoTributaria().getSecuencial(),
                factura.getInfoTributaria().getRazonSocial(),
                factura.getInfoTributaria().getDirMatriz(),
                errores);

        Factura.InfoFactura info = factura.getInfoFactura();
        if (!FECHA_SRI.matcher(nvl(info.getFechaEmision())).matches()) {
            errores.add("fechaEmision debe tener formato dd/MM/yyyy.");
        }
        validarIdentificacionComprador(info.getTipoIdentificacionComprador(),
                info.getIdentificacionComprador(), errores);
        if (vacio(info.getRazonSocialComprador())) {
            errores.add("razonSocialComprador es obligatorio.");
        }
        if (factura.getDetallesList() == null || factura.getDetallesList().isEmpty()) {
            errores.add("La factura debe tener al menos un detalle.");
        } else {
            BigDecimal sumaDetalles = BigDecimal.ZERO;
            BigDecimal sumaIvaDetalles = BigDecimal.ZERO;
            for (Factura.Detalle det : factura.getDetallesList()) {
                if (vacio(det.getDescripcion())) {
                    errores.add("Hay un detalle sin descripción.");
                }
                if (contieneSalto(det.getDescripcion())) {
                    errores.add("La descripción del detalle no puede contener saltos de línea.");
                }
                if (det.getPrecioTotalSinImpuesto() != null) {
                    sumaDetalles = sumaDetalles.add(det.getPrecioTotalSinImpuesto());
                }
                if (det.getImpuestosList() == null || det.getImpuestosList().isEmpty()) {
                    errores.add("El detalle '" + nvl(det.getDescripcion()) + "' no tiene impuestos.");
                } else {
                    for (Factura.Impuesto imp : det.getImpuestosList()) {
                        if (imp.getValor() != null) {
                            sumaIvaDetalles = sumaIvaDetalles.add(imp.getValor());
                        }
                    }
                }
            }
            comparar(sumaDetalles, info.getTotalSinImpuestos(), "totalSinImpuestos vs suma de detalles", errores);
            if (info.getTotalConImpuestosList() != null && !info.getTotalConImpuestosList().isEmpty()) {
                BigDecimal ivaTotal = info.getTotalConImpuestosList().stream()
                        .map(Factura.TotalImpuesto::getValor)
                        .filter(v -> v != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                comparar(sumaIvaDetalles, ivaTotal, "IVA de detalles vs totalConImpuestos", errores);
            }
        }
        if (info.getPagosList() == null || info.getPagosList().isEmpty()) {
            errores.add("La factura debe tener al menos una forma de pago.");
        } else {
            BigDecimal sumaPagos = info.getPagosList().stream()
                    .map(Factura.PagoSRI::getTotal)
                    .filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            comparar(sumaPagos, info.getImporteTotal(), "suma de pagos vs importeTotal", errores);
        }
        validarInfoAdicional(factura.getInfoAdicionalList(), errores);
    }

    void validarNotaCredito(NotaCredito nc, List<String> errores) {
        if (nc.getInfoTributaria() == null || nc.getInfoNotaCredito() == null) {
            errores.add("Faltan infoTributaria o infoNotaCredito.");
            return;
        }
        validarInfoTributaria(nc.getInfoTributaria().getClaveAcceso(),
                nc.getInfoTributaria().getRuc(),
                nc.getInfoTributaria().getEstab(),
                nc.getInfoTributaria().getPtoEmi(),
                nc.getInfoTributaria().getSecuencial(),
                nc.getInfoTributaria().getRazonSocial(),
                nc.getInfoTributaria().getDirMatriz(),
                errores);
        NotaCredito.InfoNotaCredito info = nc.getInfoNotaCredito();
        if (!FECHA_SRI.matcher(nvl(info.getFechaEmision())).matches()) {
            errores.add("fechaEmision debe tener formato dd/MM/yyyy.");
        }
        validarIdentificacionComprador(info.getTipoIdentificacionComprador(),
                info.getIdentificacionComprador(), errores);
        if (vacio(info.getMotivo())) {
            errores.add("El motivo de la nota de crédito es obligatorio.");
        }
        if (!NUM_DOC.matcher(nvl(info.getNumDocModificado())).matches()) {
            errores.add("numDocModificado debe tener el formato 000-000-000000000.");
        }
        if (nc.getDetallesList() == null || nc.getDetallesList().isEmpty()) {
            errores.add("La nota de crédito debe tener al menos un detalle.");
        }
        validarInfoAdicional(nc.getInfoAdicionalList(), errores);
    }

    void validarRetencion(ComprobanteRetencion ret, List<String> errores) {
        if (ret.getInfoTributaria() == null || ret.getInfoCompRetencion() == null) {
            errores.add("Faltan infoTributaria o infoCompRetencion.");
            return;
        }
        validarInfoTributaria(ret.getInfoTributaria().getClaveAcceso(),
                ret.getInfoTributaria().getRuc(),
                ret.getInfoTributaria().getEstab(),
                ret.getInfoTributaria().getPtoEmi(),
                ret.getInfoTributaria().getSecuencial(),
                ret.getInfoTributaria().getRazonSocial(),
                ret.getInfoTributaria().getDirMatriz(),
                errores);
        if (!FECHA_SRI.matcher(nvl(ret.getInfoCompRetencion().getFechaEmision())).matches()) {
            errores.add("fechaEmision debe tener formato dd/MM/yyyy.");
        }
        if (!PERIODO_FISCAL.matcher(nvl(ret.getInfoCompRetencion().getPeriodoFiscal())).matches()) {
            errores.add("periodoFiscal debe tener formato MM/yyyy.");
        }
        if (vacio(ret.getInfoCompRetencion().getIdentificacionSujetoRetenido())) {
            errores.add("identificacionSujetoRetenido es obligatorio.");
        }
        if (ret.getImpuestosList() == null || ret.getImpuestosList().isEmpty()) {
            errores.add("La retención debe tener al menos un impuesto.");
        }
        validarInfoAdicional(ret.getInfoAdicionalList(), errores);
    }

    private void validarInfoTributaria(String clave, String ruc, String estab, String ptoEmi,
                                       String secuencial, String razonSocial, String dirMatriz,
                                       List<String> errores) {
        if (!claveAccesoGenerator.esClaveAccesoValida(clave)) {
            errores.add("La clave de acceso no es válida (49 dígitos y dígito verificador módulo 11).");
        }
        if (ruc == null || !ruc.matches("[0-9]{10}001")) {
            errores.add("El RUC del emisor debe tener 13 dígitos y terminar en 001.");
        }
        if (estab == null || !estab.matches("\\d{3}")) {
            errores.add("estab debe tener 3 dígitos.");
        }
        if (ptoEmi == null || !ptoEmi.matches("\\d{3}")) {
            errores.add("ptoEmi debe tener 3 dígitos.");
        }
        if (secuencial == null || !secuencial.matches("\\d{9}")) {
            errores.add("secuencial debe tener 9 dígitos.");
        }
        if (vacio(razonSocial)) {
            errores.add("razonSocial del emisor es obligatoria.");
        }
        if (vacio(dirMatriz)) {
            errores.add("dirMatriz es obligatoria.");
        }
        if (contieneSalto(razonSocial) || contieneSalto(dirMatriz)) {
            errores.add("razonSocial y dirMatriz no pueden contener saltos de línea.");
        }
    }

    private void validarIdentificacionComprador(String tipo, String identificacion, List<String> errores) {
        if (vacio(tipo) || !tipo.matches("0[4-8]")) {
            errores.add("tipoIdentificacionComprador inválido.");
        }
        if (vacio(identificacion)) {
            errores.add("identificacionComprador es obligatorio.");
            return;
        }
        if ("04".equals(tipo) && !identificacion.matches("[0-9]{13}")) {
            errores.add("Para RUC (04) la identificación debe tener 13 dígitos.");
        }
        if ("05".equals(tipo) && !identificacion.matches("[0-9]{10}")) {
            errores.add("Para cédula (05) la identificación debe tener 10 dígitos.");
        }
        if ("07".equals(tipo) && !"9999999999999".equals(identificacion)) {
            errores.add("Consumidor final (07) debe usar identificación 9999999999999.");
        }
        if (identificacion.length() > 13) {
            errores.add("identificacionComprador no puede superar 13 caracteres.");
        }
    }

    private void validarInfoAdicional(List<?> campos, List<String> errores) {
        if (campos == null || campos.isEmpty()) {
            return;
        }
        if (campos.size() > 15) {
            errores.add("infoAdicional admite como máximo 15 campos.");
        }
        for (Object campo : campos) {
            String nombre;
            String valor;
            if (campo instanceof Factura.CampoAdicional c) {
                nombre = c.getNombre();
                valor = c.getValor();
            } else if (campo instanceof NotaCredito.CampoAdicional c) {
                nombre = c.getNombre();
                valor = c.getValor();
            } else if (campo instanceof ComprobanteRetencion.CampoAdicional c) {
                nombre = c.getNombre();
                valor = c.getValor();
            } else {
                continue;
            }
            if (vacio(nombre) || vacio(valor)) {
                errores.add("Los campos adicionales deben tener nombre y valor.");
            }
            if (nombre != null && nombre.length() > 300) {
                errores.add("El nombre de un campo adicional supera 300 caracteres.");
            }
            if (valor != null && valor.length() > 300) {
                errores.add("El valor de un campo adicional supera 300 caracteres.");
            }
            if (contieneSalto(nombre) || contieneSalto(valor)) {
                errores.add("Los campos adicionales no pueden contener saltos de línea.");
            }
        }
    }

    private void comparar(BigDecimal a, BigDecimal b, String etiqueta, List<String> errores) {
        if (a == null || b == null) {
            errores.add(etiqueta + ": hay valores nulos.");
            return;
        }
        if (a.setScale(2, RoundingMode.HALF_UP).subtract(b.setScale(2, RoundingMode.HALF_UP)).abs()
                .compareTo(TOLERANCIA) > 0) {
            errores.add(etiqueta + " no coinciden (" + a + " vs " + b + ").");
        }
    }

    private static void lanzar(List<String> errores) {
        String mensaje = "Documento electrónico inválido: " + String.join(" | ", errores);
        if (mensaje.length() > MAX_MENSAJE) {
            mensaje = mensaje.substring(0, MAX_MENSAJE);
        }
        throw new BusinessException(mensaje, "BIZ-SRI-XML-INVALIDO");
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    private static boolean contieneSalto(String s) {
        return s != null && (s.contains("\n") || s.contains("\r"));
    }

    /** Solo para pruebas unitarias sin contenedor EJB. */
    void setSchemaValidator(SriXmlSchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    void setClaveAccesoGenerator(ClaveAccesoGenerator claveAccesoGenerator) {
        this.claveAccesoGenerator = claveAccesoGenerator;
    }
}
