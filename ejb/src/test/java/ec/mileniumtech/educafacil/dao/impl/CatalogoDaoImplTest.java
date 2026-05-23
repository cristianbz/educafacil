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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

class CatalogoDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private CatalogoDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new CatalogoDaoImpl(entityManager, Catalogo.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new CatalogoDaoImpl());
    }

    @Test
    void catalogosPorTipoExitosamente() {
        when(entityManager.createNamedQuery(Catalogo.BUSCAR_POR_TIPO_CATALOGO)).thenReturn(query);
        when(query.setParameter("tipoCatalogo", "TIPO_A")).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Catalogo(), new Catalogo()));

        List<Catalogo> resultado = dao.catalogosPorTipo("TIPO_A");
        assertEquals(2, resultado.size());
    }

    @Test
    void catalogosPorTipoConNoResultException() {
        when(entityManager.createNamedQuery(Catalogo.BUSCAR_POR_TIPO_CATALOGO)).thenReturn(query);
        when(query.setParameter("tipoCatalogo", "INEXISTENTE")).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Catalogo> resultado = dao.catalogosPorTipo("INEXISTENTE");
        assertNull(resultado);
    }

    @Test
    void catalogosPorTipoLanzaSystemException() {
        when(entityManager.createNamedQuery(Catalogo.BUSCAR_POR_TIPO_CATALOGO)).thenReturn(query);
        when(query.setParameter("tipoCatalogo", "ERR")).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> dao.catalogosPorTipo("ERR"));
    }

    @Test
    void catalogosPorPadreExitosamente() {
        Catalogo padre = new Catalogo();
        when(entityManager.createNamedQuery(Catalogo.BUSCAR_POR_PADRE)).thenReturn(query);
        when(query.setParameter("padre", padre)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Catalogo()));

        List<Catalogo> resultado = dao.catalogosPorPadre(padre);
        assertEquals(1, resultado.size());
    }

    @Test
    void catalogosPorPadreConNoResultException() {
        Catalogo padre = new Catalogo();
        when(entityManager.createNamedQuery(Catalogo.BUSCAR_POR_PADRE)).thenReturn(query);
        when(query.setParameter("padre", padre)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Catalogo> resultado = dao.catalogosPorPadre(padre);
        assertNull(resultado);
    }
}
