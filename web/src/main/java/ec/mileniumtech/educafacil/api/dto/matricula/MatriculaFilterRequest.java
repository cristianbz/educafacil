package ec.mileniumtech.educafacil.api.dto.matricula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO para filtros de búsqueda de matrículas.
 * Todos los campos son opcionales; se aplican solo los presentes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaFilterRequest {

    private String estado;
    private Integer estudianteId;
    private Integer ofertaCursoId;
    private Integer cursoId;
    private Date fechaInicio;
    private Date fechaFin;
    private Integer anio;
    private Integer mes;
    private String cedula;
    private String apellidos;

    // Paginación
    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;
}
