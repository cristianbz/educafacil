package ec.mileniumtech.educafacil.dao.excepciones;

public class DetalleEvaluaCursoException  extends Exception{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @author [ Aaron Yanez ]ahyanez
	 */
	public DetalleEvaluaCursoException() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public DetalleEvaluaCursoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public DetalleEvaluaCursoException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DetalleEvaluaCursoException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DetalleEvaluaCursoException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
