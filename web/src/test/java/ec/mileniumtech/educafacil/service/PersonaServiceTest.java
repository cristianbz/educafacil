package ec.mileniumtech.educafacil.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaDaoImpl personaDaoImpl;

    private PersonaService personaService;

    @BeforeEach
    void setUp() throws Exception {
        personaService = new PersonaService();
        Field field = PersonaService.class.getDeclaredField("personaDaoImpl");
        field.setAccessible(true);
        field.set(personaService, personaDaoImpl);
    }

    @Test
    void buscarPersonaPorCedulaCorreoExitosamente() {
        Persona persona = new Persona();
        when(personaDaoImpl.buscarPersonaPorCedulaCorreo("1710034065", "test@mail.com")).thenReturn(persona);

        Persona resultado = personaService.buscarPersonaPorCedulaCorreo("1710034065", "test@mail.com");

        assertSame(persona, resultado);
    }

    @Test
    void buscarPersonaPorCedulaCorreoLanzaSystemException() {
        when(personaDaoImpl.buscarPersonaPorCedulaCorreo(anyString(), anyString()))
                .thenThrow(new RuntimeException("Error DB"));

        SystemException ex = assertThrows(SystemException.class,
                () -> personaService.buscarPersonaPorCedulaCorreo("1", "x@y.com"));
        assertEquals("PERS-SINGLE-ERR", ex.getCode());
    }
}
