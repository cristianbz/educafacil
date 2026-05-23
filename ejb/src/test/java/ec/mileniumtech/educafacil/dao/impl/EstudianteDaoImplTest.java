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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

class EstudianteDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private EstudianteDaoImpl estudianteDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        estudianteDao = new EstudianteDaoImpl(entityManager, Estudiante.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new EstudianteDaoImpl());
    }

    @Test
    void estudiantesPorApellidoExitosamente() {
        when(entityManager.createNamedQuery(Estudiante.BUSCA_POR_APELLIDO)).thenReturn(query);
        when(query.setParameter("apellidos", "%perez%")).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Estudiante(), new Estudiante()));

        List<Estudiante> resultado = estudianteDao.estudiantesPorApellido("PERez");
        assertEquals(2, resultado.size());
    }

    @Test
    void estudiantesPorApellidoConNoResultException() {
        when(entityManager.createNamedQuery(Estudiante.BUSCA_POR_APELLIDO)).thenReturn(query);
        when(query.setParameter("apellidos", "%zzzz%")).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Estudiante> resultado = estudianteDao.estudiantesPorApellido("zzzz");
        assertNull(resultado);
    }

    @Test
    void estudiantesPorApellidoLanzaSystemException() {
        when(entityManager.createNamedQuery(Estudiante.BUSCA_POR_APELLIDO)).thenReturn(query);
        when(query.setParameter("apellidos", "%error%")).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> estudianteDao.estudiantesPorApellido("error"));
    }

    @Test
    void estudiantesPorCedulaExitosamente() {
        when(entityManager.createNamedQuery(Estudiante.BUSCA_POR_CEDULA)).thenReturn(query);
        when(query.setParameter("cedula", "1710034065")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new Estudiante());

        Estudiante resultado = estudianteDao.estudiantesPorCedula("1710034065");
        assertNotNull(resultado);
    }

    @Test
    void estudiantesPorCedulaConNoResultException() {
        when(entityManager.createNamedQuery(Estudiante.BUSCA_POR_CEDULA)).thenReturn(query);
        when(query.setParameter("cedula", "0000000000")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Estudiante resultado = estudianteDao.estudiantesPorCedula("0000000000");
        assertNull(resultado);
    }

    @Test
    void estudiantesPorCedulaLanzaSystemException() {
        when(entityManager.createNamedQuery(Estudiante.BUSCA_POR_CEDULA)).thenReturn(query);
        when(query.setParameter("cedula", "error")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> estudianteDao.estudiantesPorCedula("error"));
    }

    @Test
    void actualizaEstudianteExitosamente() {
        Persona persona = new Persona();
        Estudiante estudiante = new Estudiante();
        estudiante.setPersona(persona);

        when(entityManager.merge(estudiante)).thenReturn(estudiante);
        when(entityManager.merge(persona)).thenReturn(persona);

        estudianteDao.actualizaEstudiante(estudiante);
        verify(entityManager).merge(estudiante);
        verify(entityManager).merge(persona);
    }

    @Test
    void actualizaEstudianteLanzaPersistenceException() {
        Persona persona = new Persona();
        Estudiante estudiante = new Estudiante();
        estudiante.setPersona(persona);

        when(entityManager.merge(estudiante)).thenThrow(new PersistenceException("Error DB"));

        assertThrows(SystemException.class, () -> estudianteDao.actualizaEstudiante(estudiante));
    }

    @Test
    void actualizaEstudiantePersonaMergeLanzaExcepcion() {
        Persona persona = new Persona();
        Estudiante estudiante = new Estudiante();
        estudiante.setPersona(persona);

        when(entityManager.merge(estudiante)).thenReturn(estudiante);
        doThrow(new RuntimeException("Error inesperado")).when(entityManager).merge(persona);

        assertThrows(SystemException.class, () -> estudianteDao.actualizaEstudiante(estudiante));
    }
}
