package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

class CodigoSriRetencionDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<CodigoSriRetencion> typedQuery;

    private CodigoSriRetencionDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new CodigoSriRetencionDaoImpl(entityManager, CodigoSriRetencion.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new CodigoSriRetencionDaoImpl());
    }

    @Test
    void listarPorTipoImpuestoExitosamente() {
        when(entityManager.createQuery(anyString(), eq(CodigoSriRetencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("tipo", "1")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(new CodigoSriRetencion(), new CodigoSriRetencion()));

        List<CodigoSriRetencion> resultado = dao.listarPorTipoImpuesto("1");
        assertEquals(2, resultado.size());
    }

    @Test
    void listarPorTipoImpuestoLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(CodigoSriRetencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("tipo", "ERR")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.listarPorTipoImpuesto("ERR"));
    }

    @Test
    void listarPorTipoImpuestoConListaVacia() {
        when(entityManager.createQuery(anyString(), eq(CodigoSriRetencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("tipo", "9")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        List<CodigoSriRetencion> resultado = dao.listarPorTipoImpuesto("9");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorTipoYTextoExitosamente() {
        when(entityManager.createQuery(anyString(), eq(CodigoSriRetencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("tipo", "1")).thenReturn(typedQuery);
        when(typedQuery.setParameter("patron", "%renta%")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(new CodigoSriRetencion()));

        List<CodigoSriRetencion> resultado = dao.buscarPorTipoYTexto("1", "renta");
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorTipoYTextoConQueryMayuscula() {
        when(entityManager.createQuery(anyString(), eq(CodigoSriRetencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("tipo", "2")).thenReturn(typedQuery);
        when(typedQuery.setParameter("patron", "%IVA%")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(new CodigoSriRetencion()));

        List<CodigoSriRetencion> resultado = dao.buscarPorTipoYTexto("2", "IVA");
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorTipoYTextoLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(CodigoSriRetencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("tipo", "X")).thenReturn(typedQuery);
        when(typedQuery.setParameter("patron", "%x%")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.buscarPorTipoYTexto("X", "x"));
    }
}
