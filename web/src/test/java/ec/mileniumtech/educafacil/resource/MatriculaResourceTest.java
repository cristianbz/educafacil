package ec.mileniumtech.educafacil.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.MatriculaDto;
import ec.mileniumtech.educafacil.service.AdministracionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatriculaResourceTest {

    @Mock
    private AdministracionService administracionService;

    @InjectMocks
    private MatriculaResource matriculaResource;

    @Test
    void listarPorEstudianteDelegaEnServicio() {
        List<MatriculaDto> matriculas = Collections.singletonList(new MatriculaDto());
        when(administracionService.listarMatriculasEstudianteDto(42)).thenReturn(matriculas);

        List<MatriculaDto> resultado = matriculaResource.listarPorEstudiante(42);

        assertSame(matriculas, resultado);
        verify(administracionService).listarMatriculasEstudianteDto(42);
    }
}
