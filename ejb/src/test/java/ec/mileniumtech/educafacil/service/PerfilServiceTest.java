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

import ec.mileniumtech.educafacil.dao.impl.PerfilAccionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PerfilDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;
import jakarta.persistence.EntityManager;

/**
 * Tests unitarios para los métodos de gestión de Perfiles y Acciones
 * en AdministracionService. Usa JUnit 5 + Mockito.
 *
 * @author christian
 */
@DisplayName("AdministracionService — Gestión de Perfiles y Acciones")
class PerfilServiceTest {

    @Mock
    private PerfilDaoImpl perfilDao;

    @Mock
    private PerfilAccionDaoImpl perfilAccionDao;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AdministracionService administracionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // =========================================================
    // Helpers de fábrica
    // =========================================================

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

    // =========================================================
    // Listar Perfiles
    // =========================================================

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
            when(perfilDao.listarTodosPerfiles()).thenReturn(esperados);

            List<Perfil> resultado = administracionService.listarPerfiles();

            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            verify(perfilDao, times(1)).listarTodosPerfiles();
        }

        @Test
        @DisplayName("Retorna lista vacía cuando no hay perfiles")
        void listarPerfiles_retornaListaVacia() {
            when(perfilDao.listarTodosPerfiles()).thenReturn(Collections.emptyList());

            List<Perfil> resultado = administracionService.listarPerfiles();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // =========================================================
    // Guardar Perfil
    // =========================================================

    @Nested
    @DisplayName("guardarPerfil()")
    class GuardarPerfil {

        @Test
        @DisplayName("Llama a guardar() del DAO cuando el id es null (nuevo)")
        void guardarPerfil_nuevo_llamaGuardar() {
            Perfil nuevo = crearPerfil(null, "Auditor", true);
            when(perfilDao.guardar(nuevo)).thenReturn(nuevo);

            administracionService.guardarPerfil(nuevo);

            verify(perfilDao, times(1)).guardar(nuevo);
            verify(perfilDao, never()).actualizar(any());
        }

        @Test
        @DisplayName("Llama a actualizar() del DAO cuando el id tiene valor (edición)")
        void guardarPerfil_edicion_llamaActualizar() {
            Perfil existente = crearPerfil(5, "Auditor", true);
            when(perfilDao.actualizar(existente)).thenReturn(existente);

            administracionService.guardarPerfil(existente);

            verify(perfilDao, times(1)).actualizar(existente);
            verify(perfilDao, never()).guardar(any());
        }
    }

    // =========================================================
    // Eliminación lógica
    // =========================================================

    @Nested
    @DisplayName("eliminarLogicoPerfil()")
    class EliminarLogicoPerfil {

        @Test
        @DisplayName("Pone estado=false y llama a actualizar cuando el perfil existe")
        void eliminarLogicoPerfil_perfilExiste_desactiva() {
            Perfil perfil = crearPerfil(3, "Secretaría", true);
            when(perfilDao.buscarPerfilPorId(3)).thenReturn(perfil);

            administracionService.eliminarLogicoPerfil(3);

            assertFalse(perfil.getEstado(), "El estado debe quedar en false");
            verify(perfilDao, times(1)).actualizar(perfil);
        }

        @Test
        @DisplayName("No lanza excepción ni llama a actualizar cuando el perfil no existe")
        void eliminarLogicoPerfil_perfilNoExiste_noHaceNada() {
            when(perfilDao.buscarPerfilPorId(999)).thenReturn(null);

            assertDoesNotThrow(() -> administracionService.eliminarLogicoPerfil(999));
            verify(perfilDao, never()).actualizar(any());
        }
    }

    // =========================================================
    // Listar Acciones
    // =========================================================

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
            when(perfilAccionDao.listarTodasAcciones()).thenReturn(esperadas);

            List<Accion> resultado = administracionService.listarAcciones();

            assertNotNull(resultado);
            assertEquals(3, resultado.size());
        }
    }

    // =========================================================
    // Asignar Acción a Perfil
    // =========================================================

    @Nested
    @DisplayName("asignarAccionAPerfil()")
    class AsignarAccionAPerfil {

        @Test
        @DisplayName("Crea nueva PerfilAccion cuando no existe asociación previa")
        void asignarAccion_sinAsociacionPrevia_creaRegistro() {
            Perfil perfil = crearPerfil(1, "Administrador", true);
            Accion accion = crearAccion("ACC01", "Dashboard");

            when(perfilAccionDao.buscarPerfilAccion(1, "ACC01")).thenReturn(null);
            when(perfilDao.buscarPerfilPorId(1)).thenReturn(perfil);
            when(perfilAccionDao.getEntityManager()).thenReturn(entityManager);
            when(entityManager.find(Accion.class, "ACC01")).thenReturn(accion);
            when(perfilAccionDao.siguienteId()).thenReturn(10);

            administracionService.asignarAccionAPerfil(1, "ACC01");

            verify(perfilAccionDao, times(1)).guardar(any(PerfilAccion.class));
            verify(perfilAccionDao, never()).actualizar(any());
        }

        @Test
        @DisplayName("Reactiva asociación existente cuando estaba inactiva")
        void asignarAccion_asociacionExistente_reactiva() {
            Perfil perfil = crearPerfil(1, "Administrador", true);
            Accion accion = crearAccion("ACC01", "Dashboard");
            PerfilAccion existente = crearPerfilAccion(5, perfil, accion);
            existente.setEstado(false);

            when(perfilAccionDao.buscarPerfilAccion(1, "ACC01")).thenReturn(existente);

            administracionService.asignarAccionAPerfil(1, "ACC01");

            assertTrue(existente.getEstado(), "La asociación debe quedar activa");
            verify(perfilAccionDao, times(1)).actualizar(existente);
            verify(perfilAccionDao, never()).guardar(any());
        }

        @Test
        @DisplayName("No persiste si el perfil no existe en la base de datos")
        void asignarAccion_perfilInexistente_noGuarda() {
            when(perfilAccionDao.buscarPerfilAccion(999, "ACC01")).thenReturn(null);
            when(perfilDao.buscarPerfilPorId(999)).thenReturn(null);
            when(perfilAccionDao.getEntityManager()).thenReturn(entityManager);
            when(entityManager.find(Accion.class, "ACC01")).thenReturn(crearAccion("ACC01", "Dashboard"));

            assertDoesNotThrow(() -> administracionService.asignarAccionAPerfil(999, "ACC01"));
            verify(perfilAccionDao, never()).guardar(any());
        }
    }

    // =========================================================
    // Quitar Acción de Perfil
    // =========================================================

    @Nested
    @DisplayName("quitarAccionDePerfil()")
    class QuitarAccionDePerfil {

        @Test
        @DisplayName("Llama a eliminarPorPerfilYAccion del DAO con los parámetros correctos")
        void quitarAccion_llamaDao() {
            doNothing().when(perfilAccionDao).eliminarPorPerfilYAccion(1, "ACC01");

            administracionService.quitarAccionDePerfil(1, "ACC01");

            verify(perfilAccionDao, times(1)).eliminarPorPerfilYAccion(1, "ACC01");
        }
    }

    // =========================================================
    // Listar Acciones por Perfil
    // =========================================================

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
            when(perfilAccionDao.listarAccionesPorPerfil(2)).thenReturn(esperadas);

            List<PerfilAccion> resultado = administracionService.listarAccionesPorPerfil(2);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals("ACC02", resultado.get(0).getAccion().getId());
        }

        @Test
        @DisplayName("Retorna lista vacía cuando el perfil no tiene acciones asignadas")
        void listarAccionesPorPerfil_sinAcciones_retornaVacio() {
            when(perfilAccionDao.listarAccionesPorPerfil(5)).thenReturn(Collections.emptyList());

            List<PerfilAccion> resultado = administracionService.listarAccionesPorPerfil(5);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }
}
