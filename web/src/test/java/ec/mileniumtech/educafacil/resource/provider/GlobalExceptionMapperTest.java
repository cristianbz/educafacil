package ec.mileniumtech.educafacil.resource.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ErrorDto;
import jakarta.ejb.EJBException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
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
    void toResponseDesenvuelveEJBException() {
        BusinessException causa = new BusinessException("Registro duplicado", "DUPLICATE-ENTITY");
        EJBException wrapper = new EJBException(causa);

        Response response = mapper.toResponse(wrapper);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorDto body = (ErrorDto) response.getEntity();
        assertEquals("Registro duplicado", body.getMensaje());
        assertEquals("DUPLICATE-ENTITY", body.getCodigo());
    }

    @Test
    void toResponseExcepcionGenerica() {
        Response response = mapper.toResponse(new IllegalStateException("fallo"));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ErrorDto body = (ErrorDto) response.getEntity();
        assertEquals("SERVER-ERROR", body.getCodigo());
    }

    @Test
    void toResponseConstraintViolation() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("no debe ser nulo");
        when(violation.getInvalidValue()).thenReturn(null);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("personaDto.nombres");
        when(violation.getPropertyPath()).thenReturn(path);
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException cve = new ConstraintViolationException("Validación fallida", violations);

        Response response = mapper.toResponse(cve);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorDto body = (ErrorDto) response.getEntity();
        assertEquals("VALIDATION-ERROR", body.getCodigo());
        assertNotNull(body.getErrores());
        assertEquals(1, body.getErrores().size());
        assertEquals("nombres", body.getErrores().get(0).getCampo());
        assertEquals("no debe ser nulo", body.getErrores().get(0).getMensaje());
    }

    @Test
    void toResponseConstraintViolacionEnvueltaEnEJBException() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("no debe ser nulo");
        when(violation.getInvalidValue()).thenReturn(null);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("correo");
        when(violation.getPropertyPath()).thenReturn(path);
        ConstraintViolationException cve =
                new ConstraintViolationException("Validación fallida", Collections.singleton(violation));
        EJBException wrapper = new EJBException(cve);

        Response response = mapper.toResponse(wrapper);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorDto body = (ErrorDto) response.getEntity();
        assertEquals("VALIDATION-ERROR", body.getCodigo());
    }
}
