package ec.mileniumtech.educafacil.dao.excepciones;

/**
 * Excepción para errores técnicos o del sistema (DB caida, null pointers inesperados, fallos de red).
 * Estas excepciones suelen presentarse al usuario como un error fatal genérico, protegiendo
 * los detalles técnicos.
 * 
 * @author Christian Baez
 */
public class SystemException extends BaseException {

    private static final long serialVersionUID = 1L;

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, String code) {
        super(message, code);
    }

    public SystemException(String message, String code, Throwable cause) {
        super(message, code, cause);
    }
}
