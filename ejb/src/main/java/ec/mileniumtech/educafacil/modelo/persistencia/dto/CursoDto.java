package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la entidad Curso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
}
