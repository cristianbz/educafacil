package ec.mileniumtech.educafacil.api.exception;

/**
 * Excepción para conflictos de estado (HTTP 409).
 */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(409, "CONFLICT", message);
    }
}
