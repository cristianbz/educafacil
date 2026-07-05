package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;

import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentacionProveedor;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad DocumentacionProveedor.
 * Define operaciones de acceso a datos especÃ­ficas para DocumentacionProveedor.
 */
@Local
public interface DocumentacionProveedorDao extends GenericoDao<DocumentacionProveedor, Long> {

    void agregarActualizarDocumentacionProveedor(DocumentacionProveedor documentacionProveedor);
    DocumentacionProveedor buscarDocumentacionPorProveedor(int codigoProveedor);
}
