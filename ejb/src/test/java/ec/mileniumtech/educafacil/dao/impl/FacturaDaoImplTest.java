package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

class FacturaDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Factura> typedQuery;

    private FacturaDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new FacturaDaoImpl(entityManager, Factura.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new FacturaDaoImpl());
    }

    @Test
    void buscarFacturaPorIdExitosamente() {
        Factura factura = new Factura();
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", 1)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(factura);

        Factura resultado = dao.buscarFacturaPorId(1);
        assertNotNull(resultado);
    }

    @Test
    void buscarFacturaPorIdLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", 999)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.buscarFacturaPorId(999));
    }

    @Test
    void actualizarFacturaExitosamente() {
        Factura factura = new Factura();
        dao.actualizarFactura(factura);
        verify(entityManager).merge(factura);
    }

    @Test
    void actualizarFacturaLanzaPersistenceException() {
        Factura factura = new Factura();
        doThrow(new PersistenceException("Error DB")).when(entityManager).merge(factura);

        assertThrows(SystemException.class, () -> dao.actualizarFactura(factura));
    }

    @Test
    void listarTodasLasFacturasExitosamente() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(new Factura(), new Factura()));

        java.util.List<Factura> resultado = dao.listarTodasLasFacturas();
        assertEquals(2, resultado.size());
    }
}
