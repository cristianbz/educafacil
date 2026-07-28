package ec.mileniumtech.educafacil.api.exception;

/**
 * Excepción para peticiones inválidas (HTTP 400).
 */
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(400, "BAD_REQUEST", message);
    }

    public BadRequestException(String errorCode, String message) {
        super(400, errorCode, message);
    }
}
