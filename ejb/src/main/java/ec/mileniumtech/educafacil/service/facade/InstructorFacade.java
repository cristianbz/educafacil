package ec.mileniumtech.educafacil.service.facade;

import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.CapacitacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EmpresaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FormacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.InstructorDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class InstructorFacade {

    @EJB
    private InstructorDaoImpl instructorDao;

    @EJB
    private FormacionDaoImpl formacionDao;

    @EJB
    private CapacitacionDaoImpl capacitacionDao;

    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;

    @EJB
    private CatalogoDaoImpl catalogoDao;

    @EJB
    private EmpresaDaoImpl empresaDao;

    // ========== Instructor ==========

    public List<Instructor> listaInstructores() {
        return instructorDao.listaInstructores();
    }

    public void agregarActualizarInstructor(Instructor instructor) {
        instructorDao.agregarActualizarInstructor(instructor);
    }

    // ========== Formacion ==========

    public void agregaActualizaFormacion(Formacion formacion) {
        formacionDao.agregaActualizaFormacion(formacion);
    }

    public List<Formacion> listaFormaciones(int codigoInstructor) {
        return formacionDao.listaFormaciones(codigoInstructor);
    }

    // ========== Capacitacion ==========

    public void agregarActualizarCapacitacion(Capacitacion capacitacion) {
        capacitacionDao.agregarActualizarCapacitacion(capacitacion);
    }

    public List<Capacitacion> listaCapacitaciones(int codigoInstructor) {
        return capacitacionDao.listaCapacitaciones(codigoInstructor);
    }

    // ========== Configuraciones ==========

    public List<Configuraciones> listaConfiguraciones() {
        return configuracionesDao.listaConfiguraciones();
    }

    // ========== Catalogo ==========

    public List<Catalogo> catalogosPorTipo(String tipoCatalogo) {
        return catalogoDao.catalogosPorTipo(tipoCatalogo);
    }

    public List<Catalogo> catalogosPorPadre(Catalogo padre) {
        return catalogoDao.catalogosPorPadre(padre);
    }

    // ========== Empresa ==========

    public List<Empresa> listaEmpresas() {
        return empresaDao.listaEmpresas();
    }

    public void agregarEmpresa(Empresa empresa) {
        empresaDao.agregarEmpresa(empresa);
    }
}
