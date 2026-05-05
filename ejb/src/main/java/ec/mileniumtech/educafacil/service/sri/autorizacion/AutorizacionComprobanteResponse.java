
package ec.mileniumtech.educafacil.service.sri.autorizacion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for autorizacionComprobanteResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="autorizacionComprobanteResponse">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="RespuestaAutorizacionComprobante" type="{http://ec.gob.sri.ws.autorizacion}respuestaComprobante" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizacionComprobanteResponse", propOrder = {
    "respuestaAutorizacionComprobante"
})
public class AutorizacionComprobanteResponse {

    @XmlElement(name = "RespuestaAutorizacionComprobante")
    protected RespuestaComprobante respuestaAutorizacionComprobante;

    /**
     * Gets the value of the respuestaAutorizacionComprobante property.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaComprobante }
     *     
     */
    public RespuestaComprobante getRespuestaAutorizacionComprobante() {
        return respuestaAutorizacionComprobante;
    }

    /**
     * Sets the value of the respuestaAutorizacionComprobante property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaComprobante }
     *     
     */
    public void setRespuestaAutorizacionComprobante(RespuestaComprobante value) {
        this.respuestaAutorizacionComprobante = value;
    }

}
