package ec.mileniumtech.educafacil.service.strategy;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;

public interface DocumentoElectronicoStrategy {

    String getCodigoDocumento();

    Object construirJaxb(Object entidad, EmpresaMatriz empresa, SriProcessingContext context) throws Exception;

    String generarXml(Object jaxbObject) throws Exception;

    byte[] generarRide(Object jaxbObject, EmpresaMatriz empresa, SriProcessingContext context) throws Exception;

    String getRutaReporteJrxml();

    void actualizarEntidad(Object entidad, SriProcessingContext context);

    void persistir(Object entidad);

    String getEntityIdentifier(Object entidad);
}
