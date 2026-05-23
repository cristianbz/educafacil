package ec.mileniumtech.educafacil.utilitarios.encriptacion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CriptografiaUtilTest {

    private static final String TEXTO_ORIGINAL = "DatosSensibles123";

    @Test
    void encriptarYDesencriptar() throws Exception {
        String cifrado = CriptografiaUtil.encriptar(TEXTO_ORIGINAL);
        assertNotNull(cifrado);
        assertNotEquals(TEXTO_ORIGINAL, cifrado);

        String descifrado = CriptografiaUtil.desencriptar(cifrado);
        assertEquals(TEXTO_ORIGINAL, descifrado);
    }

    @Test
    void encriptarConTextoNulo() throws Exception {
        String resultado = CriptografiaUtil.encriptar(null);
        assertNull(resultado);
    }

    @Test
    void desencriptarConTextoNulo() throws Exception {
        String resultado = CriptografiaUtil.desencriptar(null);
        assertNull(resultado);
    }

    @Test
    void encriptarConTextoVacio() throws Exception {
        String resultado = CriptografiaUtil.encriptar("");
        assertEquals("", resultado);
    }

    @Test
    void desencriptarConTextoVacio() throws Exception {
        String resultado = CriptografiaUtil.desencriptar("");
        assertEquals("", resultado);
    }

    @Test
    void desencriptarTextoNoCifrado() throws Exception {
        String resultado = CriptografiaUtil.desencriptar("texto-plano-no-cifrado");
        assertEquals("texto-plano-no-cifrado", resultado);
    }

    @Test
    void cifradoNoEsIgualAlOriginal() throws Exception {
        String cifrado = CriptografiaUtil.encriptar(TEXTO_ORIGINAL);
        assertNotEquals(TEXTO_ORIGINAL, cifrado);
    }

    @Test
    void roundTripConCaracteresEspeciales() throws Exception {
        String original = "Ñoño&áciön #123$%";
        String cifrado = CriptografiaUtil.encriptar(original);
        String descifrado = CriptografiaUtil.desencriptar(cifrado);
        assertEquals(original, descifrado);
    }

    @Test
    void roundTripConNumeros() throws Exception {
        String original = "1234567890";
        String cifrado = CriptografiaUtil.encriptar(original);
        String descifrado = CriptografiaUtil.desencriptar(cifrado);
        assertEquals(original, descifrado);
    }
}
