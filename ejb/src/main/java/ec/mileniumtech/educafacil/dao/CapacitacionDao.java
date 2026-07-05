package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Capacitacion.
 * Define operaciones de acceso a datos especÃ­ficas para Capacitacion.
 */
@Local
public interface CapacitacionDao extends GenericoDao<Capacitacion, Long> {

    void agregarActualizarCapacitacion(Capacitacion capacitacion);
    List<Capacitacion> listaCapacitaciones(int codigoInstructor);
}
