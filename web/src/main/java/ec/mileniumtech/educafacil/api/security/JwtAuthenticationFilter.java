package ec.mileniumtech.educafacil.api.security;

import ec.mileniumtech.educafacil.api.dto.ErrorResponse;
import ec.mileniumtech.educafacil.api.security.annotations.Secured;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Filtro JAX-RS que intercepta peticiones a recursos anotados con {@link Secured}
 * y valida el token JWT presente en el header {@code Authorization: Bearer <token>}.
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger log = LogManager.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    private JwtProvider jwtProvider;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Obtener el header Authorization
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            abortWithUnauthorized(requestContext, "Header Authorization requerido. Formato: Bearer <token>");
            return;
        }

        // Extraer el token
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isEmpty()) {
            abortWithUnauthorized(requestContext, "Token JWT vacío");
            return;
        }

        // Validar el token
        JsonObject claims = jwtProvider.validarToken(token);

        if (claims == null) {
            abortWithUnauthorized(requestContext, "Token JWT inválido o expirado");
            return;
        }

        // Verificar roles si el método lo requiere
        if (!tieneAccesoPermitido(claims)) {
            log.warn("Acceso denegado por rol insuficiente para: {}",
                    requestContext.getUriInfo().getPath());
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity(ErrorResponse.builder()
                                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    .status(403)
                                    .error("FORBIDDEN")
                                    .message("No tiene permisos para acceder a este recurso")
                                    .build())
                            .type(MediaType.APPLICATION_JSON)
                            .build());
            return;
        }

        // Inyectar el security context con los claims del usuario
        requestContext.setSecurityContext(new JwtSecurityContext(claims, jwtProvider));
    }

    /**
     * Verifica si el usuario autenticado tiene los roles requeridos
     * por la anotación {@code @RolesAllowed} del método o clase.
     */
    private boolean tieneAccesoPermitido(JsonObject claims) {
        Class<?> resourceClass = resourceInfo.getResourceClass();
        Method resourceMethod = resourceInfo.getResourceMethod();

        if (resourceClass == null || resourceMethod == null) {
            return true;
        }

        // Verificar @RolesAllowed a nivel de método
        jakarta.annotation.security.RolesAllowed rolesAllowed =
                resourceMethod.getAnnotation(jakarta.annotation.security.RolesAllowed.class);

        if (rolesAllowed == null) {
            // Verificar a nivel de clase
            rolesAllowed = resourceClass.getAnnotation(jakarta.annotation.security.RolesAllowed.class);
        }

        // Si no hay restricción de roles, permitir acceso
        if (rolesAllowed == null) {
            return true;
        }

        // Verificar que el usuario tenga al menos uno de los roles requeridos
        String[] rolesRequeridos = rolesAllowed.value();
        for (String rol : rolesRequeridos) {
            if (jwtProvider.tieneRol(claims, rol)) {
                return true;
            }
        }

        return false;
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponse.builder()
                                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .status(401)
                                .error("UNAUTHORIZED")
                                .message(message)
                                .build())
                        .type(MediaType.APPLICATION_JSON)
                        .build());
    }
}
