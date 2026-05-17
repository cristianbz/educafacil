package ec.mileniumtech.educafacil.utilitario.converters;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
import ec.mileniumtech.educafacil.service.RetencionService;
import jakarta.ejb.EJB;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;

/**
 * Converter CDI para CodigoSriRetencion.
 * Permite que p:autoComplete serialice/deserialice la entidad
 * usando su ID como clave de conversión.
 *
 * Se referencia en el XHTML como: converter="#{codigoSriRetencionConverter}"
 */
@Named("codigoSriRetencionConverter")
@FacesConverter(value = "codigoSriRetencionConverter", managed = true)
public class CodigoSriRetencionConverter implements Converter<CodigoSriRetencion> {

    @EJB
    private RetencionService retencionService;

    /**
     * Convierte el String (ID) de vuelta al objeto CodigoSriRetencion.
     * Busca en el servicio por ID.
     */
    @Override
    public CodigoSriRetencion getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Integer id = Integer.parseInt(value);
            // Buscamos en la lista cargada para evitar un query adicional
            // Si no está en sesión, se hace via findById desde el DAO
            return retencionService.buscarCodigoSriPorId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Convierte el objeto CodigoSriRetencion a String (su ID).
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, CodigoSriRetencion value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}
