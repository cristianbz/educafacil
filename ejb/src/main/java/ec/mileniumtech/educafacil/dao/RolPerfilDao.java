package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.RolPerfil;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad RolPerfil.
 * Define operaciones de acceso a datos especÃ­ficas para RolPerfil.
 */
@Local
public interface RolPerfilDao extends GenericoDao<RolPerfil, Integer> {

    List<RolPerfil> listarPerfilesPorRol(Integer rolId);
    RolPerfil buscarRolPerfil(Integer rolId, Integer perfilId);
    Integer siguienteId();
    void eliminarPorRolYPerfil(Integer rolId, Integer perfilId);
}
