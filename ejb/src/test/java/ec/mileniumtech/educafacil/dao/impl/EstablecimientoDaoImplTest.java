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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Establecimiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

class EstablecimientoDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Establecimiento> typedQuery;

    private EstablecimientoDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new EstablecimientoDaoImpl(entityManager, Establecimiento.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new EstablecimientoDaoImpl());
    }

    @Test
    void listarEstablecimientosPorEmpresaExitosamente() {
        when(entityManager.createQuery(anyString(), eq(Establecimiento.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("empmId", 1)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(new Establecimiento(), new Establecimiento()));

        List<Establecimiento> resultado = dao.listarEstablecimientosPorEmpresa(1);
        assertEquals(2, resultado.size());
    }

    @Test
    void listarEstablecimientosPorEmpresaConListaVacia() {
        when(entityManager.createQuery(anyString(), eq(Establecimiento.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("empmId", 999)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        List<Establecimiento> resultado = dao.listarEstablecimientosPorEmpresa(999);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarEstablecimientosPorEmpresaLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Establecimiento.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("empmId", 0)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.listarEstablecimientosPorEmpresa(0));
    }
}
