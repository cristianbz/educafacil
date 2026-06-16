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
 * Modelo JAXB de la Nota de Crédito Electrónica SRI.
 * Se utiliza la versión 1.1.0, estándar para notas de crédito en Ecuador.
 */
@XmlRootElement(name = "notaCredito")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "infoTributaria", "infoNotaCredito", "detalles", "infoAdicional" })
@Getter
@Setter
public class NotaCredito {

    @XmlAttribute(name = "id")
    private String id = "comprobante";

    @XmlAttribute(name = "version")
    private String version = "1.1.0";

    @XmlElement(name = "infoTributaria")
    private InfoTributaria infoTributaria;

    @XmlElement(name = "infoNotaCredito")
    private InfoNotaCredito infoNotaCredito;

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
    // InfoNotaCredito
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador",
        "razonSocialComprador", "identificacionComprador", "contribuyenteEspecial",
        "obligadoContabilidad", "codDocModificado", "numDocModificado", 
        "fechaEmisionDocSustento", "totalSinImpuestos", "valorModificacion", 
        "moneda", "totalConImpuestos", "motivo"
    })
    public static class InfoNotaCredito {
        private String fechaEmision;
        private String dirEstablecimiento;
        private String tipoIdentificacionComprador;
        private String razonSocialComprador;
        private String identificacionComprador;
        private String contribuyenteEspecial;
        private String obligadoContabilidad;
        private String codDocModificado; // "01" para Factura
        private String numDocModificado; // Ej: 001-001-000000123
        private String fechaEmisionDocSustento;
        private BigDecimal totalSinImpuestos;
        private BigDecimal valorModificacion;
        private String moneda = "DOLAR";

        @XmlElement(name = "totalConImpuestos")
        private TotalConImpuestosWrapper totalConImpuestos = new TotalConImpuestosWrapper();

        private String motivo; // Obligatorio en N.C.

        public List<TotalImpuesto> getTotalConImpuestosList() {
            return totalConImpuestos.getTotalImpuesto();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalConImpuestosWrapper {
        @XmlElement(name = "totalImpuesto")
        private List<TotalImpuesto> totalImpuesto = new ArrayList<>();
        public List<TotalImpuesto> getTotalImpuesto() { return totalImpuesto; }
    }

    // =========================================================================
    // Detalle
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "codigoInterno", "codigoAdicional", "descripcion", "cantidad",
        "precioUnitario", "descuento", "precioTotalSinImpuesto", "impuestos"
    })
    public static class Detalle {
        private String codigoInterno;
        private String codigoAdicional;
        private String descripcion;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal precioTotalSinImpuesto;

        @XmlElement(name = "impuestos")
        private ImpuestosWrapper impuestos = new ImpuestosWrapper();

        public List<Impuesto> getImpuestosList() { return impuestos.getImpuesto(); }
        
        /** Alias para compatibilidad con el diseño del reporte Jasper */
        public String getCodigoPrincipal() {
            return this.codigoInterno;
        }
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
    public static class CampoAdicional {
        @XmlAttribute(name = "nombre")
        private String nombre;
        @jakarta.xml.bind.annotation.XmlValue
        private String valor;
    }
}
