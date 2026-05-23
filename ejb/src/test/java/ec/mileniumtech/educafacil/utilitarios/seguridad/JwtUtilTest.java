package ec.mileniumtech.educafacil.utilitarios.seguridad;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

class JwtUtilTest {

    private static final String USERNAME = "cbaez";
    private static final List<String> ROLES = Arrays.asList("ADMIN", "VENTAS");

    @Test
    void generarToken() {
        String token = JwtUtil.generarToken(USERNAME, ROLES);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void validarToken() {
        String token = JwtUtil.generarToken(USERNAME, ROLES);
        Claims claims = JwtUtil.validarToken(token);
        assertNotNull(claims);
        assertEquals(USERNAME, claims.getSubject());
    }

    @Test
    void extraerUsername() {
        String token = JwtUtil.generarToken(USERNAME, ROLES);
        String username = JwtUtil.extraerUsername(token);
        assertEquals(USERNAME, username);
    }

    @Test
    void extraerRoles() {
        String token = JwtUtil.generarToken(USERNAME, ROLES);
        List<String> roles = JwtUtil.extraerRoles(token);
        assertNotNull(roles);
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("VENTAS"));
        assertEquals(2, roles.size());
    }

    @Test
    void tokenContieneFechaEmision() {
        String token = JwtUtil.generarToken(USERNAME, ROLES);
        Claims claims = JwtUtil.validarToken(token);
        assertNotNull(claims.getIssuedAt());
    }

    @Test
    void tokenContieneFechaExpiracion() {
        String token = JwtUtil.generarToken(USERNAME, ROLES);
        Claims claims = JwtUtil.validarToken(token);
        assertNotNull(claims.getExpiration());
    }

    @Test
    void tokenConRolVacio() {
        List<String> rolesVacio = Arrays.asList();
        String token = JwtUtil.generarToken(USERNAME, rolesVacio);
        List<String> roles = JwtUtil.extraerRoles(token);
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void tokenConUnSoloRol() {
        List<String> unRol = Arrays.asList("CONSULTA");
        String token = JwtUtil.generarToken("jperez", unRol);
        List<String> roles = JwtUtil.extraerRoles(token);
        assertEquals(1, roles.size());
        assertEquals("CONSULTA", roles.get(0));
    }

    @Test
    void validarTokenInvalidoLanzaExcepcion() {
        assertThrows(Exception.class, () -> JwtUtil.validarToken("token-invalido"));
    }

    @Test
    void validarTokenVacioLanzaExcepcion() {
        assertThrows(Exception.class, () -> JwtUtil.validarToken(""));
    }

    @Test
    void generarTokenConUsernameVacio() {
        String token = JwtUtil.generarToken("", ROLES);
        assertNotNull(token);
        String username = JwtUtil.extraerUsername(token);
        assertNull(username);
    }
}
