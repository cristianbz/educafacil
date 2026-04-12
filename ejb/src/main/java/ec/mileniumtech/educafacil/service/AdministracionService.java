/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.util.List;
import java.util.stream.Collectors;

import ec.mileniumtech.educafacil.dao.impl.AreaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EspecialidadDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EvaluacionCursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.InstructorDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ObjetoEvaluacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCapacitacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.TipoEncuestaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumTipoCatalogo;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Servicio para manejar la lógica de administración del sistema.
 * 
 * @author christian
 *
 */
@Stateless
@LocalBean
public class AdministracionService {

    @EJB
    private OfertaCursosDaoImpl ofertaCursosDao;
    
    @EJB
    private InstructorDaoImpl instructorDao;
    
    @EJB
    private AreaDaoImpl areaDao;
    
    @EJB
    private OfertaCapacitacionDaoImpl ofertaCapacitacionDao;
    
    @EJB
    private ObjetoEvaluacionDaoImpl objetoEvaluacionDao;
    
    @EJB
    private TipoEncuestaDaoImpl tipoEncuestaDao;
    
    @EJB
    private CatalogoDaoImpl catalogoDao;
    
    @EJB
    private EvaluacionCursoDaoImpl evaluacionCursoDao;

    @EJB
    private CursoDaoImpl cursoDao;

    @EJB
    private EspecialidadDaoImpl especialidadDao;

    /**
     * Lista la oferta de cursos activos ordenados por área.
     */
    public List<OfertaCursos> listarOfertaCursosActivosOrdenados() {
        return ofertaCursosDao.listaOfertaCursosActivos().stream()
                .sorted((a1, a2) -> a1.getOfertaCapacitacion().getArea().getAreaNombre()
                        .compareTo(a2.getOfertaCapacitacion().getArea().getAreaNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Lista de áreas ordenadas por nombre.
     */
    public List<Area> listarAreasOrdenadas() {
        return areaDao.listaDeAreas().stream()
                .sorted((a1, a2) -> a1.getAreaNombre().compareTo(a2.getAreaNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Lista especialidades por área ordenadas por nombre.
     */
    public List<Especialidad> listarEspecialidadesPorAreaOrdenadas(int codigoArea) {
        return ofertaCapacitacionDao.listaEspecialidadPorArea(codigoArea).stream()
                .sorted((e1, e2) -> e1.getEspeNombre().compareTo(e2.getEspeNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Lista instructores ordenados por apellidos.
     */
    public List<Instructor> listarInstructoresOrdenados() {
        return instructorDao.listaInstructores().stream()
                .sorted((i1, i2) -> i1.getPersona().getPersApellidos().compareTo(i2.getPersona().getPersApellidos()))
                .collect(Collectors.toList());
    }

    /**
     * Lista cursos por área y especialidad ordenados por nombre.
     */
    public List<Curso> listarCursosPorAreaEspecialidadOrdenados(int codigoArea, int codigoEspecialidad) {
        return ofertaCapacitacionDao.listaCursosPorAreaEspecilidad(codigoArea, codigoEspecialidad).stream()
                .sorted((c1, c2) -> c1.getCursNombre().compareTo(c2.getCursNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Carga tipos de capacitación ordenados.
     */
    public List<Catalogo> listarTipoCapacitacionOrdenados() {
        return catalogoDao.catalogosPorTipo(EnumTipoCatalogo.TIPOCAPACITACION.getNemotecnico()).stream()
                .sorted((t1, t2) -> t1.getCataDescripcion().compareTo(t2.getCataDescripcion()))
                .collect(Collectors.toList());
    }

    /**
     * Lista todos los cursos ordenados por nombre.
     */
    public List<Curso> listarTodosCursosOrdenados() {
        return cursoDao.listaCursos().stream()
                .sorted((c1, c2) -> c1.getCursNombre().compareTo(c2.getCursNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las especialidades ordenadas por nombre.
     */
    public List<Especialidad> listarTodasEspecialidadesOrdenadas() {
        return especialidadDao.listaDeEspecialidades().stream()
                .sorted((e1, e2) -> e1.getEspeNombre().compareTo(e2.getEspeNombre()))
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las ofertas de capacitación.
     */
    public List<OfertaCapacitacion> listarOfertasCapacitacion() {
        return ofertaCapacitacionDao.listarOfertasCapacitacion();
    }

    /**
     * Busca una oferta de capacitación por área, especialidad y curso.
     */
    public OfertaCapacitacion buscarOfertaCapacitacion(int codigoArea, int codigoEspecialidad, int codigoCurso) {
        return ofertaCapacitacionDao.buscarOfertaCapacitacion(codigoArea, codigoEspecialidad, codigoCurso);
    }

    /**
     * Guarda o edita una oferta de curso.
     */
    public void guardarOfertaCurso(OfertaCursos ofertaCursos) {
        if (ofertaCursos.getOcurId() > 0) {
            ofertaCursosDao.editarOfertaCursos(ofertaCursos);
        } else {
            ofertaCursosDao.agregarOfertaCursos(ofertaCursos);
        }
    }

    /**
     * Guarda una nueva oferta de capacitación con valores por defecto para la primera oferta de curso.
     */
    public void guardarNuevaOfertaCapacitacion(OfertaCapacitacion ofertaCapacitacion) {
        OfertaCursos ofertaCursos = new OfertaCursos();
        Instructor instructor = new Instructor();
        Persona persona = new Persona();
        persona.setPersId(1);
        instructor.setInstId(1);
        instructor.setPersona(persona);
        
        java.util.Date fechaActual = new java.util.Date();
        ofertaCursos.setOcurFechaInicio(fechaActual);
        ofertaCursos.setOcurFechaFin(fechaActual);
        ofertaCursos.setOcurDescuento(0);
        ofertaCursos.setOcurDuracion(0);
        ofertaCursos.setOcurEstado(ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosOfertaCurso.PORDEFECTO.getCodigo());
        ofertaCursos.setOcurPorDefecto(true);
        ofertaCursos.setOcurTipo(ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumTipoCapacitacion.CURSO.getCodigo());
        ofertaCursos.setInstructor(instructor);
        ofertaCursos.setOfertaCapacitacion(ofertaCapacitacion);
        
        ofertaCapacitacionDao.agregarOfertaCapacitacion(ofertaCapacitacion, ofertaCursos);
    }

    /**
     * Actualiza un curso.
     */
    public void actualizarCurso(Curso curso) {
        cursoDao.actualizar(curso);
    }

    /**
     * Lista objetos de evaluación.
     */
    public List<ObjetoEvaluacion> listarObjetosEvaluacion() {
        return objetoEvaluacionDao.listaDeObjetosDeEvaluacion();
    }

    /**
     * Lista evaluaciones por curso y objeto.
     */
    public List<EvaluacionCurso> listarEvaluacionesPorCurso(int ocurId, int objeId) {
        return evaluacionCursoDao.listaDeEvaluacionesPorCurso(ocurId, objeId);
    }

    /**
     * Lista tipos de encuestas por objeto de evaluación.
     */
    public List<TipoEncuesta> listarTiposEncuestasPorObjeto(int objeId) {
        return tipoEncuestaDao.listaDeTiposDeEncuestasPorOe(objeId);
    }
    
    /**
     * Agrega una evaluación de curso.
     */
    public void agregarEvaluacionCurso(EvaluacionCurso evaluacionCurso) {
        evaluacionCursoDao.agregarEvaluacionCurso(evaluacionCurso);
    }
}
