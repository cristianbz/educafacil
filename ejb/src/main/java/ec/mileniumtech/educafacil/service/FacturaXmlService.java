package ec.mileniumtech.educafacil.service;

import java.io.StringWriter;

import ec.mileniumtech.educafacil.modelo.sri.Factura;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

/**
 * Servicio para la generación de archivos XML compatibles con el SRI.
 */
@Stateless
@LocalBean
public class FacturaXmlService {

    /**
     * Convierte un objeto Factura a su representación XML en una cadena de texto.
     * 
     * @param factura Objeto factura con datos llenos.
     * @return String con el XML generado.
     * @throws Exception Si ocurre un error durante el marshalling.
     */
    public String generarXml(Factura factura) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Factura.class);
        Marshaller marshaller = context.createMarshaller();
        
        // El SRI requiere declaración XML con encoding UTF-8.
        // NO usar FORMATTED_OUTPUT=true en producción: el SRI puede rechazar
        // documentos con whitespace inesperado en versiones estrictas del validador.
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        // Asegurar que la declaración <?xml version="1.0" encoding="UTF-8"?> esté presente
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(factura, sw);
        
        return sw.toString();
    }
}
