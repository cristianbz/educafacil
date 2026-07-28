package ec.mileniumtech.educafacil.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para la autenticación exitosa.
 * Incluye el token JWT y datos básicos del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo;
    private long expiraEn;
    private String usuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private List<String> roles;
}
