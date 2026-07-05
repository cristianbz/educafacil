package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Vendedor;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Vendedor.
 * Define operaciones de acceso a datos especÃ­ficas para Vendedor.
 */
@Local
public interface VendedorDao extends GenericoDao<Vendedor, Long> {

    List<Vendedor> listaDeVendedores();
}
