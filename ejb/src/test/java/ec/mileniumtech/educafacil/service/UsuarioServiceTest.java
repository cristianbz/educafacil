package ec.mileniumtech.educafacil.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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

import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioRolDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import jakarta.persistence.EntityManager;

/**
 * Tests unitarios para los métodos de gestión de Usuarios y Roles en AdministracionService.
 *
 * @author christian
 */
@DisplayName("AdministracionService — Gestión de Usuarios y Roles")
class UsuarioServiceTest {

    @Mock
    private UsuarioDaoImpl usuarioDao;

    @Mock
    private UsuarioRolDaoImpl usuarioRolDao;

    @Mock
    private PersonaDaoImpl personaDao;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AdministracionService administracionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private Persona crearPersona(int id, String nombres, String apellidos, String correo, String cedula) {
        Persona p = new Persona();
        p.setPersId(id);
        p.setPersNombres(nombres);
        p.setPersApellidos(apellidos);
        p.setPersCorreoElectronico(correo);
        p.setPersDocumentoIdentidad(cedula);
        return p;
    }

    private Usuario crearUsuario(Integer id, String username, Persona persona, boolean estado) {
        Usuario u = new Usuario();
        u.setUsuaId(id);
        u.setUsuaUsuario(username);
        u.setUsuaClave("contrasena123");
        u.setPersona(persona);
        u.setUsuaEstado(estado);
        return u;
    }

    private Rol crearRol(Integer id, String nombre) {
        Rol r = new Rol();
        r.setRolId(id);
        r.setRolNombre(nombre);
        r.setRolEstado(true);
        return r;
    }

    private UsuarioRol crearUsuarioRol(Integer id, Usuario usuario, Rol rol, boolean estado) {
        UsuarioRol ur = new UsuarioRol();
        ur.setUrolId(id);
        ur.setUsuario(usuario);
        ur.setRol(rol);
        ur.setUrolEstado(estado);
        return ur;
    }

    @Nested
    @DisplayName("listarUsuarios()")
    class ListarUsuarios {

        @Test
        @DisplayName("Retorna todos los usuarios")
        void listarUsuarios_retornaLista() {
            Persona p1 = crearPersona(1, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u1 = crearUsuario(10, "juan@test.com", p1, true);
            when(usuarioDao.listarTodosUsuarios()).thenReturn(Arrays.asList(u1));

            List<Usuario> res = administracionService.listarUsuarios();

            assertNotNull(res);
            assertEquals(1, res.size());
            assertEquals("Perez", res.get(0).getPersona().getPersApellidos());
        }
    }

    @Nested
    @DisplayName("guardarUsuario()")
    class GuardarUsuario {

