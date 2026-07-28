package ec.mileniumtech.educafacil.api.dto.matricula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO de respuesta para una oferta de curso disponible.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfertaCursoResponse {

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
    private String grupoWhatsapp;

    // Anidados
    private CursoInfo curso;
    private InstructorInfo instructor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoInfo {
        private Integer id;
        private String nombre;
        private String contenido;
        private String area;
        private String especialidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstructorInfo {
        private Integer id;
        private String nombres;
        private String apellidos;
        private String correo;
    }
}
