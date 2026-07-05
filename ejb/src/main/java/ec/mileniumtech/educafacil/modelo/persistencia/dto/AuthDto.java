package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para peticiones de autenticación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "{auth.username.required}")
    private String username;
    
    @NotBlank(message = "{auth.password.required}")
    private String password;
}
