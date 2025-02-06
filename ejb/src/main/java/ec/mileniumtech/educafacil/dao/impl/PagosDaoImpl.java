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
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
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
public class PagosDaoImpl extends GenericoDaoImpl<Pagos, Long>{
	public PagosDaoImpl() {
		
	}
	public PagosDaoImpl(EntityManager em, Class<Pagos> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	public void agregarPago(Pagos pago)throws DaoException,EntidadDuplicadaException {
		try{
			getEntityManager().persist(pago);
			for (DetallePagos detalle : pago.getDetallePagos()) {
//				detalle.setPagos(pago);
				getEntityManager().persist(detalle);
			}

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

	@SuppressWarnings("unchecked")
	public List<DetallePagos> buscaPagosPorMatricula(int codigoMatricula) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(Pagos.BUSCAR_DETALLEPAGOS);
			query.setParameter("codigoMatricula", codigoMatricula);					
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Busca los egresos en una fecha determinada
	 * @param fechaInicial
	 * @param fechaFinal
	 * @return
	 * @throws DaoException
	 */
	public List<DtoFlujoDinero> buscaIngresosReporteria(Date fechaInicial, Date fechaFinal)
			throws DaoException {
		try {
			DateFormat formatoFecha = new SimpleDateFormat ("yyyy-MM-dd");
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd",new Locale("es","ES"));
			
			List <DtoFlujoDinero> listaFlujo = new ArrayList<DtoFlujoDinero>();
			String queryString;
			queryString="SELECT EXTRACT(YEAR FROM depa_fecha_inserto) as anio, EXTRACT(MONTH FROM depa_fecha_inserto) as mes,depa_fecha_inserto, SUM(depa_valor) "
					+ "FROM cap.detallepagos WHERE depa_estado=true AND depa_fecha_inserto BETWEEN '" + dt1.format(fechaInicial)
					+ "' AND '" + dt1.format(fechaFinal)
					+ "' GROUP BY depa_fecha_inserto ORDER BY depa_fecha_inserto,anio,mes;";
					
			
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
