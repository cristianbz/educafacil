package ec.mileniumtech.educafacil.api.exception;

/**
 * Excepción para recursos no encontrados (HTTP 404).
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String resource, Object id) {
        super(404, "NOT_FOUND",
                "Recurso no encontrado: " + resource + " con identificador " + id);
    }

    public ResourceNotFoundException(String message) {
        super(404, "NOT_FOUND", message);
    }
}
