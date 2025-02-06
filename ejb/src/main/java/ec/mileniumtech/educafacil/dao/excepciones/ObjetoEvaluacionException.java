package ec.mileniumtech.educafacil.dao.excepciones;

public class ObjetoEvaluacionException extends Exception{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @author [ Aaron Yanez ]ahyanez
	 */
	public ObjetoEvaluacionException() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ObjetoEvaluacionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public ObjetoEvaluacionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ObjetoEvaluacionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ObjetoEvaluacionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
