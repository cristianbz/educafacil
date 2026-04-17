package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import java.util.Date;
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
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private String telefonoMobil;
    private String telefonoCasa;
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
