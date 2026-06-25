/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.util.List;
import java.util.stream.Collectors;

import ec.mileniumtech.educafacil.dao.impl.AreaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EmpresaMatrizDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PagosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PerfilAccionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PerfilDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EspecialidadDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EstablecimientoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EvaluacionCursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.InstructorDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ObjetoEvaluacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCapacitacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.TipoEncuestaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioRolDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Establecimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.RolPerfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumTipoCatalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.CursoDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.PersonaDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.MatriculaDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMapper;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PuntoEmisionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RolDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RolPerfilDaoImpl;
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

    @EJB
    private PersonaDaoImpl personaDao;

    @EJB
    private MatriculaDaoImpl matriculaDao;

    @EJB
    private EmpresaMatrizDaoImpl empresaDao;

    @EJB
    private EstablecimientoDaoImpl establecimientoDao;

    @EJB
    private PuntoEmisionDaoImpl puntoEmisionDao;

    @EJB
    private PagosDaoImpl pagosDao;
    
    @EJB
    private PerfilDaoImpl perfilDao;

    @EJB
    private PerfilAccionDaoImpl perfilAccionDao;
    
    @EJB
    private RolDaoImpl rolDao;

    @EJB
    private RolPerfilDaoImpl rolPerfilDao;
    
    @EJB
    private UsuarioDaoImpl usuarioDao;

    @EJB
    private UsuarioRolDaoImpl usuarioRolDao;

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
    
    // Métodos para API REST
    
    /**
     * Lista todos los cursos en formato DTO.
     */
    public List<CursoDto> listarCursosDto() {
        return cursoDao.listaCursos().stream()
                .map(DtoMapper::entidadACursoDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca una persona por cédula y correo en formato DTO.
     */
    public PersonaDto buscarPersonaDto(String cedula, String correo) {
        Persona persona = personaDao.buscarPersonaPorCedulaCorreo(cedula, correo);
        return DtoMapper.entidadAPersonaDto(persona);
    }

    /**
     * Lista las matrículas de un estudiante en formato DTO.
     */
    public List<MatriculaDto> listarMatriculasEstudianteDto(int codigoEstudiante) {
        return matriculaDao.listaMatriculasEstudiante(codigoEstudiante).stream()
                .map(DtoMapper::entidadAMatriculaDto)
                .collect(Collectors.toList());
    }
    /**
     * Guarda una nueva persona desde un DTO.
     */
    public PersonaDto guardarPersona(PersonaDto personaDto) {
        Persona persona = DtoMapper.dtoAEntidadPersona(personaDto);
        personaDao.agregarPersona(persona);
        return DtoMapper.entidadAPersonaDto(persona);
    }

    /**
     * Actualiza una persona existente desde un DTO.
     */
    public PersonaDto actualizarPersona(PersonaDto personaDto) {
        Persona persona = DtoMapper.dtoAEntidadPersona(personaDto);
        personaDao.actualizarPersona(persona);
        return DtoMapper.entidadAPersonaDto(persona);
    }

    /**
     * Guarda un nuevo curso desde un DTO.
     */
    public CursoDto guardarCurso(CursoDto cursoDto) {
        Curso curso = DtoMapper.dtoAEntidadCurso(cursoDto);
        cursoDao.guardar(curso);
        return DtoMapper.entidadACursoDto(curso);
    }

    /**
     * Actualiza un curso existente desde un DTO.
     */
    public CursoDto actualizarCursoDto(CursoDto cursoDto) {
        Curso curso = DtoMapper.dtoAEntidadCurso(cursoDto);
        cursoDao.actualizar(curso);
        return DtoMapper.entidadACursoDto(curso);
    }
    /**
     * Elimina una persona por su ID.
     */
    public void eliminarPersona(int id) {
        Persona persona = personaDao.buscarPersonaPorId(id);
        if (persona != null) {
            personaDao.remover(persona);
        }
    }

    /**
     * Elimina un curso por su ID.
     */
    public void eliminarCurso(int id) {
        Curso curso = cursoDao.getEntityManager().find(Curso.class, id);
        if (curso != null) {
            cursoDao.remover(curso);
        }
    }

    /**
     * Lista todas las empresas activas.
     */
    public List<EmpresaMatriz> listarEmpresas() {
        return empresaDao.listaEmpresas();
    }

    /**
     * Guarda o actualiza una empresa.
     */
    public void guardarEmpresa(EmpresaMatriz empresa) {
        empresaDao.agregarEmpresa(empresa);
    }

    /**
     * Lista todos los pagos.
     */
    public List<Pagos> listarPagos() {
        return pagosDao.listarTodosLosPagos();
    }

    /**
     * Actualiza un pago.
     */
    public void actualizarPago(Pagos pago) {
        pagosDao.actualizarPago(pago);
    }
    /**
     * Lista establecimientos por empresa.
     */
    public List<Establecimiento> listarEstablecimientosPorEmpresa(int empmId) {
        return establecimientoDao.listarEstablecimientosPorEmpresa(empmId);
    }

    /**
     * Guarda o actualiza un establecimiento.
     */
    public void guardarEstablecimiento(Establecimiento establecimiento) {
        if (establecimiento.getEstaId() == null) {
            establecimientoDao.guardar(establecimiento);
        } else {
            establecimientoDao.actualizar(establecimiento);
        }
    }

    /**
     * Lista puntos de emisión por establecimiento.
     */
    public List<PuntoEmision> listarPuntosEmisionPorEstablecimiento(int estaId) {
        return puntoEmisionDao.listarPuntosEmisionPorEstablecimiento(estaId);
    }

    /**
     * Guarda o actualiza un punto de emisión.
     */
    public void guardarPuntoEmision(PuntoEmision puntoEmision) {
        if (puntoEmision.getId() == null) {
            puntoEmisionDao.guardar(puntoEmision);
        } else {
            puntoEmisionDao.actualizar(puntoEmision);
        }
    }
    /**
     * Las modalidades de estudio ordenadas
     * @return
     */
    public List<Catalogo> listarTipoModalidadOrdenados() {
        return catalogoDao.catalogosPorTipo(EnumTipoCatalogo.TIPOMODALIDADESTUDIO.getNemotecnico()).stream()
                .sorted((t1, t2) -> t1.getCataDescripcion().compareTo(t2.getCataDescripcion()))
                .collect(Collectors.toList());
    }

    // =========================================================
    // Métodos de Gestión de Perfiles y Acciones
    // =========================================================

    /**
     * Lista todos los perfiles (activos e inactivos).
     */
    public List<Perfil> listarPerfiles() {
        return perfilDao.listarTodosPerfiles();
    }

    /**
     * Lista únicamente los perfiles activos.
     */
    public List<Perfil> listarPerfilesActivos() {
        return perfilDao.listarPerfilesActivos();
    }

    /**
     * Guarda o actualiza un perfil.
     * Si el id es null se persiste como nuevo, de lo contrario se hace merge.
     */
    public void guardarPerfil(Perfil perfil) {
        if (perfil.getId() == null) {
            perfilDao.guardar(perfil);
        } else {
            perfilDao.actualizar(perfil);
        }
    }

    /**
     * Eliminación lógica: pone el estado del perfil en false.
     */
    public void eliminarLogicoPerfil(Integer id) {
        Perfil perfil = perfilDao.buscarPerfilPorId(id);
        if (perfil != null) {
            perfil.setEstado(false);
            perfilDao.actualizar(perfil);
        }
    }

    /**
     * Lista todas las acciones activas del sistema.
     */
    public List<Accion> listarAcciones() {
        return perfilAccionDao.listarTodasAcciones();
    }

    /**
     * Lista las acciones asignadas y activas de un perfil.
     */
    public List<PerfilAccion> listarAccionesPorPerfil(Integer perfilId) {
        return perfilAccionDao.listarAccionesPorPerfil(perfilId);
    }

    /**
     * Asigna una acción a un perfil. Si ya existe la asociación y estaba
     * inactiva la reactiva; si no existe la crea.
     */
    public void asignarAccionAPerfil(Integer perfilId, String accionId) {
        PerfilAccion existente = perfilAccionDao.buscarPerfilAccion(perfilId, accionId);
        if (existente == null) {
            Perfil perfil = perfilDao.buscarPerfilPorId(perfilId);
            Accion accion = perfilAccionDao.getEntityManager().find(Accion.class, accionId);
            if (perfil != null && accion != null) {
                PerfilAccion nueva = new PerfilAccion();
                nueva.setId(perfilAccionDao.siguienteId());
                nueva.setPerfil(perfil);
                nueva.setAccion(accion);
                nueva.setEstado(true);
                perfilAccionDao.guardar(nueva);
            }
        } else {
            existente.setEstado(true);
            perfilAccionDao.actualizar(existente);
        }
    }

    /**
     * Quita (elimina físicamente) la asignación de una acción a un perfil.
     */
    public void quitarAccionDePerfil(Integer perfilId, String accionId) {  		
        perfilAccionDao.eliminarPorPerfilYAccion(perfilId, accionId);
    }
 // =========================================================
    // Métodos de Gestión de Roles y Perfiles
    // =========================================================

    /**
     * Lista todos los roles (activos e inactivos).
     */
    public List<Rol> listarRoles() {
        return rolDao.listarTodosRoles();
    }

    /**
     * Lista únicamente los roles activos.
     */
    public List<Rol> listarRolesActivos() {
        return rolDao.listarRolesActivos();
    }

    /**
     * Guarda o actualiza un rol.
     * Si el id es null se persiste como nuevo, de lo contrario se hace merge.
     */
    public void guardarRol(Rol rol) {
        if (rol.getRolId() == null) {
            rolDao.guardar(rol);
        } else {
            rolDao.actualizar(rol);
        }
    }

    /**
     * Eliminación lógica: pone el estado del rol en false.
     */
    public void eliminarLogicoRol(Integer id) {
        Rol rol = rolDao.buscarRolPorId(id);
        if (rol != null) {
            rol.setRolEstado(false);
            rolDao.actualizar(rol);
        }
    }

    /**
     * Lista los perfiles asignados y activos de un rol.
     */
    public List<RolPerfil> listarPerfilesPorRol(Integer rolId) {
        return rolPerfilDao.listarPerfilesPorRol(rolId);
    }

    /**
     * Asigna un perfil a un rol. Si ya existe la asociación y estaba
     * inactiva la reactiva; si no existe la crea.
     */
    public void asignarPerfilARol(Integer rolId, Integer perfilId) {
        RolPerfil existente = rolPerfilDao.buscarRolPerfil(rolId, perfilId);
        if (existente == null) {
            Rol rol = rolDao.buscarRolPorId(rolId);
            Perfil perfil = perfilDao.buscarPerfilPorId(perfilId);
            if (rol != null && perfil != null) {
                RolPerfil nueva = new RolPerfil();
                nueva.setId(rolPerfilDao.siguienteId());
                nueva.setRol(rol);
                nueva.setPerfil(perfil);
                nueva.setEstado(true);
                rolPerfilDao.guardar(nueva);
            }
        } else {
            existente.setEstado(true);
            rolPerfilDao.actualizar(existente);
        }
    }

    /**
     * Quita (elimina físicamente) la asignación de un perfil a un rol.
     */
    public void quitarPerfilDeRol(Integer rolId, Integer perfilId) {
        rolPerfilDao.eliminarPorRolYPerfil(rolId, perfilId);
    }
 // =========================================================
    // Métodos de Gestión de Usuarios y Roles
    // =========================================================

    /**
     * Lista todos los usuarios.
     */
    public List<Usuario> listarUsuarios() {
        return usuarioDao.listarTodosUsuarios();
    }

    /**
     * Guarda o actualiza un usuario, junto con sus datos personales.
     */
    public void guardarUsuario(Usuario usuario) {
        Persona persona = usuario.getPersona();
        if (persona.getPersId() == 0) {
            personaDao.guardar(persona);
        } else {
            personaDao.actualizar(persona);
        }
        if (usuario.getUsuaId() == null) {
            usuario.setUsuaFechaRegistro(new java.util.Date());
            usuario.setUsuaClave(Encriptar.encriptarBCrypt(usuario.getUsuaClave()));
            usuarioDao.agregarUsuario(usuario);
        } else {
            if (usuario.getUsuaClave() != null && !usuario.getUsuaClave().isEmpty() && !Encriptar.esHashBCrypt(usuario.getUsuaClave())) {
                usuario.setUsuaClave(Encriptar.encriptarBCrypt(usuario.getUsuaClave()));
            }
            usuarioDao.actualizar(usuario);
        }
    }

    /**
     * Eliminación lógica: pone el estado del usuario en false.
     */
    public void eliminarLogicoUsuario(Integer id) {
        Usuario usuario = usuarioDao.getEntityManager().find(Usuario.class, id);
        if (usuario != null) {
            usuario.setUsuaEstado(false);
            usuarioDao.actualizar(usuario);
        }
    }

    /**
     * Lista los roles asignados y activos de un usuario.
     */
    public List<UsuarioRol> listarRolesPorUsuarioActivos(Integer usuarioId) {
        List<UsuarioRol> roles = usuarioRolDao.listaUsuarioRolPorUsuario(usuarioId);
        if (roles == null) return new java.util.ArrayList<>();
        return roles.stream()
                .filter(ur -> ur.getUrolEstado() != null && ur.getUrolEstado())
                .collect(Collectors.toList());
    }

    /**
     * Asigna un rol a un usuario. Si ya existe la asociación y estaba
     * inactiva la reactiva; si no existe la crea.
     */
    public void asignarRolAUsuario(Integer usuarioId, Integer rolId) {
        List<UsuarioRol> todos = usuarioRolDao.listaUsuarioRolPorUsuario(usuarioId);
        UsuarioRol existente = null;
        if (todos != null) {
            existente = todos.stream()
                .filter(ur -> ur.getRol().getRolId().equals(rolId))
                .findFirst()
                .orElse(null);
        }
        if (existente == null) {
            Usuario usuario = usuarioDao.getEntityManager().find(Usuario.class, usuarioId);
            Rol rol = usuarioDao.getEntityManager().find(Rol.class, rolId);
            if (usuario != null && rol != null) {
                UsuarioRol nuevo = new UsuarioRol();
                nuevo.setUsuario(usuario);
                nuevo.setRol(rol);
                nuevo.setUrolEstado(true);
                usuarioRolDao.agregarUsuarioRol(nuevo);
            }
        } else {
            existente.setUrolEstado(true);
            usuarioRolDao.actualizar(existente);
        }
    }

    /**
     * Quita la asignación de un rol a un usuario (desactivación lógica).
     */
    public void quitarRolDeUsuario(Integer usuarioId, Integer rolId) {
        List<UsuarioRol> todos = usuarioRolDao.listaUsuarioRolPorUsuario(usuarioId);
        if (todos != null) {
            UsuarioRol existente = todos.stream()
                .filter(ur -> ur.getRol().getRolId().equals(rolId))
                .findFirst()
                .orElse(null);
            if (existente != null) {
                existente.setUrolEstado(false);
                usuarioRolDao.actualizar(existente);
            }
        }
    }
}
