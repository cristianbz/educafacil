package ec.mileniumtech.educafacil.dao.excepciones;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SystemExceptionTest {

    @Test
    void constructorConMensaje() {
        SystemException ex = new SystemException("Error del sistema");
        assertEquals("Error del sistema", ex.getMessage());
        assertNull(ex.getCode());
    }

    @Test
    void constructorConMensajeYCodigo() {
        SystemException ex = new SystemException("Error del sistema", "SYS-001");
        assertEquals("Error del sistema", ex.getMessage());
        assertEquals("SYS-001", ex.getCode());
    }

    @Test
    void constructorConMensajeCodigoYCausa() {
        Throwable causa = new NullPointerException("Valor nulo");
        SystemException ex = new SystemException("Error del sistema", "SYS-001", causa);
        assertEquals("Error del sistema", ex.getMessage());
        assertEquals("SYS-001", ex.getCode());
        assertEquals(causa, ex.getCause());
    }

    @Test
    void esRuntimeException() {
        SystemException ex = new SystemException("test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void esBaseException() {
        SystemException ex = new SystemException("test");
        assertTrue(ex instanceof BaseException);
    }

    @Test
    void timestampNoEsNulo() {
        SystemException ex = new SystemException("test");
        assertNotNull(ex.getTimestamp());
    }

    @Test
    void mensajeConCodigoYPilaCompleta() {
        Exception causa = new Exception("Causa original");
        SystemException ex = new SystemException("Error crítico", "SYS-CRIT", causa);
        assertEquals("Error crítico", ex.getMessage());
        assertEquals("SYS-CRIT", ex.getCode());
        assertEquals(causa, ex.getCause());
        assertNotNull(ex.getStackTrace());
    }
}
