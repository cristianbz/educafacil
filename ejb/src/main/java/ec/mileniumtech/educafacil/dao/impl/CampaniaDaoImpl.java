/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;

/**
*@author christian  Jun 15, 2024
*
*/
@LocalBean
@Stateless
public class CampaniaDaoImpl extends GenericoDaoImpl<Campania,Long>{
	public CampaniaDaoImpl() {
		
	}
	public CampaniaDaoImpl(EntityManager em, Class<Campania> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Recupera la lista de campanias activas
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Campania> listaCampanias() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Campania.CAMPANIAS_ACTIVAS);			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public List<Campania> listaCampaniasporCurso() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Campania.CAMPANIA_CURSO_ACTIVAS);			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	/**
	 * Agrega o actualiza una campania
	 * @param campania
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarActualizarCampania(Campania campania) throws DaoException, EntidadDuplicadaException{
		try{
			if (campania.getCampId() == null)
				getEntityManager().persist(campania);
			else
				getEntityManager().merge(campania);
			
		}catch(PersistenceException e){
			 Throwable t = e.getCause();
			    while ((t != null) && !(t instanceof ConstraintViolationException)) {
			        t = t.getCause();
			    }
			    if (t instanceof ConstraintViolationException) {
			    	throw new EntidadDuplicadaException(e);
			    }
			throw new DaoException(e);
		} 	catch (Exception e) {
			throw new DaoException(e);
		}	
	}
	/**
	 * Lista todas las campanias existentes
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Campania> listaTodasCampanias() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Campania.CAMPANIAS_TODAS);			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public Campania campaniaCurso(int curso) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Campania.CAMPANIA_CURSO);	
			query.setParameter("curso", curso);
//			query.setParameter("ahora", new Date());
			return (Campania) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public BigDecimal totalGastoCampanias() throws DaoException{
		List<Object[]> resultado= null;
		BigDecimal valor= new BigDecimal(0);		
		String sql ="SELECT SUM(camp_costo) FROM cap.campania;";
		Query q = getEntityManager().createNativeQuery(sql);
        
		resultado = (List<Object[]>)q.getResultList();
		if(resultado.size()>0){
			for(Object obj:resultado){
				if(obj != null)
					valor = new BigDecimal(obj.toString());
			}
		}
		return valor;
	}
}
