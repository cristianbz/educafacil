package ec.mileniumtech.educafacil.web.handler;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;

/**
 * Manejador global de excepciones para JSF.
 * Captura excepciones no manejadas y las procesa según su tipo (Negocio o Sistema).
 * 
 * @author Christian Baez
 */
public class EducafacilExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger log = Logger.getLogger(EducafacilExceptionHandler.class);

    public EducafacilExceptionHandler(ExceptionHandler wrapped) {
        super(wrapped);
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator();

        while (it.hasNext()) {
            ExceptionQueuedEvent event = it.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            Throwable t = context.getException();

            try {
                Throwable cause = extractRootCause(t);

                if (cause instanceof BusinessException) {
                    handleBusinessException((BusinessException) cause);
                } else if (cause instanceof SystemException) {
                    handleSystemException((SystemException) cause);
                } else {
                    handleUnexpectedException(cause);
                }

            } finally {
                it.remove();
            }
        }
        getWrapped().handle();
    }

    private void handleBusinessException(BusinessException be) {
        log.warn("Business Exception: " + be.getMessage());
        Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", be.getMessage());
        FacesContext.getCurrentInstance().renderResponse();
    }

    private void handleSystemException(SystemException se) {
        log.error("System Exception [" + se.getCode() + "]: " + se.getMessage(), se);
        Mensaje.verMensaje(FacesMessage.SEVERITY_FATAL, "Error de Sistema", 
            "Ha ocurrido un error inesperado. Por favor, contacte al administrador. Ref: " + se.getCode());
        FacesContext.getCurrentInstance().renderResponse();
    }

    private void handleUnexpectedException(Throwable t) {
        log.error("Unhandled Exception: " + t.getMessage(), t);
        Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error Inesperado", 
            "Se ha producido un error interno en la aplicación.");
        FacesContext.getCurrentInstance().renderResponse();
    }

    private Throwable extractRootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && (cause instanceof FacesException || cause instanceof RuntimeException)) {
            cause = cause.getCause();
        }
        return cause;
    }
}
