package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Rol.
 * Define operaciones de acceso a datos especÃ­ficas para Rol.
 */
@Local
public interface RolDao extends GenericoDao<Rol, Integer> {

    List<Rol> listarTodosRoles();
    List<Rol> listarRolesActivos();
    Rol buscarRolPorId(Integer id);
}
