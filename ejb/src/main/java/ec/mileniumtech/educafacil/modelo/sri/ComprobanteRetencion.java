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
 * Modelo JAXB del Comprobante de Retención SRI.
 * Basado en la ficha técnica del SRI (versión 2.0.0).
 */
@XmlRootElement(name = "comprobanteRetencion")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "infoTributaria", "infoRetencion", "impuestos", "infoAdicional" })
@Getter
@Setter
public class ComprobanteRetencion {

    @XmlAttribute(name = "id")
    private String id = "comprobante";

    @XmlAttribute(name = "version")
    private String version = "1.0.0"; // Usaremos 1.0.0 inicialmente por compatibilidad, o 2.0.0 si se requiere Rimpe

    @XmlElement(name = "infoTributaria")
    private InfoTributaria infoTributaria;

    @XmlElement(name = "infoRetencion")
    private InfoRetencion infoRetencion;

    @XmlElement(name = "impuestos")
    private ImpuestosWrapper impuestos = new ImpuestosWrapper();

    @XmlElement(name = "infoAdicional")
    private InfoAdicionalWrapper infoAdicional = new InfoAdicionalWrapper();

    public List<Impuesto> getImpuestosList() {
        return impuestos.getImpuesto();
    }

    public List<CampoAdicional> getInfoAdicionalList() {
        return infoAdicional.getCampoAdicional();
    }

    // =========================================================================
    // Wrappers
    // =========================================================================

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ImpuestosWrapper {
        @XmlElement(name = "impuesto")
        private List<Impuesto> impuesto = new ArrayList<>();
        public List<Impuesto> getImpuesto() { return impuesto; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InfoAdicionalWrapper {
        @XmlElement(name = "campoAdicional")
        private List<CampoAdicional> campoAdicional = new ArrayList<>();
        public List<CampoAdicional> getCampoAdicional() { return campoAdicional; }
    }

    // =========================================================================
    // InfoTributaria (Mismo que Factura)
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
    // InfoRetencion
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "fechaEmision", "dirEstablecimiento", "contribuyenteEspecial",
        "obligadoContabilidad", "tipoIdentificacionSujetoRetenido",
        "razonSocialSujetoRetenido", "identificacionSujetoRetenido",
        "periodoFiscal"
    })
    public static class InfoRetencion {
        private String fechaEmision;
        private String dirEstablecimiento;
        private String contribuyenteEspecial;
        private String obligadoContabilidad;
        private String tipoIdentificacionSujetoRetenido;
        private String razonSocialSujetoRetenido;
        private String identificacionSujetoRetenido;
        private String periodoFiscal;
    }

    // =========================================================================
    // Impuesto
    // =========================================================================

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "codigo", "codigoRetencion", "baseImponible", "porcentajeRetener",
        "valorRetenido", "codDocSustento", "numDocSustento", "fechaEmisionDocSustento"
    })
    public static class Impuesto {
        private String codigo;
        private String codigoRetencion;
        private BigDecimal baseImponible;
        private BigDecimal porcentajeRetener;
        private BigDecimal valorRetenido;
        private String codDocSustento;
        private String numDocSustento;
        private String fechaEmisionDocSustento;
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
