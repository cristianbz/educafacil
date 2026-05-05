package ec.mileniumtech.educafacil.modelo.sri;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Modelo JAXB de la Factura Electrónica SRI.
 * Se utiliza la versión 1.1.0, estándar para facturación electrónica en Ecuador.
 */
@XmlRootElement(name = "factura")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "infoTributaria", "infoFactura", "detalles", "infoAdicional" })
@Getter
@Setter
public class Factura {

    @XmlAttribute(name = "id")
    private String id = "comprobante";

    @XmlAttribute(name = "version")
    private String version = "1.1.0";

    @XmlElement(name = "infoTributaria")
    private InfoTributaria infoTributaria;

    @XmlElement(name = "infoFactura")
    private InfoFactura infoFactura;

    @XmlElement(name = "detalles")
    private DetallesWrapper detalles = new DetallesWrapper();

    @XmlElement(name = "infoAdicional")
    private InfoAdicionalWrapper infoAdicional = new InfoAdicionalWrapper();

    /** Acceso conveniente a la lista de detalles */
    public List<Detalle> getDetallesList() {
        return detalles.getDetalle();
    }

    /** Acceso conveniente a la lista de información adicional */
    public List<CampoAdicional> getInfoAdicionalList() {
        return infoAdicional.getCampoAdicional();
    }

    // =========================================================================
    // Wrappers raíz
    // =========================================================================

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DetallesWrapper {
        @XmlElement(name = "detalle")
        private List<Detalle> detalle = new ArrayList<>();
        public List<Detalle> getDetalle() { return detalle; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InfoAdicionalWrapper {
        @XmlElement(name = "campoAdicional")
        private List<CampoAdicional> campoAdicional = new ArrayList<>();
        public List<CampoAdicional> getCampoAdicional() { return campoAdicional; }
    }

    // =========================================================================
    // InfoTributaria
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "ambiente", "tipoEmision", "razonSocial", "nombreComercial",
        "ruc", "claveAcceso", "codDoc", "estab", "ptoEmi",
        "secuencial", "dirMatriz"
    })
    public static class InfoTributaria {
        private String ambiente;
        private String tipoEmision;
        private String razonSocial;
        private String nombreComercial;
        private String ruc;
        private String claveAcceso;
        private String codDoc;
        private String estab;
        private String ptoEmi;
        private String secuencial;
        private String dirMatriz;
    }

    // =========================================================================
    // InfoFactura
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "fechaEmision", "dirEstablecimiento", "contribuyenteEspecial",
        "obligadoContabilidad", "tipoIdentificacionComprador",
        "razonSocialComprador", "identificacionComprador",
        "totalSinImpuestos", "totalDescuento",
        "totalConImpuestos", "propina", "importeTotal", "moneda", "pagos"
    })
    public static class InfoFactura {
        private String fechaEmision;
        private String dirEstablecimiento;
        private String contribuyenteEspecial;
        private String obligadoContabilidad;
        private String tipoIdentificacionComprador;
        private String razonSocialComprador;
        private String identificacionComprador;
        private BigDecimal totalSinImpuestos;
        private BigDecimal totalDescuento;

        @XmlElement(name = "totalConImpuestos")
        private TotalConImpuestosWrapper totalConImpuestos = new TotalConImpuestosWrapper();

        private BigDecimal propina = BigDecimal.ZERO.setScale(2);
        private BigDecimal importeTotal;
        private String moneda = "DOLAR";

        @XmlElement(name = "pagos")
        private PagosWrapper pagos = new PagosWrapper();

        public List<TotalImpuesto> getTotalConImpuestosList() {
            return totalConImpuestos.getTotalImpuesto();
        }

        public List<PagoSRI> getPagosList() {
            return pagos.getPago();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalConImpuestosWrapper {
        @XmlElement(name = "totalImpuesto")
        private List<TotalImpuesto> totalImpuesto = new ArrayList<>();
        public List<TotalImpuesto> getTotalImpuesto() { return totalImpuesto; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PagosWrapper {
        @XmlElement(name = "pago")
        private List<PagoSRI> pago = new ArrayList<>();
        public List<PagoSRI> getPago() { return pago; }
    }

    // =========================================================================
    // Detalle
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "codigoPrincipal", "codigoAuxiliar", "descripcion", "cantidad",
        "precioUnitario", "descuento", "precioTotalSinImpuesto", "impuestos"
    })
    public static class Detalle {
        private String codigoPrincipal;
        private String codigoAuxiliar;
        private String descripcion;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal precioTotalSinImpuesto;

        @XmlElement(name = "impuestos")
        private ImpuestosWrapper impuestos = new ImpuestosWrapper();

        public List<Impuesto> getImpuestosList() { return impuestos.getImpuesto(); }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ImpuestosWrapper {
        @XmlElement(name = "impuesto")
        private List<Impuesto> impuesto = new ArrayList<>();
        public List<Impuesto> getImpuesto() { return impuesto; }
    }

    // =========================================================================
    // Tipos Simples
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "codigo", "codigoPorcentaje", "baseImponible", "valor" })
    public static class TotalImpuesto {
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "codigo", "codigoPorcentaje", "tarifa", "baseImponible", "valor" })
    public static class Impuesto {
        private String codigo;
        private String codigoPorcentaje;
        private String tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "formaPago", "total" })
    public static class PagoSRI {
        private String formaPago;
        private BigDecimal total;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CampoAdicional {
        @XmlAttribute(name = "nombre")
        private String nombre;
        @jakarta.xml.bind.annotation.XmlValue
        private String valor;
    }
}
