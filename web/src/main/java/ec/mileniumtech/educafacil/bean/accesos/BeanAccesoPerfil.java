/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.accesos;

import java.io.Serializable;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean de estado para la pantalla de Gestión de Acceso por Perfil.
 * Contiene las listas y el objeto en edición.
 *
 * @author christian
 */
@Named("beanAccesoPerfil")
@ViewScoped
public class BeanAccesoPerfil implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lista completa de perfiles cargada en @PostConstruct */
    @Getter @Setter
    private List<Perfil> listaPerfiles;

    /** Perfil actualmente en edición (nuevo o seleccionado) */
    @Getter @Setter
    private Perfil perfil;

    /** Perfil seleccionado para gestionar sus acciones */
    @Getter @Setter
    private Perfil perfilSeleccionado;

    /** Todas las acciones activas del sistema */
    @Getter @Setter
    private List<Accion> listaAcciones;

    /** Acciones del perfil seleccionado (objetos PerfilAccion activos) */
    @Getter @Setter
    private List<PerfilAccion> accionesDelPerfil;

    /** Indica si el formulario está en modo "nuevo" (true) o "editar" (false) */
    @Getter @Setter
    private boolean esNuevo;

    /** Término de búsqueda para filtrar la cuadrícula de perfiles */
    @Getter @Setter
    private String filtro;

    @PostConstruct
    public void init() {
        perfil = new Perfil();
        esNuevo = true;
    }
}
