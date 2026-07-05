package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Persona.
 * Define operaciones de acceso a datos especÃ­ficas para Persona.
 */
@Local
public interface PersonaDao extends GenericoDao<Persona, Long> {

    Persona buscarPersonaPorCedula(String cedula);
    void agregarPersona(Persona persona);
    Persona actualizarPersona(Persona persona);
    List<Persona> buscarPersonaPorApellidos(String apellidos);
    Persona buscarPersonaPorId(int codigo);
    Persona buscarPersonaPorCedulaCorreo(String cedula, String correo);
}
