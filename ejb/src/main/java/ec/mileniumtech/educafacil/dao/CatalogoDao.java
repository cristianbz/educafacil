package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Catalogo.
 * Define operaciones de acceso a datos especÃ­ficas para Catalogo.
 */
@Local
public interface CatalogoDao extends GenericoDao<Catalogo, Long> {

    List<Catalogo> catalogosPorTipo(String tipoCatalogo);
    List<Catalogo> catalogosPorPadre(Catalogo padre);
}
