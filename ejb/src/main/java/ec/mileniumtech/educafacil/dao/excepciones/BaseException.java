package ec.mileniumtech.educafacil.dao.excepciones;

import java.time.LocalDateTime;

/**
 * Clase base para todas las excepciones de la aplicación.
 * Al ser una RuntimeException, no obliga a los métodos a declararla en el 'throws'.
 * 
 * @author Christian Baez
 */
public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final String code;
    private final LocalDateTime timestamp;

    /**
     * Constructor con mensaje.
     * @param message Mensaje descriptivo del error.
     */
    public BaseException(String message) {
        this(message, null, null);
    }

    /**
     * Constructor con mensaje y código.
     * @param message Mensaje descriptivo del error.
     * @param code Código único de error para rastreo.
     */
    public BaseException(String message, String code) {
        this(message, code, null);
    }

    /**
     * Constructor completo.
     * @param message Mensaje descriptivo.
     * @param code Código único.
     * @param cause Causa original del error.
     */
    public BaseException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
