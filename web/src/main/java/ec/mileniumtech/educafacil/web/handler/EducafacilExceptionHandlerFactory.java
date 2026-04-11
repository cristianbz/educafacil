package ec.mileniumtech.educafacil.web.handler;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

/**
 * Fabrica para el manejador de excepciones personalizado.
 * 
 * @author Christian Baez
 */
public class EducafacilExceptionHandlerFactory extends ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    public EducafacilExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new EducafacilExceptionHandler(parent.getExceptionHandler());
    }
}
