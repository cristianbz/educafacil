/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;


import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentacionProveedor;
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
public class DocumentacionProveedorDaoImpl extends GenericoDaoImpl<DocumentacionProveedor, Long>{
	public DocumentacionProveedorDaoImpl() {
		
	}
	public DocumentacionProveedorDaoImpl(EntityManager em, Class<DocumentacionProveedor> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega actualiza la documentacion de un proveedor
	 * @param documentacionProveedor
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarActualizarDocumentacionProveedor(DocumentacionProveedor documentacionProveedor){
		try{
			if (documentacionProveedor.getDocpId()==0)
				getEntityManager().persist(documentacionProveedor);
			else
				getEntityManager().merge(documentacionProveedor);
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en documentación de proveedor", "DOCP-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en documentación de proveedor", "DOCP-UNEXPECTED-ERR", e);
		}
	}
	/**
	 * Devuelve la documentacion de un proveedor
	 * @param codigoProveedor
	 * @return
	 * @throws DaoException
	 */
	public DocumentacionProveedor buscarDocumentacionPorProveedor(int codigoProveedor){
		try {
			Query query=getEntityManager().createNamedQuery(DocumentacionProveedor.DOCUMENTACION_POR_PROVEEDOR);
			query.setParameter("codigoProveedor", codigoProveedor);
			return (DocumentacionProveedor) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar documentación por proveedor", "DOCP-FIND-ERR", e);
		}
	}
}
