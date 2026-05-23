package ec.mileniumtech.educafacil.dao.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.MedioInformacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Proveedor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Seguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.SeguimientoClientes;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Vendedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;

class AdditionalDaosPart2Test {

    @Mock protected EntityManager entityManager;
    @Mock protected Query query;
    @Mock protected TypedQuery typedQuery;

    private InstructorDaoImpl instructorDao;
    private MedioInformacionDaoImpl medioInformacionDao;
    private ObjetoEvaluacionDaoImpl objetoEvaluacionDao;
    private OfertaCapacitacionDaoImpl ofertaCapacitacionDao;
    private OfertaCursosDaoImpl ofertaCursosDao;
    private PagosDaoImpl pagosDao;
    private PersonaDaoImpl personaDao;
    private PreguntaDaoImpl preguntaDao;
    private ProveedorDaoImpl proveedorDao;
    private PuntoEmisionDaoImpl puntoEmisionDao;
    private RespuestasDaoImpl respuestasDao;
    private RetencionDaoImpl retencionDao;
    private SeguimientoClientesDaoImpl seguimientoClientesDao;
    private SeguimientoDaoImpl seguimientoDao;
    private SriformapagoDaoImpl sriformapagoDao;
    private TipoEncuestaDaoImpl tipoEncuestaDao;
    private TipoEncuestaPreguntaDaoImpl tipoEncuestaPreguntaDao;
    private UsuarioRolDaoImpl usuarioRolDao;
    private VendedorDaoImpl vendedorDao;
    private FormacionDaoImpl formacionDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        instructorDao = new InstructorDaoImpl(entityManager, Instructor.class);
        medioInformacionDao = new MedioInformacionDaoImpl(entityManager, MedioInformacionDaoImpl.class);
        objetoEvaluacionDao = new ObjetoEvaluacionDaoImpl(entityManager, ObjetoEvaluacion.class);
        ofertaCapacitacionDao = new OfertaCapacitacionDaoImpl(entityManager, OfertaCapacitacion.class);
        ofertaCursosDao = new OfertaCursosDaoImpl(entityManager, OfertaCursos.class);
        pagosDao = new PagosDaoImpl(entityManager, Pagos.class);
        personaDao = new PersonaDaoImpl(entityManager, Persona.class);
        preguntaDao = new PreguntaDaoImpl(entityManager, Pregunta.class);
        proveedorDao = new ProveedorDaoImpl(entityManager, Proveedor.class);
        puntoEmisionDao = new PuntoEmisionDaoImpl(entityManager, PuntoEmision.class);
        respuestasDao = new RespuestasDaoImpl(entityManager, Respuestas.class);
        retencionDao = new RetencionDaoImpl(entityManager, Retencion.class);
        seguimientoClientesDao = new SeguimientoClientesDaoImpl(entityManager, SeguimientoClientes.class);
        seguimientoDao = new SeguimientoDaoImpl(entityManager, Seguimiento.class);
        sriformapagoDao = new SriformapagoDaoImpl();
        tipoEncuestaDao = new TipoEncuestaDaoImpl(entityManager, TipoEncuesta.class);
        tipoEncuestaPreguntaDao = new TipoEncuestaPreguntaDaoImpl(entityManager, TipoEncuestaPregunta.class);
        usuarioRolDao = new UsuarioRolDaoImpl(entityManager, UsuarioRol.class);
        vendedorDao = new VendedorDaoImpl(entityManager, Vendedor.class);
        formacionDao = new FormacionDaoImpl(entityManager, Formacion.class);
    }

    // ========== InstructorDaoImpl ==========

    @Test
    void instructorConstructor() {
        assertDoesNotThrow(() -> new InstructorDaoImpl());
    }

    @Test
    void instructorListaInstructoresExitosamente() {
        when(entityManager.createNamedQuery(Instructor.LISTADO_INSTRUCTORES)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Instructor()));

        List<Instructor> resultado = instructorDao.listaInstructores();
        assertEquals(1, resultado.size());
    }

    @Test
    void instructorListaInstructoresConNoResult() {
        when(entityManager.createNamedQuery(Instructor.LISTADO_INSTRUCTORES)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Instructor> resultado = instructorDao.listaInstructores();
        assertNull(resultado);
    }

    @Test
    void instructorListaInstructoresLanzaSystemException() {
        when(entityManager.createNamedQuery(Instructor.LISTADO_INSTRUCTORES)).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Falla"));

        assertThrows(SystemException.class, () -> instructorDao.listaInstructores());
    }

    @Test
    void instructorAgregarPersist() {
        Instructor i = mock(Instructor.class);
        Persona p = mock(Persona.class);
        when(p.getPersId()).thenReturn(0);
        when(i.getPersona()).thenReturn(p);
        when(i.getInstId()).thenReturn(0);

        instructorDao.agregarActualizarInstructor(i);

        verify(entityManager).persist(p);
        verify(entityManager).persist(i);
    }

    @Test
    void instructorAgregarMerge() {
        Instructor i = mock(Instructor.class);
        Persona p = mock(Persona.class);
        when(p.getPersId()).thenReturn(1);
        when(i.getPersona()).thenReturn(p);
        when(i.getInstId()).thenReturn(1);

        instructorDao.agregarActualizarInstructor(i);

        verify(entityManager).merge(p);
        verify(entityManager).merge(i);
    }

    @Test
    void instructorAgregarLanzaPersistenceException() {
        Instructor i = mock(Instructor.class);
        Persona p = mock(Persona.class);
        when(p.getPersId()).thenReturn(0);
        when(i.getPersona()).thenReturn(p);
        when(i.getInstId()).thenReturn(0);
        doThrow(new PersistenceException("Error")).when(entityManager).persist(p);

        assertThrows(SystemException.class, () -> instructorDao.agregarActualizarInstructor(i));
    }

    // ========== MedioInformacionDaoImpl ==========

    @Test
    void medioInformacionConstructor() {
        assertDoesNotThrow(() -> new MedioInformacionDaoImpl());
    }

    @Test
    void medioInformacionListaExitosamente() {
        when(entityManager.createNamedQuery(MedioInformacion.LISTADO_MEDIOS_INFORMACION)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new MedioInformacion()));

        List<MedioInformacion> resultado = medioInformacionDao.listaMediosInformacion();
        assertEquals(1, resultado.size());
    }

    @Test
    void medioInformacionListaConNoResult() {
        when(entityManager.createNamedQuery(MedioInformacion.LISTADO_MEDIOS_INFORMACION)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<MedioInformacion> resultado = medioInformacionDao.listaMediosInformacion();
        assertNull(resultado);
    }

    // ========== ObjetoEvaluacionDaoImpl ==========

    @Test
    void objetoEvaluacionConstructor() {
        assertDoesNotThrow(() -> new ObjetoEvaluacionDaoImpl());
    }

    @Test
    void objetoEvaluacionListaExitosamente() {
        when(entityManager.createNamedQuery(ObjetoEvaluacion.CARGAR_OBJETO_EVALUACION)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new ObjetoEvaluacion()));

        List<ObjetoEvaluacion> resultado = objetoEvaluacionDao.listaDeObjetosDeEvaluacion();
        assertEquals(1, resultado.size());
    }

    @Test
    void objetoEvaluacionActualizarPersist() {
        ObjetoEvaluacion o = new ObjetoEvaluacion();
        objetoEvaluacionDao.actualizarObjetoEvaluacion(o);
        verify(entityManager).persist(o);
    }

    @Test
    void objetoEvaluacionActualizarMerge() {
        ObjetoEvaluacion o = mock(ObjetoEvaluacion.class);
        when(o.getObjeId()).thenReturn(1);
        objetoEvaluacionDao.actualizarObjetoEvaluacion(o);
        verify(entityManager).merge(o);
    }

    // ========== OfertaCapacitacionDaoImpl ==========

    @Test
    void ofertaCapacitacionConstructor() {
        assertDoesNotThrow(() -> new OfertaCapacitacionDaoImpl());
    }

    @Test
    void ofertaCapacitacionBuscarExitosamente() {
        when(entityManager.createNamedQuery(OfertaCapacitacion.OFERTA_CAPACITACION)).thenReturn(query);
        when(query.setParameter("area", 1)).thenReturn(query);
        when(query.setParameter("curso", 2)).thenReturn(query);
        when(query.setParameter("especialidad", 3)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new OfertaCapacitacion());

        OfertaCapacitacion resultado = ofertaCapacitacionDao.buscarOfertaCapacitacion(1, 3, 2);
        assertNotNull(resultado);
    }

    @Test
    void ofertaCapacitacionBuscarConNoResult() {
        when(entityManager.createNamedQuery(OfertaCapacitacion.OFERTA_CAPACITACION)).thenReturn(query);
        when(query.setParameter("area", 1)).thenReturn(query);
        when(query.setParameter("curso", 2)).thenReturn(query);
        when(query.setParameter("especialidad", 3)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        OfertaCapacitacion resultado = ofertaCapacitacionDao.buscarOfertaCapacitacion(1, 3, 2);
        assertNull(resultado);
    }

    @Test
    void ofertaCapacitacionListaEspecialidadPorAreaExitosamente() {
        when(entityManager.createNamedQuery(OfertaCapacitacion.LISTA_ESPECIALIDAD_POR_AREA)).thenReturn(query);
        when(query.setParameter("area", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Especialidad()));

        List<Especialidad> resultado = ofertaCapacitacionDao.listaEspecialidadPorArea(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void ofertaCapacitacionListaCursosPorAreaEspecialidadExitosamente() {
        when(entityManager.createNamedQuery(OfertaCapacitacion.LISTA_CURSOS_POR_AREA_ESPECIALIDAD)).thenReturn(query);
        when(query.setParameter("area", 1)).thenReturn(query);
        when(query.setParameter("especialidad", 2)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Curso()));

        List<Curso> resultado = ofertaCapacitacionDao.listaCursosPorAreaEspecilidad(1, 2);
        assertEquals(1, resultado.size());
    }

    @Test
    void ofertaCapacitacionBuscarPorCursoExitosamente() {
        when(entityManager.createNamedQuery(OfertaCapacitacion.BUSCAR_POR_CURSO)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new OfertaCapacitacion());

        OfertaCapacitacion resultado = ofertaCapacitacionDao.buscarPorCurso(1);
        assertNotNull(resultado);
    }

    @Test
    void ofertaCapacitacionListarTodasExitosamente() {
        when(entityManager.createNamedQuery(OfertaCapacitacion.CARGAR_TODAS_OFERTAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new OfertaCapacitacion()));

        List<OfertaCapacitacion> resultado = ofertaCapacitacionDao.listarOfertasCapacitacion();
        assertEquals(1, resultado.size());
    }

    @Test
    void ofertaCapacitacionAgregarMerge() {
        OfertaCapacitacion o = mock(OfertaCapacitacion.class);
        when(o.getOfcaId()).thenReturn(1);
        OfertaCursos oc = new OfertaCursos();

        ofertaCapacitacionDao.agregarOfertaCapacitacion(o, oc);

        verify(entityManager).merge(o);
        verify(entityManager, never()).persist(o);
    }

    @Test
    void ofertaCapacitacionAgregarPersist() {
        OfertaCapacitacion o = mock(OfertaCapacitacion.class);
        when(o.getOfcaId()).thenReturn(null);
        OfertaCursos oc = mock(OfertaCursos.class);

        ofertaCapacitacionDao.agregarOfertaCapacitacion(o, oc);

        verify(entityManager).persist(o);
        verify(oc).setOfertaCapacitacion(o);
        verify(entityManager).persist(oc);
    }

    @Test
    void ofertaCapacitacionAgregarLanzaPersistenceException() {
        OfertaCapacitacion o = new OfertaCapacitacion();
        OfertaCursos oc = new OfertaCursos();
        doThrow(new PersistenceException("Error")).when(entityManager).persist(o);

        assertThrows(SystemException.class, () -> ofertaCapacitacionDao.agregarOfertaCapacitacion(o, oc));
    }

    // ========== OfertaCursosDaoImpl ==========

    @Test
    void ofertaCursosConstructor() {
        assertDoesNotThrow(() -> new OfertaCursosDaoImpl());
    }

    @Test
    void ofertaCursosListaDisponiblesExitosamente() {
        when(entityManager.createNamedQuery(OfertaCursos.OFERTA_CURSOS_DISPONIBLES_POR_CURSO)).thenReturn(query);
        when(query.setParameter("ofertaCapacitacion", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new OfertaCursos()));

        List<OfertaCursos> resultado = ofertaCursosDao.listaCursosDisponibles(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void ofertaCursosAgregarPersist() {
        OfertaCursos o = new OfertaCursos();
        ofertaCursosDao.agregarOfertaCursos(o);
        verify(entityManager).persist(o);
    }

    @Test
    void ofertaCursosAgregarLanzaPersistenceException() {
        OfertaCursos o = new OfertaCursos();
        doThrow(new PersistenceException("Error")).when(entityManager).persist(o);
        assertThrows(SystemException.class, () -> ofertaCursosDao.agregarOfertaCursos(o));
    }

    @Test
    void ofertaCursosEditarExitosamente() {
        OfertaCursos o = new OfertaCursos();
        when(entityManager.merge(o)).thenReturn(o);

        OfertaCursos resultado = ofertaCursosDao.editarOfertaCursos(o);
        assertEquals(o, resultado);
    }

    @Test
    void ofertaCursosEditarLanzaPersistenceException() {
        OfertaCursos o = new OfertaCursos();
        when(entityManager.merge(o)).thenThrow(new PersistenceException("Error"));

        assertThrows(SystemException.class, () -> ofertaCursosDao.editarOfertaCursos(o));
    }

    @Test
    void ofertaCursosListaActivosExitosamente() {
        OfertaCursos ofc = mock(OfertaCursos.class);
        EvaluacionCurso ecActiva = mock(EvaluacionCurso.class);
        when(ecActiva.isEvcuEstado()).thenReturn(true);
        EvaluacionCurso ecInactiva = mock(EvaluacionCurso.class);
        when(ecInactiva.isEvcuEstado()).thenReturn(false);
        List<EvaluacionCurso> evaluaciones = new ArrayList<>(Arrays.asList(ecActiva, ecInactiva));
        when(ofc.getEvaluacionCurso()).thenReturn(evaluaciones);

        when(entityManager.createNamedQuery(OfertaCursos.OFERTA_CURSOS_DISPONIBLES_ACTIVOS)).thenReturn(query);
        OfertaCursos ofc2 = mock(OfertaCursos.class);
        when(ofc2.getEvaluacionCurso()).thenReturn(new ArrayList<>());
        when(query.getResultList()).thenReturn(Arrays.asList(ofc, ofc2));

        List<OfertaCursos> resultado = ofertaCursosDao.listaOfertaCursosActivos();
        assertEquals(2, resultado.size());
    }

    @Test
    void ofertaCursosListaActivosCerradosExitosamente() {
        when(entityManager.createNamedQuery(OfertaCursos.OFERTA_CURSOS_ACTIVOS_CERRADOS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new OfertaCursos()));

        List<OfertaCursos> resultado = ofertaCursosDao.listaOfertaCursosActivosCerrados();
        assertEquals(1, resultado.size());
    }

    @Test
    void ofertaCursosFinalizarCursoActivoExitosamente() {
        OfertaCursos ofc = new OfertaCursos();
        Matricula m1 = mock(Matricula.class);
        Matricula m2 = mock(Matricula.class);
        List<Matricula> matriculas = Arrays.asList(m1, m2);

        ofertaCursosDao.finalizarCursoActivo(ofc, matriculas);

        verify(entityManager).merge(ofc);
        verify(entityManager).merge(m1);
        verify(entityManager).merge(m2);
    }

    @Test
    void ofertaCursosListaPorDefectoExitosamente() {
        when(entityManager.createNamedQuery(OfertaCursos.OFERTA_CURSOS_POR_DEFECTO)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new OfertaCursos()));

        List<OfertaCursos> resultado = ofertaCursosDao.listaOfertaCursosPorDefecto();
        assertEquals(1, resultado.size());
    }

    @Test
    void ofertaCursosListaPorCursoExitosamente() {
        when(entityManager.createNamedQuery(OfertaCursos.OFERTA_CURSOS_POR_CURSO)).thenReturn(query);
        when(query.setParameter("cursoId", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new OfertaCursos()));

        List<OfertaCursos> resultado = ofertaCursosDao.listaOfertaCursosPorCurso(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void ofertaCursosListaPorCursoAnioExitosamente() {
        when(entityManager.createNamedQuery(OfertaCursos.OFERTA_CURSOS_POR_CURSO_ANIO)).thenReturn(query);
        when(query.setParameter("cursoId", 1)).thenReturn(query);
        when(query.setParameter("anioBusqueda", 2024)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new OfertaCursos()));

        List<OfertaCursos> resultado = ofertaCursosDao.listaOfertaCursosPorCursoAnio(1, 2024);
        assertEquals(1, resultado.size());
    }

    // ========== PagosDaoImpl ==========

    @Test
    void pagosConstructor() {
        assertDoesNotThrow(() -> new PagosDaoImpl());
    }

    @Test
    void pagosAgregarPagoPersist() {
        Pagos p = mock(Pagos.class);
        DetallePagos dp = new DetallePagos();
        when(p.getDetallePagos()).thenReturn(Arrays.asList(dp));

        pagosDao.agregarPago(p);

        verify(entityManager).persist(p);
        verify(entityManager).persist(dp);
    }

    @Test
    void pagosAgregarPagoLanzaPersistenceException() {
        Pagos p = mock(Pagos.class);
        when(p.getDetallePagos()).thenReturn(new ArrayList<>());
        doThrow(new PersistenceException("Error")).when(entityManager).persist(p);

        assertThrows(SystemException.class, () -> pagosDao.agregarPago(p));
    }

    @Test
    void pagosActualizarExitosamente() {
        Pagos p = new Pagos();
        pagosDao.actualizarPago(p);
        verify(entityManager).merge(p);
    }

    @Test
    void pagosActualizarLanzaPersistenceException() {
        Pagos p = new Pagos();
        doThrow(new PersistenceException("Error")).when(entityManager).merge(p);

        assertThrows(SystemException.class, () -> pagosDao.actualizarPago(p));
    }

    @Test
    void pagosListarTodosExitosamente() {
        when(entityManager.createQuery("SELECT p FROM Pagos p ORDER BY p.pagoFecha DESC")).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Pagos()));

        List<Pagos> resultado = pagosDao.listarTodosLosPagos();
        assertEquals(1, resultado.size());
    }

    @Test
    void pagosListarTodosLanzaException() {
        when(entityManager.createQuery("SELECT p FROM Pagos p ORDER BY p.pagoFecha DESC")).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Falla"));

        assertThrows(SystemException.class, () -> pagosDao.listarTodosLosPagos());
    }

    @Test
    void pagosBuscaPorMatriculaExitosamente() {
        when(entityManager.createNamedQuery(Pagos.BUSCAR_DETALLEPAGOS)).thenReturn(query);
        when(query.setParameter("codigoMatricula", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new DetallePagos()));

        List<DetallePagos> resultado = pagosDao.buscaPagosPorMatricula(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void pagosBuscaPorMatriculaConNoResult() {
        when(entityManager.createNamedQuery(Pagos.BUSCAR_DETALLEPAGOS)).thenReturn(query);
        when(query.setParameter("codigoMatricula", 1)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<DetallePagos> resultado = pagosDao.buscaPagosPorMatricula(1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void pagosBuscaIngresosReporteriaExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("fechaInicial"), any(Date.class), eq(TemporalType.DATE))).thenReturn(query);
        when(query.setParameter(eq("fechaFinal"), any(Date.class), eq(TemporalType.DATE))).thenReturn(query);
        Object[] fila = new Object[]{2024.0, 1.0, java.sql.Date.valueOf("2024-01-15"), 1000.50};
        when(query.getResultList()).thenReturn(Collections.singletonList(fila));

        assertDoesNotThrow(() -> pagosDao.buscaIngresosReporteria(new Date(), new Date()));
    }

    // ========== PersonaDaoImpl ==========

    @Test
    void personaConstructor() {
        assertDoesNotThrow(() -> new PersonaDaoImpl());
    }

    @Test
    void personaBuscarPorCedulaExitosamente() {
        Persona p = mock(Persona.class);
        when(p.getEstudiantes()).thenReturn(new ArrayList<>());
        when(entityManager.createNamedQuery(Persona.BUSCAR_POR_CEDULA)).thenReturn(query);
        when(query.setParameter("cedula", "123")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(p);

        Persona resultado = personaDao.buscarPersonaPorCedula("123");
        assertNotNull(resultado);
    }

    @Test
    void personaBuscarPorCedulaConNoResult() {
        when(entityManager.createNamedQuery(Persona.BUSCAR_POR_CEDULA)).thenReturn(query);
        when(query.setParameter("cedula", "999")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Persona resultado = personaDao.buscarPersonaPorCedula("999");
        assertNull(resultado);
    }

    @Test
    void personaAgregarExitosamente() {
        Persona p = new Persona();
        personaDao.agregarPersona(p);
        verify(entityManager).persist(p);
    }

    @Test
    void personaAgregarLanzaPersistenceException() {
        Persona p = new Persona();
        doThrow(new PersistenceException("Error")).when(entityManager).persist(p);
        assertThrows(SystemException.class, () -> personaDao.agregarPersona(p));
    }

    @Test
    void personaActualizarPersist() {
        Persona p = new Persona();
        personaDao.actualizarPersona(p);
        verify(entityManager).persist(p);
    }

    @Test
    void personaActualizarMerge() {
        Persona p = mock(Persona.class);
        when(p.getPersId()).thenReturn(1);
        personaDao.actualizarPersona(p);
        verify(entityManager).merge(p);
    }

    @Test
    void personaBuscarPorApellidosExitosamente() {
        when(entityManager.createNamedQuery(Persona.BUSCAR_POR_APELLIDOS)).thenReturn(query);
        when(query.setParameter("apellidos", "Pérez")).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Persona()));

        List<Persona> resultado = personaDao.buscarPersonaPorApellidos("Pérez");
        assertEquals(1, resultado.size());
    }

    @Test
    void personaBuscarPorIdExitosamente() {
        Persona p = mock(Persona.class);
        when(p.getEstudiantes()).thenReturn(new ArrayList<>());
        when(entityManager.createNamedQuery(Persona.BUSCAR_POR_ID)).thenReturn(query);
        when(query.setParameter("id", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(p);

        Persona resultado = personaDao.buscarPersonaPorId(1);
        assertNotNull(resultado);
    }

    @Test
    void personaBuscarPorIdConNoResult() {
        when(entityManager.createNamedQuery(Persona.BUSCAR_POR_ID)).thenReturn(query);
        when(query.setParameter("id", 999)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Persona resultado = personaDao.buscarPersonaPorId(999);
        assertNull(resultado);
    }

    // ========== PreguntaDaoImpl ==========

    @Test
    void preguntaConstructor() {
        assertDoesNotThrow(() -> new PreguntaDaoImpl());
    }

    @Test
    void preguntaListaExitosamente() {
        when(entityManager.createNamedQuery(Pregunta.CARGAR_PREGUNTA)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Pregunta()));

        List<Pregunta> resultado = preguntaDao.listaDePreguntas();
        assertEquals(1, resultado.size());
    }

    @Test
    void preguntaAgregarPersist() {
        Pregunta p = new Pregunta();
        preguntaDao.agregarActualizarPregunta(p);
        verify(entityManager).persist(p);
    }

    @Test
    void preguntaAgregarMerge() {
        Pregunta p = mock(Pregunta.class);
        when(p.getPregId()).thenReturn(1);
        preguntaDao.agregarActualizarPregunta(p);
        verify(entityManager).merge(p);
    }

    @Test
    void preguntaListaPorCategoriaExitosamente() {
        when(entityManager.createNamedQuery(Pregunta.CARGAR_PREGUNTA_POR_CATEGORIA)).thenReturn(query);
        when(query.setParameter("codigo", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Pregunta()));

        List<Pregunta> resultado = preguntaDao.listaPreguntasPorCategoria(1);
        assertEquals(1, resultado.size());
    }

    // ========== ProveedorDaoImpl ==========

    @Test
    void proveedorConstructor() {
        assertDoesNotThrow(() -> new ProveedorDaoImpl());
    }

    @Test
    void proveedorListaExitosamente() {
        when(entityManager.createNamedQuery(Proveedor.LISTA_PROVEEDORES)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Proveedor()));

        List<Proveedor> resultado = proveedorDao.listaProveedores();
        assertEquals(1, resultado.size());
    }

    @Test
    void proveedorAgregarPersist() {
        Proveedor p = new Proveedor();
        proveedorDao.agregarActualizarProveedor(p);
        verify(entityManager).persist(p);
    }

    @Test
    void proveedorAgregarMerge() {
        Proveedor p = mock(Proveedor.class);
        when(p.getProvId()).thenReturn(1);
        proveedorDao.agregarActualizarProveedor(p);
        verify(entityManager).merge(p);
    }

    @Test
    void proveedorValidaExitosamente() {
        when(entityManager.createNamedQuery(Proveedor.RUC_PROVEEDOR)).thenReturn(query);
        when(query.setParameter("ruc", "123")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new Proveedor());

        Proveedor resultado = proveedorDao.validaProveedor("123");
        assertNotNull(resultado);
    }

    // ========== PuntoEmisionDaoImpl ==========

    @Test
    void puntoEmisionConstructor() {
        assertDoesNotThrow(() -> new PuntoEmisionDaoImpl());
    }

    @Test
    void puntoEmisionListaActivosExitosamente() {
        when(entityManager.createQuery("SELECT pe FROM PuntoEmision pe WHERE pe.estado = true", PuntoEmision.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(new PuntoEmision()));

        List<PuntoEmision> resultado = puntoEmisionDao.listarPuntosEmisionActivos();
        assertEquals(1, resultado.size());
    }

    @Test
    void puntoEmisionListaPorEstablecimientoExitosamente() {
        when(entityManager.createQuery("SELECT pe FROM PuntoEmision pe WHERE pe.establecimientos.estaId = :estaId ORDER BY pe.codigo", PuntoEmision.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("estaId", 1)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(new PuntoEmision()));

        List<PuntoEmision> resultado = puntoEmisionDao.listarPuntosEmisionPorEstablecimiento(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void puntoEmisionListaActivosLanzaPersistenceException() {
        when(entityManager.createQuery("SELECT pe FROM PuntoEmision pe WHERE pe.estado = true", PuntoEmision.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenThrow(new PersistenceException("Error"));

        assertThrows(SystemException.class, () -> puntoEmisionDao.listarPuntosEmisionActivos());
    }

    // ========== RespuestasDaoImpl ==========

    @Test
    void respuestasConstructor() {
        assertDoesNotThrow(() -> new RespuestasDaoImpl());
    }

    @Test
    void respuestasListaExitosamente() {
        when(entityManager.createNamedQuery(Respuestas.CARGAR_RESPUESTAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Respuestas()));

        List<Respuestas> resultado = respuestasDao.listaRespuestas();
        assertEquals(1, resultado.size());
    }

    @Test
    void respuestasAgregarPersist() {
        Respuestas r = new Respuestas();
        respuestasDao.agregActualizarRespuestas(r);
        verify(entityManager).persist(r);
    }

    @Test
    void respuestasAgregarMerge() {
        Respuestas r = mock(Respuestas.class);
        when(r.getRespId()).thenReturn(1);
        respuestasDao.agregActualizarRespuestas(r);
        verify(entityManager).merge(r);
    }

    @Test
    void respuestasListaPorCategoriaExitosamente() {
        when(entityManager.createNamedQuery(Respuestas.CARGAR_RESPUESTAS_POR_CATEGORIA)).thenReturn(query);
        when(query.setParameter("codigo", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Respuestas()));

        List<Respuestas> resultado = respuestasDao.listaRespuestasPorCategoria(1);
        assertEquals(1, resultado.size());
    }

    // ========== RetencionDaoImpl ==========

    @Test
    void retencionConstructor() {
        assertDoesNotThrow(() -> new RetencionDaoImpl());
    }

    @Test
    void retencionBuscarPorIdExitosamente() {
        when(entityManager.createQuery(anyString(), eq(Retencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", 1)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new Retencion());

        Retencion resultado = retencionDao.buscarRetencionPorId(1);
        assertNotNull(resultado);
    }

    @Test
    void retencionBuscarPorIdLanzaPersistenceException() {
        when(entityManager.createQuery(anyString(), eq(Retencion.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", 1)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new PersistenceException("Error"));

        assertThrows(SystemException.class, () -> retencionDao.buscarRetencionPorId(1));
    }

    @Test
    void retencionListarTodasExitosamente() {
        when(entityManager.createQuery(anyString(), eq(Retencion.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(new Retencion()));

        List<Retencion> resultado = retencionDao.listarTodas();
        assertEquals(1, resultado.size());
    }

    @Test
    void retencionActualizarExitosamente() {
        Retencion r = new Retencion();
        retencionDao.actualizarRetencion(r);
        verify(entityManager).merge(r);
    }

    // ========== SeguimientoClientesDaoImpl ==========

    @Test
    void seguimientoClientesConstructor() {
        assertDoesNotThrow(() -> new SeguimientoClientesDaoImpl());
    }

    @Test
    void seguimientoClientesAgregarPersist() {
        SeguimientoClientes s = new SeguimientoClientes();
        DetalleSeguimiento d1 = mock(DetalleSeguimiento.class);
        DetalleSeguimiento d2 = mock(DetalleSeguimiento.class);
        List<DetalleSeguimiento> detalles = Arrays.asList(d1, d2);

        seguimientoClientesDao.agregarSeguimiento(s, detalles);

        verify(entityManager).persist(s);
        verify(d1).setSeguimientoClientes(s);
        verify(entityManager).persist(d1);
        verify(d2).setSeguimientoClientes(s);
        verify(entityManager).persist(d2);
    }

    @Test
    void seguimientoClientesAgregarMerge() {
        SeguimientoClientes s = mock(SeguimientoClientes.class);
        when(s.getSegcId()).thenReturn(1);
        DetalleSeguimiento ds1 = new DetalleSeguimiento();
        DetalleSeguimiento ds2 = mock(DetalleSeguimiento.class);
        when(ds2.getDsegId()).thenReturn(1);
        List<DetalleSeguimiento> detalles = Arrays.asList(ds1, ds2);

        seguimientoClientesDao.agregarSeguimiento(s, detalles);

        verify(entityManager).merge(s);
        verify(entityManager).persist(ds1);
        verify(entityManager).merge(ds2);
    }

    @Test
    void seguimientoClientesListaExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new SeguimientoClientes()));

        List<SeguimientoClientes> resultado = seguimientoClientesDao.listaSeguimiento();
        assertEquals(1, resultado.size());
    }

    @Test
    void seguimientoClientesListaVendedorAsignadoExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR_ASIGNADO)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new SeguimientoClientes()));

        List<SeguimientoClientes> resultado = seguimientoClientesDao.listaSeguimientoVendedorAsignado();
        assertEquals(1, resultado.size());
    }

    @Test
    void seguimientoClientesListaCampaniaExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_CAMPANIA)).thenReturn(query);
        when(query.setParameter("campania", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new SeguimientoClientes()));

        List<SeguimientoClientes> resultado = seguimientoClientesDao.listaSeguimientoCampania(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void seguimientoClientesListaCampaniaVendedorExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR)).thenReturn(query);
        when(query.setParameter("campaniaS", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new SeguimientoClientes()));

        List<SeguimientoClientes> resultado = seguimientoClientesDao.listaSeguimientoCampaniaVendedor(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void seguimientoClientesAlcanceCampaniaExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("campId", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn("5");

        BigInteger resultado = seguimientoClientesDao.alcanceCampania(1);
        assertEquals(BigInteger.valueOf(5), resultado);
    }

    @Test
    void seguimientoClientesProspectosCampaniaExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("campId", 1)).thenReturn(query);
        when(query.setParameter("estado", "Interesado")).thenReturn(query);
        when(query.getSingleResult()).thenReturn("3");

        BigInteger resultado = seguimientoClientesDao.prospectosCampania(1, "Interesado");
        assertEquals(BigInteger.valueOf(3), resultado);
    }

    @Test
    void seguimientoClientesListaCampaniaCursoExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_CURSO)).thenReturn(query);
        when(query.setParameter("curso", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new SeguimientoClientes()));

        List<SeguimientoClientes> resultado = seguimientoClientesDao.listaSeguimientoCampaniaCurso(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void seguimientoClientesListaCampaniaFechasExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_FECHAS)).thenReturn(query);
        when(query.setParameter("fechaInicio", new Date(0))).thenReturn(query);
        when(query.setParameter("fechaFin", new Date())).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new SeguimientoClientes()));

        List<SeguimientoClientes> resultado = seguimientoClientesDao.listaSeguimientoCampaniaFechas(new Date(0), new Date());
        assertEquals(1, resultado.size());
    }

    @Test
    void seguimientoClientesActualizarMerge() {
        SeguimientoClientes s = mock(SeguimientoClientes.class);
        when(s.getSegcId()).thenReturn(1);

        seguimientoClientesDao.actualizarSeguimiento(s);

        verify(entityManager).merge(s);
    }

    @Test
    void seguimientoClientesBuscarPorIdExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.BUSCA_SEGUIMIENTO)).thenReturn(query);
        when(query.setParameter("id", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new SeguimientoClientes());

        SeguimientoClientes resultado = seguimientoClientesDao.seguimiento(1);
        assertNotNull(resultado);
    }

    @Test
    void seguimientoClientesValidaNumeroExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.VALIDA_NUMERO)).thenReturn(query);
        when(query.setParameter("telefono", "123")).thenReturn(query);
        when(query.setParameter("curso", 1)).thenReturn(query);
        when(query.setParameter("campania", 2)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new SeguimientoClientes());

        SeguimientoClientes resultado = seguimientoClientesDao.validaNumero("123", 1, 2);
        assertNotNull(resultado);
    }

    @Test
    void seguimientoClientesListaPendientesLlamadaExitosamente() {
        when(entityManager.createNamedQuery(SeguimientoClientes.PENDIENTE_LLAMADAS)).thenReturn(query);
        when(query.setParameter(eq("proximallamada"), any(Date.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new SeguimientoClientes()));

        List<SeguimientoClientes> resultado = seguimientoClientesDao.listaPendientesLlamada();
        assertEquals(1, resultado.size());
    }

    @Test
    void seguimientoClientesTotalDatosCRMExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("estado", "Interesado")).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList("10"));

        BigDecimal resultado = seguimientoClientesDao.totalDatosCRM("Interesado");
        assertEquals(new BigDecimal("10"), resultado);
    }

    @Test
    void seguimientoClientesTotalDatosCRMVendedorExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("estado", "Interesado")).thenReturn(query);
        when(query.setParameter("vendedorId", 1)).thenReturn(query);
        when(query.setParameter("campId", 2)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList("7"));

        BigDecimal resultado = seguimientoClientesDao.totalDatosCRMVendedor("Interesado", 1, 2);
        assertEquals(new BigDecimal("7"), resultado);
    }

    @Test
    void seguimientoClientesListaInteresadosCursoCRMExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        Object[] fila = new Object[]{5, "Matemáticas"};
        when(query.getResultList()).thenReturn(Collections.singletonList(fila));

        assertDoesNotThrow(() -> seguimientoClientesDao.listaInteresadosCursoCRM());
    }

    @Test
    void seguimientoClientesListaEstadosContactoCursoCRMExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("estado", "Interesado")).thenReturn(query);
        Object[] fila = new Object[]{3, "Física"};
        when(query.getResultList()).thenReturn(Collections.singletonList(fila));

        assertDoesNotThrow(() -> seguimientoClientesDao.listaEstadosContactoCursoCRM("Interesado"));
    }

    // ========== SeguimientoDaoImpl ==========

    @Test
    void seguimientoConstructor() {
        assertDoesNotThrow(() -> new SeguimientoDaoImpl());
    }

    @Test
    void seguimientoAgregarPersist() {
        Seguimiento s = new Seguimiento();
        s.setSegmId(0);
        seguimientoDao.agregarActualizarSeguimiento(s);
        verify(entityManager).persist(s);
    }

    @Test
    void seguimientoAgregarMerge() {
        Seguimiento s = mock(Seguimiento.class);
        when(s.getSegmId()).thenReturn(1);
        seguimientoDao.agregarActualizarSeguimiento(s);
        verify(entityManager).merge(s);
    }

    @Test
    void seguimientoListaPorMatriculaExitosamente() {
        when(entityManager.createNamedQuery(Seguimiento.BUSCAR_POR_MATRICULA)).thenReturn(query);
        when(query.setParameter("matricula", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Seguimiento()));

        List<Seguimiento> resultado = seguimientoDao.listaSeguimientoMatricula(1);
        assertEquals(1, resultado.size());
    }

    // ========== SriformapagoDaoImpl ==========

    @Test
    void sriformapagoConstructor() {
        assertDoesNotThrow(() -> new SriformapagoDaoImpl());
    }

    // ========== TipoEncuestaDaoImpl ==========

    @Test
    void tipoEncuestaConstructor() {
        assertDoesNotThrow(() -> new TipoEncuestaDaoImpl());
    }

    @Test
    void tipoEncuestaListaExitosamente() {
        TipoEncuesta te = mock(TipoEncuesta.class);
        when(te.getTipoEncuestaPregunta()).thenReturn(new ArrayList<>());
        when(entityManager.createNamedQuery(TipoEncuesta.CARGAR_TIPOS_ENCUESTAS)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(te));

        List<TipoEncuesta> resultado = tipoEncuestaDao.listaDeTiposDeEncuestas();
        assertEquals(1, resultado.size());
    }

    @Test
    void tipoEncuestaListaPorOeExitosamente() {
        when(entityManager.createNamedQuery(TipoEncuesta.CARGAR_TIPOS_ENCUESTAS_POR_OE)).thenReturn(query);
        when(query.setParameter("codigo", 1)).thenReturn(query);
        TipoEncuesta te = mock(TipoEncuesta.class);
        when(te.getTipoEncuestaPregunta()).thenReturn(new ArrayList<>());
        when(query.getResultList()).thenReturn(Arrays.asList(te));

        List<TipoEncuesta> resultado = tipoEncuestaDao.listaDeTiposDeEncuestasPorOe(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void tipoEncuestaActualizarPersist() {
        TipoEncuesta t = new TipoEncuesta();
        tipoEncuestaDao.actualizarTipoEncuesta(t);
        verify(entityManager).persist(t);
    }

    @Test
    void tipoEncuestaActualizarMerge() {
        TipoEncuesta t = mock(TipoEncuesta.class);
        when(t.getTipeId()).thenReturn(1);
        tipoEncuestaDao.actualizarTipoEncuesta(t);
        verify(entityManager).merge(t);
    }

    // ========== TipoEncuestaPreguntaDaoImpl ==========

    @Test
    void tipoEncuestaPreguntaConstructor() {
        assertDoesNotThrow(() -> new TipoEncuestaPreguntaDaoImpl());
    }

    @Test
    void tipoEncuestaPreguntaListaDePreguntasExitosamente() {
        when(entityManager.createNamedQuery(TipoEncuestaPregunta.CARGAR_PREGUNTA)).thenReturn(query);
        when(query.setParameter("codigo", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new TipoEncuestaPregunta()));

        List<TipoEncuestaPregunta> resultado = tipoEncuestaPreguntaDao.listaDePreguntas(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void tipoEncuestaPreguntaListaDeTiposExitosamente() {
        when(entityManager.createNamedQuery(TipoEncuestaPregunta.CARGAR_TIPO_ENCUESTA)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new TipoEncuestaPregunta()));

        List<TipoEncuestaPregunta> resultado = tipoEncuestaPreguntaDao.listaDeTiposDeEncuestas();
        assertEquals(1, resultado.size());
    }

    @Test
    void tipoEncuestaPreguntaListaDeEncuestasExitosamente() {
        when(entityManager.createNamedQuery(TipoEncuestaPregunta.CARGAR_ENCUESTAS)).thenReturn(query);
        when(query.setParameter("codigoTipo", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new TipoEncuestaPregunta()));

        List<TipoEncuestaPregunta> resultado = tipoEncuestaPreguntaDao.listaDeEncuestas(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void tipoEncuestaPreguntaListaPorTipoExitosamente() {
        when(entityManager.createNamedQuery(TipoEncuestaPregunta.CARGAR_POR_TIPO_ENCUESTA)).thenReturn(query);
        when(query.setParameter("codigo", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new TipoEncuestaPregunta()));

        List<TipoEncuestaPregunta> resultado = tipoEncuestaPreguntaDao.listaPorTipoDeEncuestas(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void tipoEncuestaPreguntaAgregarPersist() {
        TipoEncuestaPregunta t = new TipoEncuestaPregunta();
        tipoEncuestaPreguntaDao.agregarActualizarTipoEncuestaPregunta(t);
        verify(entityManager).persist(t);
    }

    @Test
    void tipoEncuestaPreguntaAgregarMerge() {
        TipoEncuestaPregunta t = mock(TipoEncuestaPregunta.class);
        when(t.getTeprId()).thenReturn(1);
        tipoEncuestaPreguntaDao.agregarActualizarTipoEncuestaPregunta(t);
        verify(entityManager).merge(t);
    }

    @Test
    void tipoEncuestaPreguntaGuardarRespuestasExitosamente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("encuestaId", 1)).thenReturn(query);
        Object[] fila = new Object[]{"1", "Pregunta 1", "Respuesta 1"};
        when(query.getResultList()).thenReturn(Collections.singletonList(fila));

        assertDoesNotThrow(() -> tipoEncuestaPreguntaDao.guardarRespuestasEncuestas(1));
    }

    // ========== UsuarioRolDaoImpl ==========

    @Test
    void usuarioRolConstructor() {
        assertDoesNotThrow(() -> new UsuarioRolDaoImpl());
    }

    @Test
    void usuarioRolListaExitosamente() {
        when(entityManager.createNamedQuery(UsuarioRol.CARGAR_Usuario_Rol)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new UsuarioRol()));

        List<UsuarioRol> resultado = usuarioRolDao.listaDeUsuarioRol();
        assertEquals(1, resultado.size());
    }

    @Test
    void usuarioRolListaPorUsuarioExitosamente() {
        when(entityManager.createNamedQuery(UsuarioRol.CARGAR_Usuario_Rol_Por_IDUsuario)).thenReturn(query);
        when(query.setParameter("idUsuario", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new UsuarioRol()));

        List<UsuarioRol> resultado = usuarioRolDao.listaUsuarioRolPorUsuario(1);
        assertEquals(1, resultado.size());
    }

    @Test
    void usuarioRolAgregarPersist() {
        UsuarioRol u = new UsuarioRol();
        usuarioRolDao.agregarUsuarioRol(u);
        verify(entityManager).persist(u);
    }

    // ========== VendedorDaoImpl ==========

    @Test
    void vendedorConstructor() {
        assertDoesNotThrow(() -> new VendedorDaoImpl());
    }

    @Test
    void vendedorListaExitosamente() {
        when(entityManager.createNamedQuery(Vendedor.BUSCAR_VENDEDOR)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Vendedor()));

        List<Vendedor> resultado = vendedorDao.listaDeVendedores();
        assertEquals(1, resultado.size());
    }

    @Test
    void vendedorListaConNoResult() {
        when(entityManager.createNamedQuery(Vendedor.BUSCAR_VENDEDOR)).thenReturn(query);
        when(query.getResultList()).thenThrow(new NoResultException());

        List<Vendedor> resultado = vendedorDao.listaDeVendedores();
        assertNull(resultado);
    }

    // ========== FormacionDaoImpl ==========

    @Test
    void formacionConstructor() {
        assertDoesNotThrow(() -> new FormacionDaoImpl());
    }

    @Test
    void formacionAgregarPersist() {
        Formacion f = new Formacion();
        f.setFormId(0);
        formacionDao.agregaActualizaFormacion(f);
        verify(entityManager).persist(f);
    }

    @Test
    void formacionAgregarMerge() {
        Formacion f = mock(Formacion.class);
        when(f.getFormId()).thenReturn(1);
        formacionDao.agregaActualizaFormacion(f);
        verify(entityManager).merge(f);
    }

    @Test
    void formacionListaExitosamente() {
        when(entityManager.createNamedQuery(Formacion.LISTADO_FORMACIONES)).thenReturn(query);
        when(query.setParameter("codigoInstructor", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Formacion()));

        List<Formacion> resultado = formacionDao.listaFormaciones(1);
        assertEquals(1, resultado.size());
    }
}
