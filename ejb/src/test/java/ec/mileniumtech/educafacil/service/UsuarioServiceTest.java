package ec.mileniumtech.educafacil.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;

/**
 * Tests unitarios para los métodos de gestión de Usuarios a través
 * de la fachada {@link AdministracionService}.
 * <p>
 * AdministracionService actualmente delega en {@link SeguridadService}.
 * Solo se prueban los métodos expuestos por la fachada.
 *
 * @author christian
 */
@DisplayName("AdministracionService — Usuarios (delegado a SeguridadService)")
class UsuarioServiceTest {

    @Mock
    private SeguridadService seguridadService;

    @Mock
    private OfertaService ofertaService;

    @Mock
    private EvaluacionService evaluacionService;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private PlanificacionCursoService planificacionCursoService;

    @Mock
    private PersonaService personaService;

    @InjectMocks
    private AdministracionService administracionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private Usuario crearUsuario(Integer usuaId, String usuaUsuario, boolean usuaEstado) {
        Usuario u = new Usuario();
        u.setUsuaId(usuaId);
        u.setUsuaUsuario(usuaUsuario);
        u.setUsuaEstado(usuaEstado);
        return u;
    }

    @Nested
    @DisplayName("listarUsuarios()")
    class ListarUsuarios {

        @Test
        @DisplayName("Retorna todos los usuarios del sistema")
        void listarUsuarios_retornaListaCompleta() {
            List<Usuario> esperados = Arrays.asList(
                    crearUsuario(1, "admin", true),
                    crearUsuario(2, "docente1", true),
                    crearUsuario(3, "inactivo", false)
            );
            when(seguridadService.listarUsuarios()).thenReturn(esperados);

            List<Usuario> resultado = administracionService.listarUsuarios();

            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            verify(seguridadService, times(1)).listarUsuarios();
        }

        @Test
        @DisplayName("Retorna lista vacía cuando no hay usuarios registrados")
        void listarUsuarios_retornaListaVacia() {
            when(seguridadService.listarUsuarios()).thenReturn(Collections.emptyList());

            List<Usuario> resultado = administracionService.listarUsuarios();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(seguridadService, times(1)).listarUsuarios();
        }
    }

    @Nested
    @DisplayName("guardarUsuario()")
    class GuardarUsuario {

        @Test
        @DisplayName("Delega en SeguridadService cuando el id es null (nuevo)")
        void guardarUsuario_nuevo_llamaGuardar() {
            Usuario nuevo = crearUsuario(null, "nuevo_user", true);

            administracionService.guardarUsuario(nuevo);

            verify(seguridadService, times(1)).guardarUsuario(nuevo);
        }

        @Test
        @DisplayName("Delega en SeguridadService cuando el id tiene valor (edición)")
        void guardarUsuario_edicion_llamaActualizar() {
            Usuario existente = crearUsuario(10, "existente", true);

            administracionService.guardarUsuario(existente);

            verify(seguridadService, times(1)).guardarUsuario(existente);
        }
    }

    @Nested
    @DisplayName("eliminarLogicoUsuario()")
    class EliminarLogicoUsuario {

        @Test
        @DisplayName("Delega en SeguridadService cuando el usuario existe")
        void eliminarLogicoUsuario_usuarioExiste_desactiva() {
            administracionService.eliminarLogicoUsuario(5);

            verify(seguridadService, times(1)).eliminarLogicoUsuario(5);
        }

        @Test
        @DisplayName("Delega en SeguridadService incluso cuando el usuario no existe")
        void eliminarLogicoUsuario_usuarioNoExiste_noLanzaExcepcion() {
            assertDoesNotThrow(() -> administracionService.eliminarLogicoUsuario(999));
            verify(seguridadService, times(1)).eliminarLogicoUsuario(999);
        }
    }
}
