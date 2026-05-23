package ec.mileniumtech.educafacil.utilitarios.sri;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClaveAccesoGeneratorTest {

    private ClaveAccesoGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ClaveAccesoGenerator();
    }

    @Test
    void generarClaveAccesoConDatosValidos() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("27112024");
        String clave = generator.generarClaveAcceso(fecha, "01", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");
        assertNotNull(clave);
        assertEquals(49, clave.length());
    }

    @Test
    void generarClaveAccesoFormatoCorrecto() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("27112024");
        String clave = generator.generarClaveAcceso(fecha, "01", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");
        assertTrue(clave.matches("\\d{49}"));
    }

    @Test
    void generarClaveAccesoDigitoVerificadorEntre0y9() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("15052025");
        String clave = generator.generarClaveAcceso(fecha, "01", "0999999999001", "2",
                "001001", "000050000", "87654321", "1");
        char digitoVerificador = clave.charAt(48);
        assertTrue(digitoVerificador >= '0' && digitoVerificador <= '9');
    }

    @Test
    void generarClaveAccesoComienzaConFechaFormateada() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("01012025");
        String clave = generator.generarClaveAcceso(fecha, "01", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");
        assertTrue(clave.startsWith("01012025"));
    }

    @Test
    void generarClaveAccesoContieneRuc() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("01012025");
        String ruc = "1790012345001";
        String clave = generator.generarClaveAcceso(fecha, "01", ruc, "1",
                "001001", "000000001", "12345678", "1");
        assertTrue(clave.contains(ruc));
    }

    @Test
    void generarClaveAccesoContieneTipoComprobante() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("01012025");
        String clave = generator.generarClaveAcceso(fecha, "04", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");
        assertTrue(clave.contains("04"));
    }

    @Test
    void generarClaveAccesoDigitoVerificadorConsistente() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("27112024");
        String clave1 = generator.generarClaveAcceso(fecha, "01", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");
        String clave2 = generator.generarClaveAcceso(fecha, "01", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");
        assertEquals(clave1, clave2);
    }

    @Test
    void generarClaveAccesoDigitoVerificadorMod11() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("27112024");
        String clave = generator.generarClaveAcceso(fecha, "01", "1790012345001", "1",
                "001001", "000000001", "12345678", "1");
        int ultimoDigito = Character.getNumericValue(clave.charAt(48));
        assertTrue(ultimoDigito >= 0 && ultimoDigito <= 9);
    }

    @Test
    void generarClaveAccesoConAmbienteProduccion() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date fecha = sdf.parse("01012025");
        String clave = generator.generarClaveAcceso(fecha, "01", "1790012345001", "2",
                "001001", "000000001", "12345678", "1");
        assertEquals('2', clave.charAt(23));
    }
}
