package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad OfertaCapacitacion.
 * Define operaciones de acceso a datos espec\u00edficas para OfertaCapacitacion.
 */
@Local
public interface OfertaCapacitacionDao extends GenericoDao<OfertaCapacitacion, Long> {

    OfertaCapacitacion buscarOfertaCapacitacion(int area, int especialidad, int curso);
    List<Especialidad> listaEspecialidadPorArea(int area);
    List<Curso> listaCursosPorAreaEspecilidad(int area, int especialidad);
    OfertaCapacitacion buscarPorCurso(int codigoCurso);
    List<OfertaCapacitacion> listarOfertasCapacitacion();
    void agregarOfertaCapacitacion(OfertaCapacitacion ofertaCapacitacion, OfertaCursos ofertaCursos);
}
