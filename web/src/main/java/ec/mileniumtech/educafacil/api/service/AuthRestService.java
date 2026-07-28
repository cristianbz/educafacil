package ec.mileniumtech.educafacil.api.service;

import ec.mileniumtech.educafacil.api.dto.auth.LoginResponse;
import ec.mileniumtech.educafacil.api.exception.BadRequestException;
import ec.mileniumtech.educafacil.api.exception.UnauthorizedException;
import ec.mileniumtech.educafacil.api.security.JwtProvider;
import ec.mileniumtech.educafacil.dao.UsuarioDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación para la API REST.
 * Valida credenciales y genera tokens JWT.
 */
@Stateless
public class AuthRestService {

    private static final Logger log = LogManager.getLogger(AuthRestService.class);

    @Inject
    private JwtProvider jwtProvider;

    @EJB
    private MatriculaFacade matriculaFacade;

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param usuario nombre de usuario
     * @param clave   contraseña (sin encriptar)
     * @return respuesta con token JWT y datos del usuario
     * @throws UnauthorizedException si las credenciales son inválidas
     */
    public LoginResponse login(String usuario, String clave) {
    	boolean valido=false;
        if (usuario == null || usuario.isBlank()) {
            throw new BadRequestException("El nombre de usuario es obligatorio");
        }
        if (clave == null || clave.isBlank()) {
            throw new BadRequestException("La clave es obligatoria");
        }

        // Buscar usuario
        Usuario user = matriculaFacade.consultarUsuario(usuario);
        if (user == null) {
            log.warn("Intento de login con usuario inexistente: {}", usuario);
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Validar estado activo
        if (!user.isUsuaEstado()) {
            log.warn("Intento de login con usuario inactivo: {}", usuario);
            throw new UnauthorizedException("Usuario inactivo");
        }

        // Validar contraseña
//        String claveEncriptada = Encriptar.encriptarSHA512(clave);
        String hashAlmacenado = user.getUsuaClave();
        valido=Encriptar.verificarBCrypt(clave, hashAlmacenado);
        if (!valido) {
            log.warn("Intento de login con clave incorrecta: {}", usuario);
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Obtener roles
        List<UsuarioRol> roles = matriculaFacade.listaUsuarioRolPorUsuario(user.getUsuaId());
        String[] rolesArray = roles.stream()
                .map(ur -> String.valueOf(ur.getRol().getRolId()))
                .toArray(String[]::new);

        // Generar token
        int personaId = user.getPersona() != null ? user.getPersona().getPersId() : 0;
        String token = jwtProvider.generarToken(
                user.getUsuaUsuario(),
                personaId,
                rolesArray);

        log.info("Login exitoso: {}", usuario);

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .expiraEn(System.currentTimeMillis() + (8 * 3600 * 1000L)) // 8 horas
                .usuario(user.getUsuaUsuario())
                .nombres(user.getPersona() != null ? user.getPersona().getPersNombres() : null)
                .apellidos(user.getPersona() != null ? user.getPersona().getPersApellidos() : null)
                .correo(user.getPersona() != null ? user.getPersona().getPersCorreoElectronico() : null)
                .roles(roles.stream()
                        .map(ur -> ur.getRol().getRolNombre())
                        .collect(Collectors.toList()))
                .build();
    }
}
