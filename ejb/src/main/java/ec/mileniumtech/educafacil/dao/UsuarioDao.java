package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.ObjetosMenuDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Usuario.
 * Define operaciones de acceso a datos espec\u00edficas para Usuario.
 */
@Local
public interface UsuarioDao extends GenericoDao<Usuario, Long> {

    Usuario actualizaUsuario(Usuario usuario);
    Usuario agregarUsuario(Usuario usuario);
    Usuario consultarUsuario(String usuario);
    Usuario consultarUsuarioPorDocumento(String documento);
    List<ObjetosMenuDto> buscarAccesosUsuario(String correo);
    List<Usuario> consultarUsuariosPorIdRol(int idRol);
    List<Usuario> listarTodosUsuarios();
}
