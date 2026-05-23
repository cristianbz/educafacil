package ec.mileniumtech.educafacil.utilitarios.encriptacion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EncriptarTest {

    private static final String TEXTO_PLANO = "EducaFacil2024";
    private static final String SHA512_ESPERADO = "dfe095db27225ea743ecad3e60302513cfc65b5ab4bfd9e6a65cecd2382d6c9adcbfb8d8bdd128749acc6c2e3bc4eb8710182d8531fcc1e1e9cebbd09904eba4";

    @Test
    void encriptarSHA512() {
        String resultado = Encriptar.encriptarSHA512(TEXTO_PLANO);
        assertNotNull(resultado);
        assertEquals(128, resultado.length());
        assertEquals(SHA512_ESPERADO, resultado);
    }

    @Test
    void encriptarSHA256() {
        String resultado = Encriptar.encriptarSHA256(TEXTO_PLANO);
        assertNotNull(resultado);
        assertEquals(64, resultado.length());
    }

    @Test
    void encriptarSHA256NoEsIgualAEntrada() {
        String resultado = Encriptar.encriptarSHA256(TEXTO_PLANO);
        assertNotEquals(TEXTO_PLANO, resultado);
    }

    @Test
    void encriptarSHA1() {
        String resultado = Encriptar.encriptarSHA1(TEXTO_PLANO);
        assertNotNull(resultado);
        assertEquals(40, resultado.length());
    }

    @Test
    void encriptarSHA512ConCadenaVacia() {
        String resultado = Encriptar.encriptarSHA512("");
        assertNotNull(resultado);
        assertEquals(128, resultado.length());
    }

    @Test
    void encriptarSHA256ConCadenaVacia() {
        String resultado = Encriptar.encriptarSHA256("");
        assertNotNull(resultado);
        assertEquals(64, resultado.length());
    }

    @Test
    void encriptarSHA1ConCadenaVacia() {
        String resultado = Encriptar.encriptarSHA1("");
        assertNotNull(resultado);
        assertEquals(40, resultado.length());
    }

    @Test
    void encriptarSHA512EsConsistente() {
        String primera = Encriptar.encriptarSHA512(TEXTO_PLANO);
        String segunda = Encriptar.encriptarSHA512(TEXTO_PLANO);
        assertEquals(primera, segunda);
    }

    @Test
    void encriptarSHA512ConNulo() {
        assertThrows(NullPointerException.class, () -> Encriptar.encriptarSHA512(null));
    }
}
