/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.excepciones;

/**
 * @author cbaez [Christian Baez] Mar 2, 2020
 * @version 1.0.0
 *
 */
public class InstructorException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InstructorException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InstructorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InstructorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InstructorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InstructorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	

}
