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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CategoriaRespuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleEvaluaCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentacionProveedor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

class AdditionalDaosTest {

    @Mock protected EntityManager entityManager;
    @Mock protected Query query;

    private CapacitacionDaoImpl capacitacionDao;
    private CategoriaRespuestaDaoImpl categoriaRespuestaDao;
    private ConfiguracionesDaoImpl configuracionesDao;
    private DetalleEvaluaCursoDaoImpl detalleEvaluaCursoDao;
    private DetalleSeguimientoDaoImpl detalleSeguimientoDao;
    private DocumentacionProveedorDaoImpl documentacionProveedorDao;
    private EmpresaDaoImpl empresaDao;
    private EmpresaMatrizDaoImpl empresaMatrizDao;
    private EspecialidadDaoImpl especialidadDao;
    private EvaluacionCursoDaoImpl evaluacionCursoDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        capacitacionDao = new CapacitacionDaoImpl(entityManager, Capacitacion.class);
        categoriaRespuestaDao = new CategoriaRespuestaDaoImpl(entityManager, CategoriaRespuesta.class);
        configuracionesDao = new ConfiguracionesDaoImpl(entityManager, Configuraciones.class);
        detalleEvaluaCursoDao = new DetalleEvaluaCursoDaoImpl(entityManager, DetalleEvaluaCurso.class);
        detalleSeguimientoDao = new DetalleSeguimientoDaoImpl(entityManager, DetalleSeguimiento.class);
        documentacionProveedorDao = new DocumentacionProveedorDaoImpl(entityManager, DocumentacionProveedor.class);
        empresaDao = new EmpresaDaoImpl(entityManager, Empresa.class);
        empresaMatrizDao = new EmpresaMatrizDaoImpl(entityManager, EmpresaMatriz.class);
        especialidadDao = new EspecialidadDaoImpl(entityManager, Especialidad.class);
        evaluacionCursoDao = new EvaluacionCursoDaoImpl(entityManager, EvaluacionCurso.class);
    }

    // ========== CapacitacionDaoImpl ==========

    @Test
    void capacitacionConstructor() {
        assertDoesNotThrow(() -> new CapacitacionDaoImpl());
    }

    @Test
    void capacitacionAgregarPersist() {
        Capacitacion c = new Capacitacion();
        c.setCapaId(0);
        capacitacionDao.agregarActualizarCapacitacion(c);
        verify(entityManager).persist(c);
    }

    @Test
    void capacitacionAgregarMerge() {
        Capacitacion c = mock(Capacitacion.class);
        when(c.getCapaId()).thenReturn(1);
        capacitacionDao.agregarActualizarCapacitacion(c);
        verify(entityManager).merge(c);
    }

    @Test
    void capacitacionAgregarLanzaPersistenceException() {
        Capacitacion c = new Capacitacion();
        doThrow(new PersistenceException("Error")).when(entityManager).persist(c);
        assertThrows(SystemException.class, () -> capacitacionDao.agregarActualizarCapacitacion(c));
    }

    @Test
    void capacitacionListaCapacitacionesExitosamente() {
        when(entityManager.createNamedQuery(Capacitacion.LISTADO_CAPACITACIONES)).thenReturn(query);
        when(query.setParameter("codigoInstructor", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Capacitacion()));

        List<Capacitacion> resultado = capacitacionDao.listaCapacitaciones(1);
        assertEquals(1, resultado.size());
    }

    // ========== ConfiguracionesDaoImpl ==========

    @Test
    void configuracionesConstructor() {
        assertDoesNotThrow(() -> new ConfiguracionesDaoImpl());
    }

    @Test
    void configuracionesListaExitosamente() {
        when(entityManager.createNamedQuery(Configuraciones.LISTA_CONFIGURACIONES)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Configuraciones()));

        List<Configuraciones> resultado = configuracionesDao.listaConfiguraciones();
        assertEquals(1, resultado.size());
    }

    // ========== DetalleEvaluaCursoDaoImpl ==========

    @Test
    void detalleEvaluaCursoConstructor() {
        assertDoesNotThrow(() -> new DetalleEvaluaCursoDaoImpl());
    }

    @Test
    void detalleEvaluaCursoListaExitosamente() {
        when(entityManager.createNamedQuery(DetalleEvaluaCurso.CARGAR_DETALLE_EVALUACION)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new DetalleEvaluaCurso()));

        List<DetalleEvaluaCurso> resultado = detalleEvaluaCursoDao.listaDeDetallesDeEvaluacionDeCursos();
        assertEquals(1, resultado.size());
    }

    @Test
    void detalleEvaluaCursoGuardarPersist() {
        DetalleEvaluaCurso d = new DetalleEvaluaCurso();
        detalleEvaluaCursoDao.guardarEncuesta(d);
        verify(entityManager).persist(d);
    }

    @Test
    void detalleEvaluaCursoGuardarMerge() {
        DetalleEvaluaCurso d = mock(DetalleEvaluaCurso.class);
        when(d.getDevcId()).thenReturn(1);
        detalleEvaluaCursoDao.guardarEncuesta(d);
        verify(entityManager).merge(d);
    }

    // ========== DetalleSeguimientoDaoImpl ==========

    @Test
    void detalleSeguimientoConstructor() {
        assertDoesNotThrow(() -> new DetalleSeguimientoDaoImpl());
    }

    @Test
    void detalleSeguimientoAgregarPersist() {
        DetalleSeguimiento d = new DetalleSeguimiento();
        d.setDsegId(0);
        detalleSeguimientoDao.agregarDetalle(d);
        verify(entityManager).persist(d);
    }

    @Test
    void detalleSeguimientoAgregarMerge() {
        DetalleSeguimiento d = mock(DetalleSeguimiento.class);
        when(d.getDsegId()).thenReturn(1);
        detalleSeguimientoDao.agregarDetalle(d);
        verify(entityManager).merge(d);
    }

    @Test
    void detalleSeguimientoListaExitosamente() {
        when(entityManager.createNamedQuery(DetalleSeguimiento.LISTA_DETALLE)).thenReturn(query);
        when(query.setParameter("seguimiento", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new DetalleSeguimiento()));

        List<DetalleSeguimiento> resultado = detalleSeguimientoDao.listaDetalle(1);
        assertEquals(1, resultado.size());
    }

    // ========== DocumentacionProveedorDaoImpl ==========

    @Test
    void documentacionProveedorConstructor() {
        assertDoesNotThrow(() -> new DocumentacionProveedorDaoImpl());
    }

    @Test
    void documentacionProveedorAgregarPersist() {
        DocumentacionProveedor d = new DocumentacionProveedor();
        d.setDocpId(0);
        documentacionProveedorDao.agregarActualizarDocumentacionProveedor(d);
        verify(entityManager).persist(d);
    }

    @Test
    void documentacionProveedorBuscarPorProveedorExitosamente() {
        when(entityManager.createNamedQuery(DocumentacionProveedor.DOCUMENTACION_POR_PROVEEDOR)).thenReturn(query);
        when(query.setParameter("codigoProveedor", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new DocumentacionProveedor());

        DocumentacionProveedor resultado = documentacionProveedorDao.buscarDocumentacionPorProveedor(1);
        assertNotNull(resultado);
    }

    @Test
    void documentacionProveedorBuscarPorProveedorNoResult() {
        when(entityManager.createNamedQuery(DocumentacionProveedor.DOCUMENTACION_POR_PROVEEDOR)).thenReturn(query);
        when(query.setParameter("codigoProveedor", 999)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        DocumentacionProveedor resultado = documentacionProveedorDao.buscarDocumentacionPorProveedor(999);
        assertNull(resultado);
    }

    // ========== EmpresaDaoImpl ==========

    @Test
    void empresaConstructor() {
        assertDoesNotThrow(() -> new EmpresaDaoImpl());
    }

    @Test
    void empresaListaExitosamente() {
        when(entityManager.createNamedQuery(Empresa.EMPRESAS_ACTIVAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Empresa()));

        List<Empresa> resultado = empresaDao.listaEmpresas();
        assertEquals(1, resultado.size());
    }

    @Test
    void empresaAgregarPersist() {
        Empresa e = new Empresa();
        e.setEmprId(0);
        empresaDao.agregarEmpresa(e);
        verify(entityManager).persist(e);
    }

    @Test
    void empresaAgregarMerge() {
        Empresa e = mock(Empresa.class);
        when(e.getEmprId()).thenReturn(1);
        empresaDao.agregarEmpresa(e);
        verify(entityManager).merge(e);
    }

    // ========== EmpresaMatrizDaoImpl ==========

    @Test
    void empresaMatrizConstructor() {
        assertDoesNotThrow(() -> new EmpresaMatrizDaoImpl());
    }

    @Test
    void empresaMatrizListaExitosamente() {
        when(entityManager.createNamedQuery(EmpresaMatriz.EMPRESAMATRIZ_ACTIVAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new EmpresaMatriz()));

        List<EmpresaMatriz> resultado = empresaMatrizDao.listaEmpresas();
        assertEquals(1, resultado.size());
    }

    @Test
    void empresaMatrizListaConNoResult() {
        when(entityManager.createNamedQuery(EmpresaMatriz.EMPRESAMATRIZ_ACTIVAS)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<EmpresaMatriz> resultado = empresaMatrizDao.listaEmpresas();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ========== EspecialidadDaoImpl ==========

    @Test
    void especialidadConstructor() {
        assertDoesNotThrow(() -> new EspecialidadDaoImpl());
    }

    @Test
    void especialidadListaExitosamente() {
        when(entityManager.createNamedQuery(Especialidad.LISTA_DE_ESPECIALIDAD)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Especialidad(), new Especialidad()));

        List<Especialidad> resultado = especialidadDao.listaDeEspecialidades();
        assertEquals(2, resultado.size());
    }

    // ========== EvaluacionCursoDaoImpl ==========

    @Test
    void evaluacionCursoConstructor() {
        assertDoesNotThrow(() -> new EvaluacionCursoDaoImpl());
    }

    @Test
    void evaluacionCursoListaExitosamente() {
        when(entityManager.createNamedQuery(EvaluacionCurso.CARGAR_EVALUACION_CURSO)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new EvaluacionCurso()));

        List<EvaluacionCurso> resultado = evaluacionCursoDao.listaDeEvaluacionesDeCurso();
        assertEquals(1, resultado.size());
    }

    @Test
    void evaluacionCursoListaPorCursoExitosamente() {
        when(entityManager.createNamedQuery(EvaluacionCurso.CARGAR_ENCUESTAS_POR_CURSO_OBJETOEVALUACION)).thenReturn(query);
        when(query.setParameter("codigo", 1)).thenReturn(query);
        when(query.setParameter("codigoobj", 2)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new EvaluacionCurso()));

        List<EvaluacionCurso> resultado = evaluacionCursoDao.listaDeEvaluacionesPorCurso(1, 2);
        assertEquals(1, resultado.size());
    }

    @Test
    void evaluacionCursoListaActivasExitosamente() {
        when(entityManager.createNamedQuery(EvaluacionCurso.CARGAR_ENCUESTAS_POR_CURSO_ACTIVO)).thenReturn(query);
        when(query.setParameter("codigoOferta", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new EvaluacionCurso()));

        List<EvaluacionCurso> resultado = evaluacionCursoDao.listaDeEvaluacionesDeCursoActivas(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void evaluacionCursoAgregarPersist() {
        EvaluacionCurso e = new EvaluacionCurso();
        evaluacionCursoDao.agregarEvaluacionCurso(e);
        verify(entityManager).persist(e);
    }

    @Test
    void evaluacionCursoAgregarMerge() {
        EvaluacionCurso e = mock(EvaluacionCurso.class);
        when(e.getEvcuId()).thenReturn(1);
        evaluacionCursoDao.agregarEvaluacionCurso(e);
        verify(entityManager).merge(e);
    }

    @Test
    void evaluacionCursoAgregarLanzaPersistenceException() {
        EvaluacionCurso e = new EvaluacionCurso();
        doThrow(new PersistenceException("Error")).when(entityManager).persist(e);
        assertThrows(SystemException.class, () -> evaluacionCursoDao.agregarEvaluacionCurso(e));
    }
}
