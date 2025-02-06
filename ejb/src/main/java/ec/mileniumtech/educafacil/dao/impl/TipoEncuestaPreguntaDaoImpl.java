/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoEncuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
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
public class TipoEncuestaPreguntaDaoImpl extends GenericoDaoImpl<TipoEncuestaPregunta, Long>{
	public TipoEncuestaPreguntaDaoImpl() {
		
	}
	public TipoEncuestaPreguntaDaoImpl(EntityManager em, Class<TipoEncuestaPregunta> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("unchecked")
	public List<TipoEncuestaPregunta> listaDePreguntas(int codigoP)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(TipoEncuestaPregunta.CARGAR_PREGUNTA);
			query.setParameter("codigo", codigoP);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
		
}
	@SuppressWarnings("unchecked")
	public List<TipoEncuestaPregunta> listaDeTiposDeEncuestas()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(TipoEncuestaPregunta.CARGAR_TIPO_ENCUESTA);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
/**
 * 
 * @param codigoT para el id del tipo de encuesta
 * @return
 * @throws DaoException
 */
	public List<TipoEncuestaPregunta> listaDeEncuestas(int codigoT)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(TipoEncuestaPregunta.CARGAR_ENCUESTAS);
			query.setParameter("codigoTipo", codigoT);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public List<TipoEncuestaPregunta> listaPorTipoDeEncuestas(int codigoTipo)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(TipoEncuestaPregunta.CARGAR_POR_TIPO_ENCUESTA);
			query.setParameter("codigo", codigoTipo);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public TipoEncuestaPregunta agregarActualizarTipoEncuestaPregunta(TipoEncuestaPregunta tipoEncuestaPregunta)throws DaoException,EntidadDuplicadaException {
		try{
			if(tipoEncuestaPregunta.getTeprId()==null)
				getEntityManager().persist(tipoEncuestaPregunta);
			else
				getEntityManager().merge(tipoEncuestaPregunta);
			return tipoEncuestaPregunta;
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
	
	/**
	 * metodo para guardar las respuestas de las encuestas mediante la concatenacion de preg_Id y resp_Id, ademas se grabara 
	 * siguiendo el siguiente formato "preg_Descripcion, resp_Descipcion y (preg_ID - resp_Id)"
	 * @param encuesta 
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<DtoEncuestas> guardarRespuestasEncuestas(int encuesta)
			throws DaoException {
		try {
			
			List <DtoEncuestas> listaRespuestasEncuestas= new ArrayList<DtoEncuestas>();
			String queryString;
			queryString="SELECT DISTINCT P.preg_Id, P.preg_descripcion, R.resp_descripcion,  concat(P.preg_Id,'-',R.resp_Id)"
					+ "FROM  cap.pregunta P,"
					+ "cap.categoriarespuesta CR, cap.respuestas R , cap.tipoencuestapregunta TEP"
					+ "WHERE P.catr_Id = CR.catr_Id AND R.catr_Id = CR.catr_Id AND TEP.tipe_Id = '"+encuesta+"' ORDER BY P.preg_Id";
					Query query = getEntityManager().createNativeQuery(queryString);
			List<Object[]> objetos = query.getResultList();
			if(!objetos.isEmpty()){
				for (Object[] encuestaResuelta: objetos) {
					DtoEncuestas dtoEncuestas = new DtoEncuestas();
					dtoEncuestas.setPregunta(encuestaResuelta[0].toString());
					dtoEncuestas.setRespuesta(encuestaResuelta[1].toString());
					dtoEncuestas.setCodigoPregResp(encuestaResuelta[2].toString());
				}
				return listaRespuestasEncuestas;
			}else{
				return null;
			}
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
}
