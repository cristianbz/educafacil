package ec.mileniumtech.educafacil.dao.impl;


import ec.mileniumtech.educafacil.dao.SriformapagoDao;import ec.mileniumtech.educafacil.modelo.persistencia.entity.Sriformapago;
import jakarta.ejb.Stateless;

@Stateless
public class SriformapagoDaoImpl extends GenericoDaoImpl<Sriformapago, Integer> implements SriformapagoDao {

	public SriformapagoDaoImpl() {
		super();
	}
}
