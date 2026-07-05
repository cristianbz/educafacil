package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Formacion.
 * Define operaciones de acceso a datos especÃ­ficas para Formacion.
 */
@Local
public interface FormacionDao extends GenericoDao<Formacion, Long> {

    void agregaActualizaFormacion(Formacion formacion);
    List<Formacion> listaFormaciones(int codigoInstructor);
}
