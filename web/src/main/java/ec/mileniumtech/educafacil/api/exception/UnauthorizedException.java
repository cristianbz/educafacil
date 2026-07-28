package ec.mileniumtech.educafacil.api.exception;

/**
 * Excepción para acceso no autorizado (HTTP 401).
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(401, "UNAUTHORIZED", message);
    }
}
