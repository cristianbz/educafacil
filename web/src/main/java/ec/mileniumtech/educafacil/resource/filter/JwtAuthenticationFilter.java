package ec.mileniumtech.educafacil.resource.filter;

import ec.mileniumtech.educafacil.utilitarios.seguridad.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Filtro de autenticación para validar tokens JWT en las peticiones REST.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Excluir el endpoint de login y la documentación OpenAPI de la validación
        if (path.contains("auth/login") || path.contains("openapi")) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Token faltante o formato inválido (Authorization: Bearer <token>)")
                    .build());
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            Claims claims = JwtUtil.validarToken(token);
            String username = claims.getSubject();
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");

            // Establecer el contexto de seguridad para la petición
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> username;
                }

                @Override
                public boolean isUserInRole(String role) {
                    return roles != null && roles.contains(role);
                }

                @Override
                public boolean isSecure() {
                    return requestContext.getSecurityContext().isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return "JWT";
                }
            });

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Token inválido o expirado. Motivo: " + e.getMessage())
                    .build());
        }
    }
}
