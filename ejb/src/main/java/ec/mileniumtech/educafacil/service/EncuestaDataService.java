package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.CategoriaRespuestaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.DetalleEvaluaCursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EvaluacionCursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ObjetoEvaluacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PreguntaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RespuestasDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.TipoEncuestaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.TipoEncuestaPreguntaDaoImpl;
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
public class EncuestaDataService {

    @EJB
    private ObjetoEvaluacionDaoImpl objetoEvaluacionDao;

    @EJB
    private CategoriaRespuestaDaoImpl categoriaRespuestaDao;

    @EJB
    private RespuestasDaoImpl respuestasDao;

    @EJB
    private TipoEncuestaDaoImpl tipoEncuestaDao;

    @EJB
    private PreguntaDaoImpl preguntaDao;

    @EJB
    private TipoEncuestaPreguntaDaoImpl tipoEncuestaPreguntaDao;

    @EJB
    private EvaluacionCursoDaoImpl evaluacionCursoDao;

    @EJB
    private DetalleEvaluaCursoDaoImpl detalleEvaluaCursoDao;

    // ========== ObjetoEvaluacion methods ==========

    public List<ObjetoEvaluacion> listaDeObjetosDeEvaluacion() {
        return objetoEvaluacionDao.listaDeObjetosDeEvaluacion();
    }

    public ObjetoEvaluacion actualizarObjetoEvaluacion(ObjetoEvaluacion objetoEvaluacion) {
        return objetoEvaluacionDao.actualizarObjetoEvaluacion(objetoEvaluacion);
    }

    // ========== CategoriaRespuesta methods ==========

    public List<CategoriaRespuesta> listaDeCategorias() {
        return categoriaRespuestaDao.listaDeCategorias();
    }

    public CategoriaRespuesta actualizarCategoriaRespuesta(CategoriaRespuesta categoriaRespuesta) {
        return categoriaRespuestaDao.actualizarCategoriaRespuesta(categoriaRespuesta);
    }

    public CategoriaRespuesta buscaCategoria(int codigoCategoria) {
        return categoriaRespuestaDao.buscaCategoria(codigoCategoria);
    }

    // ========== Respuestas methods ==========

    public List<Respuestas> listaRespuestas() {
        return respuestasDao.listaRespuestas();
    }

    public Respuestas agregActualizarRespuestas(Respuestas respuestas) {
        return respuestasDao.agregActualizarRespuestas(respuestas);
    }

    public List<Respuestas> listaRespuestasPorCategoria(int codigoCategoria) {
        return respuestasDao.listaRespuestasPorCategoria(codigoCategoria);
    }

    // ========== TipoEncuesta methods ==========

    public List<TipoEncuesta> listaDeTiposDeEncuestas() {
        return tipoEncuestaDao.listaDeTiposDeEncuestas();
    }

    public List<TipoEncuesta> listaDeTiposDeEncuestasPorOe(int codigo) {
        return tipoEncuestaDao.listaDeTiposDeEncuestasPorOe(codigo);
    }

    public TipoEncuesta actualizarTipoEncuesta(TipoEncuesta tipoEncuesta) {
        return tipoEncuestaDao.actualizarTipoEncuesta(tipoEncuesta);
    }

    // ========== Pregunta methods ==========

    public List<Pregunta> listaDePreguntas() {
        return preguntaDao.listaDePreguntas();
    }

    public Pregunta agregarActualizarPregunta(Pregunta pregunta) {
        return preguntaDao.agregarActualizarPregunta(pregunta);
    }

    public List<Pregunta> listaPreguntasPorCategoria(int codigoCategoriaP) {
        return preguntaDao.listaPreguntasPorCategoria(codigoCategoriaP);
    }

    // ========== TipoEncuestaPregunta methods ==========

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

    // ========== EvaluacionCurso methods ==========

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

    // ========== DetalleEvaluaCurso methods ==========

    public List<DetalleEvaluaCurso> listaDeDetallesDeEvaluacionDeCursos() {
        return detalleEvaluaCursoDao.listaDeDetallesDeEvaluacionDeCursos();
    }

    public void guardarEncuesta(DetalleEvaluaCurso detalle) {
        detalleEvaluaCursoDao.guardarEncuesta(detalle);
    }
}
