package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad OfertaCursos.
 * Define operaciones de acceso a datos especÃ­ficas para OfertaCursos.
 */
@Local
public interface OfertaCursosDao extends GenericoDao<OfertaCursos, Long> {

    List<OfertaCursos> listaCursosDisponibles(int ofertaCapacitacion);
    void agregarOfertaCursos(OfertaCursos ofertaCursos);
    OfertaCursos editarOfertaCursos(OfertaCursos ofertaCursos);
    List<OfertaCursos> listaOfertaCursosActivos();
    List<OfertaCursos> listaOfertaCursosActivosCerrados();
    void finalizarCursoActivo(OfertaCursos ofertaCurso,List<Matricula> matriculas);
    List<OfertaCursos> listaOfertaCursosPorDefecto();
    List<OfertaCursos> listaOfertaCursosPorCurso(int codigoCurso);
    List<OfertaCursos> listaOfertaCursosPorCursoAnio(int codigoCurso,int anio);
}
