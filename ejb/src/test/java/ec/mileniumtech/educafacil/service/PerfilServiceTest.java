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

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;

/**
 * Tests unitarios para los m&eacute;todos de gesti&oacute;n de Perfiles y Acciones
 * a trav&eacute;s de la fachada {@link AdministracionService}.
 *
 * @author christian
 */
@DisplayName("AdministracionService — Perfiles y Acciones (delegado a SeguridadService)")
class PerfilServiceTest {

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

    private Perfil crearPerfil(Integer id, String nombre, Boolean estado) {
        Perfil p = new Perfil();
        p.setId(id);
        p.setNombre(nombre);
        p.setDescripcion("Descripción de " + nombre);
        p.setEstado(estado);
        p.setIcono("pi pi-user");
        return p;
    }

    private Accion crearAccion(String id, String nombre) {
        Accion a = new Accion();
        a.setId(id);
        a.setNombre(nombre);
        a.setDescripcion("Descripción de " + nombre);
        a.setRuta("/paginas/" + nombre.toLowerCase());
        a.setEstado(true);
        return a;
    }

    private PerfilAccion crearPerfilAccion(Integer id, Perfil perfil, Accion accion) {
        PerfilAccion pa = new PerfilAccion();
        pa.setId(id);
        pa.setPerfil(perfil);
        pa.setAccion(accion);
        pa.setEstado(true);
        return pa;
    }

    @Nested
    @DisplayName("listarPerfiles()")
    class ListarPerfiles {

        @Test
        @DisplayName("Retorna lista de perfiles activos e inactivos")
        void listarPerfiles_retornaListaCompleta() {
            List<Perfil> esperados = Arrays.asList(
                    crearPerfil(1, "Administrador", true),
                    crearPerfil(2, "Docente", true),
                    crearPerfil(3, "Secretaría", false)
            );
            when(seguridadService.listarPerfiles()).thenReturn(esperados);

            List<Perfil> resultado = administracionService.listarPerfiles();

            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            verify(seguridadService, times(1)).listarPerfiles();
        }

        @Test
        @DisplayName("Retorna lista vacía cuando no hay perfiles")
        void listarPerfiles_retornaListaVacia() {
            when(seguridadService.listarPerfiles()).thenReturn(Collections.emptyList());

            List<Perfil> resultado = administracionService.listarPerfiles();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(seguridadService, times(1)).listarPerfiles();
        }
    }

    @Nested
    @DisplayName("guardarPerfil()")
    class GuardarPerfil {

        @Test
        @DisplayName("Delega en SeguridadService cuando el id es null (nuevo)")
        void guardarPerfil_nuevo_llamaGuardar() {
            Perfil nuevo = crearPerfil(null, "Auditor", true);

            administracionService.guardarPerfil(nuevo);

            verify(seguridadService, times(1)).guardarPerfil(nuevo);
        }

        @Test
        @DisplayName("Delega en SeguridadService cuando el id tiene valor (edición)")
        void guardarPerfil_edicion_llamaActualizar() {
            Perfil existente = crearPerfil(5, "Auditor", true);

            administracionService.guardarPerfil(existente);

            verify(seguridadService, times(1)).guardarPerfil(existente);
        }
    }

    @Nested
    @DisplayName("eliminarLogicoPerfil()")
    class EliminarLogicoPerfil {

        @Test
        @DisplayName("Delega en SeguridadService cuando el perfil existe")
        void eliminarLogicoPerfil_perfilExiste_desactiva() {
            administracionService.eliminarLogicoPerfil(3);

            verify(seguridadService, times(1)).eliminarLogicoPerfil(3);
        }

        @Test
        @DisplayName("Delega en SeguridadService incluso cuando el perfil no existe")
        void eliminarLogicoPerfil_perfilNoExiste_noHaceNada() {
            assertDoesNotThrow(() -> administracionService.eliminarLogicoPerfil(999));
            verify(seguridadService, times(1)).eliminarLogicoPerfil(999);
        }
    }

    @Nested
    @DisplayName("listarAcciones()")
    class ListarAcciones {

        @Test
        @DisplayName("Retorna todas las acciones activas del sistema")
        void listarAcciones_retornaListaCompleta() {
            List<Accion> esperadas = Arrays.asList(
                    crearAccion("ACC01", "Dashboard"),
                    crearAccion("ACC02", "Cursos"),
                    crearAccion("ACC03", "Estudiantes")
            );
            when(seguridadService.listarAcciones()).thenReturn(esperadas);

            List<Accion> resultado = administracionService.listarAcciones();

            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            verify(seguridadService, times(1)).listarAcciones();
        }
    }

    @Nested
    @DisplayName("asignarAccionAPerfil()")
    class AsignarAccionAPerfil {

        @Test
        @DisplayName("Delega en SeguridadService")
        void asignarAccion_delegaEnSeguridadService() {
            administracionService.asignarAccionAPerfil(1, "ACC01");

            verify(seguridadService, times(1)).asignarAccionAPerfil(1, "ACC01");
        }
    }

    @Nested
    @DisplayName("quitarAccionDePerfil()")
    class QuitarAccionDePerfil {

        @Test
        @DisplayName("Delega en SeguridadService con los parámetros correctos")
        void quitarAccion_delegaEnSeguridadService() {
            administracionService.quitarAccionDePerfil(1, "ACC01");

            verify(seguridadService, times(1)).quitarAccionDePerfil(1, "ACC01");
        }
    }

    @Nested
    @DisplayName("listarAccionesPorPerfil()")
    class ListarAccionesPorPerfil {

        @Test
        @DisplayName("Retorna las acciones asignadas al perfil dado")
        void listarAccionesPorPerfil_retornaAccionesAsignadas() {
            Perfil perfil = crearPerfil(2, "Docente", true);
            Accion accion1 = crearAccion("ACC02", "Cursos");
            Accion accion2 = crearAccion("ACC03", "Estudiantes");

            List<PerfilAccion> esperadas = Arrays.asList(
                    crearPerfilAccion(1, perfil, accion1),
                    crearPerfilAccion(2, perfil, accion2)
            );
            when(seguridadService.listarAccionesPorPerfil(2)).thenReturn(esperadas);

            List<PerfilAccion> resultado = administracionService.listarAccionesPorPerfil(2);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals("ACC02", resultado.get(0).getAccion().getId());
            verify(seguridadService, times(1)).listarAccionesPorPerfil(2);
        }

        @Test
        @DisplayName("Retorna lista vacía cuando el perfil no tiene acciones asignadas")
        void listarAccionesPorPerfil_sinAcciones_retornaVacio() {
            when(seguridadService.listarAccionesPorPerfil(5)).thenReturn(Collections.emptyList());

            List<PerfilAccion> resultado = administracionService.listarAccionesPorPerfil(5);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(seguridadService, times(1)).listarAccionesPorPerfil(5);
        }
    }
}
