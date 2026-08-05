package ec.mileniumtech.educafacil.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ec.mileniumtech.educafacil.dao.PersonaDao;
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
    private PersonaDao personaDao;

    private PersonaService personaService;

    @BeforeEach
    void setUp() throws Exception {
        personaService = new PersonaService();
        Field field = PersonaService.class.getDeclaredField("personaDao");
        field.setAccessible(true);
        field.set(personaService, personaDao);
    }

    @Test
    void buscarPersonaPorCedulaCorreoExitosamente() {
        Persona persona = new Persona();
        when(personaDao.buscarPersonaPorCedulaCorreo("1710034065", "test@mail.com")).thenReturn(persona);

        Persona resultado = personaService.buscarPersonaPorCedulaCorreo("1710034065", "test@mail.com");

        assertSame(persona, resultado);
    }

    @Test
    void buscarPersonaPorCedulaCorreoPropagaExcepcionDelDao() {
        RuntimeException original = new RuntimeException("Error DB");
        when(personaDao.buscarPersonaPorCedulaCorreo(anyString(), anyString()))
                .thenThrow(original);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> personaService.buscarPersonaPorCedulaCorreo("1", "x@y.com"));
        assertSame(original, ex);
    }
}
