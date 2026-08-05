package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Subgraph;
import jakarta.persistence.TypedQuery;

class FacturaDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Factura> typedQuery;

    @Mock
    private EntityGraph<Factura> graph;

    @Mock
    @SuppressWarnings("rawtypes")
    private Subgraph puntoGraph;

    @Mock
    @SuppressWarnings("rawtypes")
    private Subgraph estGraph;

    @Mock
    @SuppressWarnings("rawtypes")
    private Subgraph detalleGraph;

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
        when(entityManager.createEntityGraph(Factura.class)).thenReturn(graph);
        when(graph.addSubgraph("puntoEmision")).thenReturn(puntoGraph);
        when(puntoGraph.addSubgraph("establecimientos")).thenReturn(estGraph);
        when(graph.addSubgraph("detalles")).thenReturn(detalleGraph);
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", 1)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(factura);

        Factura resultado = dao.buscarFacturaPorId(1);
        assertNotNull(resultado);
    }

    @Test
    void buscarFacturaPorIdLanzaPersistenceException() {
        when(entityManager.createEntityGraph(Factura.class)).thenReturn(graph);
        when(graph.addSubgraph("puntoEmision")).thenReturn(puntoGraph);
        when(puntoGraph.addSubgraph("establecimientos")).thenReturn(estGraph);
        when(graph.addSubgraph("detalles")).thenReturn(detalleGraph);
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

    @Test
    void listarTodasLasFacturasLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.listarTodasLasFacturas());
    }

    @Test
    void listarTodasLasFacturasDelDiaExitosamente() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(java.util.Collections.singletonList(new Factura()));

        java.util.List<Factura> resultado = dao.listarTodasLasFacturasDelDia();
        assertEquals(1, resultado.size());
    }

    @Test
    void listarTodasLasFacturasDelDiaLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.listarTodasLasFacturasDelDia());
    }

    @Test
    void buscarFacturasPorFiltrosSinParametros() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(java.util.Collections.singletonList(new Factura()));

        java.util.List<Factura> resultado = dao.buscarFacturasPorFiltros(null, null, null, null, null);
        assertEquals(1, resultado.size());
        verify(typedQuery, never()).setParameter(eq("estadoAutorizacion"), any());
    }

    @Test
    void buscarFacturasPorFiltrosConTodosLosFiltros() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(java.util.Collections.singletonList(new Factura()));

        java.time.LocalDate inicio = java.time.LocalDate.of(2024, 1, 1);
        java.time.LocalDate fin = java.time.LocalDate.of(2024, 12, 31);
        java.util.List<Factura> resultado = dao.buscarFacturasPorFiltros(inicio, fin, "1710034065", "12345", "AUTORIZADO");
        assertEquals(1, resultado.size());
        verify(typedQuery).setParameter("estadoAutorizacion", "AUTORIZADO");
        verify(typedQuery).setParameter("fechaInicio", inicio);
        verify(typedQuery).setParameter("fechaFin", fin);
        verify(typedQuery).setParameter("identificacion", "1710034065");
        verify(typedQuery).setParameter("numeroAutorizacion", "12345");
    }

    @Test
    void buscarFacturasPorFiltrosLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.buscarFacturasPorFiltros(null, null, null, null, null));
    }
}
