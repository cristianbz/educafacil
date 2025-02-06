package ec.mileniumtech.educafacil.dao.excepciones;

public class PreguntaException extends Exception{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @author [ Aaron Yanez ]ahyanez
	 */
	public PreguntaException() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public PreguntaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public PreguntaException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public PreguntaException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public PreguntaException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}


