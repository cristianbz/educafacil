package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;

class EgresoDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private EgresoDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new EgresoDaoImpl(entityManager, Egresos.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new EgresoDaoImpl());
    }

    @Test
    void agregarActualizarEgresoPersist() {
        Egresos egreso = new Egresos();
        when(egreso.getEgreId()).thenReturn(null);

        dao.agregarActualizarEgreso(egreso);
        verify(entityManager).persist(egreso);
        verify(entityManager, never()).merge(egreso);
    }

    @Test
    void agregarActualizarEgresoMerge() {
        Egresos egreso = mock(Egresos.class);
        when(egreso.getEgreId()).thenReturn(1);

        dao.agregarActualizarEgreso(egreso);
        verify(entityManager).merge(egreso);
        verify(entityManager, never()).persist(egreso);
    }

    @Test
    void agregarActualizarEgresoLanzaPersistenceException() {
        Egresos egreso = mock(Egresos.class);
        when(egreso.getEgreId()).thenReturn(null);
        doThrow(new PersistenceException("Error DB")).when(entityManager).persist(egreso);

        assertThrows(SystemException.class, () -> dao.agregarActualizarEgreso(egreso));
    }

    @Test
    void listaEgresosExitosamente() {
        when(entityManager.createNamedQuery(Egresos.CARGA_EGRESOS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Egresos()));

        List<Egresos> resultado = dao.listaEgresos();
        assertEquals(1, resultado.size());
    }

    @Test
    void listaEgresosConNoResultException() {
        when(entityManager.createNamedQuery(Egresos.CARGA_EGRESOS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Egresos> resultado = dao.listaEgresos();
        assertNull(resultado);
    }

    @Test
    void listaEgresosFechasExitosamente() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date desde = sdf.parse("2024-01-01");
        Date hasta = sdf.parse("2024-12-31");

        when(entityManager.createNamedQuery(Egresos.CARGA_EGRESOS_POR_FECHA)).thenReturn(query);
        when(query.setParameter("fechauno", desde)).thenReturn(query);
        when(query.setParameter("fechados", hasta)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Egresos(), new Egresos()));

        List<Egresos> resultado = dao.listaEgresosFechas(desde, hasta);
        assertEquals(2, resultado.size());
    }

    @Test
    void buscaEgresosReporteriaExitosamente() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicial = sdf.parse("2024-01-01");
        Date fechaFinal = sdf.parse("2024-12-31");
        Query nativeQuery = mock(Query.class);

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(eq("fechaInicial"), any(Date.class), eq(TemporalType.DATE))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(eq("fechaFinal"), any(Date.class), eq(TemporalType.DATE))).thenReturn(nativeQuery);
        Object[] fila = new Object[]{2024.0, 1.0, java.sql.Date.valueOf("2024-01-15"), 1000.50};
        when(nativeQuery.getResultList()).thenReturn(singletonList(fila));

        List<DtoFlujoDinero> resultado = dao.buscaEgresosReporteria(fechaInicial, fechaFinal);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(2024.0, resultado.get(0).getAnio(), 0.001);
        assertEquals(1.0, resultado.get(0).getMes(), 0.001);
    }

    @Test
    void buscaEgresosReporteriaSinResultados() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicial = sdf.parse("2024-01-01");
        Date fechaFinal = sdf.parse("2024-12-31");
        Query nativeQuery = mock(Query.class);

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(eq("fechaInicial"), any(Date.class), eq(TemporalType.DATE))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(eq("fechaFinal"), any(Date.class), eq(TemporalType.DATE))).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenReturn(Collections.emptyList());

        List<DtoFlujoDinero> resultado = dao.buscaEgresosReporteria(fechaInicial, fechaFinal);
        assertNull(resultado);
    }

    @Test
    void buscaEgresosReporteriaLanzaSystemException() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicial = sdf.parse("2024-01-01");
        Date fechaFinal = sdf.parse("2024-12-31");
        Query nativeQuery = mock(Query.class);

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(eq("fechaInicial"), any(Date.class), eq(TemporalType.DATE))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(eq("fechaFinal"), any(Date.class), eq(TemporalType.DATE))).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenThrow(new RuntimeException("Error DB"));

        assertThrows(SystemException.class, () -> dao.buscaEgresosReporteria(fechaInicial, fechaFinal));
    }
}
