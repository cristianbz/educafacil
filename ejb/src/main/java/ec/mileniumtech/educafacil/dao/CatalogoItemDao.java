package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;

import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad CatalogoItem.
 * Define operaciones de acceso a datos especÃ­ficas para CatalogoItem.
 */
@Local
public interface CatalogoItemDao extends GenericoDao<CatalogoItem, Integer> {

    CatalogoItem buscarPorCodigo(String codigo);
}
