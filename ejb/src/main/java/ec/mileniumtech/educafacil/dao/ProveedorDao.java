package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Proveedor;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Proveedor.
 * Define operaciones de acceso a datos especÃ­ficas para Proveedor.
 */
@Local
public interface ProveedorDao extends GenericoDao<Proveedor, Long> {

    void agregarActualizarProveedor(Proveedor proveedor);
    List<Proveedor> listaProveedores();
    Proveedor validaProveedor(String ruc);
}
