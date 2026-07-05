/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.time.LocalDate;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.CursoDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.MatriculaDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.PersonaDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PlanificacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.RolPerfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Establecimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio de fachada para la administraci&oacute;n del sistema.
 * <p>
 * <strong>Nota:</strong> Esta clase es una fachada de migraci&oacute;n. Los m&eacute;todos
 * delegan en servicios especializados ({@link OfertaService}, {@link EvaluacionService},
 * {@link EmpresaService}, {@link SeguridadService}, {@link PlanificacionCursoService}).
 * Se recomienda inyectar directamente el servicio especializado en nuevo c&oacute;digo.
 * </p>
 *
 * @author christian
 * @deprecated Usar los servicios espec&iacute;ficos en lugar de esta fachada.
 */
@Stateless
@LocalBean
@Deprecated(since = "FASE 1", forRemoval = false)
public class AdministracionService {

    private static final Logger log = LogManager.getLogger(AdministracionService.class);

    // =========================================================
    // Servicios especializados (delegación)
    // =========================================================

    @EJB
    private OfertaService ofertaService;

    @EJB
    private EvaluacionService evaluacionService;

    @EJB
    private EmpresaService empresaService;

    @EJB
    private SeguridadService seguridadService;

    @EJB
    private PlanificacionCursoService planificacionCursoService;

    @EJB
    private PersonaService personaService;

    // =========================================================
    // Delega en OfertaService
    // =========================================================

    public List<OfertaCursos> listarOfertaCursosActivosOrdenados() {
        return ofertaService.listarOfertaCursosActivosOrdenados();
    }

    public List<Area> listarAreasOrdenadas() {
        return ofertaService.listarAreasOrdenadas();
    }

    public List<Especialidad> listarEspecialidadesPorAreaOrdenadas(int codigoArea) {
        return ofertaService.listarEspecialidadesPorAreaOrdenadas(codigoArea);
    }

    public List<Instructor> listarInstructoresOrdenados() {
        return ofertaService.listarInstructoresOrdenados();
    }

    public List<Curso> listarCursosPorAreaEspecialidadOrdenados(int codigoArea, int codigoEspecialidad) {
        return ofertaService.listarCursosPorAreaEspecialidadOrdenados(codigoArea, codigoEspecialidad);
    }

    public List<Catalogo> listarTipoCapacitacionOrdenados() {
        return ofertaService.listarTipoCapacitacionOrdenados();
    }

    public List<Curso> listarTodosCursosOrdenados() {
        return ofertaService.listarTodosCursosOrdenados();
    }

    public List<Especialidad> listarTodasEspecialidadesOrdenadas() {
        return ofertaService.listarTodasEspecialidadesOrdenadas();
    }

    public List<OfertaCapacitacion> listarOfertasCapacitacion() {
        return ofertaService.listarOfertasCapacitacion();
    }

    public OfertaCapacitacion buscarOfertaCapacitacion(int codigoArea, int codigoEspecialidad, int codigoCurso) {
        return ofertaService.buscarOfertaCapacitacion(codigoArea, codigoEspecialidad, codigoCurso);
    }

    public void guardarOfertaCurso(OfertaCursos ofertaCursos) {
        ofertaService.guardarOfertaCurso(ofertaCursos);
    }

    public void guardarNuevaOfertaCapacitacion(OfertaCapacitacion ofertaCapacitacion) {
        ofertaService.guardarNuevaOfertaCapacitacion(ofertaCapacitacion);
    }

    public void actualizarCurso(Curso curso) {
        ofertaService.actualizarCurso(curso);
    }

    public List<Catalogo> listarTipoModalidadOrdenados() {
        return ofertaService.listarTipoModalidadOrdenados();
    }

    // =========================================================
    // Delega en EvaluacionService
    // =========================================================

    public List<ObjetoEvaluacion> listarObjetosEvaluacion() {
        return evaluacionService.listarObjetosEvaluacion();
    }

    public List<EvaluacionCurso> listarEvaluacionesPorCurso(int ocurId, int objeId) {
        return evaluacionService.listarEvaluacionesPorCurso(ocurId, objeId);
    }

    public List<TipoEncuesta> listarTiposEncuestasPorObjeto(int objeId) {
        return evaluacionService.listarTiposEncuestasPorObjeto(objeId);
    }

    public void agregarEvaluacionCurso(EvaluacionCurso evaluacionCurso) {
        evaluacionService.agregarEvaluacionCurso(evaluacionCurso);
    }

    // =========================================================
    // Métodos para API REST (Cursos) — delega en OfertaService
    // =========================================================

    public List<CursoDto> listarCursosDto() {
        return ofertaService.listarCursosDto();
    }

    public CursoDto guardarCurso(CursoDto cursoDto) {
        return ofertaService.guardarCurso(cursoDto);
    }

    public CursoDto actualizarCursoDto(CursoDto cursoDto) {
        return ofertaService.actualizarCursoDto(cursoDto);
    }

    public void eliminarCurso(int id) {
        ofertaService.eliminarCurso(id);
    }

    // =========================================================
    // Delega en PersonaService
    // =========================================================

    public PersonaDto buscarPersonaDto(String cedula, String correo) {
        return personaService.buscarPersonaDto(cedula, correo);
    }

