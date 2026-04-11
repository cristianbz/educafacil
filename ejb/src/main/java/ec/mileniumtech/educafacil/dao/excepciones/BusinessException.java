package ec.mileniumtech.educafacil.dao.excepciones;

/**
 * Excepción para errores de lógica de negocio (validaciones, reglas de dominio).
 * Estas excepciones suelen ser presentadas al usuario como advertencias o mensajes informativos.
 * 
 * @author Christian Baez
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, String code) {
        super(message, code);
    }

    public BusinessException(String message, String code, Throwable cause) {
        super(message, code, cause);
    }
}
