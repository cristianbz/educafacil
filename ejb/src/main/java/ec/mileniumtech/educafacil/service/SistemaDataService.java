package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EmpresaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class SistemaDataService {

    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;

    @EJB
    private CatalogoDaoImpl catalogoDao;

    @EJB
    private EmpresaDaoImpl empresaDao;

    // ========== Configuraciones methods ==========

    public List<Configuraciones> listaConfiguraciones() {
        return configuracionesDao.listaConfiguraciones();
    }

    // ========== Catalogo methods ==========

    public List<Catalogo> catalogosPorTipo(String tipoCatalogo) {
        return catalogoDao.catalogosPorTipo(tipoCatalogo);
    }

    public List<Catalogo> catalogosPorPadre(Catalogo padre) {
        return catalogoDao.catalogosPorPadre(padre);
    }

    // ========== Empresa methods ==========

    public List<Empresa> listaEmpresas() {
        return empresaDao.listaEmpresas();
    }

    public void agregarEmpresa(Empresa empresa) {
        empresaDao.agregarEmpresa(empresa);
    }
}
