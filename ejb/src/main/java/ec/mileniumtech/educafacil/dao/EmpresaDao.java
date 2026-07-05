package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad Empresa.
 * Define operaciones de acceso a datos especÃ­ficas para Empresa.
 */
@Local
public interface EmpresaDao extends GenericoDao<Empresa, Long> {

    List<Empresa> listaEmpresas();
    void agregarEmpresa(Empresa empresa);
}
