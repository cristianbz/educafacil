
package ec.mileniumtech.educafacil.service.sri.recepcion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validarComprobante complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="validarComprobante">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="xml" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validarComprobante", propOrder = {
    "xml"
})
public class ValidarComprobante {

    protected byte[] xml;

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setXml(byte[] value) {
        this.xml = value;
    }

}
