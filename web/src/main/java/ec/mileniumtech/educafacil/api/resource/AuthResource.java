package ec.mileniumtech.educafacil.api.resource;

import ec.mileniumtech.educafacil.api.dto.ApiResponse;
import ec.mileniumtech.educafacil.api.dto.auth.LoginRequest;
import ec.mileniumtech.educafacil.api.dto.auth.LoginResponse;
import ec.mileniumtech.educafacil.api.service.AuthRestService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para autenticación de usuarios.
 * <p>
 * Endpoints públicos (no requieren JWT).
 * </p>
 */
@Path("/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthRestService authService;

    /**
     * Autentica un usuario con sus credenciales.
     *
     * @param request credenciales (usuario y clave)
     * @return token JWT y datos del usuario
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        LoginResponse loginResponse = authService.login(
                request.getUsuario(), request.getClave());
        return Response.ok(ApiResponse.ok(loginResponse, "Autenticación exitosa")).build();
    }
}
