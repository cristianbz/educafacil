/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

/**
*@author christian  Jun 15, 2024
*
*/
@LocalBean
@Stateless
public class CursoDaoImpl extends GenericoDaoImpl<Curso, Long>{
	public CursoDaoImpl() {
		
	}
	public CursoDaoImpl(EntityManager em, Class<Curso> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Consulta los cursos existentes
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Curso> listaCursos()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Curso.CARGAR_CURSOS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Curso> listaOfertaCursosActivos()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Curso.OFERTA_CURSOS_ACTIVOS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
}
