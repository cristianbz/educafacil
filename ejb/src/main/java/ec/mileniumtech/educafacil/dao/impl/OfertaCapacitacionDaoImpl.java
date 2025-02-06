/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Especialidad;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
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
public class OfertaCapacitacionDaoImpl extends GenericoDaoImpl<OfertaCapacitacion, Long>{
	public OfertaCapacitacionDaoImpl() {
		
	}
	public OfertaCapacitacionDaoImpl(EntityManager em, Class<OfertaCapacitacion> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Devuelve una oferta de capacitacion en una area y especialidad
	 * @param area
	 * @param especialidad
	 * @param curso
	 * @return
	 * @throws DaoException
	 */
	public OfertaCapacitacion buscarOfertaCapacitacion(int area,int especialidad,int curso) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCapacitacion.OFERTA_CAPACITACION);
			query.setParameter("area", area);
			query.setParameter("curso", curso);
			query.setParameter("especialidad", especialidad);
			return (OfertaCapacitacion) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Devuelve las especialidades por area
	 * @param area
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Especialidad> listaEspecialidadPorArea(int area)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCapacitacion.LISTA_ESPECIALIDAD_POR_AREA);
			query.setParameter("area", area);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Devuelve los cursos por area y especialidad
	 * @param area
	 * @param especialidad
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Curso> listaCursosPorAreaEspecilidad(int area,int especialidad) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCapacitacion.LISTA_CURSOS_POR_AREA_ESPECIALIDAD);
			query.setParameter("area", area);
			query.setParameter("especialidad", especialidad);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Retorna una oferta de capacitacion por curso
	 * @param codigoCurso
	 * @return
	 * @throws DaoException
	 */
	public OfertaCapacitacion buscarPorCurso(int codigoCurso)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCapacitacion.BUSCAR_POR_CURSO);
			return (OfertaCapacitacion) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Carga todas las ofertas de capacitacion
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<OfertaCapacitacion> listarOfertasCapacitacion() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCapacitacion.CARGAR_TODAS_OFERTAS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Agrega una oferta de capacitacion
	 * @param ofertaCapacitacion
	 * @param ofertaCursos
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarOfertaCapacitacion(OfertaCapacitacion ofertaCapacitacion, OfertaCursos ofertaCursos) throws DaoException,EntidadDuplicadaException{
		try {
			getEntityManager().persist(ofertaCapacitacion);
			ofertaCursos.setOfertaCapacitacion(ofertaCapacitacion);
			getEntityManager().persist(ofertaCursos);
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
