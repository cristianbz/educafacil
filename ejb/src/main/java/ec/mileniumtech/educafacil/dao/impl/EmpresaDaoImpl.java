/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;


import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
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
	public List<Empresa> listaEmpresas() {
		try {
			Query query=getEntityManager().createNamedQuery(Empresa.EMPRESAS_ACTIVAS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al listar empresas", "EMP-LIST-ERR", e);
		}
	}
	/**
	 * Agrega o actualiza una empresa
	 * @param empresa
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarEmpresa(Empresa empresa)  {
		try{
			if (empresa.getEmprId()==0)
				getEntityManager().persist(empresa);
			else
				getEntityManager().merge(empresa);
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en empresa", "EMPR-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en empresa", "EMPR-UNEXPECTED-ERR", e);
		}	
	}

}
