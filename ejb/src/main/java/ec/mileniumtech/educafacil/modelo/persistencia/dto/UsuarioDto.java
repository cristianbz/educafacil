package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la entidad Usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String usuario;
    private Date fechaRegistro;
    private Date fechaInicio;
    private Date fechaCaducidad;
    private boolean estado;
    private PersonaDto persona;
}
