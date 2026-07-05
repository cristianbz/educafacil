package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Accion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PerfilAccion;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad PerfilAccion.
 * Define operaciones de acceso a datos espec\u00edficas para PerfilAccion.
 */
@Local
public interface PerfilAccionDao extends GenericoDao<PerfilAccion, Integer> {

    List<Accion> listarTodasAcciones();
    List<PerfilAccion> listarAccionesPorPerfil(Integer perfilId);
    PerfilAccion buscarPerfilAccion(Integer perfilId, String accionId);
    Integer siguienteId();
    void eliminarPorPerfilYAccion(Integer perfilId, String accionId);
}
