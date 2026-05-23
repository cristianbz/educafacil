package ec.mileniumtech.educafacil.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ec.mileniumtech.educafacil.dao.impl.AreaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;

class GenericoDaoTest {

    private AreaDaoImpl areaDao;

    @BeforeEach
    void setUp() {
        areaDao = new AreaDaoImpl();
    }

    @Test
    void validarCadenaNulaConCadenaValida() {
        assertFalse(areaDao.validarCadenaNula("Hola"));
    }

    @Test
    void validarCadenaNulaConNull() {
        assertTrue(areaDao.validarCadenaNula(null));
    }

    @Test
    void validarCadenaNulaConVacio() {
        assertTrue(areaDao.validarCadenaNula(""));
    }

    @Test
    void validarCadenaNulaConEspacios() {
        assertFalse(areaDao.validarCadenaNula("   "));
    }
}
