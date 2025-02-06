/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ec.mileniumtech.educafacil.dao.GenericoDao;
import ec.mileniumtech.educafacil.dao.impl.AreaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import jakarta.inject.Inject;

/**
*@author christian  Jun 16, 2024
*
*/
class PruebasUnitarias {

	@Inject
	AreaDaoImpl areaDao;
	@Test
	void test() {
		fail("Not yet implemented");
	}
	
	@Test
	void pruebaCantidadAreas() {
		assertEquals(5, areaDao.findById((long) 5));
	}

}
