package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
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
    
    private String username;
    private String password;
}
