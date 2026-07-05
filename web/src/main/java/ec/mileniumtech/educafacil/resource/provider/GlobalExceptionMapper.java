package ec.mileniumtech.educafacil.resource.provider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.dao.excepciones.BaseException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ErrorDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ErrorDto.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Manejador global de excepciones para la API REST.
 * Captura excepciones basadas en BaseException, ConstraintViolationException y otras excepciones genéricas.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
	private static final Logger log = LogManager.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        // Extraer la causa raíz si está envuelta (ej: en EJBException)
        Throwable cause = exception;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }

        if (cause instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) cause);
        }

        if (cause instanceof BaseException) {
            return handleBaseException((BaseException) cause);
        }

        // Error genérico para excepciones no controladas
        ErrorDto errorDto = ErrorDto.builder()
                .mensaje("Ocurrió un error inesperado en el servidor.")
                .codigo("SERVER-ERROR")
                .timestamp(LocalDateTime.now())
                .build();
        log.error("Error interno del servidor", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorDto)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Maneja excepciones de violación de constraints de Bean Validation.
     */
    private Response handleConstraintViolation(ConstraintViolationException cve) {
        Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();

        List<ValidationError> erroresValidacion = violations.stream()
                .map(v -> ValidationError.builder()
                        .campo(extractFieldName(v.getPropertyPath()))
                        .mensaje(v.getMessage())
                        .valorRechazado(v.getInvalidValue())
                        .build())
                .collect(Collectors.toList());

        ErrorDto errorDto = ErrorDto.builder()
                .mensaje("Errores de validación en los datos de entrada.")
                .codigo("VALIDATION-ERROR")
                .errores(erroresValidacion)
                .timestamp(LocalDateTime.now())
                .build();

        log.warn("Validación fallida: {} errores encontrados", erroresValidacion.size());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDto)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Maneja excepciones de la jerarquía BaseException (BusinessException / SystemException).
     */
    private Response handleBaseException(BaseException baseEx) {
        ErrorDto errorDto = ErrorDto.builder()
                .mensaje(baseEx.getMessage())
                .codigo(baseEx.getCode())
                .timestamp(LocalDateTime.now())
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDto)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Extrae el nombre del campo desde la ruta de la propiedad.
     * Ej: "authDto.username" → "username", "personaDto.nombres" → "nombres"
     */
    private String extractFieldName(Path propertyPath) {
        String path = propertyPath.toString();
        int lastDot = path.lastIndexOf('.');
        return (lastDot >= 0) ? path.substring(lastDot + 1) : path;
    }
}