        @Test
        @DisplayName("Crea nueva persona y nuevo usuario (con clave encriptada)")
        void guardarUsuario_nuevo_encriptaYGuarda() {
            Persona p = crearPersona(0, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u = crearUsuario(null, "juan@test.com", p, true);

            when(personaDao.guardar(p)).thenReturn(p);
            when(usuarioDao.agregarUsuario(u)).thenReturn(u);

            administracionService.guardarUsuario(u);

            verify(personaDao, times(1)).guardar(p);
            verify(usuarioDao, times(1)).agregarUsuario(u);
            assertNotEquals("contrasena123", u.getUsuaClave());
        }

        @Test
        @DisplayName("Actualiza persona y usuario existente (encripta clave si no está encriptada)")
        void guardarUsuario_edicionClaveSimple_encriptaYActualiza() {
            Persona p = crearPersona(1, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u = crearUsuario(10, "juan@test.com", p, true);
            u.setUsuaClave("nuevaClave");

            when(personaDao.actualizar(p)).thenReturn(p);
            when(usuarioDao.actualizar(u)).thenReturn(u);

            administracionService.guardarUsuario(u);

            verify(personaDao, times(1)).actualizar(p);
            verify(usuarioDao, times(1)).actualizar(u);
            assertTrue(u.getUsuaClave().startsWith("$2a$") || u.getUsuaClave().startsWith("$2b$") || u.getUsuaClave().startsWith("$2y$"));
        }
    }

    @Nested
    @DisplayName("eliminarLogicoUsuario()")
    class EliminarLogicoUsuario {

        @Test
        @DisplayName("Cambia el estado a inactivo (false)")
        void eliminarLogicoUsuario_desactiva() {
            Persona p = crearPersona(1, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u = crearUsuario(10, "juan@test.com", p, true);
            when(usuarioDao.getEntityManager()).thenReturn(entityManager);
            when(entityManager.find(Usuario.class, 10)).thenReturn(u);

            administracionService.eliminarLogicoUsuario(10);

            assertFalse(u.isUsuaEstado());
            verify(usuarioDao, times(1)).actualizar(u);
        }
    }

    @Nested
    @DisplayName("listarRolesPorUsuarioActivos()")
    class ListarRolesPorUsuarioActivos {

        @Test
        @DisplayName("Filtra solo las asociaciones activas")
        void listarRoles_retornaSoloActivos() {
            Persona p = crearPersona(1, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u = crearUsuario(10, "juan@test.com", p, true);
            Rol r1 = crearRol(1, "Admin");
            Rol r2 = crearRol(2, "Supervisor");
            UsuarioRol ur1 = crearUsuarioRol(100, u, r1, true);
            UsuarioRol ur2 = crearUsuarioRol(101, u, r2, false);

            when(usuarioRolDao.listaUsuarioRolPorUsuario(10)).thenReturn(Arrays.asList(ur1, ur2));

            List<UsuarioRol> res = administracionService.listarRolesPorUsuarioActivos(10);

            assertNotNull(res);
            assertEquals(1, res.size());
            assertEquals("Admin", res.get(0).getRol().getRolNombre());
        }
    }

    @Nested
    @DisplayName("asignarRolAUsuario()")
    class AsignarRolAUsuario {

        @Test
        @DisplayName("Inserta nueva relacion si no existía")
        void asignarRol_sinRelacionPrevia_inserta() {
            Persona p = crearPersona(1, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u = crearUsuario(10, "juan@test.com", p, true);
            Rol r = crearRol(2, "Supervisor");

            when(usuarioRolDao.listaUsuarioRolPorUsuario(10)).thenReturn(new ArrayList<>());
            when(usuarioDao.getEntityManager()).thenReturn(entityManager);
            when(entityManager.find(Usuario.class, 10)).thenReturn(u);
            when(entityManager.find(Rol.class, 2)).thenReturn(r);

            administracionService.asignarRolAUsuario(10, 2);

            verify(usuarioRolDao, times(1)).agregarUsuarioRol(any(UsuarioRol.class));
            verify(usuarioRolDao, never()).actualizar(any());
        }

        @Test
        @DisplayName("Reactiva relacion existente si estaba inactiva")
        void asignarRol_relacionInactiva_reactiva() {
            Persona p = crearPersona(1, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u = crearUsuario(10, "juan@test.com", p, true);
            Rol r = crearRol(2, "Supervisor");
            UsuarioRol ur = crearUsuarioRol(100, u, r, false);

            when(usuarioRolDao.listaUsuarioRolPorUsuario(10)).thenReturn(Arrays.asList(ur));

            administracionService.asignarRolAUsuario(10, 2);

            assertTrue(ur.getUrolEstado());
            verify(usuarioRolDao, times(1)).actualizar(ur);
            verify(usuarioRolDao, never()).agregarUsuarioRol(any());
        }
    }

    @Nested
    @DisplayName("quitarRolDeUsuario()")
    class QuitarRolDeUsuario {

        @Test
        @DisplayName("Desactiva lógicamente la relación")
        void quitarRol_desactivaRelacion() {
            Persona p = crearPersona(1, "Juan", "Perez", "juan@test.com", "1712345678");
            Usuario u = crearUsuario(10, "juan@test.com", p, true);
            Rol r = crearRol(2, "Supervisor");
            UsuarioRol ur = crearUsuarioRol(100, u, r, true);

            when(usuarioRolDao.listaUsuarioRolPorUsuario(10)).thenReturn(Arrays.asList(ur));

            administracionService.quitarRolDeUsuario(10, 2);

            assertFalse(ur.getUrolEstado());
            verify(usuarioRolDao, times(1)).actualizar(ur);
        }
    }
}
