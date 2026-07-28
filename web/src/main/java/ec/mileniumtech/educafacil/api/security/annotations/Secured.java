package ec.mileniumtech.educafacil.api.security.annotations;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar recursos REST que requieren autenticación JWT.
 * <p>
 * Uso:
 * <pre>{@code
 * @Secured
 * @GET
 * @Path("/matriculas")
 * public Response listarMatriculas() { ... }
 * }</pre>
 * </p>
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {
}
