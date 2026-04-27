package ec.mileniumtech.educafacil.modelo.sri;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "factura")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "infoTributaria", "infoFactura", "detalles", "infoAdicional" })
@Getter
@Setter
public class Factura {

    @XmlAttribute(name = "id")
    private String id = "comprobante";

    @XmlAttribute(name = "version")
    private String version = "2.1.0";

    @XmlElement(name = "infoTributaria")
    private InfoTributaria infoTributaria;

    @XmlElement(name = "infoFactura")
    private InfoFactura infoFactura;

    @XmlElementWrapper(name = "detalles")
    @XmlElement(name = "detalle")
    private List<Detalle> detalles = new ArrayList<>();

    @XmlElementWrapper(name = "infoAdicional")
    @XmlElement(name = "campoAdicional")
    private List<CampoAdicional> infoAdicional = new ArrayList<>();

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
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
        private String agenteRetencion;
        private String contribuyenteRimpe;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InfoFactura {
        private String fechaEmision;
        private String dirEstablecimiento;
        private String obligadoContabilidad;
        private String tipoIdentificacionComprador;
        private String razonSocialComprador;
        private String identificacionComprador;
        private String direccionComprador;
        private double totalSinImpuestos;
        private double totalDescuento;
        private double importeTotal;
        private String moneda = "DOLAR";
        
        @XmlElementWrapper(name = "totalConImpuestos")
        @XmlElement(name = "totalImpuesto")
        private List<TotalImpuesto> totalConImpuestos = new ArrayList<>();
        
        @XmlElementWrapper(name = "pagos")
        @XmlElement(name = "pago")
        private List<PagoSRI> pagos = new ArrayList<>();
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Detalle {
        private String codigoPrincipal;
        private String descripcion;
        private double cantidad;
        private double precioUnitario;
        private double descuento;
        private double precioTotalSinImpuesto;
        
        @XmlElementWrapper(name = "impuestos")
        @XmlElement(name = "impuesto")
        private List<Impuesto> impuestos = new ArrayList<>();
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalImpuesto {
        private String codigo;
        private String codigoPorcentaje;
        private double baseImponible;
        private double valor;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Impuesto {
        private String codigo;
        private String codigoPorcentaje;
        private String tarifa;
        private double baseImponible;
        private double valor;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PagoSRI {
        private String formaPago;
        private double total;
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
