package ec.mileniumtech.educafacil.resource.provider;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.dao.excepciones.BaseException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ErrorDto;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Manejador global de excepciones para la API REST.
 * Captura excepciones basadas en BaseException y otras excepciones genéricas.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
	private static final Logger log = LogManager.getLogger(GlobalExceptionMapper.class);
    @Override
    public Response toResponse(Throwable exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setTimestamp(LocalDateTime.now());
        
        if (exception instanceof BaseException) {
            BaseException baseEx = (BaseException) exception;
            errorDto.setMensaje(baseEx.getMessage());
            errorDto.setCodigo(baseEx.getCode());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDto)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Error genérico para excepciones no controladas
        errorDto.setMensaje("Ocurrió un error inesperado en el servidor.");
        errorDto.setCodigo("SERVER-ERROR");
        log.error("Error interno del servidor", exception);
        // En un entorno real, aquí registraríamos el error en un log (usando slf4j o java.util.logging)
        exception.printStackTrace(); 

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorDto)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
