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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

class MatriculaDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private MatriculaDaoImpl matriculaDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        matriculaDao = new MatriculaDaoImpl(entityManager, Matricula.class);
    }

    @Test
    void constructorPorDefecto() {
        assertDoesNotThrow(() -> new MatriculaDaoImpl());
    }

    @Test
    void listaMatriculasAlumnoExitosamente() {
        when(entityManager.createNamedQuery(Matricula.INSCRIPCION_MATRICULA_CULMINACION_ALUMNO)).thenReturn(query);
        when(query.setParameter("codigoEstado", "INSMAT02")).thenReturn(query);
        when(query.setParameter("codigoPersona", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula()));

        List<Matricula> resultado = matriculaDao.listaMatriculasAlumno(1, "INSMAT02");
        assertEquals(1, resultado.size());
    }

    @Test
    void listaMatriculasAlumnoConNoResultException() {
        when(entityManager.createNamedQuery(Matricula.INSCRIPCION_MATRICULA_CULMINACION_ALUMNO)).thenReturn(query);
        when(query.setParameter("codigoEstado", "X")).thenReturn(query);
        when(query.setParameter("codigoPersona", 999)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Matricula> resultado = matriculaDao.listaMatriculasAlumno(999, "X");
        assertNull(resultado);
    }

    @Test
    void listaMatriculasAlumnoLanzaSystemException() {
        when(entityManager.createNamedQuery(Matricula.INSCRIPCION_MATRICULA_CULMINACION_ALUMNO)).thenReturn(query);
        when(query.setParameter("codigoEstado", "ERR")).thenReturn(query);
        when(query.setParameter("codigoPersona", 0)).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Error"));

        assertThrows(SystemException.class, () -> matriculaDao.listaMatriculasAlumno(0, "ERR"));
    }

    @Test
    void listaMatriculasInscripcionExitosamente() {
        Date fecha = new Date();
        when(entityManager.createNamedQuery(Matricula.BUSCAR_INSCRIPCION)).thenReturn(query);
        when(query.setParameter("codigoEstado", "INSMAT01")).thenReturn(query);
        when(query.setParameter("fechaInicio", fecha)).thenReturn(query);
        when(query.setParameter("fechaFin", fecha)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula()));

        List<Matricula> resultado = matriculaDao.listaMatriculasInscripcion("INSMAT01", fecha, fecha);
        assertEquals(1, resultado.size());
    }

    @Test
    void listaMatriculasInscripcionMatriculadosExitosamente() {
        Date fecha = new Date();
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULA_PORFECHA)).thenReturn(query);
        when(query.setParameter("codigoEstado", "INSMAT02")).thenReturn(query);
        when(query.setParameter("fechaInicio", fecha)).thenReturn(query);
        when(query.setParameter("fechaFin", fecha)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula()));

        List<Matricula> resultado = matriculaDao.listaMatriculasInscripcion("INSMAT02", fecha, fecha);
        assertEquals(1, resultado.size());
    }

    @Test
    void listaMatriculasCursoExitosamente() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_POR_CURSO_ESTADO)).thenReturn(query);
        when(query.setParameter("codigoEstado", "INSMAT02")).thenReturn(query);
        when(query.setParameter("codigoCurso", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula(), new Matricula()));

        List<Matricula> resultado = matriculaDao.listaMatriculasCurso("INSMAT02", 1);
        assertEquals(2, resultado.size());
    }

    @Test
    void listaMatriculadosOEnCursoPorOfertaExitosamente() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULADOS_O_ENCURSO_POR_OFERTA)).thenReturn(query);
        when(query.setParameter("codigoOferta", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula()));

        List<Matricula> resultado = matriculaDao.listaMatriculadosOEnCursoPorOferta(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void listaOportunidadesExitosamente() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_OPORTUNIDADES)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula(), new Matricula()));

        List<Matricula> resultado = matriculaDao.listaOportunidades();
        assertEquals(2, resultado.size());
    }

    @Test
    void listaMatriculasEstudianteExitosamente() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULAS_ALUMNO)).thenReturn(query);
        when(query.setParameter("codigoEstudiante", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula()));

        List<Matricula> resultado = matriculaDao.listaMatriculasEstudiante(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void actualizaMatriculaExitosamente() {
        Matricula matricula = new Matricula();
        matriculaDao.actualizaMatricula(matricula);
        verify(entityManager).merge(matricula);
    }

    @Test
    void actualizaMatriculaLanzaSystemException() {
        Matricula matricula = new Matricula();
        doThrow(new RuntimeException("Error")).when(entityManager).merge(matricula);

        assertThrows(SystemException.class, () -> matriculaDao.actualizaMatricula(matricula));
    }

    @Test
    void existeMatriculaExitosamente() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULA_ESTUDIANTE_CURSO)).thenReturn(query);
        when(query.setParameter("codigoOferta", 1)).thenReturn(query);
        when(query.setParameter("codigoEstudiante", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new Matricula());

        Matricula resultado = matriculaDao.existeMatricula(1, 1);
        assertNotNull(resultado);
    }

    @Test
    void existeMatriculaSinResultado() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULA_ESTUDIANTE_CURSO)).thenReturn(query);
        when(query.setParameter("codigoOferta", 999)).thenReturn(query);
        when(query.setParameter("codigoEstudiante", 999)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Matricula resultado = matriculaDao.existeMatricula(999, 999);
        assertNull(resultado);
    }

    @Test
    void listaMatriculadosPorOfertaCursoExitosamente() {
        Matricula matricula = new Matricula();
        matricula.setPagos(Collections.emptyList());
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULA_POR_OFERTA_CURSO)).thenReturn(query);
        when(query.setParameter("codigoOferta", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(matricula));

        List<Matricula> resultado = matriculaDao.listaMatriculadosPorOfertaCurso(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void listaMatriculadosPorOfertaCursoConNoResultException() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULA_POR_OFERTA_CURSO)).thenReturn(query);
        when(query.setParameter("codigoOferta", 999)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Matricula> resultado = matriculaDao.listaMatriculadosPorOfertaCurso(999);
        assertNull(resultado);
    }

    @Test
    void listaMatriculasEstudianteActivasExitosamente() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULAS_ALUMNO_ACTIVAS)).thenReturn(query);
        when(query.setParameter("codigoEstudiante", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Matricula()));

        List<Matricula> resultado = matriculaDao.listaMatriculasEstudianteActivas(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void listaMatriculasEstudianteActivasConNoResultException() {
        when(entityManager.createNamedQuery(Matricula.BUSCAR_MATRICULAS_ALUMNO_ACTIVAS)).thenReturn(query);
        when(query.setParameter("codigoEstudiante", 999)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Matricula> resultado = matriculaDao.listaMatriculasEstudianteActivas(999);
        assertNull(resultado);
    }

    @Test
    void totalDatosMatriculaInscritos() {
        Query nativeQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenReturn(Arrays.asList(new Object[]{java.math.BigDecimal.valueOf(5)}));

        java.math.BigDecimal resultado = matriculaDao.totalDatosMatricula(1);
        assertEquals(java.math.BigDecimal.valueOf(5), resultado);
    }

    @Test
    void totalDatosMatriculaDesertados() {
        Query nativeQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenReturn(Arrays.asList(new Object[]{java.math.BigDecimal.valueOf(3)}));

        java.math.BigDecimal resultado = matriculaDao.totalDatosMatricula(2);
        assertEquals(java.math.BigDecimal.valueOf(3), resultado);
    }
}
