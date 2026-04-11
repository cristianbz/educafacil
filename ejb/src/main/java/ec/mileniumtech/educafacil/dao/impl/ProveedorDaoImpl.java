/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Proveedor;
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
public class ProveedorDaoImpl extends GenericoDaoImpl<Proveedor, Long>{
	public ProveedorDaoImpl() {
		
	}
	public ProveedorDaoImpl(EntityManager em, Class<Proveedor> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega actualiza un proveedor
	 * @param proveedor
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarActualizarProveedor(Proveedor proveedor) {
		try{			
			if (proveedor.getProvId()==null) {
				getEntityManager().persist(proveedor);
			}else {
				getEntityManager().merge(proveedor);
			}
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en proveedor", "PROVE-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en proveedor", "PROVE-UNEXPECTED-ERR", e);
		}	
	}
	/**
	 * Devuelve la lista de proveedores
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Proveedor> listaProveedores() {
		try {
			Query query=getEntityManager().createNamedQuery(Proveedor.LISTA_PROVEEDORES);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista proveedor", "PROVE-LIST-ERR", e);
		}
	}
	/**
	 * Valida el proveedor
	 * @param ruc
	 * @return
	 * @throws DaoException
	 */
	public Proveedor validaProveedor(String ruc) {
		try {
			Query query = getEntityManager().createNamedQuery(Proveedor.RUC_PROVEEDOR);
			query.setParameter("ruc", ruc);
			return JpaDaoSupport.singleResultOrNull(query);
		}catch(Exception e) {
			throw new SystemException("Error al buscar proveedor por ruc", "PROVE-SINGLE-ERR", e);
		}
	}
}
