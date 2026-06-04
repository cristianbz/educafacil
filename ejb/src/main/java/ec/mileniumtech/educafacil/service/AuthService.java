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

	    public Usuario autenticar(String username, String password) {
	        Usuario usuario = usuarioDao.consultarUsuarioPorDocumento(username);
	        
	        if (usuario != null && usuario.isUsuaEstado()) {
	            String hashAlmacenado = usuario.getUsuaClave();
	            boolean valido = false;

	            if (Encriptar.esHashBCrypt(hashAlmacenado)) {
	                valido = Encriptar.verificarBCrypt(password, hashAlmacenado);
	            } else {
	                valido = hashAlmacenado.equals(Encriptar.encriptarSHA512(password));
	                if (valido) {
	                    usuario.setUsuaClave(Encriptar.encriptarBCrypt(password));
	                    usuarioDao.actualizar(usuario);
	                }
	            }
	            if (valido) {
	                return usuario;
	            }
	        }
	        return null;
	    }

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
