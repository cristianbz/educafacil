package ec.mileniumtech.educafacil.resource.provider;

import static org.junit.jupiter.api.Assertions.*;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ErrorDto;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GlobalExceptionMapperTest {

    private GlobalExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GlobalExceptionMapper();
    }

    @Test
    void toResponseBusinessException() {
        BusinessException ex = new BusinessException("Registro duplicado", "DUPLICATE-ENTITY");

        Response response = mapper.toResponse(ex);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorDto body = (ErrorDto) response.getEntity();
        assertEquals("Registro duplicado", body.getMensaje());
        assertEquals("DUPLICATE-ENTITY", body.getCodigo());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void toResponseSystemExceptionComoBaseException() {
        SystemException ex = new SystemException("Error de BD", "DB-ERR", new RuntimeException("causa"));

        Response response = mapper.toResponse(ex);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorDto body = (ErrorDto) response.getEntity();
        assertEquals("Error de BD", body.getMensaje());
        assertEquals("DB-ERR", body.getCodigo());
    }

    @Test
    void toResponseExcepcionGenerica() {
        Response response = mapper.toResponse(new IllegalStateException("fallo"));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ErrorDto body = (ErrorDto) response.getEntity();
        assertEquals("SERVER-ERROR", body.getCodigo());
    }
}
