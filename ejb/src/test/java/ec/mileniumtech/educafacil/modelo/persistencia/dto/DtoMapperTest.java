package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;

class DtoMapperTest {

    @Test
    void entidadAPersonaDtoConPersonaValida() {
        Persona persona = crearPersona();
        PersonaDto dto = DtoMapper.entidadAPersonaDto(persona);
        assertNotNull(dto);
        assertEquals(persona.getPersId(), dto.getId());
        assertEquals(persona.getPersNombres(), dto.getNombres());
        assertEquals(persona.getPersApellidos(), dto.getApellidos());
        assertEquals(persona.getPersDocumentoIdentidad(), dto.getDocumentoIdentidad());
        assertEquals(persona.getPersCorreoElectronico(), dto.getCorreoElectronico());
    }

    @Test
    void entidadAPersonaDtoConNull() {
        assertNull(DtoMapper.entidadAPersonaDto(null));
    }

    @Test
    void entidadAPersonaDtoConTodosLosCampos() {
        Persona persona = crearPersona();
        PersonaDto dto = DtoMapper.entidadAPersonaDto(persona);
        assertEquals(persona.getPersTelefonoMobil(), dto.getTelefonoMobil());
        assertEquals(persona.getPersTelefonoCasa(), dto.getTelefonoCasa());
        assertEquals(persona.getPersDomicilio(), dto.getDomicilio());
        assertEquals(persona.getPersNacionalidad(), dto.getNacionalidad());
        assertEquals(persona.getPersEstadoCivil(), dto.getEstadoCivil());
        assertEquals(persona.isPersCargasFamiliares(), dto.isCargasFamiliares());
        assertEquals(persona.getPersFechaNacimiento(), dto.getFechaNacimiento());
        assertEquals(persona.getPersProvincia(), dto.getProvincia());
        assertEquals(persona.getPersCiudad(), dto.getCiudad());
        assertEquals(persona.getPersSector(), dto.getSector());
    }

    @Test
    void entidadACursoDtoConCursoValido() {
        Curso curso = new Curso();
        curso.setCursId(1);
        curso.setCursNombre("Java Avanzado");
        CursoDto dto = DtoMapper.entidadACursoDto(curso);
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Java Avanzado", dto.getNombre());
    }

    @Test
    void entidadACursoDtoConNull() {
        assertNull(DtoMapper.entidadACursoDto(null));
    }

    @Test
    void entidadAUsuarioDtoConUsuarioValido() {
        Persona persona = crearPersona();
        Usuario usuario = new Usuario();
        usuario.setUsuaId(1);
        usuario.setUsuaUsuario("cbaez");
        usuario.setUsuaFechaRegistro(new Date());
        usuario.setUsuaFechaInicio(new Date());
        usuario.setUsuaFechaCaducidad(new Date());
        usuario.setUsuaEstado(true);
        usuario.setPersona(persona);

        UsuarioDto dto = DtoMapper.entidadAUsuarioDto(usuario);
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("cbaez", dto.getUsuario());
        assertTrue(dto.isEstado());
        assertNotNull(dto.getPersona());
    }

    @Test
    void entidadAUsuarioDtoConNull() {
        assertNull(DtoMapper.entidadAUsuarioDto(null));
    }

    @Test
    void entidadAEstudianteDtoConEstudianteValido() {
        Persona persona = crearPersona();
        Estudiante estudiante = new Estudiante();
        estudiante.setEstuId(1);
        estudiante.setEstuCargoOcupa("Ingeniero");
        estudiante.setEstuNivelEstudio("Superior");
        estudiante.setPersona(persona);

        EstudianteDto dto = DtoMapper.entidadAEstudianteDto(estudiante);
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Ingeniero", dto.getCargoOcupa());
        assertEquals("Superior", dto.getNivelEstudio());
        assertNotNull(dto.getPersona());
    }

    @Test
    void entidadAEstudianteDtoConNull() {
        assertNull(DtoMapper.entidadAEstudianteDto(null));
    }

