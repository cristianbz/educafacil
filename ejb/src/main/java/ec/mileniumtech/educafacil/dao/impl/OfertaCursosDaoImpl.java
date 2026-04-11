/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import lombok.Getter;

/**
*@author christian  Jun 15, 2024
*
*/
@LocalBean
@Stateless
public class OfertaCursosDaoImpl extends GenericoDaoImpl<OfertaCursos, Long>{
	public OfertaCursosDaoImpl() {
		
	}
	public OfertaCursosDaoImpl(EntityManager em, Class<OfertaCursos> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaDaoImpl;
	@EJB
	@Getter
	private UsuarioDaoImpl usuarioDaoImpl;
	/**
	 * Retorna la lista de cursos disponibles para ser impartidos
	 * @param ofertaCapacitacion
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<OfertaCursos> listaCursosDisponibles(int ofertaCapacitacion) {
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCursos.OFERTA_CURSOS_DISPONIBLES_POR_CURSO);
			query.setParameter("ofertaCapacitacion", ofertaCapacitacion);
			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  lista cursos disponibles por oferta capacitacion", "OFERCUR-LIST-ERR", e);
		}
	}
	/**
	 * Agrega una oferta de cursos
	 * @param ofertaCursos
	 * @throws DaoException
	 */
	public void agregarOfertaCursos(OfertaCursos ofertaCursos) {
		try {
			getEntityManager().persist(ofertaCursos);
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en ofertaCursos", "OFERCUR-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en ofertaCursos", "OFERCUR-UNEXPECTED-ERR", e);
		}		
	}
	/**
	 * Edita una OfertaCurso
	 * @param ofertaCursos
	 * @return
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public OfertaCursos editarOfertaCursos(OfertaCursos ofertaCursos){
		try {
			return getEntityManager().merge(ofertaCursos);
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en ofertaCursos", "OFERCUR-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en ofertaCursos", "OFERCUR-UNEXPECTED-ERR", e);
		}	
	}
	/**
	 * Lista OfertaCursos activos
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<OfertaCursos> listaOfertaCursosActivos(){
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCursos.OFERTA_CURSOS_DISPONIBLES_ACTIVOS);			
			for (Object object : query.getResultList()) {
				OfertaCursos ofc = (OfertaCursos) object;
				Iterator itera = ofc.getEvaluacionCurso().iterator();
				while (itera.hasNext()) {
					EvaluacionCurso eva = (EvaluacionCurso)itera.next();
					if(eva.isEvcuEstado()== false)
						itera.remove();
					Hibernate.initialize(eva);
				}
			}
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  lista ofertaCursosActivos", "OFERCUR-LIST-ERR", e);
		}
	}
	/**
	 * Lista de cursos activos o cerrados
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<OfertaCursos> listaOfertaCursosActivosCerrados(){
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCursos.OFERTA_CURSOS_ACTIVOS_CERRADOS);			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  lista ofertaCursosCerrados", "OFERCUR-LIST-ERR", e);
		}
	}
	
	/**
	 * Finaliza un curso activo
	 * @param ofertaCurso
	 * @param matriculas
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void finalizarCursoActivo(OfertaCursos ofertaCurso,List<Matricula> matriculas){
		try {
			getEntityManager().merge(ofertaCurso);
			matriculas.stream().forEach(m -> getEntityManager().merge(m));

		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en ofertaCursos", "OFERCUR-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en ofertaCursos", "OFERCUR-UNEXPECTED-ERR", e);
		}	
	}
	/**
	 * Devuelve los cursos por defecto
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<OfertaCursos> listaOfertaCursosPorDefecto(){
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCursos.OFERTA_CURSOS_POR_DEFECTO);			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  lista ofertaCursosPorDefecto", "OFERCUR-LIST-ERR", e);
		}
	}
	@SuppressWarnings("unchecked")
	public List<OfertaCursos> listaOfertaCursosPorCurso(int codigoCurso){
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCursos.OFERTA_CURSOS_POR_CURSO);
			query.setParameter("cursoId", codigoCurso);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  lista ofertaCursosPorCurso por curso", "OFERCUR-LIST-ERR", e);
		}
	}
	public List<OfertaCursos> listaOfertaCursosPorCursoAnio(int codigoCurso,int anio) {
		try {
			Query query=getEntityManager().createNamedQuery(OfertaCursos.OFERTA_CURSOS_POR_CURSO_ANIO);
			query.setParameter("cursoId", codigoCurso);
			query.setParameter("anioBusqueda", anio);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar  lista ofertaCursosPorCursoAnio", "OFERCUR-LIST-ERR", e);
		}
	}

}
