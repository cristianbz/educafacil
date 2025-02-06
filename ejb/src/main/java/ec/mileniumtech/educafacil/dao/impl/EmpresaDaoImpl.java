/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
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
public class EmpresaDaoImpl extends GenericoDaoImpl<Empresa, Long>{
	public EmpresaDaoImpl() {
		
	}
	public EmpresaDaoImpl(EntityManager em, Class<Empresa> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Devuelve la lista de empresas activas
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Empresa> listaEmpresas() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Empresa.EMPRESAS_ACTIVAS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Agrega o actualiza una empresa
	 * @param empresa
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarEmpresa(Empresa empresa) throws DaoException,EntidadDuplicadaException {
		try{
			if (empresa.getEmprId()==0)
				getEntityManager().persist(empresa);
			else
				getEntityManager().merge(empresa);
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
