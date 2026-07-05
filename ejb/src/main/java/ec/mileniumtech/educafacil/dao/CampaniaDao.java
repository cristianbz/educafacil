package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Campania.
 * Define operaciones de acceso a datos especÃ­ficas para Campania.
 */
@Local
public interface CampaniaDao extends GenericoDao<Campania, Long> {

    List<Campania> listaCampanias();
    List<Campania> listaCampaniasporCurso();
    void agregarActualizarCampania(Campania campania);
    List<Campania> listaTodasCampanias();
    Campania campaniaCurso(int curso);
    BigDecimal totalGastoCampanias();
}
