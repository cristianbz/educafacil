package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.CapacitacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FormacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.InstructorDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Capacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class InstructorDataService {

    @EJB
    private InstructorDaoImpl instructorDao;

    @EJB
    private FormacionDaoImpl formacionDao;

    @EJB
    private CapacitacionDaoImpl capacitacionDao;

    // ========== Instructor methods ==========

    public List<Instructor> listaInstructores() {
        return instructorDao.listaInstructores();
    }

    public void agregarActualizarInstructor(Instructor instructor) {
        instructorDao.agregarActualizarInstructor(instructor);
    }

    // ========== Formacion methods ==========

    public void agregaActualizaFormacion(Formacion formacion) {
        formacionDao.agregaActualizaFormacion(formacion);
    }

    public List<Formacion> listaFormaciones(int codigoInstructor) {
        return formacionDao.listaFormaciones(codigoInstructor);
    }

    // ========== Capacitacion methods ==========

    public void agregarActualizarCapacitacion(Capacitacion capacitacion) {
        capacitacionDao.agregarActualizarCapacitacion(capacitacion);
    }

    public List<Capacitacion> listaCapacitaciones(int codigoInstructor) {
        return capacitacionDao.listaCapacitaciones(codigoInstructor);
    }
}
