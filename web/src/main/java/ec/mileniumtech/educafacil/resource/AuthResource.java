package ec.mileniumtech.educafacil.resource;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.AuthDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.TokenResponseDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.service.AuthService;
import ec.mileniumtech.educafacil.utilitarios.seguridad.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import java.util.List;

/**
 * Recurso REST para la autenticación y obtención de tokens.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/login")
    @Operation(summary = "Inicia sesión y obtiene un token JWT", description = "Valida las credenciales de un usuario y retorna un token válido por 8 horas.")
    @APIResponse(responseCode = "200", description = "Autenticación exitosa")
    @APIResponse(responseCode = "401", description = "Credenciales inválidas")
    public Response login(AuthDto authDto) {
        Usuario usuario = authService.autenticar(authDto.getUsername(), authDto.getPassword());

        if (usuario != null) {
            List<String> roles = authService.obtenerRoles(usuario.getUsuaId());
            String token = JwtUtil.generarToken(usuario.getUsuaUsuario(), roles);

            TokenResponseDto response = new TokenResponseDto(
                    token,
                    usuario.getUsuaUsuario(),
                    roles,
                    "8 hours"
            );
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Usuario o contraseña incorrectos")
                    .build();
        }
    }
}
