/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
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
public class UsuarioRolDaoImpl extends GenericoDaoImpl<UsuarioRol, Long>{
	public UsuarioRolDaoImpl() {
		
	}
	public UsuarioRolDaoImpl(EntityManager em, Class<UsuarioRol> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<UsuarioRol> listaDeUsuarioRol()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(UsuarioRol.CARGAR_Usuario_Rol);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public UsuarioRol agregarUsuarioRol(UsuarioRol usuarioRol)throws DaoException,EntidadDuplicadaException {
		try{
			if(usuarioRol.getUrolId()==null) 
				getEntityManager().persist(usuarioRol);
			
			
			return usuarioRol;
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
}
