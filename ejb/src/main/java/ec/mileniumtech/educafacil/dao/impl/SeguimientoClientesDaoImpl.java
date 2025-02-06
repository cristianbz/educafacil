/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.SeguimientoClientes;
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
public class SeguimientoClientesDaoImpl extends GenericoDaoImpl<SeguimientoClientes, Long>{
	public SeguimientoClientesDaoImpl() {
		
	}
	public SeguimientoClientesDaoImpl(EntityManager em, Class<SeguimientoClientes> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega un nuevo seguimiento a cliente
	 * @param seguimiento
	 * @param detalle
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarSeguimiento(SeguimientoClientes seguimiento, List<DetalleSeguimiento> detalle)throws DaoException,EntidadDuplicadaException {
		try{
			if(seguimiento.getSegcId() == null) {
				getEntityManager().persist(seguimiento);
				for (DetalleSeguimiento detalleseg : detalle) {
					detalleseg.setSeguimientoClientes(seguimiento);
					getEntityManager().persist(detalleseg);
				}
			}else {
				getEntityManager().merge(seguimiento);
				for (DetalleSeguimiento detalleseg : detalle) {
					if(detalleseg.getDsegId() == null)
						getEntityManager().persist(detalleseg);
					else
						getEntityManager().merge(detalleseg);
				}
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
	public List<SeguimientoClientes> listaSeguimiento() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}	
	
//	LISTA_SEGUIMIENTO_ESTADO
	public List<SeguimientoClientes> listaSeguimientoVendedorAsignado() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR_ASIGNADO);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}	
	@SuppressWarnings("unchecked")
	public List<SeguimientoClientes> listaSeguimientoCampania(Integer campania) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_CAMPANIA);
			query.setParameter("campania", campania);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Consulta en base a la campaña los clientes que no tienen asignado un vendedor
	 * @param campaniaS
	 * @return
	 * @throws DaoException
	 */
	public List<SeguimientoClientes> listaSeguimientoCampaniaVendedor(Integer campaniaS) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR);
			query.setParameter("campaniaS", campaniaS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Alcance de la campania
	 * @param campania
	 * @return
	 * @throws DaoException
	 */
	public BigInteger alcanceCampania(int campania)throws DaoException {
		try {
			BigInteger alcance = null;
			String queryString;
			queryString = " select count(segc_id) from cap.seguimientoclientes where camp_id=" + campania;
			Query query = getEntityManager().createNativeQuery(queryString);						
			Object obj= query.getSingleResult();
			alcance = new BigInteger(obj.toString());
			return alcance;
			
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public BigInteger prospectosCampania(int campania, String estado)throws DaoException {
		try {
			BigInteger alcance = null;
			String queryString;
			queryString = " select count(segc_id) from cap.seguimientoclientes where camp_id=" + campania +" AND segc_estado='"+ estado + "'";
			Query query = getEntityManager().createNativeQuery(queryString);
			Object obj= query.getSingleResult();
			alcance = new BigInteger(obj.toString());
			return alcance;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	public List<SeguimientoClientes> listaSeguimientoCampaniaCurso(Integer curso) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_CURSO);
			query.setParameter("curso", curso);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public List<SeguimientoClientes> listaSeguimientoCampaniaFechas(Date inicio, Date fin) throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_FECHAS);
			query.setParameter("fechaInicio", inicio);
			query.setParameter("fechaFin", fin);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public void actualizarSeguimiento(SeguimientoClientes seguimiento) throws DaoException,EntidadDuplicadaException{
		try{
			if(seguimiento.getSegcId() != null) 			
				getEntityManager().merge(seguimiento);				
			
			
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
	 * Busca informacion del seguimiento cliente por el id
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public SeguimientoClientes seguimiento(int id)throws DaoException{
		try {			
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.BUSCA_SEGUIMIENTO);
			query.setParameter("id", id);								
			return (SeguimientoClientes) query.getSingleResult();			
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	public SeguimientoClientes validaNumero(String telefono, int curso, int campania) throws DaoException{
		try {			
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.VALIDA_NUMERO);
			query.setParameter("telefono", telefono);
			query.setParameter("curso", curso);
			query.setParameter("campania", campania);
			return (SeguimientoClientes) query.getSingleResult();			
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Busca la proxima llamada
	 * @return
	 * @throws DaoException
	 */
	public List<SeguimientoClientes> listaPendientesLlamada() throws DaoException{
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.PENDIENTE_LLAMADAS);
			query.setParameter("proximallamada", new Date());			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Devuelve el total del movimiento de datos del CRM.
	 * @param estado
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public BigDecimal totalDatosCRM(String estado) throws DaoException{
		List<Object[]> resultado= null;
		BigDecimal valor= new BigDecimal(0);		
		String sql ="SELECT COUNT(segc_id) FROM cap.seguimientoclientes WHERE segc_estado = '" + estado +"'";
		Query q = getEntityManager().createNativeQuery(sql);
        
		resultado = (List<Object[]>)q.getResultList();
		if(resultado.size()>0){
			for(Object obj:resultado){
				if(obj != null)
					valor = new BigDecimal(obj.toString());
			}
		}
		return valor;
	}
	
	@SuppressWarnings("unchecked")
	public BigDecimal totalDatosCRMVendedor(String estado, Integer vendedor, Integer campania) throws DaoException{
		List<Object[]> resultado= null;
		BigDecimal valor= new BigDecimal(0);		
		String sql ="SELECT COUNT(segc_id) FROM cap.seguimientoclientes WHERE segc_estado = '" + estado +"'"
		+" AND vend_id = '" + vendedor + "'" + "AND camp_id = '" + campania + "'";
		Query q = getEntityManager().createNativeQuery(sql);
        
		resultado = (List<Object[]>)q.getResultList();
		if(resultado.size()>0){
			for(Object obj:resultado){
				if(obj != null)
					valor = new BigDecimal(obj.toString());
			}
		}
		return valor;
	}
	/**
	 * Consulta los interesados por curso del CRM
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<DtoMatriculasCurso> listaInteresadosCursoCRM() throws DaoException{
		List<Object[]> resultado= null;
		List<DtoMatriculasCurso> listaResultado = new ArrayList<DtoMatriculasCurso>();
		String sql ="SELECT COUNT(segc_id) as cantidad,cu.curs_nombre  FROM cap.seguimientoclientes sc, cap.curso cu " +
				 " WHERE sc.curs_id = cu.curs_id GROUP BY cu.curs_nombre ORDER BY cantidad DESC;";
		
		Query q = getEntityManager().createNativeQuery(sql);
		
		resultado = (List<Object[]>)q.getResultList();
		if(resultado.size()>0){
			for(Object obj:resultado){
				Object[] dataObj = (Object[]) obj;
				DtoMatriculasCurso mc= new DtoMatriculasCurso();
				mc.setCantidad(Integer.parseInt(dataObj[0].toString()));
				mc.setCurso(dataObj[1].toString());
				listaResultado.add(mc);
			}
		}
		return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<DtoMatriculasCurso> listaEstadosContactoCursoCRM(String estado) throws DaoException{
		List<Object[]> resultado= null;
		List<DtoMatriculasCurso> listaResultado = new ArrayList<DtoMatriculasCurso>();
		String sql ="SELECT COUNT(segc_id) as cantidad,cu.curs_nombre  FROM cap.seguimientoclientes sc, cap.curso cu "+
				 " WHERE sc.curs_id = cu.curs_id AND sc.segc_estado ='"+estado + "'" 
				+ "GROUP BY cu.curs_nombre ORDER BY cantidad DESC;";
		
		Query q = getEntityManager().createNativeQuery(sql);
		
		resultado = (List<Object[]>)q.getResultList();
		if(resultado.size()>0){
			for(Object obj:resultado){
				Object[] dataObj = (Object[]) obj;
				DtoMatriculasCurso mc= new DtoMatriculasCurso();
				mc.setCantidad(Integer.parseInt(dataObj[0].toString()));
				mc.setCurso(dataObj[1].toString());
				listaResultado.add(mc);
			}
		}
		return listaResultado;
	}
}
