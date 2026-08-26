package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        when(typedQuery.setHint(anyString(), any())).thenReturn(typedQuery);
        // Sin id: no dispara la consulta de metadatos del documento electrónico
        when(typedQuery.getResultList()).thenReturn(java.util.Collections.singletonList(new Factura()));

        java.util.List<Factura> resultado = dao.listarTodasLasFacturasDelDia();
        assertEquals(1, resultado.size());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Factura.class));
        // clear al inicio + clear tras la consulta (antes de metadatos sintéticos)
        verify(entityManager, times(2)).clear();
    }

    @Test
    void listarTodasLasFacturasDelDiaConMetadatosDocumento() {
        Factura factura = new Factura();
        factura.setId(10);
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.setHint(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(java.util.Collections.singletonList(factura));

        @SuppressWarnings("rawtypes")
        TypedQuery metaQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString())).thenReturn(metaQuery);
        when(metaQuery.setParameter(anyString(), any())).thenReturn(metaQuery);
        when(metaQuery.setHint(anyString(), any())).thenReturn(metaQuery);
        when(metaQuery.getResultList()).thenReturn(java.util.Collections.singletonList(
                new Object[]{10, 20, "AUTORIZADO", "pdf/key", "xml/key", null, "123"}));

        java.util.List<Factura> resultado = dao.listarTodasLasFacturasDelDia();
        assertEquals(1, resultado.size());
        assertNotNull(resultado.get(0).getDocumentoElectronico());
        assertEquals("AUTORIZADO", resultado.get(0).getDocumentoElectronico().getEstado());
        assertEquals("pdf/key", resultado.get(0).getDocumentoElectronico().getUrlPdf());
    }

    @Test
    void listarTodasLasFacturasDelDiaLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Factura.class))).thenReturn(typedQuery);
        when(typedQuery.setHint(anyString(), any())).thenReturn(typedQuery);
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
