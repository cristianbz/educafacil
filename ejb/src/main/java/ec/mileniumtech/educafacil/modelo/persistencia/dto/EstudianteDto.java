package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la entidad Estudiante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String cargoOcupa;
    private String nivelEstudio;
    private PersonaDto persona;
}
