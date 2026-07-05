/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.EvaluacionCursoDao;
import ec.mileniumtech.educafacil.dao.ObjetoEvaluacionDao;
import ec.mileniumtech.educafacil.dao.TipoEncuestaDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio para manejar la lógica de evaluaciones de cursos, objetos de evaluación y encuestas.
 *
 * @author christian
 */
@Stateless
@LocalBean
public class EvaluacionService {

    private static final Logger log = LogManager.getLogger(EvaluacionService.class);

    @EJB
    private ObjetoEvaluacionDao objetoEvaluacionDao;

    @EJB
    private TipoEncuestaDao tipoEncuestaDao;

    @EJB
    private EvaluacionCursoDao evaluacionCursoDao;

    /**
     * Lista objetos de evaluación.
     */
    public List<ObjetoEvaluacion> listarObjetosEvaluacion() {
        return objetoEvaluacionDao.listaDeObjetosDeEvaluacion();
    }

    /**
     * Lista evaluaciones por curso y objeto.
     */
    public List<EvaluacionCurso> listarEvaluacionesPorCurso(int ocurId, int objeId) {
        return evaluacionCursoDao.listaDeEvaluacionesPorCurso(ocurId, objeId);
    }

    /**
     * Lista tipos de encuestas por objeto de evaluación.
     */
    public List<TipoEncuesta> listarTiposEncuestasPorObjeto(int objeId) {
        return tipoEncuestaDao.listaDeTiposDeEncuestasPorOe(objeId);
    }

    /**
     * Agrega una evaluación de curso.
     */
    public void agregarEvaluacionCurso(EvaluacionCurso evaluacionCurso) {
        evaluacionCursoDao.agregarEvaluacionCurso(evaluacionCurso);
    }
}
