/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
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
public class EgresoDaoImpl extends GenericoDaoImpl<Egresos, Long> {
	public EgresoDaoImpl() {
		
	}
	public EgresoDaoImpl(EntityManager em, Class<Egresos> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega / actualiza un egreso
	 * @param egreso
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarActualizarEgreso(Egresos egreso) throws DaoException, EntidadDuplicadaException{
		try{
			if (egreso.getEgreId() == null)
				getEntityManager().persist(egreso);
			else
				getEntityManager().merge(egreso);
			
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
	 * Carga los egresos del dia
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Egresos> listaEgresos()throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Egresos.CARGA_EGRESOS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public List<Egresos> listaEgresosFechas(Date fechaUno, Date fechaDos)throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Egresos.CARGA_EGRESOS_POR_FECHA);
			query.setParameter("fechauno", fechaUno);
			query.setParameter("fechados", fechaDos);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public List<DtoFlujoDinero> buscaEgresosReporteria(Date fechaInicial, Date fechaFinal)
			throws DaoException {
		try {
			DateFormat formatoFecha = new SimpleDateFormat ("yyyy-MM-dd");
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd",new Locale("es","ES"));
			
			List <DtoFlujoDinero> listaFlujo = new ArrayList<DtoFlujoDinero>();
			String queryString;
			queryString="SELECT EXTRACT(YEAR FROM egre_fecha) as anio, EXTRACT(MONTH FROM egre_fecha) as mes,egre_fecha, sum(egre_valor) "
					+ " FROM cap.egresos WHERE egre_fecha BETWEEN '" + dt1.format(fechaInicial)
					+ "' AND '" + dt1.format(fechaFinal)
					+ "' GROUP BY egre_fecha ORDER BY egre_fecha, anio, mes;";
			
			Query query = getEntityManager().createNativeQuery(queryString);
			List<Object[]> objetos = query.getResultList();
			
			if(!objetos.isEmpty()){
		
				for (Object[] registro: objetos) {
					DtoFlujoDinero flujoDinero = new DtoFlujoDinero();
					flujoDinero.setAnio(Double.parseDouble(registro[0].toString()));
					flujoDinero.setMes(Double.parseDouble(registro[1].toString()));					
					flujoDinero.setFecha(formatoFecha.parse(registro[2].toString()));
					flujoDinero.setValor(Double.parseDouble(registro[3].toString()));
					listaFlujo.add(flujoDinero);
					
				}
				return listaFlujo;
			}else{
				return null;
			}
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
}
