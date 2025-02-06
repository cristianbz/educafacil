/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */

package ec.mileniumtech.educafacil.web.sesion;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

/**
 * @author [ Christian Baez ] cbaez Jan 17, 2020
 *
 */
public class ViewExpiredExceptionHandlerFactory extends ExceptionHandlerFactory{

	/**
	 * 
	 */
	private ExceptionHandlerFactory factory;
	@SuppressWarnings("deprecation")
	public ViewExpiredExceptionHandlerFactory(ExceptionHandlerFactory factory) {
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.ExceptionHandlerFactory#getExceptionHandler()
	 */
	@Override
	public ExceptionHandler getExceptionHandler() {
		ExceptionHandler handler = factory.getExceptionHandler();
		handler = (ExceptionHandler) new ViewExpiredExceptionHandler(handler);
		return handler;
	}

}
