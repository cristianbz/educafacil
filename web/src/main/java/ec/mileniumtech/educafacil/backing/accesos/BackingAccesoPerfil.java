/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.accesos;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.accesos.BeanAccesoPerfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
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
 * Backing Bean para la pantalla de Gestión de Acceso por Perfil.
 * Orquesta las operaciones CRUD de Perfil y la asignación de Acciones.
 *
 * @author christian
 */
@Named("backingAccesoPerfil")
@ViewScoped
public class BackingAccesoPerfil implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingAccesoPerfil.class);

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @Inject
    @Getter
    private BeanAccesoPerfil beanAccesoPerfil;

    @EJB
    private AdministracionService administracionService;

    // =========================================================
    // Inicialización
    // =========================================================

    @PostConstruct
    public void init() {
        cargarPerfiles();
        getBeanAccesoPerfil().setListaAcciones(administracionService.listarAcciones());
        getBeanAccesoPerfil().setPerfil(new Perfil());
        getBeanAccesoPerfil().setEsNuevo(true);
    }

    // =========================================================
    // CRUD de Perfiles
    // =========================================================

    /**
     * Prepara el formulario para registrar un nuevo perfil y abre el diálogo.
     */
    public void nuevoPerfil() {
        getBeanAccesoPerfil().setPerfil(new Perfil());
        getBeanAccesoPerfil().setEsNuevo(true);
        Mensaje.verDialogo("dlgPerfil");
    }

    /**
     * Carga el perfil seleccionado en el formulario y abre el diálogo de edición.
     *
     * @param perfil perfil a editar
     */
    public void editarPerfil(Perfil perfil) {
        if (perfil != null) {
            getBeanAccesoPerfil().setPerfil(perfil);
            getBeanAccesoPerfil().setEsNuevo(false);
            Mensaje.verDialogo("dlgPerfil");
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    getMensajesBacking().getPropiedad("error.editarOferta"));
        }
    }

    /**
     * Guarda (crea o actualiza) el perfil en edición y refresca la lista.
     */
    public void guardarPerfil() {
        try {
            Perfil perfil = getBeanAccesoPerfil().getPerfil();
            // Al crear, el estado inicia en activo por defecto
            if (getBeanAccesoPerfil().isEsNuevo() && perfil.getEstado() == null) {
                perfil.setEstado(true);
            }
            administracionService.guardarPerfil(perfil);
            cargarPerfiles();
            Mensaje.ocultarDialogo("dlgPerfil");
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO,
                    getMensajesBacking().getPropiedad("info"),
                    getMensajesBacking().getPropiedad("info.agregar"));
        } catch (Exception e) {
            log.error("Error al guardar perfil", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    /**
     * Realiza la eliminación lógica del perfil (estado → false).
     *
     * @param perfil perfil a desactivar
     */
    public void eliminarLogicoPerfil(Perfil perfil) {
        try {
            if (perfil != null && perfil.getId() != null) {
                administracionService.eliminarLogicoPerfil(perfil.getId());
                cargarPerfiles();
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN,
                        getMensajesBacking().getPropiedad("info"),
                        "Perfil desactivado exitosamente.");
            }
        } catch (Exception e) {
            log.error("Error al eliminar perfil", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    // =========================================================
    // Gestión de Acciones por Perfil
    // =========================================================

    /**
     * Carga las acciones asignadas al perfil y abre el diálogo de asignación.
     *
     * @param perfil perfil cuyas acciones se van a gestionar
     */
    public void gestionarAcciones(Perfil perfil) {
        if (perfil != null && perfil.getId() != null) {
            getBeanAccesoPerfil().setPerfilSeleccionado(perfil);
            getBeanAccesoPerfil().setAccionesDelPerfil(
                    administracionService.listarAccionesPorPerfil(perfil.getId()));
            Mensaje.verDialogo("dlgAcciones");
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN,
                    getMensajesBacking().getPropiedad("info"),
                    "Debe seleccionar un perfil válido.");
        }
    }

    /**
     * Verifica si una acción ya está asignada al perfil seleccionado.
     *
     * @param accionId identificador de la acción
     * @return true si la acción está asignada
     */
    public boolean accionAsignada(String accionId) {
        if (getBeanAccesoPerfil().getAccionesDelPerfil() == null) return false;
        return getBeanAccesoPerfil().getAccionesDelPerfil().stream()
                .anyMatch(pa -> pa.getAccion().getId().equals(accionId));
    }

    /**
     * Alterna la asignación de una acción al perfil seleccionado.
     * Si estaba asignada la quita; si no estaba la agrega.
     *
     * @param accion acción a alternar
     */
    public void toggleAccion(Accion accion) {
        try {
            Perfil perfilSeleccionado = getBeanAccesoPerfil().getPerfilSeleccionado();
            if (perfilSeleccionado == null || accion == null) return;

            Integer perfilId = perfilSeleccionado.getId();
            String accionId = accion.getId();

            if (accionAsignada(accionId)) {
                administracionService.quitarAccionDePerfil(perfilId, accionId);
            } else {
                administracionService.asignarAccionAPerfil(perfilId, accionId);
            }
            // Refrescar acciones asignadas
            getBeanAccesoPerfil().setAccionesDelPerfil(
                    administracionService.listarAccionesPorPerfil(perfilId));

        } catch (Exception e) {
            log.error("Error al alternar acción del perfil", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    e.getMessage());
        }
    }

    /**
     * Cierra el diálogo de acciones y limpia el estado de selección.
     */
    public void cerrarDialogoAcciones() {
        getBeanAccesoPerfil().setPerfilSeleccionado(null);
        getBeanAccesoPerfil().setAccionesDelPerfil(null);
        Mensaje.ocultarDialogo("dlgAcciones");
    }

    // =========================================================
    // Helpers públicos para la vista XHTML
    // =========================================================

    /**
     * Devuelve las acciones asignadas a un perfil dado su ID.
     * Usado en la expansión de fila de la tabla de perfiles.
     *
     * @param perfilId identificador del perfil
     * @return lista de PerfilAccion activas
     */
    public java.util.List<ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion>
            obtenerAccionesDePerfilPorId(Integer perfilId) {
        if (perfilId == null) return new java.util.ArrayList<>();
        return administracionService.listarAccionesPorPerfil(perfilId);
    }

    /**
     * Obtiene la lista de perfiles filtrados por el criterio de búsqueda (nombre o descripción).
     *
     * @return lista de perfiles filtrados
     */
    public java.util.List<Perfil> getPerfilesFiltrados() {
        if (getBeanAccesoPerfil().getListaPerfiles() == null) {
            return new java.util.ArrayList<>();
        }
        String filter = getBeanAccesoPerfil().getFiltro();
        if (filter == null || filter.trim().isEmpty()) {
            return getBeanAccesoPerfil().getListaPerfiles();
        }
        String lowerFilter = filter.toLowerCase().trim();
        return getBeanAccesoPerfil().getListaPerfiles().stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(lowerFilter))
                        || (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(lowerFilter)))
                .collect(java.util.stream.Collectors.toList());
    }

    // =========================================================
    // Helpers privados
    // =========================================================

    private void cargarPerfiles() {
        getBeanAccesoPerfil().setListaPerfiles(administracionService.listarPerfiles());
    }
}
