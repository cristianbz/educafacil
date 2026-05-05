
package ec.mileniumtech.educafacil.service.sri.recepcion;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.mileniumtech.educafacil.service.sri.recepcion package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _RespuestaSolicitud_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "RespuestaSolicitud");
    private static final QName _Comprobante_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "comprobante");
    private static final QName _Mensaje_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "mensaje");
    private static final QName _ValidarComprobante_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "validarComprobante");
    private static final QName _ValidarComprobanteResponse_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "validarComprobanteResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.mileniumtech.educafacil.service.sri.recepcion
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Comprobante }
     * 
     * @return
     *     the new instance of {@link Comprobante }
     */
    public Comprobante createComprobante() {
        return new Comprobante();
    }

    /**
     * Create an instance of {@link RespuestaSolicitud }
     * 
     * @return
     *     the new instance of {@link RespuestaSolicitud }
     */
    public RespuestaSolicitud createRespuestaSolicitud() {
        return new RespuestaSolicitud();
    }

    /**
     * Create an instance of {@link Mensaje }
     * 
     * @return
     *     the new instance of {@link Mensaje }
     */
    public Mensaje createMensaje() {
        return new Mensaje();
    }

    /**
     * Create an instance of {@link ValidarComprobante }
     * 
     * @return
     *     the new instance of {@link ValidarComprobante }
     */
    public ValidarComprobante createValidarComprobante() {
        return new ValidarComprobante();
    }

    /**
     * Create an instance of {@link ValidarComprobanteResponse }
     * 
     * @return
     *     the new instance of {@link ValidarComprobanteResponse }
     */
    public ValidarComprobanteResponse createValidarComprobanteResponse() {
        return new ValidarComprobanteResponse();
    }

    /**
     * Create an instance of {@link Comprobante.Mensajes }
     * 
     * @return
     *     the new instance of {@link Comprobante.Mensajes }
     */
    public Comprobante.Mensajes createComprobanteMensajes() {
        return new Comprobante.Mensajes();
    }

    /**
     * Create an instance of {@link RespuestaSolicitud.Comprobantes }
     * 
     * @return
     *     the new instance of {@link RespuestaSolicitud.Comprobantes }
     */
    public RespuestaSolicitud.Comprobantes createRespuestaSolicitudComprobantes() {
        return new RespuestaSolicitud.Comprobantes();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaSolicitud }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaSolicitud }{@code >}
     */
    @XmlElementDecl(namespace = "http://ec.gob.sri.ws.recepcion", name = "RespuestaSolicitud")
    public JAXBElement<RespuestaSolicitud> createRespuestaSolicitud(RespuestaSolicitud value) {
        return new JAXBElement<>(_RespuestaSolicitud_QNAME, RespuestaSolicitud.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Comprobante }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Comprobante }{@code >}
     */
    @XmlElementDecl(namespace = "http://ec.gob.sri.ws.recepcion", name = "comprobante")
    public JAXBElement<Comprobante> createComprobante(Comprobante value) {
        return new JAXBElement<>(_Comprobante_QNAME, Comprobante.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mensaje }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Mensaje }{@code >}
     */
    @XmlElementDecl(namespace = "http://ec.gob.sri.ws.recepcion", name = "mensaje")
    public JAXBElement<Mensaje> createMensaje(Mensaje value) {
        return new JAXBElement<>(_Mensaje_QNAME, Mensaje.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidarComprobante }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ValidarComprobante }{@code >}
     */
    @XmlElementDecl(namespace = "http://ec.gob.sri.ws.recepcion", name = "validarComprobante")
    public JAXBElement<ValidarComprobante> createValidarComprobante(ValidarComprobante value) {
        return new JAXBElement<>(_ValidarComprobante_QNAME, ValidarComprobante.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidarComprobanteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ValidarComprobanteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ec.gob.sri.ws.recepcion", name = "validarComprobanteResponse")
    public JAXBElement<ValidarComprobanteResponse> createValidarComprobanteResponse(ValidarComprobanteResponse value) {
        return new JAXBElement<>(_ValidarComprobanteResponse_QNAME, ValidarComprobanteResponse.class, null, value);
    }

}
