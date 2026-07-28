package ec.mileniumtech.educafacil.api.dto.matricula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO de respuesta con los datos de una persona.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResponse {

    private Integer id;
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private String telefonoMobil;
    private String telefonoCasa;
    private String correoElectronico;
    private String domicilio;
    private String nacionalidad;
    private String estadoCivil;
    private Boolean cargasFamiliares;
    private Date fechaNacimiento;
    private Integer provincia;
    private Integer ciudad;
    private String sector;

    // Datos del estudiante si existe
    private EstudianteInfo estudiante;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstudianteInfo {
        private Integer id;
        private String cargoOcupa;
        private String nivelEstudio;
        private String ultimoCurso;
        private String direccionTrabajo;
        private String ingresosMensuales;
        private String telefonoTrabajo;
    }
}
