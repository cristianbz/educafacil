/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.accesos;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de estado para la pantalla de Gestión de Usuarios.
 * Contiene las listas y el objeto en edición.
 *
 * @author christian
 */
@Named("beanAccesoUsuario")
@ViewScoped
public class BeanAccesoUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lista completa de usuarios cargada en @PostConstruct */
    @Getter @Setter
    private List<Usuario> listaUsuarios;

    /** Usuario actualmente en edición (nuevo o seleccionado) */
    @Getter @Setter
    private Usuario usuario;

    /** Usuario seleccionado para gestionar sus roles */
    @Getter @Setter
    private Usuario usuarioSeleccionado;

    /** Todos los roles activos del sistema */
    @Getter @Setter
    private List<Rol> listaRoles;

    /** Roles del usuario seleccionado (objetos UsuarioRol activos) */
    @Getter @Setter
    private List<UsuarioRol> rolesDelUsuario;

    /** Indica si el formulario está en modo "nuevo" (true) o "editar" (false) */
    @Getter @Setter
    private boolean esNuevo;

    /** Término de búsqueda para filtrar la cuadrícula de usuarios */
    @Getter @Setter
    private String filtro;

    /** Estado seleccionado para filtrar la lista (Todos, Activos, Inactivos) */
    @Getter @Setter
    private String filtroEstado;

    @PostConstruct
    public void init() {
        usuario = new Usuario();
        usuario.setPersona(new Persona());
        esNuevo = true;
        filtroEstado = "TODOS";
    }
}