    @Test
    void entidadAMatriculaDtoConMatriculaValida() {
        Persona persona = crearPersona();
        Estudiante estudiante = new Estudiante();
        estudiante.setEstuId(1);
        estudiante.setPersona(persona);

        Curso curso = new Curso();
        curso.setCursId(1);
        curso.setCursNombre("Java");

        OfertaCapacitacion ofertaCap = new OfertaCapacitacion();
        ofertaCap.setCurso(curso);

        OfertaCursos ofertaCur = new OfertaCursos();
        ofertaCur.setOfertaCapacitacion(ofertaCap);

        Matricula matricula = new Matricula();
        matricula.setMatrId(1);
        matricula.setMatrEstado("INSMAT02");
        matricula.setMatrFechaMatricula(new Date());
        matricula.setMatrFechaInscripcion(new Date());
        matricula.setMatrObservacion("Observación");
        matricula.setEstudiante(estudiante);
        matricula.setOfertaCursos(ofertaCur);

        MatriculaDto dto = DtoMapper.entidadAMatriculaDto(matricula);
        assertNotNull(dto);
        assertEquals(1, dto.getId().intValue());
        assertEquals("INSMAT02", dto.getEstado());
        assertEquals("Observación", dto.getObservacion());
        assertNotNull(dto.getEstudiante());
        assertNotNull(dto.getCurso());
    }

    @Test
    void entidadAMatriculaDtoConOfertaCursosNulo() {
        Estudiante estudiante = new Estudiante();
        estudiante.setEstuId(1);

        Matricula matricula = new Matricula();
        matricula.setMatrId(1);
        matricula.setEstudiante(estudiante);
        matricula.setOfertaCursos(null);

        MatriculaDto dto = DtoMapper.entidadAMatriculaDto(matricula);
        assertNotNull(dto);
        assertNull(dto.getCurso());
    }

    @Test
    void entidadAMatriculaDtoConNull() {
        assertNull(DtoMapper.entidadAMatriculaDto(null));
    }

    @Test
    void dtoAEntidadPersonaConDtoValido() {
        PersonaDto dto = new PersonaDto();
        dto.setId(1);
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setDocumentoIdentidad("1710034065");
        dto.setCorreoElectronico("juan@test.com");
        dto.setCargasFamiliares(true);

        Persona persona = DtoMapper.dtoAEntidadPersona(dto);
        assertNotNull(persona);
        assertEquals(1, persona.getPersId());
        assertEquals("Juan", persona.getPersNombres());
        assertEquals("Pérez", persona.getPersApellidos());
        assertEquals("1710034065", persona.getPersDocumentoIdentidad());
        assertTrue(persona.isPersCargasFamiliares());
    }

    @Test
    void dtoAEntidadPersonaConNull() {
        assertNull(DtoMapper.dtoAEntidadPersona(null));
    }

    @Test
    void dtoAEntidadCursoConDtoValido() {
        CursoDto dto = new CursoDto();
        dto.setId(1);
        dto.setNombre("Python Básico");

        Curso curso = DtoMapper.dtoAEntidadCurso(dto);
        assertNotNull(curso);
        assertEquals(1, curso.getCursId());
        assertEquals("Python Básico", curso.getCursNombre());
    }

    @Test
    void dtoAEntidadCursoConNull() {
        assertNull(DtoMapper.dtoAEntidadCurso(null));
    }

    private Persona crearPersona() {
        Persona persona = new Persona();
        persona.setPersId(1);
        persona.setPersNombres("Juan Carlos");
        persona.setPersApellidos("Pérez García");
        persona.setPersDocumentoIdentidad("1710034065");
        persona.setPersTelefonoMobil("0999999999");
        persona.setPersTelefonoCasa("022222222");
        persona.setPersCorreoElectronico("juan.perez@email.com");
        persona.setPersDomicilio("Av. Siempre Viva 123");
        persona.setPersNacionalidad("Ecuatoriana");
        persona.setPersEstadoCivil("Soltero");
        persona.setPersCargasFamiliares(false);
        persona.setPersFechaNacimiento(new Date());
        persona.setPersProvincia(17);
        persona.setPersCiudad(1);
        persona.setPersSector("Norte");
        return persona;
    }
}
