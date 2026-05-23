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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

class AreaDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private AreaDaoImpl areaDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        areaDao = new AreaDaoImpl(entityManager, Area.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new AreaDaoImpl());
    }

    @Test
    void listaDeAreasExitosamente() {
        when(entityManager.createNamedQuery(Area.LISTA_DE_AREAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Area(), new Area()));

        List<Area> resultado = areaDao.listaDeAreas();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void listaDeAreasConNoResultException() {
        when(entityManager.createNamedQuery(Area.LISTA_DE_AREAS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Area> resultado = areaDao.listaDeAreas();
        assertNull(resultado);
    }

    @Test
    void listaDeAreasLanzaSystemException() {
        when(entityManager.createNamedQuery(Area.LISTA_DE_AREAS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Error inesperado"));

        assertThrows(SystemException.class, () -> areaDao.listaDeAreas());
    }

    @Test
    void listaDeAreasConListaVacia() {
        when(entityManager.createNamedQuery(Area.LISTA_DE_AREAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList());

        List<Area> resultado = areaDao.listaDeAreas();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
