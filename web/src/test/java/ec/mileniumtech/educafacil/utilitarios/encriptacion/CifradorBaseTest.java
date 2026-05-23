package ec.mileniumtech.educafacil.utilitarios.encriptacion;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CifradorBaseTest {

    @Test
    void cifrarYDescifrarBase64() {
        String original = "Educafacil-2026";
        String cifrado = CifradorBase.cifrarBase64(original);
        assertNotEquals(original, cifrado);

        String descifrado = CifradorBase.descifrarBase64(cifrado);
        assertEquals(original, descifrado);
    }

    @Test
    void cifrarBase64CadenaVacia() {
        String cifrado = CifradorBase.cifrarBase64("");
        assertEquals("", CifradorBase.descifrarBase64(cifrado));
    }
}
