package ec.mileniumtech.educafacil.resource.filter;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import ec.mileniumtech.educafacil.utilitarios.seguridad.JwtUtil;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private UriInfo uriInfo;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
    }

    @Test
    void excluyeRutaLogin() throws Exception {
        when(uriInfo.getPath()).thenReturn("api/auth/login");

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
    }

    @Test
    void excluyeRutaOpenApi() throws Exception {
        when(uriInfo.getPath()).thenReturn("openapi");

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
    }

    @Test
    void rechazaPeticionSinAuthorization() throws Exception {
        when(uriInfo.getPath()).thenReturn("api/cursos");
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.filter(requestContext);

        verify(requestContext).abortWith(argThat(response ->
                response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
    }

    @Test
    void rechazaPeticionConFormatoInvalido() throws Exception {
        when(uriInfo.getPath()).thenReturn("api/cursos");
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Token sin-bearer");

        filter.filter(requestContext);

        verify(requestContext).abortWith(any(Response.class));
    }

    @Test
    void aceptaTokenValidoYEstableceSecurityContext() throws Exception {
        when(uriInfo.getPath()).thenReturn("api/cursos");
        List<String> roles = Arrays.asList("ADMIN");
        String token = JwtUtil.generarToken("usuario@test.com", roles);
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(requestContext).setSecurityContext(any(SecurityContext.class));
    }

    @Test
    void rechazaTokenInvalido() throws Exception {
        when(uriInfo.getPath()).thenReturn("api/cursos");
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token.invalido");

        filter.filter(requestContext);

        verify(requestContext).abortWith(argThat(response ->
                response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
    }
}
