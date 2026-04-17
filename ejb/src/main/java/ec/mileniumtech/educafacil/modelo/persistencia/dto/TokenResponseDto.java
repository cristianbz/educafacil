package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación con el token generado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String token;
    private String username;
    private List<String> roles;
    private String duration;
}
