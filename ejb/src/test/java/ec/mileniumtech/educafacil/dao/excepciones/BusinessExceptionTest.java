package ec.mileniumtech.educafacil.dao.excepciones;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

    @Test
    void constructorConMensaje() {
        BusinessException ex = new BusinessException("Mensaje de error");
        assertEquals("Mensaje de error", ex.getMessage());
        assertNull(ex.getCode());
        assertNotNull(ex.getTimestamp());
    }

    @Test
    void constructorConMensajeYCodigo() {
        BusinessException ex = new BusinessException("Mensaje de error", "BUS-001");
        assertEquals("Mensaje de error", ex.getMessage());
        assertEquals("BUS-001", ex.getCode());
    }

    @Test
    void constructorConMensajeCodigoYCausa() {
        Throwable causa = new IllegalArgumentException("Causa raíz");
        BusinessException ex = new BusinessException("Mensaje de error", "BUS-001", causa);
        assertEquals("Mensaje de error", ex.getMessage());
        assertEquals("BUS-001", ex.getCode());
        assertEquals(causa, ex.getCause());
    }

    @Test
    void esRuntimeException() {
        BusinessException ex = new BusinessException("test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void esBaseException() {
        BusinessException ex = new BusinessException("test");
        assertTrue(ex instanceof BaseException);
    }

    @Test
    void timestampNoEsNulo() {
        BusinessException ex = new BusinessException("test");
        assertNotNull(ex.getTimestamp());
    }

    @Test
    void herenciaCorrecta() {
        BusinessException ex = new BusinessException("test");
        assertInstanceOf(BaseException.class, ex);
        assertInstanceOf(RuntimeException.class, ex);
    }
}
