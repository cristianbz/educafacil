package ec.mileniumtech.educafacil.dao.excepciones;

public class VendedorException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * @author [ Aaron Yanez ]ahyanez
	 */
	public VendedorException() {	
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public VendedorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public VendedorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public VendedorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public VendedorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}

