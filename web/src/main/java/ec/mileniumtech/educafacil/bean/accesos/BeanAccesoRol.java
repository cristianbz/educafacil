/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.accesos;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.RolPerfil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de estado para la pantalla de Gestión de Roles.
 * Contiene las listas y el objeto en edición.
 *
 * @author christian
 */
@Named("beanAccesoRol")
@ViewScoped
public class BeanAccesoRol implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lista completa de roles cargada en @PostConstruct */
    @Getter @Setter
    private List<Rol> listaRoles;

    /** Rol actualmente en edición (nuevo o seleccionado) */
    @Getter @Setter
    private Rol rol;

    /** Rol seleccionado para gestionar sus perfiles */
    @Getter @Setter
    private Rol rolSeleccionado;

    /** Todos los perfiles activos del sistema */
    @Getter @Setter
    private List<Perfil> listaPerfiles;

    /** Perfiles del rol seleccionado (objetos RolPerfil activos) */
    @Getter @Setter
    private List<RolPerfil> perfilesDelRol;

    /** Indica si el formulario está en modo "nuevo" (true) o "editar" (false) */
    @Getter @Setter
    private boolean esNuevo;

    /** Término de búsqueda para filtrar la cuadrícula de roles */
    @Getter @Setter
    private String filtro;

    @PostConstruct
    public void init() {
        rol = new Rol();
        esNuevo = true;
    }
}
