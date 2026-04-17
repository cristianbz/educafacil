package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la entidad Matricula.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String estado;
    private Date fechaMatricula;
    private Date fechaInscripcion;
    private String observacion;
    private EstudianteDto estudiante;
    private CursoDto curso;
}
