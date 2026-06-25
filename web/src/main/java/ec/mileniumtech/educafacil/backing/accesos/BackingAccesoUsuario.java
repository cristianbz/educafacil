/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.accesos;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.accesos.BeanAccesoUsuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * Backing Bean para la pantalla de Gestión de Usuarios.
 * Orquesta las operaciones CRUD de Usuario y la asignación de Roles.
 *
 * @author christian
 */
@Named("backingAccesoUsuario")
@ViewScoped
public class BackingAccesoUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingAccesoUsuario.class);

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @Inject
    @Getter
    private BeanAccesoUsuario beanAccesoUsuario;

    @EJB
    private AdministracionService administracionService;

    private String passwordOriginal;

    // =========================================================
    // Inicialización
    // =========================================================

    @PostConstruct
    public void init() {
        cargarUsuarios();
        getBeanAccesoUsuario().setListaRoles(administracionService.listarRolesActivos());
        Usuario u = new Usuario();
        u.setPersona(new Persona());
        getBeanAccesoUsuario().setUsuario(u);
        getBeanAccesoUsuario().setEsNuevo(true);
    }

    // =========================================================
    // CRUD de Usuarios
    // =========================================================

    /**
     * Prepara el formulario para registrar un nuevo usuario y abre el diálogo.
     */
    public void nuevoUsuario() {
        Usuario u = new Usuario();
        u.setPersona(new Persona());
        getBeanAccesoUsuario().setUsuario(u);
        getBeanAccesoUsuario().setEsNuevo(true);
        this.passwordOriginal = null;
        Mensaje.verDialogo("dlgUsuario");
    }

    /**
     * Carga el usuario seleccionado en el formulario y abre el diálogo de edición.
     *
     * @param usuario usuario a editar
     */
    public void editarUsuario(Usuario usuario) {
        if (usuario != null) {
            this.passwordOriginal = usuario.getUsuaClave();
            // Limpiar clave para que no se muestre el hash en la UI
            usuario.setUsuaClave("");
            getBeanAccesoUsuario().setUsuario(usuario);
            getBeanAccesoUsuario().setEsNuevo(false);
            Mensaje.verDialogo("dlgUsuario");
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    getMensajesBacking().getPropiedad("error.editarOferta"));
        }
    }

    /**
     * Guarda (crea o actualiza) el usuario en edición y refresca la lista.
     */
    public void guardarUsuario() {
        try {
            Usuario usuario = getBeanAccesoUsuario().getUsuario();
            
            // Si es edición y no ingresaron nueva clave, restaurar original
            if (!getBeanAccesoUsuario().isEsNuevo()) {
                if (usuario.getUsuaClave() == null || usuario.getUsuaClave().trim().isEmpty()) {
                    usuario.setUsuaClave(this.passwordOriginal);
                }
            } else {
                // Al crear, el estado inicia en activo por defecto
                usuario.setUsuaEstado(true);
            }

            // El usuaUsuario (username para login/consulta) será igual al correo electrónico de la persona
            usuario.setUsuaUsuario(usuario.getPersona().getPersDocumentoIdentidad());

            administracionService.guardarUsuario(usuario);
            cargarUsuarios();
            Mensaje.ocultarDialogo("dlgUsuario");
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO,
                    getMensajesBacking().getPropiedad("info"),
                    getMensajesBacking().getPropiedad("info.agregar"));
        } catch (Exception e) {
            log.error("Error al guardar usuario", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    /**
     * Realiza la eliminación lógica del usuario (estado → false).
     *
     * @param usuario usuario a desactivar
     */
    public void eliminarLogicoUsuario(Usuario usuario) {
        try {
            if (usuario != null && usuario.getUsuaId() != null) {
                administracionService.eliminarLogicoUsuario(usuario.getUsuaId());
                cargarUsuarios();
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN,
                        getMensajesBacking().getPropiedad("info"),
                        "Usuario desactivado exitosamente.");
            }
        } catch (Exception e) {
            log.error("Error al desactivar usuario", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    // =========================================================
    // Gestión de Roles por Usuario
    // =========================================================

    /**
     * Carga los roles asignados al usuario y abre el diálogo de asignación.
     *
     * @param usuario usuario cuyos roles se van a gestionar
     */
    public void gestionarRoles(Usuario usuario) {
        if (usuario != null && usuario.getUsuaId() != null) {
            getBeanAccesoUsuario().setUsuarioSeleccionado(usuario);
            getBeanAccesoUsuario().setRolesDelUsuario(
                    administracionService.listarRolesPorUsuarioActivos(usuario.getUsuaId()));
            Mensaje.verDialogo("dlgRolesAsignacion");
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN,
                    getMensajesBacking().getPropiedad("info"),
                    "Debe seleccionar un usuario válido.");
        }
    }

    /**
     * Verifica si un rol ya está asignado al usuario seleccionado.
     *
     * @param rolId identificador del rol
     * @return true si el rol está asignado
     */
    public boolean rolAsignado(Integer rolId) {
        if (getBeanAccesoUsuario().getRolesDelUsuario() == null) return false;
        return getBeanAccesoUsuario().getRolesDelUsuario().stream()
                .anyMatch(ur -> ur.getRol().getRolId().equals(rolId));
    }

    /**
     * Alterna la asignación de un rol al usuario seleccionado.
     * Si estaba asignado lo quita; si no estaba lo agrega.
     *
     * @param rol rol a alternar
     */
    public void toggleRol(Rol rol) {
        try {
            Usuario usuarioSeleccionado = getBeanAccesoUsuario().getUsuarioSeleccionado();
            if (usuarioSeleccionado == null || rol == null) return;

            Integer usuarioId = usuarioSeleccionado.getUsuaId();
            Integer rolId = rol.getRolId();

            if (rolAsignado(rolId)) {
                administracionService.quitarRolDeUsuario(usuarioId, rolId);
            } else {
                administracionService.asignarRolAUsuario(usuarioId, rolId);
            }
            // Refrescar roles asignados
            getBeanAccesoUsuario().setRolesDelUsuario(
                    administracionService.listarRolesPorUsuarioActivos(usuarioId));

        } catch (Exception e) {
            log.error("Error al alternar rol del usuario", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    /**
     * Cierra el diálogo de roles y limpia el estado de selección.
     */
    public void cerrarDialogoRoles() {
        getBeanAccesoUsuario().setUsuarioSeleccionado(null);
        getBeanAccesoUsuario().setRolesDelUsuario(null);
        Mensaje.ocultarDialogo("dlgRolesAsignacion");
    }

    // =========================================================
    // Helpers públicos para la vista XHTML
    // =========================================================

    /**
     * Devuelve los roles asignados a un usuario dado su ID.
     * Usado para mostrar los chips de roles en la tabla de usuarios.
     *
     * @param usuarioId identificador del usuario
     * @return lista de UsuarioRol activas
     */
    public java.util.List<UsuarioRol> obtenerRolesDeUsuarioPorId(Integer usuarioId) {
        if (usuarioId == null) return new java.util.ArrayList<>();
        return administracionService.listarRolesPorUsuarioActivos(usuarioId);
    }

    /**
     * Genera las iniciales de una persona (Ej. Juan Pérez -> JP).
     *
     * @param p persona
     * @return iniciales en mayúscula
     */
    public String obtenerIniciales(Persona p) {
        if (p == null) return "U";
        String n = p.getPersNombres() != null ? p.getPersNombres().trim() : "";
        String a = p.getPersApellidos() != null ? p.getPersApellidos().trim() : "";
        String init = "";
        if (!n.isEmpty()) init += n.substring(0, 1).toUpperCase();
        if (!a.isEmpty()) init += a.substring(0, 1).toUpperCase();
        return init.isEmpty() ? "U" : init;
    }

    /**
     * Obtiene la lista de usuarios filtrados por el criterio de búsqueda (nombre, email)
     * y el estado seleccionado en el combo.
     *
     * @return lista de usuarios filtrados
     */
    public java.util.List<Usuario> getUsuariosFiltrados() {
        if (getBeanAccesoUsuario().getListaUsuarios() == null) {
            return new java.util.ArrayList<>();
        }

        // Filtro por Estado
        String stateFilter = getBeanAccesoUsuario().getFiltroEstado();
        java.util.stream.Stream<Usuario> stream = getBeanAccesoUsuario().getListaUsuarios().stream();
        if ("ACTIVOS".equals(stateFilter)) {
            stream = stream.filter(Usuario::isUsuaEstado);
        } else if ("INACTIVOS".equals(stateFilter)) {
            stream = stream.filter(u -> !u.isUsuaEstado());
        }

        // Filtro por Texto
        String filter = getBeanAccesoUsuario().getFiltro();
        if (filter == null || filter.trim().isEmpty()) {
            return stream.collect(java.util.stream.Collectors.toList());
        }

        String lowerFilter = filter.toLowerCase().trim();
        return stream.filter(u -> {
            Persona p = u.getPersona();
            if (p == null) return false;
            boolean matchNombres = p.getPersNombres() != null && p.getPersNombres().toLowerCase().contains(lowerFilter);
            boolean matchApellidos = p.getPersApellidos() != null && p.getPersApellidos().toLowerCase().contains(lowerFilter);
            boolean matchEmail = p.getPersCorreoElectronico() != null && p.getPersCorreoElectronico().toLowerCase().contains(lowerFilter);
            boolean matchUsername = u.getUsuaUsuario() != null && u.getUsuaUsuario().toLowerCase().contains(lowerFilter);
            return matchNombres || matchApellidos || matchEmail || matchUsername;
        }).collect(java.util.stream.Collectors.toList());
    }

    // =========================================================
    // Helpers privados
    // =========================================================

    private void cargarUsuarios() {
        getBeanAccesoUsuario().setListaUsuarios(administracionService.listarUsuarios());
    }

    /**
     * Devuelve el total de usuarios activos.
     */
    public long getCountActivos() {
        if (getBeanAccesoUsuario().getListaUsuarios() == null) return 0;
        return getBeanAccesoUsuario().getListaUsuarios().stream().filter(Usuario::isUsuaEstado).count();
    }

    /**
     * Devuelve el total de usuarios inactivos.
     */
    public long getCountInactivos() {
        if (getBeanAccesoUsuario().getListaUsuarios() == null) return 0;
        return getBeanAccesoUsuario().getListaUsuarios().stream().filter(u -> !u.isUsuaEstado()).count();
    }
}
