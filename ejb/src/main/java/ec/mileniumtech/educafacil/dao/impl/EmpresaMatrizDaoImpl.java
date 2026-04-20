package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
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
public class EmpresaMatrizDaoImpl extends GenericoDaoImpl<EmpresaMatriz, Long>{
	public EmpresaMatrizDaoImpl() {
		
	}
	public EmpresaMatrizDaoImpl(EntityManager em, Class<EmpresaMatriz> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Devuelve la lista de empresas activas
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<EmpresaMatriz> listaEmpresas() {
		try {
			Query query=getEntityManager().createNamedQuery(EmpresaMatriz.EMPRESAMATRIZ_ACTIVAS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al listar empresa matriz", "EMPMATRIZ-LIST-ERR", e);
		}
	}
	/**
	 * Agrega o actualiza una empresa matriz
	 * @param empresa
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarEmpresa(EmpresaMatriz empresa)  {
		try{
			if (empresa.getEmpmId()==0)
				getEntityManager().persist(empresa);
			else
				getEntityManager().merge(empresa);
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en empresa matriz", "EMPRMATRIZ-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en empresa matriz", "EMPRMATRIZ-UNEXPECTED-ERR", e);
		}	
	}
}