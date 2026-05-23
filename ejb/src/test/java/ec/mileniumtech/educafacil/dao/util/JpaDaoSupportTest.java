package ec.mileniumtech.educafacil.dao.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;

class JpaDaoSupportTest {

    @Test
    void resultListOrEmptyConResultados() {
        Query query = mock(Query.class);
        List<String> esperada = Arrays.asList("a", "b", "c");
        when(query.getResultList()).thenReturn(esperada);

        List<String> resultado = JpaDaoSupport.resultListOrEmpty(query);
        assertEquals(esperada, resultado);
    }

    @Test
    void resultListOrEmptyConNull() {
        Query query = mock(Query.class);
        when(query.getResultList()).thenReturn(null);

        List<String> resultado = JpaDaoSupport.resultListOrEmpty(query);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void resultListOrEmptyConNoResultException() {
        Query query = mock(Query.class);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<String> resultado = JpaDaoSupport.resultListOrEmpty(query);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void resultListOrEmptyConListaVacia() {
        Query query = mock(Query.class);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<String> resultado = JpaDaoSupport.resultListOrEmpty(query);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void singleResultOrNullConResultado() {
        Query query = mock(Query.class);
        when(query.getSingleResult()).thenReturn("resultado");

        String resultado = JpaDaoSupport.singleResultOrNull(query);
        assertEquals("resultado", resultado);
    }

    @Test
    void singleResultOrNullConNoResultException() {
        Query query = mock(Query.class);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        String resultado = JpaDaoSupport.singleResultOrNull(query);
        assertNull(resultado);
    }

    @Test
    void singleResultOrNullConNull() {
        Query query = mock(Query.class);
        when(query.getSingleResult()).thenReturn(null);

        String resultado = JpaDaoSupport.singleResultOrNull(query);
        assertNull(resultado);
    }

    @Test
    void throwIfConstraintViolationDuplicateConViolacion() {
        ConstraintViolationException cv = mock(ConstraintViolationException.class);
        PersistenceException pe = new PersistenceException(cv);

        assertThrows(ec.mileniumtech.educafacil.dao.excepciones.BusinessException.class,
                () -> JpaDaoSupport.throwIfConstraintViolationDuplicate(pe));
    }

    @Test
    void throwIfConstraintViolationDuplicateSinViolacion() {
        PersistenceException pe = new PersistenceException(new IllegalArgumentException("otro error"));

        assertDoesNotThrow(() -> JpaDaoSupport.throwIfConstraintViolationDuplicate(pe));
    }

    @Test
    void throwIfConstraintViolationDuplicateConCadenaDeCausas() {
        Exception causaProfunda = new ConstraintViolationException("violación", null);
        Exception nivelMedio = new Exception(causaProfunda);
        PersistenceException pe = new PersistenceException(nivelMedio);

        assertThrows(ec.mileniumtech.educafacil.dao.excepciones.BusinessException.class,
                () -> JpaDaoSupport.throwIfConstraintViolationDuplicate(pe));
    }

    @SuppressWarnings("unchecked")
    @Test
    void resultListOrEmptyConTiposGenericos() {
        Query query = mock(Query.class);
        when(query.getResultList()).thenReturn(Arrays.asList(1, 2, 3));

        List<Integer> resultado = JpaDaoSupport.resultListOrEmpty(query);
        assertEquals(3, resultado.size());
        assertTrue(resultado.contains(2));
    }

    @Test
    void resultListOrEmptyConListaNulaInternamente() {
        Query query = mock(Query.class);
        when(query.getResultList()).thenReturn(null);

        List<Object> resultado = JpaDaoSupport.resultListOrEmpty(query);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
