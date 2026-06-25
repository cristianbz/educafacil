/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.accesos;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.accesos.BeanAccesoRol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.RolPerfil;
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
 * Backing Bean para la pantalla de Gestión de Roles.
 * Orquesta las operaciones CRUD de Rol y la asignación de Perfiles.
 *
 * @author christian
 */
@Named("backingAccesoRol")
@ViewScoped
public class BackingAccesoRol implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingAccesoRol.class);

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @Inject
    @Getter
    private BeanAccesoRol beanAccesoRol;

    @EJB
    private AdministracionService administracionService;

    // =========================================================
    // Inicialización
    // =========================================================

    @PostConstruct
    public void init() {
        cargarRoles();
        getBeanAccesoRol().setListaPerfiles(administracionService.listarPerfilesActivos());
        getBeanAccesoRol().setRol(new Rol());
        getBeanAccesoRol().setEsNuevo(true);
    }

    // =========================================================
    // CRUD de Roles
    // =========================================================

    /**
     * Prepara el formulario para registrar un nuevo rol y abre el diálogo.
     */
    public void nuevoRol() {
        getBeanAccesoRol().setRol(new Rol());
        getBeanAccesoRol().setEsNuevo(true);
        Mensaje.verDialogo("dlgRol");
    }

    /**
     * Carga el rol seleccionado en el formulario y abre el diálogo de edición.
     *
     * @param rol rol a editar
     */
    public void editarRol(Rol rol) {
        if (rol != null) {
            getBeanAccesoRol().setRol(rol);
            getBeanAccesoRol().setEsNuevo(false);
            Mensaje.verDialogo("dlgRol");
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    getMensajesBacking().getPropiedad("error.editarOferta"));
        }
    }

    /**
     * Guarda (crea o actualiza) el rol en edición y refresca la lista.
     */
    public void guardarRol() {
        try {
            Rol rol = getBeanAccesoRol().getRol();
            // Al crear, el estado inicia en activo por defecto
            if (getBeanAccesoRol().isEsNuevo() && rol.getRolEstado() == null) {
                rol.setRolEstado(true);
            }
            administracionService.guardarRol(rol);
            cargarRoles();
            Mensaje.ocultarDialogo("dlgRol");
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO,
                    getMensajesBacking().getPropiedad("info"),
                    getMensajesBacking().getPropiedad("info.agregar"));
        } catch (Exception e) {
            log.error("Error al guardar rol", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    /**
     * Realiza la eliminación lógica del rol (estado → false).
     *
     * @param rol rol a desactivar
     */
    public void eliminarLogicoRol(Rol rol) {
        try {
            if (rol != null && rol.getRolId() != null) {
                administracionService.eliminarLogicoRol(rol.getRolId());
                cargarRoles();
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN,
                        getMensajesBacking().getPropiedad("info"),
                        "Rol desactivado exitosamente.");
            }
        } catch (Exception e) {
            log.error("Error al eliminar rol", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    // =========================================================
    // Gestión de Perfiles por Rol
    // =========================================================

    /**
     * Carga los perfiles asignados al rol y abre el diálogo de asignación.
     *
     * @param rol rol cuyos perfiles se van a gestionar
     */
    public void gestionarPerfiles(Rol rol) {
        if (rol != null && rol.getRolId() != null) {
            getBeanAccesoRol().setRolSeleccionado(rol);
            getBeanAccesoRol().setPerfilesDelRol(
                    administracionService.listarPerfilesPorRol(rol.getRolId()));
            Mensaje.verDialogo("dlgPerfiles");
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN,
                    getMensajesBacking().getPropiedad("info"),
                    "Debe seleccionar un rol válido.");
        }
    }

    /**
     * Verifica si un perfil ya está asignado al rol seleccionado.
     *
     * @param perfilId identificador del perfil
     * @return true si el perfil está asignado
     */
    public boolean perfilAsignado(Integer perfilId) {
        if (getBeanAccesoRol().getPerfilesDelRol() == null) return false;
        return getBeanAccesoRol().getPerfilesDelRol().stream()
                .anyMatch(rp -> rp.getPerfil().getId().equals(perfilId));
    }

    /**
     * Alterna la asignación de un perfil al rol seleccionado.
     * Si estaba asignado lo quita; si no estaba lo agrega.
     *
     * @param perfil perfil a alternar
     */
    public void togglePerfil(Perfil perfil) {
        try {
            Rol rolSeleccionado = getBeanAccesoRol().getRolSeleccionado();
            if (rolSeleccionado == null || perfil == null) return;

            Integer rolId = rolSeleccionado.getRolId();
            Integer perfilId = perfil.getId();

            if (perfilAsignado(perfilId)) {
                administracionService.quitarPerfilDeRol(rolId, perfilId);
            } else {
                administracionService.asignarPerfilARol(rolId, perfilId);
            }
            // Refrescar perfiles asignados
            getBeanAccesoRol().setPerfilesDelRol(
                    administracionService.listarPerfilesPorRol(rolId));

        } catch (Exception e) {
            log.error("Error al alternar perfil del rol", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    /**
     * Cierra el diálogo de perfiles y limpia el estado de selección.
     */
    public void cerrarDialogoPerfiles() {
        getBeanAccesoRol().setRolSeleccionado(null);
        getBeanAccesoRol().setPerfilesDelRol(null);
        Mensaje.ocultarDialogo("dlgPerfiles");
    }

    // =========================================================
    // Helpers públicos para la vista XHTML
    // =========================================================

    /**
     * Devuelve los perfiles asignados a un rol dado su ID.
     * Usado para mostrar los chips de perfiles en la tarjeta del rol.
     *
     * @param rolId identificador del rol
     * @return lista de RolPerfil activas
     */
    public java.util.List<RolPerfil> obtenerPerfilesDeRolPorId(Integer rolId) {
        if (rolId == null) return new java.util.ArrayList<>();
        return administracionService.listarPerfilesPorRol(rolId);
    }

    /**
     * Obtiene la lista de roles filtrados por el criterio de búsqueda (nombre o descripción).
     *
     * @return lista de roles filtrados
     */
    public java.util.List<Rol> getRolesFiltrados() {
        if (getBeanAccesoRol().getListaRoles() == null) {
            return new java.util.ArrayList<>();
        }
        String filter = getBeanAccesoRol().getFiltro();
        if (filter == null || filter.trim().isEmpty()) {
            return getBeanAccesoRol().getListaRoles();
        }
        String lowerFilter = filter.toLowerCase().trim();
        return getBeanAccesoRol().getListaRoles().stream()
                .filter(r -> (r.getRolNombre() != null && r.getRolNombre().toLowerCase().contains(lowerFilter))
                        || (r.getRolDescripcion() != null && r.getRolDescripcion().toLowerCase().contains(lowerFilter)))
                .collect(java.util.stream.Collectors.toList());
    }

    // =========================================================
    // Helpers privados
    // =========================================================

    private void cargarRoles() {
        getBeanAccesoRol().setListaRoles(administracionService.listarRoles());
    }
}
