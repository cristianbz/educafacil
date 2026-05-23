package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

class CampaniaDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private CampaniaDaoImpl campaniaDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        campaniaDao = new CampaniaDaoImpl(entityManager, Campania.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new CampaniaDaoImpl());
    }

    @Test
    void listaCampaniasExitosamente() {
        when(entityManager.createNamedQuery(Campania.CAMPANIAS_ACTIVAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Campania()));

        List<Campania> resultado = campaniaDao.listaCampanias();
        assertEquals(1, resultado.size());
    }

    @Test
    void listaCampaniasConNoResultException() {
        when(entityManager.createNamedQuery(Campania.CAMPANIAS_ACTIVAS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Campania> resultado = campaniaDao.listaCampanias();
        assertNull(resultado);
    }

    @Test
    void listaCampaniasLanzaSystemException() {
        when(entityManager.createNamedQuery(Campania.CAMPANIAS_ACTIVAS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> campaniaDao.listaCampanias());
    }

    @Test
    void listaCampaniasporCursoExitosamente() {
        when(entityManager.createNamedQuery(Campania.CAMPANIA_CURSO_ACTIVAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Campania(), new Campania()));

        List<Campania> resultado = campaniaDao.listaCampaniasporCurso();
        assertEquals(2, resultado.size());
    }

    @Test
    void agregarActualizarCampaniaPersist() {
        Campania campania = new Campania();
        when(campania.getCampId()).thenReturn(null);

        campaniaDao.agregarActualizarCampania(campania);
        verify(entityManager).persist(campania);
        verify(entityManager, never()).merge(campania);
    }

    @Test
    void agregarActualizarCampaniaMerge() {
        Campania campania = mock(Campania.class);
        when(campania.getCampId()).thenReturn(1);

        campaniaDao.agregarActualizarCampania(campania);
        verify(entityManager).merge(campania);
        verify(entityManager, never()).persist(campania);
    }

    @Test
    void agregarActualizarCampaniaLanzaPersistenceException() {
        Campania campania = new Campania();
        doThrow(new PersistenceException("Error DB")).when(entityManager).persist(campania);

        assertThrows(SystemException.class, () -> campaniaDao.agregarActualizarCampania(campania));
    }

    @Test
    void listaTodasCampaniasExitosamente() {
        when(entityManager.createNamedQuery(Campania.CAMPANIAS_TODAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Campania()));

        List<Campania> resultado = campaniaDao.listaTodasCampanias();
        assertEquals(1, resultado.size());
    }

    @Test
    void campaniaCursoExitosamente() {
        Campania campania = new Campania();
        when(entityManager.createNamedQuery(Campania.CAMPANIA_CURSO)).thenReturn(query);
        when(query.setParameter("curso", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(campania);

        Campania resultado = campaniaDao.campaniaCurso(1);
        assertNotNull(resultado);
    }

    @Test
    void campaniaCursoConNoResultException() {
        when(entityManager.createNamedQuery(Campania.CAMPANIA_CURSO)).thenReturn(query);
        when(query.setParameter("curso", 999)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Campania resultado = campaniaDao.campaniaCurso(999);
        assertNull(resultado);
    }

    @Test
    void totalGastoCampanias() {
        Query nativeQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenReturn(Arrays.asList(new Object[]{new BigDecimal("1000.50")}));

        BigDecimal resultado = campaniaDao.totalGastoCampanias();
        assertEquals(new BigDecimal("1000.50"), resultado);
    }

    @Test
    void totalGastoCampaniasSinResultados() {
        Query nativeQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenReturn(Collections.emptyList());

        BigDecimal resultado = campaniaDao.totalGastoCampanias();
        assertEquals(BigDecimal.ZERO, resultado);
    }
}
