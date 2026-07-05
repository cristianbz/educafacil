package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad TipoEncuesta.
 * Define operaciones de acceso a datos especÃ­ficas para TipoEncuesta.
 */
@Local
public interface TipoEncuestaDao extends GenericoDao<TipoEncuesta, Long> {

    List<TipoEncuesta> listaDeTiposDeEncuestas();
    List<TipoEncuesta> listaDeTiposDeEncuestasPorOe(int codigo);
    TipoEncuesta actualizarTipoEncuesta(TipoEncuesta tipoEncuesta);
}
