package ec.mileniumtech.educafacil.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.PersonaDto;
import ec.mileniumtech.educafacil.service.AdministracionService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonaResourceTest {

    @Mock
    private AdministracionService administracionService;

    @InjectMocks
    private PersonaResource personaResource;

    @Test
    void getPersonaPorCedulaCorreoEncontrada() {
        PersonaDto dto = new PersonaDto();
        when(administracionService.buscarPersonaDto("1710034065", "correo@test.com")).thenReturn(dto);

        Response response = personaResource.getPersonaPorCedulaCorreo("1710034065", "correo@test.com");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(dto, response.getEntity());
    }

    @Test
    void getPersonaPorCedulaCorreoNoEncontrada() {
        when(administracionService.buscarPersonaDto("000", "x@y.com")).thenReturn(null);

        Response response = personaResource.getPersonaPorCedulaCorreo("000", "x@y.com");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void crearPersonaRetorna201() {
        PersonaDto entrada = new PersonaDto();
        PersonaDto creada = new PersonaDto();
        when(administracionService.guardarPersona(entrada)).thenReturn(creada);

        Response response = personaResource.crearPersona(entrada);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(creada, response.getEntity());
    }

    @Test
    void eliminarPersonaRetorna204() {
        Response response = personaResource.eliminarPersona(3);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(administracionService).eliminarPersona(3);
    }
}
