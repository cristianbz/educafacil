package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad ObjetoEvaluacion.
 * Define operaciones de acceso a datos especÃ­ficas para ObjetoEvaluacion.
 */
@Local
public interface ObjetoEvaluacionDao extends GenericoDao<ObjetoEvaluacion, Long> {

    List<ObjetoEvaluacion> listaDeObjetosDeEvaluacion();
    ObjetoEvaluacion actualizarObjetoEvaluacion(ObjetoEvaluacion objetoEvaluacion);
}