    public List<MatriculaDto> listarMatriculasEstudianteDto(int codigoEstudiante) {
        return personaService.listarMatriculasEstudianteDto(codigoEstudiante);
    }

    public PersonaDto guardarPersona(PersonaDto personaDto) {
        return personaService.guardarPersona(personaDto);
    }

    public PersonaDto actualizarPersona(PersonaDto personaDto) {
        return personaService.actualizarPersona(personaDto);
    }

    public void eliminarPersona(int id) {
        personaService.eliminarPersona(id);
    }

    // =========================================================
    // Delega en EmpresaService
    // =========================================================

    public List<EmpresaMatriz> listarEmpresas() {
        return empresaService.listarEmpresas();
    }

    public void guardarEmpresa(EmpresaMatriz empresa) {
        empresaService.guardarEmpresa(empresa);
    }

    public List<Pagos> listarPagos() {
        return empresaService.listarPagos();
    }

    public void actualizarPago(Pagos pago) {
        empresaService.actualizarPago(pago);
    }

    public List<Establecimiento> listarEstablecimientosPorEmpresa(int empmId) {
        return empresaService.listarEstablecimientosPorEmpresa(empmId);
    }

    public void guardarEstablecimiento(Establecimiento establecimiento) {
        empresaService.guardarEstablecimiento(establecimiento);
    }

    public List<PuntoEmision> listarPuntosEmisionPorEstablecimiento(int estaId) {
        return empresaService.listarPuntosEmisionPorEstablecimiento(estaId);
    }

    public void guardarPuntoEmision(PuntoEmision puntoEmision) {
        empresaService.guardarPuntoEmision(puntoEmision);
    }

    // =========================================================
    // Delega en SeguridadService
    // =========================================================

    public List<Perfil> listarPerfiles() {
        return seguridadService.listarPerfiles();
    }

    public List<Perfil> listarPerfilesActivos() {
        return seguridadService.listarPerfilesActivos();
    }

    public void guardarPerfil(Perfil perfil) {
        seguridadService.guardarPerfil(perfil);
    }

    public void eliminarLogicoPerfil(Integer id) {
        seguridadService.eliminarLogicoPerfil(id);
    }

    public List<Accion> listarAcciones() {
        return seguridadService.listarAcciones();
    }

    public List<PerfilAccion> listarAccionesPorPerfil(Integer perfilId) {
        return seguridadService.listarAccionesPorPerfil(perfilId);
    }

    public void asignarAccionAPerfil(Integer perfilId, String accionId) {
        seguridadService.asignarAccionAPerfil(perfilId, accionId);
    }

    public void quitarAccionDePerfil(Integer perfilId, String accionId) {
        seguridadService.quitarAccionDePerfil(perfilId, accionId);
    }

    public List<Rol> listarRoles() {
        return seguridadService.listarRoles();
    }

    public List<Rol> listarRolesActivos() {
        return seguridadService.listarRolesActivos();
    }

    public void guardarRol(Rol rol) {
        seguridadService.guardarRol(rol);
    }

    public void eliminarLogicoRol(Integer id) {
        seguridadService.eliminarLogicoRol(id);
    }

    public List<RolPerfil> listarPerfilesPorRol(Integer rolId) {
        return seguridadService.listarPerfilesPorRol(rolId);
    }

    public void asignarPerfilARol(Integer rolId, Integer perfilId) {
        seguridadService.asignarPerfilARol(rolId, perfilId);
    }

    public void quitarPerfilDeRol(Integer rolId, Integer perfilId) {
        seguridadService.quitarPerfilDeRol(rolId, perfilId);
    }

    public List<Usuario> listarUsuarios() {
        return seguridadService.listarUsuarios();
    }

    public void guardarUsuario(Usuario usuario) {
        seguridadService.guardarUsuario(usuario);
    }

    public void eliminarLogicoUsuario(Integer id) {
        seguridadService.eliminarLogicoUsuario(id);
    }

    public List<UsuarioRol> listarRolesPorUsuarioActivos(Integer usuarioId) {
        return seguridadService.listarRolesPorUsuarioActivos(usuarioId);
    }

    public void asignarRolAUsuario(Integer usuarioId, Integer rolId) {
        seguridadService.asignarRolAUsuario(usuarioId, rolId);
    }

    public void quitarRolDeUsuario(Integer usuarioId, Integer rolId) {
        seguridadService.quitarRolDeUsuario(usuarioId, rolId);
    }

    // =========================================================
    // Delega en PlanificacionCursoService
    // =========================================================

    public List<PlanificacionCurso> listarPlanificacionesCursos() {
        return planificacionCursoService.listarPlanificacionesCursos();
    }

    public List<PlanificacionCurso> listarPlanificacionesPorSemana(LocalDate fechaInicio, LocalDate fechaFin) {
        return planificacionCursoService.listarPlanificacionesPorSemana(fechaInicio, fechaFin);
    }

    public void guardarPlanificacionCurso(PlanificacionCurso planificacion) {
        planificacionCursoService.guardarPlanificacionCurso(planificacion);
    }

    public void eliminarPlanificacionCurso(Integer plcuId) {
        planificacionCursoService.eliminarPlanificacionCurso(plcuId);
    }
}
