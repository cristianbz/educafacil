/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.excepciones;

/**
 * @author cbaez [Christian Baez] Feb 28, 2020
 * @version 1.0.0
 *
 */
public class CatalogoException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CatalogoException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CatalogoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CatalogoException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public CatalogoException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public CatalogoException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
