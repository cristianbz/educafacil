package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Perfil;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Perfil.
 * Define operaciones de acceso a datos especÃ­ficas para Perfil.
 */
@Local
public interface PerfilDao extends GenericoDao<Perfil, Integer> {

    List<Perfil> listarTodosPerfiles();
    List<Perfil> listarPerfilesActivos();
    Perfil buscarPerfilPorId(Integer id);
}
