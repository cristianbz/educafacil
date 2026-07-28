package ec.mileniumtech.educafacil.api.security;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

/**
 * Proveedor de tokens JWT para la API REST.
 * <p>
 * Implementa generación y validación de JWT usando HMAC-SHA256
 * sin dependencias externas (solo JDK + JSON-P de Jakarta EE).
 * </p>
 *
 * <p>
 * <strong>Uso en producción:</strong> Reemplazar {@link #SECRET} por una clave
 * segura obtenida de configuración externa ({@code -D} o variable de entorno).
 * </p>
 */
@ApplicationScoped
public class JwtProvider {

    private static final Logger log = LogManager.getLogger(JwtProvider.class);

    /**
     * Clave secreta para firmar tokens. En producción debe venir de
     * una fuente segura (vault, variable de entorno, etc.).
     */
    private String secret;

    private static final String ALGORITHM = "HmacSHA256";
    private static final String ISSUER = "educafacil-api";
    private static final long ACCESS_TOKEN_EXPIRATION_HOURS = 8;

    @PostConstruct
    void init() {
        // Intentar leer de variable de entorno o propiedad del sistema
        this.secret = System.getenv("JWT_SECRET");
        if (this.secret == null || this.secret.isBlank()) {
            this.secret = System.getProperty("jwt.secret");
        }
        if (this.secret == null || this.secret.isBlank()) {
            // Solo para desarrollo — en producción usar siempre una clave externa
            this.secret = "educafacil-dev-secret-key-change-in-production-32bytes";
            log.warn("JWT_SECRET no configurada. Usando clave por defecto (SOLO DESARROLLO).");
        }
    }

    // =========================================================
    // Generación de tokens
    // =========================================================

    /**
     * Genera un token JWT de acceso para un usuario.
     *
     * @param usuario  nombre de usuario
     * @param personaId ID de la persona asociada
     * @param roles    lista de roles del usuario
     * @return token JWT codificado
     */
    public String generarToken(String usuario, int personaId, String... roles) {
        try {
            Instant now = Instant.now();
            Instant expiration = now.plus(ACCESS_TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS);

            // HEADER: {"alg":"HS256","typ":"JWT"}
            JsonObject header = Json.createObjectBuilder()
                    .add("alg", "HS256")
                    .add("typ", "JWT")
                    .build();

            // PAYLOAD: claims estándar + personalizados
            var payloadBuilder = Json.createObjectBuilder()
                    .add("sub", usuario)
                    .add("iss", ISSUER)
                    .add("iat", now.getEpochSecond())
                    .add("exp", expiration.getEpochSecond())
                    .add("personaId", personaId);

            if (roles != null && roles.length > 0) {
                var rolesArrayBuilder = Json.createArrayBuilder();
                for (String role : roles) {
                    rolesArrayBuilder.add(role);
                }
                payloadBuilder.add("roles", rolesArrayBuilder.build());
            }

            JsonObject payload = payloadBuilder.build();

            // Firmar
            String headerBase64 = base64UrlEncode(header.toString().getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = base64UrlEncode(payload.toString().getBytes(StandardCharsets.UTF_8));
            String signature = sign(headerBase64 + "." + payloadBase64);

            return headerBase64 + "." + payloadBase64 + "." + signature;

        } catch (Exception e) {
            log.error("Error al generar token JWT", e);
            throw new RuntimeException("No se pudo generar el token de autenticación", e);
        }
    }

    // =========================================================
    // Validación y extracción
    // =========================================================

    /**
     * Valida un token JWT y retorna los claims si es válido.
     *
     * @param token token JWT
     * @return claims del payload, o {@code null} si el token no es válido
     */
    public JsonObject validarToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("Token JWT con formato inválido");
                return null;
            }

            String headerBase64 = parts[0];
            String payloadBase64 = parts[1];
            String signature = parts[2];

            // Verificar firma
            String expectedSignature = sign(headerBase64 + "." + payloadBase64);
            if (!MessageDigest.isEqual(signature.getBytes(StandardCharsets.UTF_8),
                    expectedSignature.getBytes(StandardCharsets.UTF_8))) {
                log.warn("Firma JWT inválida");
                return null;
            }

            // Decodificar payload
            byte[] payloadBytes = Base64.getUrlDecoder().decode(payloadBase64);
            try (JsonReader reader = Json.createReader(new StringReader(new String(payloadBytes, StandardCharsets.UTF_8)))) {
                JsonObject payload = reader.readObject();

                // Verificar expiración
                long exp = payload.getJsonNumber("exp").longValue();
                if (Instant.now().getEpochSecond() > exp) {
                    log.warn("Token JWT expirado");
                    return null;
                }

                // Verificar issuer
                String iss = payload.getString("iss", "");
                if (!ISSUER.equals(iss)) {
                    log.warn("Issuer JWT inválido: {}", iss);
                    return null;
                }

                return payload;
            }

        } catch (Exception e) {
            log.error("Error al validar token JWT", e);
            return null;
        }
    }

    /**
     * Extrae el nombre de usuario del token.
     */
    public String getUsuario(JsonObject claims) {
        return claims != null ? claims.getString("sub", null) : null;
    }

    /**
     * Extrae el ID de persona del token.
     */
    public int getPersonaId(JsonObject claims) {
        return claims != null ? claims.getInt("personaId", 0) : 0;
    }

    /**
     * Verifica si el token contiene un rol específico.
     */
    public boolean tieneRol(JsonObject claims, String rol) {
        if (claims == null) return false;
        try {
            var rolesArray = claims.getJsonArray("roles");
            if (rolesArray == null) return false;
            return rolesArray.stream()
                    .anyMatch(v -> v.toString().replace("\"", "").equals(rol));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Indica si el token ha expirado.
     */
    public boolean isExpirado(JsonObject claims) {
        if (claims == null) return true;
        long exp = claims.getJsonNumber("exp").longValue();
        return Instant.now().getEpochSecond() > exp;
    }

    // =========================================================
    // Métodos privados
    // =========================================================

    private String sign(String input) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(keySpec);
            byte[] signatureBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar JWT", e);
        }
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
