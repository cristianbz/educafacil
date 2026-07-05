package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CategoriaRespuesta;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad CategoriaRespuesta.
 * Define operaciones de acceso a datos especÃ­ficas para CategoriaRespuesta.
 */
@Local
public interface CategoriaRespuestaDao extends GenericoDao<CategoriaRespuesta, Long> {

    List<CategoriaRespuesta> listaDeCategorias();
    CategoriaRespuesta actualizarCategoriaRespuesta(CategoriaRespuesta categoriaRespuesta);
    CategoriaRespuesta buscaCategoria(int codigoCategoria);
}
