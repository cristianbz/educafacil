package ec.mileniumtech.educafacil.utilitarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ValidacionUtilTest {

    @Test
    void validarCedulaConCedulaValida() {
        assertTrue(ValidacionUtil.validarCedula("1710034065"));
    }

    @Test
    void validarCedulaConCedulaInvalida() {
        assertFalse(ValidacionUtil.validarCedula("1710034066"));
    }

    @Test
    void validarCedulaConLongitudIncorrecta() {
        assertFalse(ValidacionUtil.validarCedula("12345"));
    }

    @Test
    void validarCedulaConNulo() {
        assertFalse(ValidacionUtil.validarCedula(null));
    }

    @Test
    void validarCedulaConLetras() {
        assertFalse(ValidacionUtil.validarCedula("171003406A"));
    }

    @Test
    void validarCedulaConProvinciaInvalida() {
        assertFalse(ValidacionUtil.validarCedula("2510034065"));
    }

    @Test
    void validarCedulaConTercerDigitoMayorIgualSeis() {
        assertFalse(ValidacionUtil.validarCedula("1760034065"));
    }

    @Test
    void validarCedulaConTodosCeros() {
        assertFalse(ValidacionUtil.validarCedula("0000000000"));
    }

    @Test
    void validarCedulaConDigitoVerificadorCero() {
        assertTrue(ValidacionUtil.validarCedula("1710034065"));
    }

    @Test
    void verificarConexionConUrlNula() {
        assertFalse(ValidacionUtil.verificarConexion(null, 1000));
    }
}
