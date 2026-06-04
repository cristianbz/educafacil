package ec.mileniumtech.educafacil.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.AreaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EspecialidadDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EstudianteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MedioInformacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCapacitacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioRolDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ObjetosMenuDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.MedioInformacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class MatriculaDataService {

    @EJB
    private MatriculaDaoImpl matriculaDao;

    @EJB
    private EstudianteDaoImpl estudianteDao;

    @EJB
    private PersonaDaoImpl personaDao;

    @EJB
    private OfertaCursosDaoImpl ofertaCursosDao;

    @EJB
    private OfertaCapacitacionDaoImpl ofertaCapacitacionDao;

    @EJB
    private UsuarioDaoImpl usuarioDao;

    @EJB
    private UsuarioRolDaoImpl usuarioRolDao;

    @EJB
    private CursoDaoImpl cursoDao;

    @EJB
    private AreaDaoImpl areaDao;

    @EJB
    private EspecialidadDaoImpl especialidadDao;

    @EJB
    private MedioInformacionDaoImpl medioInformacionDao;

    // ========== Matricula methods ==========

    public void agregarMatriculaInscripcion(Persona persona, Matricula matricula, Usuario usuario, UsuarioRol usuarioRol) {
        matriculaDao.agregarMatriculaInscripcion(persona, matricula, usuario, usuarioRol);
    }

    public List<Matricula> listaMatriculasAlumno(int codigoPersona, String codigoEstado) {
        return matriculaDao.listaMatriculasAlumno(codigoPersona, codigoEstado);
    }

    public List<Matricula> listaMatriculasInscripcion(String estado, Date fechaInicio, Date fechaFin) {
        return matriculaDao.listaMatriculasInscripcion(estado, fechaInicio, fechaFin);
    }

    public List<Matricula> listaMatriculasCurso(String estado, int codigoCurso) {
        return matriculaDao.listaMatriculasCurso(estado, codigoCurso);
    }

    public List<Matricula> listaMatriculadosOEnCursoPorOferta(int codigoOferta) {
        return matriculaDao.listaMatriculadosOEnCursoPorOferta(codigoOferta);
    }

    public List<Matricula> listaOportunidades() {
        return matriculaDao.listaOportunidades();
    }

    public List<Matricula> listaMatriculasEstudiante(int codigoEstudiante) {
        return matriculaDao.listaMatriculasEstudiante(codigoEstudiante);
    }

    public void actualizaMatricula(Matricula matricula) {
        matriculaDao.actualizaMatricula(matricula);
    }

    public void actualizaMatriculaUsuario(Matricula matricula, Usuario usuario) {
        matriculaDao.actualizaMatriculaUsuario(matricula, usuario);
    }

    public Matricula existeMatricula(int oferta, int estudiante) {
        return matriculaDao.existeMatricula(oferta, estudiante);
    }

    public List<Matricula> listaMatriculadosPorOfertaCurso(int codigoOferta) {
        return matriculaDao.listaMatriculadosPorOfertaCurso(codigoOferta);
    }

    public List<Matricula> listaMatriculasEstudianteActivas(int codigoEstudiante) {
        return matriculaDao.listaMatriculasEstudianteActivas(codigoEstudiante);
    }

    public BigDecimal totalDatosMatricula(int estado) {
        return matriculaDao.totalDatosMatricula(estado);
    }

    public List<DtoMatriculasCurso> listaMatriculasCurso(int tipo) {
        return matriculaDao.listaMatriculasCurso(tipo);
    }

    public Matricula guardarMatricula(Matricula matricula) {
        return matriculaDao.guardar(matricula);
    }

    public Matricula actualizarMatricula(Matricula matricula) {
        return matriculaDao.actualizar(matricula);
    }

    // ========== Estudiante methods ==========

    public List<Estudiante> estudiantesPorApellido(String apellidos) {
        return estudianteDao.estudiantesPorApellido(apellidos);
    }

    public Estudiante estudiantesPorCedula(String cedula) {
        return estudianteDao.estudiantesPorCedula(cedula);
    }

    public void actualizaEstudiante(Estudiante estudiante) {
        estudianteDao.actualizaEstudiante(estudiante);
    }

    public Estudiante guardarEstudiante(Estudiante estudiante) {
        return estudianteDao.guardar(estudiante);
    }

    // ========== Persona methods ==========

    public Persona buscarPersonaPorCedula(String cedula) {
        return personaDao.buscarPersonaPorCedula(cedula);
    }

    public void agregarPersona(Persona persona) {
        personaDao.agregarPersona(persona);
    }

    public Persona actualizarPersona(Persona persona) {
        return personaDao.actualizarPersona(persona);
    }

    public List<Persona> buscarPersonaPorApellidos(String apellidos) {
        return personaDao.buscarPersonaPorApellidos(apellidos);
    }

    public Persona buscarPersonaPorId(int codigo) {
        return personaDao.buscarPersonaPorId(codigo);
    }

    public Persona buscarPersonaPorCedulaCorreo(String cedula, String correo) {
        return personaDao.buscarPersonaPorCedulaCorreo(cedula, correo);
    }

    public Persona guardarPersona(Persona persona) {
        return personaDao.guardar(persona);
    }

    // ========== OfertaCursos methods ==========

    public List<OfertaCursos> listaCursosDisponibles(int ofertaCapacitacion) {
        return ofertaCursosDao.listaCursosDisponibles(ofertaCapacitacion);
    }

    public void agregarOfertaCursos(OfertaCursos ofertaCursos) {
        ofertaCursosDao.agregarOfertaCursos(ofertaCursos);
    }

    public OfertaCursos editarOfertaCursos(OfertaCursos ofertaCursos) {
        return ofertaCursosDao.editarOfertaCursos(ofertaCursos);
    }

    public List<OfertaCursos> listaOfertaCursosActivos() {
        return ofertaCursosDao.listaOfertaCursosActivos();
    }

    public List<OfertaCursos> listaOfertaCursosActivosCerrados() {
        return ofertaCursosDao.listaOfertaCursosActivosCerrados();
    }

    public void finalizarCursoActivo(OfertaCursos ofertaCurso, List<Matricula> matriculas) {
        ofertaCursosDao.finalizarCursoActivo(ofertaCurso, matriculas);
    }

    public List<OfertaCursos> listaOfertaCursosPorDefecto() {
        return ofertaCursosDao.listaOfertaCursosPorDefecto();
    }

    public List<OfertaCursos> listaOfertaCursosPorCurso(int codigoCurso) {
        return ofertaCursosDao.listaOfertaCursosPorCurso(codigoCurso);
    }

    public List<OfertaCursos> listaOfertaCursosPorCursoAnio(int codigoCurso, int anio) {
        return ofertaCursosDao.listaOfertaCursosPorCursoAnio(codigoCurso, anio);
    }

    // ========== OfertaCapacitacion methods ==========

    public OfertaCapacitacion buscarOfertaCapacitacion(int area, int especialidad, int curso) {
        return ofertaCapacitacionDao.buscarOfertaCapacitacion(area, especialidad, curso);
    }

    public List<Especialidad> listaEspecialidadPorArea(int area) {
        return ofertaCapacitacionDao.listaEspecialidadPorArea(area);
    }

    public List<Curso> listaCursosPorAreaEspecilidad(int area, int especialidad) {
        return ofertaCapacitacionDao.listaCursosPorAreaEspecilidad(area, especialidad);
    }

    public OfertaCapacitacion buscarPorCurso(int codigoCurso) {
        return ofertaCapacitacionDao.buscarPorCurso(codigoCurso);
    }

    public List<OfertaCapacitacion> listarOfertasCapacitacion() {
        return ofertaCapacitacionDao.listarOfertasCapacitacion();
    }

    public void agregarOfertaCapacitacion(OfertaCapacitacion ofertaCapacitacion, OfertaCursos ofertaCursos) {
        ofertaCapacitacionDao.agregarOfertaCapacitacion(ofertaCapacitacion, ofertaCursos);
    }

    // ========== Usuario methods ==========

    public Usuario actualizaUsuario(Usuario usuario) {
        return usuarioDao.actualizaUsuario(usuario);
    }

    public Usuario agregarUsuario(Usuario usuario) {
        return usuarioDao.agregarUsuario(usuario);
    }

    public Usuario consultarUsuario(String usuario) {
        return usuarioDao.consultarUsuario(usuario);
    }

    public List<ObjetosMenuDto> buscarAccesosUsuario(String correo) {
        return usuarioDao.buscarAccesosUsuario(correo);
    }

    public Usuario consultarUsuarioPorDocumento(String documento) {
        return usuarioDao.consultarUsuarioPorDocumento(documento);
    }

    public List<Usuario> consultarUsuariosPorIdRol(int idRol) {
        return usuarioDao.consultarUsuariosPorIdRol(idRol);
    }

    // ========== UsuarioRol methods ==========

    public List<UsuarioRol> listaDeUsuarioRol() {
        return usuarioRolDao.listaDeUsuarioRol();
    }

    public List<UsuarioRol> listaUsuarioRolPorUsuario(int idUsuario) {
        return usuarioRolDao.listaUsuarioRolPorUsuario(idUsuario);
    }

    public UsuarioRol agregarUsuarioRol(UsuarioRol usuarioRol) {
        return usuarioRolDao.agregarUsuarioRol(usuarioRol);
    }

    // ========== Curso methods ==========

    public List<Curso> listaCursos() {
        return cursoDao.listaCursos();
    }

    public List<Curso> listaOfertaCursosActivosCursos() {
        return cursoDao.listaOfertaCursosActivos();
    }

    // ========== Area methods ==========

    public List<Area> listaDeAreas() {
        return areaDao.listaDeAreas();
    }

    // ========== Especialidad methods ==========

    public List<Especialidad> listaDeEspecialidades() {
        return especialidadDao.listaDeEspecialidades();
    }

    // ========== MedioInformacion methods ==========

    public List<MedioInformacion> listaMedioInformacion() {
        return medioInformacionDao.listaMediosInformacion();
    }
}
