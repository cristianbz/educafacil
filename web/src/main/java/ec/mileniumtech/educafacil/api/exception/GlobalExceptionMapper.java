package ec.mileniumtech.educafacil.api.exception;

import ec.mileniumtech.educafacil.api.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la API REST.
 * Convierte cualquier excepción no capturada en una respuesta JSON estructurada.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger log = LogManager.getLogger(GlobalExceptionMapper.class);

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ApiException apiEx) {
            return buildResponse(apiEx.getStatusCode(), apiEx.getErrorCode(), apiEx.getMessage());
        }

        if (exception instanceof ConstraintViolationException cve) {
            List<String> detalles = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.toList());
            return buildResponse(400, "VALIDATION_ERROR", "Error de validación", detalles);
        }

        if (exception instanceof jakarta.ws.rs.NotFoundException) {
            return buildResponse(404, "NOT_FOUND", "El recurso solicitado no existe");
        }

        // Error inesperado — loguear con stacktrace
        log.error("Error interno no manejado en API REST", exception);
        return buildResponse(500, "INTERNAL_ERROR", "Error interno del servidor");
    }

    private Response buildResponse(int status, String code, String message) {
        return buildResponse(status, code, message, null);
    }

    private Response buildResponse(int status, String code, String message, List<String> detalles) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(status)
                .error(code)
                .message(message)
                .details(detalles)
                .path(getRequestPath())
                .build();

        return Response.status(status)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String getRequestPath() {
        try {
            return jakarta.ws.rs.core.UriInfo.class.getMethod("getPath").toString();
        } catch (Exception e) {
            return null;
        }
    }
}
