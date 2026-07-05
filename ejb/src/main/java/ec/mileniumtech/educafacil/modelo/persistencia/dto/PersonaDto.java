package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import java.util.Date;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la entidad Persona.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    @NotBlank(message = "{persona.nombres.required}")
    @Size(min = 2, max = 100, message = "{persona.nombres.size}")
    private String nombres;

    @NotBlank(message = "{persona.apellidos.required}")
    @Size(min = 2, max = 100, message = "{persona.apellidos.size}")
    private String apellidos;

    @NotBlank(message = "{persona.documentoIdentidad.required}")
    @Size(min = 10, max = 13, message = "{persona.documentoIdentidad.size}")
    private String documentoIdentidad;

    private String telefonoMobil;
    private String telefonoCasa;

    @Email(message = "{persona.correoElectronico.email}")
    private String correoElectronico;

    private String domicilio;
    private String nacionalidad;
    private String estadoCivil;
    private boolean cargasFamiliares;
    private Date fechaNacimiento;
    private Integer provincia;
    private Integer ciudad;
    private String sector;
}
