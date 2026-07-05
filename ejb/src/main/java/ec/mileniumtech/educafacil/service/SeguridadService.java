/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.util.List;
import java.util.stream.Collectors;

import ec.mileniumtech.educafacil.dao.PerfilAccionDao;
import ec.mileniumtech.educafacil.dao.PerfilDao;
import ec.mileniumtech.educafacil.dao.PersonaDao;
import ec.mileniumtech.educafacil.dao.RolDao;
import ec.mileniumtech.educafacil.dao.RolPerfilDao;
import ec.mileniumtech.educafacil.dao.UsuarioDao;
import ec.mileniumtech.educafacil.dao.UsuarioRolDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.RolPerfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para manejar la lógica de seguridad: perfiles, acciones, roles y usuarios.
 *
 * @author christian
 */
@Stateless
@LocalBean
public class SeguridadService {

    private static final Logger log = LogManager.getLogger(SeguridadService.class);

    @EJB
    private PerfilDao perfilDao;

    @EJB
    private PerfilAccionDao perfilAccionDao;

    @EJB
    private RolDao rolDao;

    @EJB
    private RolPerfilDao rolPerfilDao;

    @EJB
    private UsuarioDao usuarioDao;

    @EJB
    private UsuarioRolDao usuarioRolDao;

    @EJB
    private PersonaDao personaDao;

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
     * Asigna una acción a un perfil.
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
     * Asigna un perfil a un rol.
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
     * Asigna un rol a un usuario.
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
