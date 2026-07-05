package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.MedioInformacion;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad MedioInformacion.
 * Define operaciones de acceso a datos espec\u00edficas para MedioInformacion.
 */
@Local
public interface MedioInformacionDao extends GenericoDao<MedioInformacion, Long> {

    List<MedioInformacion> listaMediosInformacion();
}
