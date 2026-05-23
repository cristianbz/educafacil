package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

class UsuarioDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private UsuarioDaoImpl usuarioDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        usuarioDao = new UsuarioDaoImpl(entityManager, Usuario.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new UsuarioDaoImpl());
    }

    @Test
    void actualizaUsuarioExitosamente() {
        Usuario usuario = new Usuario();
        when(entityManager.merge(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioDao.actualizaUsuario(usuario);
        assertNotNull(resultado);
        verify(entityManager).merge(usuario);
    }

    @Test
    void actualizaUsuarioLanzaSystemException() {
        Usuario usuario = new Usuario();
        when(entityManager.merge(usuario)).thenThrow(new RuntimeException("Error DB"));

        assertThrows(SystemException.class, () -> usuarioDao.actualizaUsuario(usuario));
    }

    @Test
    void agregarUsuarioConIdNuloPersiste() {
        Usuario usuario = new Usuario();

        Usuario resultado = usuarioDao.agregarUsuario(usuario);
        assertNotNull(resultado);
        verify(entityManager).persist(usuario);
    }

    @Test
    void agregarUsuarioConIdExistenteNoPersiste() {
        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);

        Usuario resultado = usuarioDao.agregarUsuario(usuario);
        assertNotNull(resultado);
        verify(entityManager, never()).persist(usuario);
    }

    @Test
    void agregarUsuarioLanzaPersistenceException() {
        Usuario usuario = new Usuario();
        doThrow(new PersistenceException("Error DB")).when(entityManager).persist(usuario);

        assertThrows(SystemException.class, () -> usuarioDao.agregarUsuario(usuario));
    }

    @Test
    void consultarUsuarioExitosamente() {
        Usuario usuario = new Usuario();
        usuario.setUsuaUsuario("cbaez");

        when(entityManager.createNamedQuery(Usuario.BUSCAR_USUARIO_POR_USUARIO)).thenReturn(query);
        when(query.setParameter("usuario", "cbaez")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(usuario);

        Usuario resultado = usuarioDao.consultarUsuario("cbaez");
        assertNotNull(resultado);
        assertEquals("cbaez", resultado.getUsuaUsuario());
    }

    @Test
    void consultarUsuarioConNoResultException() {
        when(entityManager.createNamedQuery(Usuario.BUSCAR_USUARIO_POR_USUARIO)).thenReturn(query);
        when(query.setParameter("usuario", "inexistente")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Usuario resultado = usuarioDao.consultarUsuario("inexistente");
        assertNull(resultado);
    }

    @Test
    void consultarUsuarioLanzaSystemException() {
        when(entityManager.createNamedQuery(Usuario.BUSCAR_USUARIO_POR_USUARIO)).thenReturn(query);
        when(query.setParameter("usuario", "error")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Error inesperado"));

        assertThrows(SystemException.class, () -> usuarioDao.consultarUsuario("error"));
    }

    @Test
    void consultarUsuarioPorDocumentoExitosamente() {
        Usuario usuario = new Usuario();
        when(entityManager.createNamedQuery(Usuario.BUSCAR_USUARIO_POR_NRO_IDENTIFICACION)).thenReturn(query);
        when(query.setParameter("nrodocumento", "1710034065")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(usuario);

        Usuario resultado = usuarioDao.consultarUsuarioPorDocumento("1710034065");
        assertNotNull(resultado);
    }

    @Test
    void consultarUsuarioPorDocumentoConNoResultException() {
        when(entityManager.createNamedQuery(Usuario.BUSCAR_USUARIO_POR_NRO_IDENTIFICACION)).thenReturn(query);
        when(query.setParameter("nrodocumento", "0000000000")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Usuario resultado = usuarioDao.consultarUsuarioPorDocumento("0000000000");
        assertNull(resultado);
    }

    @Test
    void consultarUsuarioPorDocumentoLanzaSystemException() {
        when(entityManager.createNamedQuery(Usuario.BUSCAR_USUARIO_POR_NRO_IDENTIFICACION)).thenReturn(query);
        when(query.setParameter("nrodocumento", "error")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> usuarioDao.consultarUsuarioPorDocumento("error"));
    }

    @Test
    void consultarUsuariosPorIdRolExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("rolId", 1)).thenReturn(query);
        Object[] row = new Object[]{ "1", "Juan", "Pérez" };
        when(query.getResultList()).thenReturn(singletonList(row));

        List<Usuario> resultado = usuarioDao.consultarUsuariosPorIdRol(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getPersona().getPersNombres());
    }

    @Test
    void consultarUsuariosPorIdRolConResultadoVacio() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("rolId", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Usuario> resultado = usuarioDao.consultarUsuariosPorIdRol(1);
        assertNull(resultado);
    }

    @Test
    void consultarUsuariosPorIdRolLanzaSystemException() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("rolId", 1)).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> usuarioDao.consultarUsuariosPorIdRol(1));
    }

    @Test
    void actualizaUsuarioMergeYLanzaException() {
        Usuario usuario = new Usuario();
        when(entityManager.merge(usuario)).thenThrow(new RuntimeException("Fallo merge"));

        assertThrows(SystemException.class, () -> usuarioDao.actualizaUsuario(usuario));
    }
}
