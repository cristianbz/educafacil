package ec.mileniumtech.educafacil.utilitarios.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;

/**
 * Utilidad para la gestión de tokens JWT.
 */
public class JwtUtil {

    // Clave secreta generada para esta implementación. 
    // En producción, esto debería cargarse desde una variable de entorno o almacén de claves.
    private static final String SECRET_KEY = "Educafacil_Secret_Key_Random_2026_Secure_Key_Generation_1234567890";
    
    // Duración de 8 horas en milisegundos
    private static final long EXPIRATION_TIME = 8 * 60 * 60 * 1000;

    /**
     * Genera un token para el usuario con sus roles.
     * @param username Nombre de usuario.
     * @param roles Lista de roles.
     * @return Token JWT firmado.
     */
    public static String generarToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * Valida y obtiene los claims del token.
     * @param token Token JWT.
     * @return Los claims si es válido.
     */
    public static Claims validarToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extrae el nombre de usuario del token.
     */
    public static String extraerUsername(String token) {
        return validarToken(token).getSubject();
    }

    /**
     * Extrae los roles del token.
     */
    @SuppressWarnings("unchecked")
    public static List<String> extraerRoles(String token) {
        return (List<String>) validarToken(token).get("roles");
    }
}
