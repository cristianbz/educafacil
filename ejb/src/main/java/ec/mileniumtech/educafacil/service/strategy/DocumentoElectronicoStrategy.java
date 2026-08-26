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

    /**
     * Persiste el progreso del proceso SRI (clave de acceso y XML firmado)
     * tan pronto como estén disponibles, ANTES de que la autorización se resuelva.
     * <p>
     * Esto garantiza que, si el servicio del SRI está lento o cae durante la
     * consulta de autorización, el comprobante quede persistido en BD con su
     * clave de acceso y XML firmado, permitiendo que un proceso de reconciliación
     * posterior consulte la autorización sin necesidad de re-firmar ni re-enviar.
     *
     * @param entidad entidad de negocio (Factura, NotaCredito, Retencion)
     * @param context contexto de procesamiento SRI con claveAcceso y xmlFirmado
     */
    default void persistirProgreso(Object entidad, SriProcessingContext context) {
        // No-op por defecto; las estrategias que tengan campos de progreso lo implementan.
    }

    /**
     * Persiste la entidad en estado {@code EN_PROCESO} ANTES de disparar el
     * procesamiento asíncrono contra el SRI (Paso 3 del plan).
     * <p>
     * Permite que la UI muestre "se está procesando" de inmediato y que un
     * proceso de reconciliación posterior identifique los comprobantes cuyo
     * envío no llegó a completarse.
     *
     * @param entidad entidad de negocio (Factura, NotaCredito, Retencion)
     */
    default void marcarEnProceso(Object entidad) {
        // No-op por defecto; las estrategias que tengan estado lo implementan.
    }
}
