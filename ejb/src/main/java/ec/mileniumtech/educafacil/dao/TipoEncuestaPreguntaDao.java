package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoEncuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad TipoEncuestaPregunta.
 * Define operaciones de acceso a datos espec\u00edficas para TipoEncuestaPregunta.
 */
@Local
public interface TipoEncuestaPreguntaDao extends GenericoDao<TipoEncuestaPregunta, Long> {

    List<TipoEncuestaPregunta> listaDePreguntas(int codigoP);
    List<TipoEncuestaPregunta> listaDeTiposDeEncuestas();
    List<TipoEncuestaPregunta> listaDeEncuestas(int codigoT);
    List<TipoEncuestaPregunta> listaPorTipoDeEncuestas(int codigoTipo);
    TipoEncuestaPregunta agregarActualizarTipoEncuestaPregunta(TipoEncuestaPregunta tipoEncuestaPregunta);
    List<DtoEncuestas> guardarRespuestasEncuestas(int encuesta);
}
