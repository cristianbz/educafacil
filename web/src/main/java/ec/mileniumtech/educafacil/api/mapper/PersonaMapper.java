package ec.mileniumtech.educafacil.api.mapper;

import ec.mileniumtech.educafacil.api.dto.matricula.PersonaResponse;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Persona/Estudiante
 * y DTOs de la API REST.
 */
public final class PersonaMapper {

    private PersonaMapper() {
        // Utilidad — no instanciable
    }

    /**
     * Convierte una entidad Persona (con su posible estudiante) a DTO de respuesta.
     */
    public static PersonaResponse toResponse(Persona persona) {
        return toResponse(persona, null);
    }

    /**
     * Convierte Persona + Estudiante a DTO de respuesta.
     */
    public static PersonaResponse toResponse(Persona persona, Estudiante estudiante) {
        if (persona == null) return null;

        PersonaResponse.PersonaResponseBuilder builder = PersonaResponse.builder()
                .id(persona.getPersId())
                .nombres(persona.getPersNombres())
                .apellidos(persona.getPersApellidos())
                .documentoIdentidad(persona.getPersDocumentoIdentidad())
                .telefonoMobil(persona.getPersTelefonoMobil())
                .telefonoCasa(persona.getPersTelefonoCasa())
                .correoElectronico(persona.getPersCorreoElectronico())
                .domicilio(persona.getPersDomicilio())
                .nacionalidad(persona.getPersNacionalidad())
                .estadoCivil(persona.getPersEstadoCivil())
                .cargasFamiliares(persona.isPersCargasFamiliares())
                .fechaNacimiento(persona.getPersFechaNacimiento())
                .provincia(persona.getPersProvincia())
                .ciudad(persona.getPersCiudad())
                .sector(persona.getPersSector());

        if (estudiante != null) {
            builder.estudiante(PersonaResponse.EstudianteInfo.builder()
                    .id(estudiante.getEstuId())
                    .cargoOcupa(estudiante.getEstuCargoOcupa())
                    .nivelEstudio(estudiante.getEstuNivelEstudio())
                    .ultimoCurso(estudiante.getEstuUltimoCurso())
                    .direccionTrabajo(estudiante.getEstuDireccionTrabajo())
                    .ingresosMensuales(estudiante.getEstuIngresosMensuales())
                    .telefonoTrabajo(estudiante.getEstuTelefonoTrabajo())
                    .build());
        } else if (persona.getEstudiantes() != null && !persona.getEstudiantes().isEmpty()) {
            // Tomar el primer estudiante si existe
            Estudiante e = persona.getEstudiantes().get(0);
            builder.estudiante(PersonaResponse.EstudianteInfo.builder()
                    .id(e.getEstuId())
                    .cargoOcupa(e.getEstuCargoOcupa())
                    .nivelEstudio(e.getEstuNivelEstudio())
                    .ultimoCurso(e.getEstuUltimoCurso())
                    .direccionTrabajo(e.getEstuDireccionTrabajo())
                    .ingresosMensuales(e.getEstuIngresosMensuales())
                    .telefonoTrabajo(e.getEstuTelefonoTrabajo())
                    .build());
        }

        return builder.build();
    }

    /**
     * Convierte una lista de personas a lista de DTOs.
     */
    public static List<PersonaResponse> toResponseList(List<Persona> personas) {
        if (personas == null) return List.of();
        return personas.stream()
                .map(p -> toResponse(p, null))
                .collect(Collectors.toList());
    }
}
