package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Respuestas.
 * Define operaciones de acceso a datos especÃ­ficas para Respuestas.
 */
@Local
public interface RespuestasDao extends GenericoDao<Respuestas, Long> {

    List<Respuestas> listaRespuestas();
    Respuestas agregActualizarRespuestas(Respuestas respuestas);
    List<Respuestas> listaRespuestasPorCategoria(int codigoCategoria);
}
