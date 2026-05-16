package ec.mileniumtech.educafacil.service;

import java.io.StringWriter;

import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

/**
 * Servicio para la generación de archivos XML de Comprobantes de Retención compatibles con el SRI.
 */
@Stateless
@LocalBean
public class RetencionXmlService {

    /**
     * Convierte un objeto ComprobanteRetencion a su representación XML en una cadena de texto.
     * 
     * @param retencion Objeto JAXB con datos llenos.
     * @return String con el XML generado.
     * @throws Exception Si ocurre un error durante el marshalling.
     */
    public String generarXml(ComprobanteRetencion retencion) throws Exception {
        JAXBContext context = JAXBContext.newInstance(ComprobanteRetencion.class);
        Marshaller marshaller = context.createMarshaller();
        
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(retencion, sw);
        
        return sw.toString();
    }
}
