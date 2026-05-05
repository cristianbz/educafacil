
package ec.mileniumtech.educafacil.service.sri.autorizacion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for autorizacionComprobanteLoteResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="autorizacionComprobanteLoteResponse">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="RespuestaAutorizacionLote" type="{http://ec.gob.sri.ws.autorizacion}respuestaLote" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizacionComprobanteLoteResponse", propOrder = {
    "respuestaAutorizacionLote"
})
public class AutorizacionComprobanteLoteResponse {

    @XmlElement(name = "RespuestaAutorizacionLote")
    protected RespuestaLote respuestaAutorizacionLote;

    /**
     * Gets the value of the respuestaAutorizacionLote property.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaLote }
     *     
     */
    public RespuestaLote getRespuestaAutorizacionLote() {
        return respuestaAutorizacionLote;
    }

    /**
     * Sets the value of the respuestaAutorizacionLote property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaLote }
     *     
     */
    public void setRespuestaAutorizacionLote(RespuestaLote value) {
        this.respuestaAutorizacionLote = value;
    }

}
