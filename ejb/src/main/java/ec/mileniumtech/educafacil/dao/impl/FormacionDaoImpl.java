/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;


import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Formacion;
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
public class FormacionDaoImpl extends GenericoDaoImpl<Formacion, Long>{
	public FormacionDaoImpl() {
		
	}
	public FormacionDaoImpl(EntityManager em, Class<Formacion> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega actualiza una formacion del instructor
	 * @param formacion
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregaActualizaFormacion(Formacion formacion){
		try{
			if (formacion.getFormId()==0)
				getEntityManager().persist(formacion);
			else
				getEntityManager().merge(formacion);
			
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en formacion", "FORMACION-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en formacion", "FORMACION-UNEXPECTED-ERR", e);
		}
	}
	/**
	 * Devuelve la lista de formaciones del instructor
	 * @param codigoInstructor
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Formacion> listaFormaciones(int codigoInstructor){
		try {
			Query query=getEntityManager().createNamedQuery(Formacion.LISTADO_FORMACIONES);
			query.setParameter("codigoInstructor", codigoInstructor);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  lista formaciones", "FORMACION-LIST-ERR", e);
		}
	}
}
