package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "{curso.nombre.required}")
    @Size(min = 3, max = 200, message = "{curso.nombre.size}")
    private String nombre;
}
