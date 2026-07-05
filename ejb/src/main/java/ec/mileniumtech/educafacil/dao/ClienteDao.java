package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;

import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Cliente.
 * Define operaciones de acceso a datos especÃ­ficas para Cliente.
 */
@Local
public interface ClienteDao extends GenericoDao<Cliente, Integer> {

    Cliente buscarPorIdentificacion(String identificacion);
}
