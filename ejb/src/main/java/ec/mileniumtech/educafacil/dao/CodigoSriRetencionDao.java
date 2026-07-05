package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad CodigoSriRetencion.
 * Define operaciones de acceso a datos especÃ­ficas para CodigoSriRetencion.
 */
@Local
public interface CodigoSriRetencionDao extends GenericoDao<CodigoSriRetencion, Integer> {

    List<CodigoSriRetencion> listarPorTipoImpuesto(String tipoImpuesto);
    List<CodigoSriRetencion> buscarPorTipoYTexto(String tipoImpuesto, String query);
}
