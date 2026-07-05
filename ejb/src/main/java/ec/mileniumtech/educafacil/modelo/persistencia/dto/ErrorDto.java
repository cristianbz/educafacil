package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar errores en la API REST.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mensaje;
    private String codigo;
    
    /**
     * Lista de errores de validación detallados (Bean Validation).
     */
    private List<ValidationError> errores;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * DTO interno para representar un error de validación individual.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidationError implements Serializable {
        private static final long serialVersionUID = 1L;
        private String campo;
        private String mensaje;
        private Object valorRechazado;
    }
}
