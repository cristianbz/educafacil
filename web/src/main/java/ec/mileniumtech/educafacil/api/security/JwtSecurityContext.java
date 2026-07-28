package ec.mileniumtech.educafacil.api.security;

import jakarta.json.JsonObject;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Implementación de {@link SecurityContext} que inyecta la información
 * del usuario autenticado vía JWT en el contexto de la petición REST.
 * <p>
 * Permite a los recursos obtener el usuario actual mediante:
 * {@code securityContext.getUserPrincipal().getName()}
 * </p>
 */
public class JwtSecurityContext implements SecurityContext {

    private final JsonObject claims;
    private final JwtProvider jwtProvider;
    private final Principal principal;

    public JwtSecurityContext(JsonObject claims, JwtProvider jwtProvider) {
        this.claims = claims;
        this.jwtProvider = jwtProvider;
        this.principal = () -> jwtProvider.getUsuario(claims);
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return jwtProvider.tieneRol(claims, role);
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }

    /**
     * Retorna el ID de persona del usuario autenticado.
     */
    public int getPersonaId() {
        return jwtProvider.getPersonaId(claims);
    }
}
