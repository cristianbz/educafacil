package ec.mileniumtech.educafacil.dao.impl;

import ec.mileniumtech.educafacil.dao.GenericoDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Area;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class DaoProducer {
	 @PersistenceContext
	 private EntityManager em;
	 
	 @Produces
	 @ApplicationScoped
	 private GenericoDao<Area, Long> produceAreaDao(){
		return new GenericoDaoImpl<Area, Long>(em, Area.class);		 
	 }
	 
	 @Produces
	 @ApplicationScoped
	 private GenericoDao<Campania, Long> produceCampaniaDao(){
		return new GenericoDaoImpl<Campania, Long>(em, Campania.class);		 
	 }
}
