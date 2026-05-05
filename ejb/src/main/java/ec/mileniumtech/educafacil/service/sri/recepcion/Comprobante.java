
package ec.mileniumtech.educafacil.service.sri.recepcion;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for comprobante complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="comprobante">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="claveAcceso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="mensajes" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element ref="{http://ec.gob.sri.ws.recepcion}mensaje" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comprobante", propOrder = {
    "claveAcceso",
    "mensajes"
})
public class Comprobante {

    protected String claveAcceso;
    protected Comprobante.Mensajes mensajes;

    /**
     * Gets the value of the claveAcceso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveAcceso() {
        return claveAcceso;
    }

    /**
     * Sets the value of the claveAcceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveAcceso(String value) {
        this.claveAcceso = value;
    }

    /**
     * Gets the value of the mensajes property.
     * 
     * @return
     *     possible object is
     *     {@link Comprobante.Mensajes }
     *     
     */
    public Comprobante.Mensajes getMensajes() {
        return mensajes;
    }

    /**
     * Sets the value of the mensajes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Comprobante.Mensajes }
     *     
     */
    public void setMensajes(Comprobante.Mensajes value) {
        this.mensajes = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element ref="{http://ec.gob.sri.ws.recepcion}mensaje" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "mensaje"
    })
    public static class Mensajes {

        @XmlElement(namespace = "")
        protected List<Mensaje> mensaje;

        /**
         * Gets the value of the mensaje property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the mensaje property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMensaje().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Mensaje }
         * 
         * 
         * @return
         *     The value of the mensaje property.
         */
        public List<Mensaje> getMensaje() {
            if (mensaje == null) {
                mensaje = new ArrayList<>();
            }
            return this.mensaje;
        }

    }

}
