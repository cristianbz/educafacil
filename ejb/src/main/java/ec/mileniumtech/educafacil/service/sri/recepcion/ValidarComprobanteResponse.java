
package ec.mileniumtech.educafacil.service.sri.recepcion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validarComprobanteResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="validarComprobanteResponse">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="RespuestaRecepcionComprobante" type="{http://ec.gob.sri.ws.recepcion}respuestaSolicitud" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validarComprobanteResponse", propOrder = {
    "respuestaRecepcionComprobante"
})
public class ValidarComprobanteResponse {

    @XmlElement(name = "RespuestaRecepcionComprobante")
    protected RespuestaSolicitud respuestaRecepcionComprobante;

    /**
     * Gets the value of the respuestaRecepcionComprobante property.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaSolicitud }
     *     
     */
    public RespuestaSolicitud getRespuestaRecepcionComprobante() {
        return respuestaRecepcionComprobante;
    }

    /**
     * Sets the value of the respuestaRecepcionComprobante property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaSolicitud }
     *     
     */
    public void setRespuestaRecepcionComprobante(RespuestaSolicitud value) {
        this.respuestaRecepcionComprobante = value;
    }

}
