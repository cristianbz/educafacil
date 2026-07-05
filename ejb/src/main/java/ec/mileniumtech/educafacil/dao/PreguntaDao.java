package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Pregunta.
 * Define operaciones de acceso a datos especÃ­ficas para Pregunta.
 */
@Local
public interface PreguntaDao extends GenericoDao<Pregunta, Long> {

    List<Pregunta> listaDePreguntas();
    Pregunta agregarActualizarPregunta(Pregunta pregunta);
    List<Pregunta> listaPreguntasPorCategoria(int codigoCategoriaP);
}
