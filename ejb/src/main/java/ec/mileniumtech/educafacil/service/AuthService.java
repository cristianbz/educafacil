package ec.mileniumtech.educafacil.service;

import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para manejar la autenticación y validación de usuarios.
 */
@Stateless
@LocalBean
public class AuthService {

    @EJB
    private UsuarioDaoImpl usuarioDao;

    /**
     * Valida las credenciales de un usuario.
     * @param username Nombre de usuario.
     * @param password Contraseña plana.
     * @return El usuario si las credenciales son válidas, null en caso contrario.
     */
    public Usuario autenticar(String username, String password) {
        Usuario usuario = usuarioDao.consultarUsuarioPorDocumento(username);
        
        if (usuario != null && usuario.isUsuaEstado()) {
            // Se asume que el password se guarda en SHA-256 según lo visto en Encriptar.java
            // Si el password en BD está encriptado, lo comparamos
            String passwordEncriptado = Encriptar.encriptarSHA512(password);
            if (usuario.getUsuaClave().equals(passwordEncriptado)) {
                return usuario;
            }
        }
        return null;
    }

    /**
     * Obtiene los nombres de los roles de un usuario.
     * @param idUsuario ID del usuario.
     * @return Lista de nombres de roles.
     */
    public List<String> obtenerRoles(int idUsuario) {
        List<String> roles = new ArrayList<>();
        Usuario usuario = usuarioDao.getEntityManager().find(Usuario.class, idUsuario);
        if (usuario != null && usuario.getUsuarioRol() != null) {
            for (UsuarioRol ur : usuario.getUsuarioRol()) {
                if (ur.getUrolEstado()) {
                    roles.add(ur.getRol().getRolNombre());
                }
            }
        }
        return roles;
    }
}
