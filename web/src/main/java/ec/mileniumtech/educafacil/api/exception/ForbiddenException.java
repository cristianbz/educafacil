package ec.mileniumtech.educafacil.api.exception;

/**
 * Excepción para permisos insuficientes (HTTP 403).
 */
public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(403, "FORBIDDEN", message);
    }
}
