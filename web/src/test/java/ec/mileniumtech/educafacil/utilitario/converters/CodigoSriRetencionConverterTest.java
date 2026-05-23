package ec.mileniumtech.educafacil.utilitario.converters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
import ec.mileniumtech.educafacil.service.RetencionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodigoSriRetencionConverterTest {

    @Mock
    private RetencionService retencionService;

    private CodigoSriRetencionConverter converter;

    @BeforeEach
    void setUp() throws Exception {
        converter = new CodigoSriRetencionConverter();
        Field field = CodigoSriRetencionConverter.class.getDeclaredField("retencionService");
        field.setAccessible(true);
        field.set(converter, retencionService);
    }

    @Test
    void getAsObjectRetornaNullSiValorVacio() {
        assertNull(converter.getAsObject(null, null, null));
        assertNull(converter.getAsObject(null, null, "   "));
    }

    @Test
    void getAsObjectRetornaNullSiIdNoEsNumerico() {
        assertNull(converter.getAsObject(null, null, "abc"));
    }

    @Test
    void getAsObjectBuscaPorId() {
        CodigoSriRetencion codigo = new CodigoSriRetencion();
        codigo.setId(5);
        when(retencionService.buscarCodigoSriPorId(5)).thenReturn(codigo);

        CodigoSriRetencion resultado = converter.getAsObject(null, null, "5");

        assertSame(codigo, resultado);
        verify(retencionService).buscarCodigoSriPorId(5);
    }

    @Test
    void getAsStringRetornaIdComoTexto() {
        CodigoSriRetencion codigo = new CodigoSriRetencion();
        codigo.setId(12);

        assertEquals("12", converter.getAsString(null, null, codigo));
    }

    @Test
    void getAsStringRetornaVacioSiValorNulo() {
        assertEquals("", converter.getAsString(null, null, null));

        CodigoSriRetencion codigo = new CodigoSriRetencion();
        assertEquals("", converter.getAsString(null, null, codigo));
    }
}
