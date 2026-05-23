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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

class CatalogoItemDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<CatalogoItem> typedQuery;

    private CatalogoItemDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new CatalogoItemDaoImpl(entityManager, CatalogoItem.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new CatalogoItemDaoImpl());
    }

    @Test
    void buscarPorCodigoExitosamente() {
        CatalogoItem item = new CatalogoItem();
        when(entityManager.createQuery(anyString(), eq(CatalogoItem.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("codigo", "ABC")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(item));

        CatalogoItem resultado = dao.buscarPorCodigo("ABC");
        assertNotNull(resultado);
    }

    @Test
    void buscarPorCodigoSinResultados() {
        when(entityManager.createQuery(anyString(), eq(CatalogoItem.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("codigo", "ZZZ")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        CatalogoItem resultado = dao.buscarPorCodigo("ZZZ");
        assertNull(resultado);
    }

    @Test
    void buscarPorCodigoLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(CatalogoItem.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("codigo", "ERR")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.buscarPorCodigo("ERR"));
    }

    @Test
    void buscarPorCodigoConResultadoNulo() {
        when(entityManager.createQuery(anyString(), eq(CatalogoItem.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("codigo", "NULO")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList((CatalogoItem) null));

        CatalogoItem resultado = dao.buscarPorCodigo("NULO");
        assertNull(resultado);
    }
}
