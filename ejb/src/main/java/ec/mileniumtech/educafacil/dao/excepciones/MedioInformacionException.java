/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.excepciones;

/**
 * @author cbaez [Christian Baez] Jan 1, 2020
 * @version 1.0.0
 *
 */
public class MedioInformacionException extends Exception {

	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public MedioInformacionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MedioInformacionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MedioInformacionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MedioInformacionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MedioInformacionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
