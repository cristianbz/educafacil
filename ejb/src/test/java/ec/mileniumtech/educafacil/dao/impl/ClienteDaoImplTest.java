package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

class ClienteDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Cliente> typedQuery;

    private ClienteDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new ClienteDaoImpl(entityManager, Cliente.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new ClienteDaoImpl());
    }

    @Test
    void buscarPorIdentificacionExitosamente() {
        Cliente cliente = new Cliente();
        when(entityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("identificacion", "1710034065001")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(cliente));

        Cliente resultado = dao.buscarPorIdentificacion("1710034065001");
        assertNotNull(resultado);
    }

    @Test
    void buscarPorIdentificacionSinResultados() {
        when(entityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("identificacion", "0000000000001")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        Cliente resultado = dao.buscarPorIdentificacion("0000000000001");
        assertNull(resultado);
    }

    @Test
    void buscarPorIdentificacionLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("identificacion", "ERROR")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.buscarPorIdentificacion("ERROR"));
    }
}
