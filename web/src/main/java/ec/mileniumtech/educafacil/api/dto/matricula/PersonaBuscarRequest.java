package ec.mileniumtech.educafacil.api.dto.matricula;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la búsqueda de personas por cédula.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaBuscarRequest {

    @NotBlank(message = "La cédula es obligatoria")
    private String cedula;

    private String correo;
}
