package ec.mileniumtech.educafacil.service.facade;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import ec.mileniumtech.educafacil.dao.CategoriaRespuestaDao;
import ec.mileniumtech.educafacil.dao.DetalleEvaluaCursoDao;
import ec.mileniumtech.educafacil.dao.EvaluacionCursoDao;
import ec.mileniumtech.educafacil.dao.ObjetoEvaluacionDao;
import ec.mileniumtech.educafacil.dao.PreguntaDao;
import ec.mileniumtech.educafacil.dao.RespuestasDao;
import ec.mileniumtech.educafacil.dao.TipoEncuestaDao;
import ec.mileniumtech.educafacil.dao.TipoEncuestaPreguntaDao;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoEncuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CategoriaRespuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleEvaluaCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class EncuestaFacade {

    private static final Logger log = LogManager.getLogger(EncuestaFacade.class);

    @EJB
    private ObjetoEvaluacionDao objetoEvaluacionDao;

    @EJB
    private CategoriaRespuestaDao categoriaRespuestaDao;

    @EJB
    private RespuestasDao respuestasDao;

    @EJB
    private TipoEncuestaDao tipoEncuestaDao;

    @EJB
    private PreguntaDao preguntaDao;

    @EJB
    private TipoEncuestaPreguntaDao tipoEncuestaPreguntaDao;

    @EJB
    private EvaluacionCursoDao evaluacionCursoDao;

    @EJB
    private DetalleEvaluaCursoDao detalleEvaluaCursoDao;

    // ========== ObjetoEvaluacion ==========

    public List<ObjetoEvaluacion> listaDeObjetosDeEvaluacion() {
        return objetoEvaluacionDao.listaDeObjetosDeEvaluacion();
    }

    public ObjetoEvaluacion actualizarObjetoEvaluacion(ObjetoEvaluacion objetoEvaluacion) {
        return objetoEvaluacionDao.actualizarObjetoEvaluacion(objetoEvaluacion);
    }

    // ========== CategoriaRespuesta ==========

    public List<CategoriaRespuesta> listaDeCategorias() {
        return categoriaRespuestaDao.listaDeCategorias();
    }

    public CategoriaRespuesta actualizarCategoriaRespuesta(CategoriaRespuesta categoriaRespuesta) {
        return categoriaRespuestaDao.actualizarCategoriaRespuesta(categoriaRespuesta);
    }

    public CategoriaRespuesta buscaCategoria(int codigoCategoria) {
        return categoriaRespuestaDao.buscaCategoria(codigoCategoria);
    }

    // ========== Respuestas ==========

    public List<Respuestas> listaRespuestas() {
        return respuestasDao.listaRespuestas();
    }

    public Respuestas agregActualizarRespuestas(Respuestas respuestas) {
        return respuestasDao.agregActualizarRespuestas(respuestas);
    }

    public List<Respuestas> listaRespuestasPorCategoria(int codigoCategoria) {
        return respuestasDao.listaRespuestasPorCategoria(codigoCategoria);
    }

    // ========== TipoEncuesta ==========

    public List<TipoEncuesta> listaDeTiposDeEncuestas() {
        return tipoEncuestaDao.listaDeTiposDeEncuestas();
    }

    public List<TipoEncuesta> listaDeTiposDeEncuestasPorOe(int codigo) {
        return tipoEncuestaDao.listaDeTiposDeEncuestasPorOe(codigo);
    }

    public TipoEncuesta actualizarTipoEncuesta(TipoEncuesta tipoEncuesta) {
        return tipoEncuestaDao.actualizarTipoEncuesta(tipoEncuesta);
    }

    // ========== Pregunta ==========

    public List<Pregunta> listaDePreguntas() {
        return preguntaDao.listaDePreguntas();
    }

    public Pregunta agregarActualizarPregunta(Pregunta pregunta) {
        return preguntaDao.agregarActualizarPregunta(pregunta);
    }

    public List<Pregunta> listaPreguntasPorCategoria(int codigoCategoriaP) {
        return preguntaDao.listaPreguntasPorCategoria(codigoCategoriaP);
    }

    // ========== TipoEncuestaPregunta ==========

    public List<TipoEncuestaPregunta> listaDePreguntasPorTipoEncuesta(int codigoP) {
        return tipoEncuestaPreguntaDao.listaDePreguntas(codigoP);
    }

    public List<TipoEncuestaPregunta> listaDeTiposDeEncuestasPregunta() {
        return tipoEncuestaPreguntaDao.listaDeTiposDeEncuestas();
    }

    public List<TipoEncuestaPregunta> listaDeEncuestas(int codigoT) {
        return tipoEncuestaPreguntaDao.listaDeEncuestas(codigoT);
    }

    public List<TipoEncuestaPregunta> listaPorTipoDeEncuestas(int codigoTipo) {
        return tipoEncuestaPreguntaDao.listaPorTipoDeEncuestas(codigoTipo);
    }

    public TipoEncuestaPregunta agregarActualizarTipoEncuestaPregunta(TipoEncuestaPregunta tipoEncuestaPregunta) {
        return tipoEncuestaPreguntaDao.agregarActualizarTipoEncuestaPregunta(tipoEncuestaPregunta);
    }

    public List<DtoEncuestas> guardarRespuestasEncuestas(int encuesta) {
        return tipoEncuestaPreguntaDao.guardarRespuestasEncuestas(encuesta);
    }

    // ========== EvaluacionCurso ==========

    public List<EvaluacionCurso> listaDeEvaluacionesDeCurso() {
        return evaluacionCursoDao.listaDeEvaluacionesDeCurso();
    }

    public List<EvaluacionCurso> listaDeEvaluacionesPorCurso(int codigo, int codigoobj) {
        return evaluacionCursoDao.listaDeEvaluacionesPorCurso(codigo, codigoobj);
    }

    public List<EvaluacionCurso> listaDeEvaluacionesDeCursoActivas(int codigoC) {
        return evaluacionCursoDao.listaDeEvaluacionesDeCursoActivas(codigoC);
    }

    public EvaluacionCurso agregarEvaluacionCurso(EvaluacionCurso evaluacionCurso) {
        return evaluacionCursoDao.agregarEvaluacionCurso(evaluacionCurso);
    }

    // ========== DetalleEvaluaCurso ==========

    public List<DetalleEvaluaCurso> listaDeDetallesDeEvaluacionDeCursos() {
        return detalleEvaluaCursoDao.listaDeDetallesDeEvaluacionDeCursos();
    }

    public void guardarEncuesta(DetalleEvaluaCurso detalle) {
        detalleEvaluaCursoDao.guardarEncuesta(detalle);
    }
}

