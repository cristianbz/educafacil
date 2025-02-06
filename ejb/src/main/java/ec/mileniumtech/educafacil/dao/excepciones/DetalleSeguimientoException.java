/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.excepciones;

/**
 * @author christian [Christian Baez] 30 dic. 2021
 * @version 1.0.0
 *
 */
public class DetalleSeguimientoException extends Exception {


	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DetalleSeguimientoException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public DetalleSeguimientoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DetalleSeguimientoException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DetalleSeguimientoException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DetalleSeguimientoException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
