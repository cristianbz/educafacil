/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;


import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

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
	public List<UsuarioRol> listaDeUsuarioRol(){
		try {
			Query query=getEntityManager().createNamedQuery(UsuarioRol.CARGAR_Usuario_Rol);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al consultar lista de usuario rol", "UROL-LIST-ERR", e);
		}
	}
	
	public List<UsuarioRol> listaUsuarioRolPorUsuario(int idUsuario){
		try {
			Query query=getEntityManager().createNamedQuery(UsuarioRol.CARGAR_Usuario_Rol_Por_IDUsuario);
			query.setParameter("idUsuario", idUsuario);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al consultar lista de usuario rol por usuario", "UROL-LIST-ERR", e);
		}
	}
	
	public UsuarioRol agregarUsuarioRol(UsuarioRol usuarioRol) {
		try{
			if(usuarioRol.getUrolId()==null) 
				getEntityManager().persist(usuarioRol);
			
			
			return usuarioRol;
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de usuario rol", "UROL-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en usuario rol", "UROL-UNEXPECTED-ERR", e);
		}	
	}
}
