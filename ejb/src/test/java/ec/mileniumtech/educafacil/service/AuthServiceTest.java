package ec.mileniumtech.educafacil.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import jakarta.persistence.EntityManager;

class AuthServiceTest {

    @Mock
    private UsuarioDaoImpl usuarioDao;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void autenticarConCredencialesValidas() {
        String password = "miclave";
        String passwordEncriptado = Encriptar.encriptarSHA512(password);

        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuaUsuario("cbaez");
        usuario.setUsuaClave(passwordEncriptado);
        usuario.setUsuaEstado(true);

        when(usuarioDao.consultarUsuarioPorDocumento("1710034065")).thenReturn(usuario);

        Usuario resultado = authService.autenticar("1710034065", password);
        assertNotNull(resultado);
        assertEquals("cbaez", resultado.getUsuaUsuario());
    }

    @Test
    void autenticarConPasswordIncorrecta() {
        String password = "miclave";
        String passwordEncriptado = Encriptar.encriptarSHA512("otraclave");

        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuaClave(passwordEncriptado);
        usuario.setUsuaEstado(true);

        when(usuarioDao.consultarUsuarioPorDocumento("1710034065")).thenReturn(usuario);

        Usuario resultado = authService.autenticar("1710034065", password);
        assertNull(resultado);
    }

    @Test
    void autenticarConUsuarioInactivo() {
        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuaEstado(false);

        when(usuarioDao.consultarUsuarioPorDocumento("1710034065")).thenReturn(usuario);

        Usuario resultado = authService.autenticar("1710034065", "password");
        assertNull(resultado);
    }

    @Test
    void autenticarConUsuarioNoEncontrado() {
        when(usuarioDao.consultarUsuarioPorDocumento("1710034065")).thenReturn(null);

        Usuario resultado = authService.autenticar("1710034065", "password");
        assertNull(resultado);
    }

    @Test
    void obtenerRolesConUsuarioValido() {
        Rol rolAdmin = new Rol();
        rolAdmin.setRolNombre("ADMIN");

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUrolEstado(true);
        usuarioRol.setRol(rolAdmin);

        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuarioRol(Arrays.asList(usuarioRol));

        when(usuarioDao.getEntityManager()).thenReturn(entityManager);
        when(entityManager.find(Usuario.class, 1)).thenReturn(usuario);

        List<String> roles = authService.obtenerRoles(1);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertTrue(roles.contains("ADMIN"));
    }

    @Test
    void obtenerRolesConMultiplesRoles() {
        Rol rolAdmin = new Rol();
        rolAdmin.setRolNombre("ADMIN");
        Rol rolVentas = new Rol();
        rolVentas.setRolNombre("VENTAS");

        UsuarioRol ur1 = new UsuarioRol();
        ur1.setUrolEstado(true);
        ur1.setRol(rolAdmin);

        UsuarioRol ur2 = new UsuarioRol();
        ur2.setUrolEstado(true);
        ur2.setRol(rolVentas);

        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuarioRol(Arrays.asList(ur1, ur2));

        when(usuarioDao.getEntityManager()).thenReturn(entityManager);
        when(entityManager.find(Usuario.class, 1)).thenReturn(usuario);

        List<String> roles = authService.obtenerRoles(1);
        assertEquals(2, roles.size());
        assertTrue(roles.containsAll(Arrays.asList("ADMIN", "VENTAS")));
    }

    @Test
    void obtenerRolesConRolInactivoNoIncluido() {
        Rol rolAdmin = new Rol();
        rolAdmin.setRolNombre("ADMIN");
        Rol rolVentas = new Rol();
        rolVentas.setRolNombre("VENTAS");

        UsuarioRol ur1 = new UsuarioRol();
        ur1.setUrolEstado(true);
        ur1.setRol(rolAdmin);

        UsuarioRol ur2 = new UsuarioRol();
        ur2.setUrolEstado(false);
        ur2.setRol(rolVentas);

        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuarioRol(Arrays.asList(ur1, ur2));

        when(usuarioDao.getEntityManager()).thenReturn(entityManager);
        when(entityManager.find(Usuario.class, 1)).thenReturn(usuario);

        List<String> roles = authService.obtenerRoles(1);
        assertEquals(1, roles.size());
        assertTrue(roles.contains("ADMIN"));
    }

    @Test
    void obtenerRolesConUsuarioSinRoles() {
        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuarioRol(new ArrayList<>());

        when(usuarioDao.getEntityManager()).thenReturn(entityManager);
        when(entityManager.find(Usuario.class, 1)).thenReturn(usuario);

        List<String> roles = authService.obtenerRoles(1);
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void obtenerRolesConUsuarioNulo() {
        when(usuarioDao.getEntityManager()).thenReturn(entityManager);
        when(entityManager.find(Usuario.class, 999)).thenReturn(null);

        List<String> roles = authService.obtenerRoles(999);
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void obtenerRolesConUsuarioRolNulo() {
        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuarioRol(null);

        when(usuarioDao.getEntityManager()).thenReturn(entityManager);
        when(entityManager.find(Usuario.class, 1)).thenReturn(usuario);

        List<String> roles = authService.obtenerRoles(1);
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }
}
