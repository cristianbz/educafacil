package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;

class DtoClassesTest {

    @Test
    void authDtoConstructorVacio() {
        AuthDto dto = new AuthDto();
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }

    @Test
    void authDtoConstructorConParametros() {
        AuthDto dto = new AuthDto("cbaez", "clave123");
        assertEquals("cbaez", dto.getUsername());
        assertEquals("clave123", dto.getPassword());
    }

    @Test
    void authDtoSetters() {
        AuthDto dto = new AuthDto();
        dto.setUsername("admin");
        dto.setPassword("admin123");
        assertEquals("admin", dto.getUsername());
        assertEquals("admin123", dto.getPassword());
    }

    @Test
    void authDtoEqualsYHashCode() {
        AuthDto dto1 = new AuthDto("user1", "pass1");
        AuthDto dto2 = new AuthDto("user1", "pass1");
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void tokenResponseDtoConstructorVacio() {
        TokenResponseDto dto = new TokenResponseDto();
        assertNull(dto.getToken());
        assertNull(dto.getUsername());
        assertNull(dto.getRoles());
        assertNull(dto.getDuration());
    }

    @Test
    void tokenResponseDtoConstructorConParametros() {
        TokenResponseDto dto = new TokenResponseDto("token123", "cbaez",
                Arrays.asList("ADMIN"), "8h");
        assertEquals("token123", dto.getToken());
        assertEquals("cbaez", dto.getUsername());
        assertEquals(1, dto.getRoles().size());
        assertEquals("8h", dto.getDuration());
    }

    @Test
    void tokenResponseDtoSetters() {
        TokenResponseDto dto = new TokenResponseDto();
        dto.setToken("abc");
        dto.setUsername("user");
        dto.setRoles(Arrays.asList("ROL1", "ROL2"));
        dto.setDuration("24h");
        assertEquals("abc", dto.getToken());
        assertEquals("user", dto.getUsername());
        assertEquals(2, dto.getRoles().size());
        assertEquals("24h", dto.getDuration());
    }

    @Test
    void errorDtoConstructorVacio() {
        ErrorDto dto = new ErrorDto();
        assertNull(dto.getMensaje());
        assertNull(dto.getCodigo());
        assertNull(dto.getTimestamp());
    }

    @Test
    void errorDtoConstructorConParametros() {
        ErrorDto dto = new ErrorDto("Error interno", "ERR-001", null);
        assertEquals("Error interno", dto.getMensaje());
        assertEquals("ERR-001", dto.getCodigo());
    }

    @Test
    void errorDtoSetters() {
        ErrorDto dto = new ErrorDto();
        dto.setMensaje("No encontrado");
        dto.setCodigo("404");
        assertEquals("No encontrado", dto.getMensaje());
        assertEquals("404", dto.getCodigo());
    }

    @Test
    void personaDtoConstructorVacio() {
        PersonaDto dto = new PersonaDto();
        assertEquals(0, dto.getId());
        assertNull(dto.getNombres());
    }

    @Test
    void personaDtoConstructorConParametros() {
        PersonaDto dto = new PersonaDto(1, "Juan", "Pérez", "1710034065",
                "0999999999", "022222222", "juan@test.com", "Dir", "Ec",
                "Soltero", false, new Date(), 17, 1, "Norte");
        assertEquals(1, dto.getId());
        assertEquals("Juan", dto.getNombres());
        assertEquals("Pérez", dto.getApellidos());
        assertEquals("1710034065", dto.getDocumentoIdentidad());
    }

    @Test
    void personaDtoSetters() {
        PersonaDto dto = new PersonaDto();
        dto.setId(5);
        dto.setNombres("María");
        dto.setApellidos("López");
        assertEquals(5, dto.getId());
        assertEquals("María", dto.getNombres());
        assertEquals("López", dto.getApellidos());
    }

    @Test
    void cursoDtoConstructorVacio() {
        CursoDto dto = new CursoDto();
        assertEquals(0, dto.getId());
        assertNull(dto.getNombre());
    }

    @Test
    void cursoDtoConstructorConParametros() {
        CursoDto dto = new CursoDto(1, "Java");
        assertEquals(1, dto.getId());
        assertEquals("Java", dto.getNombre());
    }

    @Test
    void cursoDtoSetters() {
        CursoDto dto = new CursoDto();
        dto.setId(2);
        dto.setNombre("Python");
        assertEquals(2, dto.getId());
        assertEquals("Python", dto.getNombre());
    }

    @Test
    void estudianteDtoConstructorVacio() {
        EstudianteDto dto = new EstudianteDto();
        assertEquals(0, dto.getId());
        assertNull(dto.getCargoOcupa());
        assertNull(dto.getPersona());
    }

    @Test
    void estudianteDtoConstructorConParametros() {
        PersonaDto personaDto = new PersonaDto();
        EstudianteDto dto = new EstudianteDto(1, "Ingeniero", "Superior", personaDto);
        assertEquals(1, dto.getId());
        assertEquals("Ingeniero", dto.getCargoOcupa());
        assertEquals("Superior", dto.getNivelEstudio());
        assertNotNull(dto.getPersona());
    }

    @Test
    void matriculaDtoConstructorVacio() {
        MatriculaDto dto = new MatriculaDto();
        assertNull(dto.getId());
        assertNull(dto.getEstado());
    }

    @Test
    void matriculaDtoConstructorConParametros() {
        MatriculaDto dto = new MatriculaDto(1, "INSMAT01", new Date(), new Date(),
                "Obs", new EstudianteDto(), new CursoDto());
        assertEquals(1, dto.getId());
        assertEquals("INSMAT01", dto.getEstado());
    }

    @Test
    void usuarioDtoConstructorVacio() {
        UsuarioDto dto = new UsuarioDto();
        assertNull(dto.getId());
        assertNull(dto.getUsuario());
    }

    @Test
    void usuarioDtoConstructorConParametros() {
        UsuarioDto dto = new UsuarioDto(1, "cbaez", new Date(), new Date(),
                new Date(), true, new PersonaDto());
        assertEquals(1, dto.getId());
        assertEquals("cbaez", dto.getUsuario());
        assertTrue(dto.isEstado());
    }

    @Test
    void objetosMenuDtoConstructorVacio() {
        ObjetosMenuDto dto = new ObjetosMenuDto();
        assertNull(dto.getRol_id());
        assertNull(dto.getRol_nombre());
    }

    @Test
    void objetosMenuDtoConstructorConParametros() {
        ObjetosMenuDto dto = new ObjetosMenuDto("1", "ADMIN", true, true,
                "1", "PerfilAdmin", true, "Accion1", "Desc", "/ruta",
                true, "0", "1", "icono.svg");
        assertEquals("1", dto.getRol_id());
        assertEquals("ADMIN", dto.getRol_nombre());
        assertTrue(dto.getRol_estado());
    }

    @Test
    void objetosMenuDtoSetters() {
        ObjetosMenuDto dto = new ObjetosMenuDto();
        dto.setRol_id("2");
        dto.setRol_nombre("VENTAS");
        dto.setAcc_ruta("/ventas");
        assertEquals("2", dto.getRol_id());
        assertEquals("VENTAS", dto.getRol_nombre());
        assertEquals("/ventas", dto.getAcc_ruta());
    }

    @Test
    void dtoFlujoDineroConstructorVacio() {
        DtoFlujoDinero dto = new DtoFlujoDinero();
        assertNull(dto.getFecha());
    }

    @Test
    void dtoEncuestasConstructorVacio() {
        DtoEncuestas dto = new DtoEncuestas();
        assertNull(dto.getPregunta());
        assertNull(dto.getRespuesta());
        assertNull(dto.getCodigoPregResp());
    }

    @Test
    void dtoEncuestasSetters() {
        DtoEncuestas dto = new DtoEncuestas();
        dto.setPregunta("¿Color favorito?");
        dto.setRespuesta("Azul");
        dto.setCodigoPregResp("P001");
        assertEquals("¿Color favorito?", dto.getPregunta());
        assertEquals("Azul", dto.getRespuesta());
        assertEquals("P001", dto.getCodigoPregResp());
    }

    @Test
    void dtoMatriculasCursoConstructorVacio() {
        DtoMatriculasCurso dto = new DtoMatriculasCurso();
        assertNull(dto.getCurso());
        assertEquals(0, dto.getCantidad());
    }

    @Test
    void dtoMatriculasCursoSetters() {
        DtoMatriculasCurso dto = new DtoMatriculasCurso();
        dto.setCurso("Java Avanzado");
        dto.setCantidad(15);
        assertEquals("Java Avanzado", dto.getCurso());
        assertEquals(15, dto.getCantidad());
    }

    @Test
    void authDtoToString() {
        AuthDto dto = new AuthDto("user", "pass");
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("user"));
    }

    @Test
    void tokenResponseDtoToString() {
        TokenResponseDto dto = new TokenResponseDto("t", "u", Arrays.asList("R"), "8h");
        assertNotNull(dto.toString());
    }

    @Test
    void personaDtoEquals() {
        PersonaDto dto1 = new PersonaDto(1, "A", "B", "C", "D", "E", "F",
                "G", "H", "I", false, null, 1, 1, "J");
        PersonaDto dto2 = new PersonaDto(1, "A", "B", "C", "D", "E", "F",
                "G", "H", "I", false, null, 1, 1, "J");
        assertEquals(dto1, dto2);
    }
}
