package ec.mileniumtech.educafacil.dao.excepciones;

public class TipoEncuestaPreguntaException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public TipoEncuestaPreguntaException() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public TipoEncuestaPreguntaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	
	public TipoEncuestaPreguntaException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param message
	 */
	
	public TipoEncuestaPreguntaException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param cause
	 */
	
	public TipoEncuestaPreguntaException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
