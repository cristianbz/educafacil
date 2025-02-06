/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */

package ec.mileniumtech.educafacil.web.sesion;

import java.util.Iterator;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;



/**
 * @author [ Christian Baez ] cbaez Jan 17, 2020
 *
 */
public class ViewExpiredExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler handler;

	@SuppressWarnings("deprecation")
	public ViewExpiredExceptionHandler(ExceptionHandler handler) {
		this.handler = handler;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return handler;
	}

	//once we override getWrapped(), we need only override those methods we're interested in. In this case, we want to override only handle()
	@Override
	public void handle() throws FacesException {
		//iterate over unhandler exceptions using the iterator returned from getUnhandledExceptionQueuedEvents().iterator()
		for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents()
				.iterator(); i.hasNext();) {
			//The ExeceptionQueuedEvent is a SystemEvent from which we can get the actual ViewExpiredException
			ExceptionQueuedEvent queuedEvent = i.next();
			ExceptionQueuedEventContext queuedEventContext = (ExceptionQueuedEventContext) queuedEvent
					.getSource();
			Throwable throwable = queuedEventContext.getException();
			if (throwable instanceof ViewExpiredException) {
				ViewExpiredException viewExpiredException = (ViewExpiredException) throwable;
				FacesContext facesContext = FacesContext.getCurrentInstance();
				//for ultimately showing a JSF page we want to extract some information from the exception and place it in request scope,
				//so we can access it via EL in the page
				Map<String, Object> map = facesContext.getExternalContext()
						.getRequestMap();
				NavigationHandler navigationHandler = facesContext
						.getApplication().getNavigationHandler();
				try {
					//we put the current view, where ViewExpiredException occurrs, in "currentViewId" variable
					map.put("currentViewId", viewExpiredException.getViewId());
					//leverage the JSF implicit navigation system and cause the server to navigate to the "viewExpired" page
					//we will show viewExpired page with meaningful message. the view or page name is viewExpired.xhtml
					//viewExpired.xhtml is put on the root path as index.xhtml file
					navigationHandler
					.handleNavigation(facesContext, null,
							"/finsesion.jsf");
					//render the response
					facesContext.renderResponse();
				} finally {
					//we call remove() on the iterator. This is an important part of the ExceptionHandler usage contract.
					//If you handle an exception, you have to remove it from the list of unhandled exceptions
					i.remove();
				}
			}
		}
		getWrapped().handle();
	}
}