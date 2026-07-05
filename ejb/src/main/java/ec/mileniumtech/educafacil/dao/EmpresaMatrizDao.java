package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import jakarta.ejb.Local;

/**
 * Interfaz DAO para la entidad EmpresaMatriz.
 * Define operaciones de acceso a datos especÃ­ficas para EmpresaMatriz.
 */
@Local
public interface EmpresaMatrizDao extends GenericoDao<EmpresaMatriz, Long> {

    List<EmpresaMatriz> listaEmpresas();
    void agregarEmpresa(EmpresaMatriz empresa);
}
