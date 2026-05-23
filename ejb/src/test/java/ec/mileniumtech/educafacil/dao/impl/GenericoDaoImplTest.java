package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

class GenericoDaoImplTest {

    @Mock
    private EntityManager entityManager;

    private GenericoDaoImpl<Area, Long> dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dao = new GenericoDaoImpl<>(entityManager, Area.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new GenericoDaoImpl<>());
    }

    @Test
    void findByIdCuandoExiste() {
        Area area = new Area();
        area.setAreaId(1);
        when(entityManager.find(Area.class, 1L)).thenReturn(area);

        Optional<Area> resultado = dao.findById(1L);
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getAreaId());
    }

    @Test
    void findByIdCuandoNoExiste() {
        when(entityManager.find(Area.class, 999L)).thenReturn(null);

        Optional<Area> resultado = dao.findById(999L);
        assertFalse(resultado.isPresent());
    }

    @Test
    void guardarExitosamente() {
        Area area = new Area();
        area.setAreaId(1);

        Area resultado = dao.guardar(area);
        assertNotNull(resultado);
        verify(entityManager).persist(area);
    }

    @Test
    void guardarLanzaSystemException() {
        Area area = new Area();
        doThrow(new PersistenceException("Error DB")).when(entityManager).persist(area);

        assertThrows(SystemException.class, () -> dao.guardar(area));
    }

    @Test
    void removerExitosamente() {
        Area area = new Area();
        when(entityManager.merge(area)).thenReturn(area);

        dao.remover(area);
        verify(entityManager).remove(area);
    }

    @Test
    void removerLanzaSystemException() {
        Area area = new Area();
        when(entityManager.merge(area)).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.remover(area));
    }

    @Test
    void detachExitosamente() {
        Area area = new Area();
        dao.detach(area);
        verify(entityManager).detach(area);
    }

    @Test
    void actualizarExitosamente() {
        Area area = new Area();
        when(entityManager.merge(area)).thenReturn(area);

        Area resultado = dao.actualizar(area);
        assertNotNull(resultado);
        verify(entityManager).merge(area);
        verify(entityManager).flush();
    }

    @Test
    void actualizarLanzaSystemException() {
        Area area = new Area();
        when(entityManager.merge(area)).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> dao.actualizar(area));
    }

    @Test
    void validarCadenaNulaConNull() {
        assertTrue(dao.validarCadenaNula(null));
    }

    @Test
    void validarCadenaNulaConVacio() {
        assertTrue(dao.validarCadenaNula(""));
    }

    @Test
    void validarCadenaNulaConTexto() {
        assertFalse(dao.validarCadenaNula("texto"));
    }

    @Test
    void findAllExitosamente() {
        TypedQuery<Area> typedQuery = mock(TypedQuery.class);
        List<Area> areas = Arrays.asList(new Area(), new Area());
        when(entityManager.createQuery(anyString(), eq(Area.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(areas);

        List<Area> resultado = dao.findAll();
        assertEquals(2, resultado.size());
    }

    @Test
    void findAllConListaVacia() {
        TypedQuery<Area> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Area.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Area> resultado = dao.findAll();
        assertTrue(resultado.isEmpty());
    }
}
