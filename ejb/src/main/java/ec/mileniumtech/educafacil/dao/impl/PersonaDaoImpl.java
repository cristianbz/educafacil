/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
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
public class PersonaDaoImpl extends GenericoDaoImpl<Persona, Long>{
	public PersonaDaoImpl() {
		
	}
	public PersonaDaoImpl(EntityManager em, Class<Persona> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Consulta datos de la persona por cedula
	 * @param cedula
	 * @return
	 * @throws DaoException
	 */
	public Persona buscarPersonaPorCedula(String cedula)throws DaoException{
		try {
			Persona persona=new Persona();
			Query query = getEntityManager().createNamedQuery(Persona.BUSCAR_POR_CEDULA);
			query.setParameter("cedula", cedula);
			
			persona= (Persona) query.getSingleResult();
			if(persona.getEstudiantes().size()>1)
				Hibernate.initialize(persona.getEstudiantes().get(0));
			return persona;
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Agrega una persona
	 * @param persona
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarPersona(Persona persona)throws DaoException,EntidadDuplicadaException {
		try{
			getEntityManager().persist(persona);
			
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
	 * Actualiza la informacion de una persona
	 * @param persona
	 * @return
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public Persona actualizarPersona(Persona persona)throws DaoException,EntidadDuplicadaException {
		try{
			if(persona.getPersId()==0)
				getEntityManager().persist(persona);
			else
				getEntityManager().merge(persona);
			return persona;
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
	/***
	 * Devuelve una persona o personas por apellidos
	 * @param apellidos
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Persona> buscarPersonaPorApellidos(String apellidos)throws DaoException{
		try {			
			Query query = getEntityManager().createNamedQuery(Persona.BUSCAR_POR_APELLIDOS);
			query.setParameter("apellidos", apellidos);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Buscar persona por id
	 * @param codigo
	 * @return
	 * @throws DaoException
	 */
	public Persona buscarPersonaPorId(int codigo)throws DaoException{
		try {
			Persona persona=new Persona();
			Query query = getEntityManager().createNamedQuery(Persona.BUSCAR_POR_ID);
			query.setParameter("id", codigo);
			
			persona= (Persona) query.getSingleResult();
			if(!persona.getEstudiantes().isEmpty())
				Hibernate.initialize(persona.getEstudiantes().get(0));
			return persona;
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Busca por cedula y correo
	 * @param cedula
	 * @param correo
	 * @return
	 * @throws DaoException
	 */
	public Persona buscarPersonaPorCedulaCorreo(String cedula, String correo)throws DaoException{
		try {
			Persona persona=new Persona();
			Query query = getEntityManager().createNamedQuery(Persona.BUSCAR_POR_CEDULA_CORREO);
			query.setParameter("cedula", cedula);
			query.setParameter("correo", correo);
			
			persona= (Persona) query.getSingleResult();			
			return persona;
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	} 
}
