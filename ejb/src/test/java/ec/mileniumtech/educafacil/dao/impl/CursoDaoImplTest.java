package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

class CursoDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private CursoDaoImpl cursoDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        cursoDao = new CursoDaoImpl(entityManager, Curso.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new CursoDaoImpl());
    }

    @Test
    void listaCursosExitosamente() {
        when(entityManager.createNamedQuery(Curso.CARGAR_CURSOS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Curso(), new Curso()));

        List<Curso> resultado = cursoDao.listaCursos();
        assertEquals(2, resultado.size());
    }

    @Test
    void listaCursosConNoResultException() {
        when(entityManager.createNamedQuery(Curso.CARGAR_CURSOS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Curso> resultado = cursoDao.listaCursos();
        assertNull(resultado);
    }

    @Test
    void listaCursosLanzaSystemException() {
        when(entityManager.createNamedQuery(Curso.CARGAR_CURSOS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> cursoDao.listaCursos());
    }

    @Test
    void listaOfertaCursosActivosExitosamente() {
        when(entityManager.createNamedQuery(Curso.OFERTA_CURSOS_ACTIVOS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Curso()));

        List<Curso> resultado = cursoDao.listaOfertaCursosActivos();
        assertEquals(1, resultado.size());
    }

    @Test
    void listaOfertaCursosActivosConNoResultException() {
        when(entityManager.createNamedQuery(Curso.OFERTA_CURSOS_ACTIVOS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Curso> resultado = cursoDao.listaOfertaCursosActivos();
        assertNull(resultado);
    }
}
