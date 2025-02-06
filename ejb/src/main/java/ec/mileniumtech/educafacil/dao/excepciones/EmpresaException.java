/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.dao.excepciones;

/**
 * @author [ Christian Baez ]cbaez
 *
 */
public class EmpresaException extends Exception {


	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public EmpresaException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EmpresaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EmpresaException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public EmpresaException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public EmpresaException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
