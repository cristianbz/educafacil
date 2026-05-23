package ec.mileniumtech.educafacil.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.CursoDto;
import ec.mileniumtech.educafacil.service.AdministracionService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CursoResourceTest {

    @Mock
    private AdministracionService administracionService;

    @InjectMocks
    private CursoResource cursoResource;

    @Test
    void listarTodosDelegaEnServicio() {
        CursoDto dto = new CursoDto(1, "Java");
        when(administracionService.listarCursosDto()).thenReturn(Arrays.asList(dto));

        List<CursoDto> resultado = cursoResource.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Java", resultado.get(0).getNombre());
        verify(administracionService).listarCursosDto();
    }

    @Test
    void crearCursoRetorna201() {
        CursoDto entrada = new CursoDto(0, "Nuevo curso");
        CursoDto creado = new CursoDto(10, "Nuevo curso");
        when(administracionService.guardarCurso(entrada)).thenReturn(creado);

        Response response = cursoResource.crearCurso(entrada);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(creado, response.getEntity());
    }

    @Test
    void actualizarCursoRetorna200() {
        CursoDto dto = new CursoDto(5, "Actualizado");
        when(administracionService.actualizarCursoDto(dto)).thenReturn(dto);

        Response response = cursoResource.actualizarCurso(dto);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(dto, response.getEntity());
    }

    @Test
    void eliminarCursoRetorna204() {
        Response response = cursoResource.eliminarCurso(7);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(administracionService).eliminarCurso(7);
    }
}
