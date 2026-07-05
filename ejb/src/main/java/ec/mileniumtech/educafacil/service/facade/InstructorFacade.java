package ec.mileniumtech.educafacil.service.facade;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import ec.mileniumtech.educafacil.dao.CapacitacionDao;
import ec.mileniumtech.educafacil.dao.CatalogoDao;
import ec.mileniumtech.educafacil.dao.ConfiguracionesDao;
import ec.mileniumtech.educafacil.dao.EmpresaDao;
import ec.mileniumtech.educafacil.dao.FormacionDao;
import ec.mileniumtech.educafacil.dao.InstructorDao;
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

    private static final Logger log = LogManager.getLogger(InstructorFacade.class);

    @EJB
    private InstructorDao instructorDao;

    @EJB
    private FormacionDao formacionDao;

    @EJB
    private CapacitacionDao capacitacionDao;

    @EJB
    private ConfiguracionesDao configuracionesDao;

    @EJB
    private CatalogoDao catalogoDao;

    @EJB
    private EmpresaDao empresaDao;

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

    public void eliminarFormacion(Formacion formacion) {
        formacionDao.remover(formacion);
    }

    public void eliminarCapacitacion(Capacitacion capacitacion) {
        capacitacionDao.remover(capacitacion);
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

