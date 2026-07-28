package ec.mileniumtech.educafacil.api.dto.matricula;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO de petición para crear una nueva matrícula/inscripción.
 * Incluye los datos del estudiante, persona, curso y facturación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaCreateRequest {

    // ── Datos de la Persona ──────────────────────────────────
    @NotBlank(message = "La cédula es obligatoria")
    private String documentoIdentidad;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    private String telefonoMobil;
    private String telefonoCasa;

    @jakarta.validation.constraints.Email(message = "Correo electrónico inválido")
    private String correoElectronico;

    private String domicilio;
    private String nacionalidad;
    private String estadoCivil;
    private Boolean cargasFamiliares;
    private Date fechaNacimiento;
    private Integer provincia;
    private Integer ciudad;
    private String sector;

    // ── Datos del Estudiante ─────────────────────────────────
    private String cargoOcupa;
    private String nivelEstudio;
    private String ultimoCurso;
    private String direccionTrabajo;
    private String ingresosMensuales;
    private String telefonoTrabajo;

    // ── Datos de la Matrícula ────────────────────────────────
    @NotNull(message = "El ID de la oferta de curso es obligatorio")
    @Positive(message = "El ID de la oferta de curso debe ser positivo")
    private Integer ofertaCursoId;

    private String medioInformacion;
    private String paraQueCurso;
    private Boolean trabajaEnArea;
    private String motivacionCurso;
    private String observacion;
    private Date fechaMatricula;
    private Date fechaInscripcion;
    private String modalidadCurso;

    // ── Facturación a Empresa ────────────────────────────────
    private Boolean facturacionEmpresa;
    private String empresaRuc;
    private String empresaRazonSocial;
    private String empresaDireccion;
    private String empresaTelefono;
}
