package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;

/**
 * Utilitario para convertir entre entidades y DTOs.
 */
public class DtoMapper {

    public static PersonaDto entidadAPersonaDto(Persona persona) {
        if (persona == null) return null;
        return new PersonaDto(
            persona.getPersId(),
            persona.getPersNombres(),
            persona.getPersApellidos(),
            persona.getPersDocumentoIdentidad(),
            persona.getPersTelefonoMobil(),
            persona.getPersTelefonoCasa(),
            persona.getPersCorreoElectronico(),
            persona.getPersDomicilio(),
            persona.getPersNacionalidad(),
            persona.getPersEstadoCivil(),
            persona.isPersCargasFamiliares(),
            persona.getPersFechaNacimiento(),
            persona.getPersProvincia(),
            persona.getPersCiudad(),
            persona.getPersSector()
        );
    }

    public static CursoDto entidadACursoDto(Curso curso) {
        if (curso == null) return null;
        return new CursoDto(
            curso.getCursId(),
            curso.getCursNombre()
        );
    }

    public static UsuarioDto entidadAUsuarioDto(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioDto(
            usuario.getUsuaId(),
            usuario.getUsuaUsuario(),
            usuario.getUsuaFechaRegistro(),
            usuario.getUsuaFechaInicio(),
            usuario.getUsuaFechaCaducidad(),
            usuario.isUsuaEstado(),
            entidadAPersonaDto(usuario.getPersona())
        );
    }

    public static EstudianteDto entidadAEstudianteDto(Estudiante estudiante) {
        if (estudiante == null) return null;
        return new EstudianteDto(
            estudiante.getEstuId(),
            estudiante.getEstuCargoOcupa(),
            estudiante.getEstuNivelEstudio(),
            entidadAPersonaDto(estudiante.getPersona())
        );
    }

    public static MatriculaDto entidadAMatriculaDto(Matricula matricula) {
        if (matricula == null) return null;
        Curso curso = null;
        if (matricula.getOfertaCursos() != null && matricula.getOfertaCursos().getOfertaCapacitacion() != null) {
            curso = matricula.getOfertaCursos().getOfertaCapacitacion().getCurso();
        }
        
        return new MatriculaDto(
            matricula.getMatrId(),
            matricula.getMatrEstado(),
            matricula.getMatrFechaMatricula(),
            matricula.getMatrFechaInscripcion(),
            matricula.getMatrObservacion(),
            entidadAEstudianteDto(matricula.getEstudiante()),
            entidadACursoDto(curso)
        );
    }
    public static Persona dtoAEntidadPersona(PersonaDto dto) {
        if (dto == null) return null;
        Persona persona = new Persona();
        persona.setPersId(dto.getId());
        persona.setPersNombres(dto.getNombres());
        persona.setPersApellidos(dto.getApellidos());
        persona.setPersDocumentoIdentidad(dto.getDocumentoIdentidad());
        persona.setPersTelefonoMobil(dto.getTelefonoMobil());
        persona.setPersTelefonoCasa(dto.getTelefonoCasa());
        persona.setPersCorreoElectronico(dto.getCorreoElectronico());
        persona.setPersDomicilio(dto.getDomicilio());
        persona.setPersNacionalidad(dto.getNacionalidad());
        persona.setPersEstadoCivil(dto.getEstadoCivil());
        persona.setPersCargasFamiliares(dto.isCargasFamiliares());
        persona.setPersFechaNacimiento(dto.getFechaNacimiento());
        persona.setPersProvincia(dto.getProvincia());
        persona.setPersCiudad(dto.getCiudad());
        persona.setPersSector(dto.getSector());
        return persona;
    }

    public static Curso dtoAEntidadCurso(CursoDto dto) {
        if (dto == null) return null;
        Curso curso = new Curso();
        curso.setCursId(dto.getId());
        curso.setCursNombre(dto.getNombre());
        return curso;
    }
}
