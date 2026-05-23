package ec.mileniumtech.educafacil.utilitarios.fechas;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

class FechaFormatoTest {

    @Test
    void cambiarStringaDateFormatoPorDefecto() {
        Date fecha = FechaFormato.cambiarStringaDate("2024-06-15 10:30:00");
        assertNotNull(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        assertEquals(2024, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void cambiarStringaDateRetornaNullSiFormatoInvalido() {
        assertNull(FechaFormato.cambiarStringaDate("fecha-invalida"));
    }

    @Test
    void cambiarStringaDateConFormatoPersonalizado() {
        Date fecha = FechaFormato.cambiarStringaDate("15/06/2024", "dd/MM/yyyy");
        assertNotNull(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
    }

    @Test
    void sumarRestarDiasFecha() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date base = sdf.parse("2024-01-10");
        Date resultado = FechaFormato.sumarRestarDiasFecha(base, 5);

        Calendar cal = Calendar.getInstance();
        cal.setTime(resultado);
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void sumaRestarFechaPorDias() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date base = sdf.parse("2024-03-20");
        Date resultado = FechaFormato.sumaRestarFecha(base, 10, "DAYS");

        Calendar cal = Calendar.getInstance();
        cal.setTime(resultado);
        assertEquals(10, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
    }

    @Test
    void sumaRestarFechaPorMeses() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date base = sdf.parse("2024-06-15");
        Date resultado = FechaFormato.sumaRestarFecha(base, 2, "MONTHS");

        Calendar cal = Calendar.getInstance();
        cal.setTime(resultado);
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH));
    }

    @Test
    void cambiarFormatoFecha() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = sdf.parse("2024-12-25");
        String texto = FechaFormato.cambiarFormato(fecha, "yyyy-MM-dd");
        assertEquals("2024-12-25", texto);
    }
}
