package ec.mileniumtech.educafacil.api.dto.matricula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DTO de respuesta para una matrícula.
 * Incluye información completa de la matrícula, estudiante, curso y pagos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaResponse {

    private Integer id;
    private String estado;
    private String estadoLabel;
    private Date fechaRegistro;
    private Date fechaMatricula;
    private Date fechaInscripcion;
    private String observacion;
    private String medioInformacion;
    private String motivacionCurso;
    private Boolean trabajaEnArea;
    private String paraQueCurso;
    private BigDecimal saldoPagoCurso;
    private Date fechaUltimoPago;
    private Boolean facturacionEmpresa;

    // ── Relaciones ───────────────────────────────────────────
    private EstudianteResponse estudiante;
    private OfertaCursoResponse ofertaCurso;
    private EmpresaResponse empresa;
    private List<PagoResponse> pagos;

    // ── DTOs anidados ────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstudianteResponse {
        private Integer id;
        private String cargoOcupa;
        private String nivelEstudio;
        private String ultimoCurso;
        private PersonaResponse persona;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonaResponse {
        private Integer id;
        private String nombres;
        private String apellidos;
        private String documentoIdentidad;
        private String telefonoMobil;
        private String telefonoCasa;
        private String correoElectronico;
        private String domicilio;
        private String estadoCivil;
        private Date fechaNacimiento;
        private Integer provincia;
        private Integer ciudad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfertaCursoResponse {
        private Integer id;
        private Date fechaInicio;
        private Date fechaFin;
        private Double valor;
        private Double descuento;
        private String tipo;
        private String estado;
        private Integer duracion;
        private String horario;
        private String modalidad;
        private String cursoNombre;
        private String instructorNombre;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpresaResponse {
        private Integer id;
        private String ruc;
        private String razonSocial;
        private String direccion;
        private String telefono;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagoResponse {
        private Integer id;
        private BigDecimal valor;
        private Date fechaPago;
        private String formaPago;
        private String estado;
    }
}
