package ec.mileniumtech.educafacil.api.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Filtro de logging para todas las peticiones/respuestas REST.
 * Registra método HTTP, ruta, tiempo de respuesta y código de estado.
 */
@Provider
@Priority(Priorities.USER)
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger log = LogManager.getLogger(LoggingFilter.class);

    private static final String PROPERTY_START_TIME = "requestStartTime";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(PROPERTY_START_TIME, System.currentTimeMillis());

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        String contentType = requestContext.getHeaderString("Content-Type");

        if (log.isDebugEnabled()) {
            log.debug("→ {} {} [Content-Type: {}]", method, path, contentType);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        Long startTime = (Long) requestContext.getProperty(PROPERTY_START_TIME);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        int status = responseContext.getStatus();

        // Log lento si la petición tomó más de 2 segundos
        String slowWarning = duration > 2000 ? " (LENTO)" : "";

        log.info("← {} {} → {} ({} ms{})", method, path, status, duration, slowWarning);
    }
}
