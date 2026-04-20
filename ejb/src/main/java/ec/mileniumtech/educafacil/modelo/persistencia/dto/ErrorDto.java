package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para representar errores en la API REST.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mensaje;
    private String codigo;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
