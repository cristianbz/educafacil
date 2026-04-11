/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.SeguimientoClientes;
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
	public void agregarSeguimiento(SeguimientoClientes seguimiento, List<DetalleSeguimiento> detalle) {
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
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en seguimiento cliente", "SEGCLI-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en seguimiento cliente", "SEGCLI-UNEXPECTED-ERR", e);
		}	
	}
	
	@SuppressWarnings("unchecked")
	public List<SeguimientoClientes> listaSeguimiento() {
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista seguimiento cliente", "SEGCLI-LIST-ERR", e);
		}
	}	
	
//	LISTA_SEGUIMIENTO_ESTADO
	public List<SeguimientoClientes> listaSeguimientoVendedorAsignado() {
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR_ASIGNADO);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista vendedor asignado", "SEGCLI-LIST-ERR", e);
		}
	}	
	@SuppressWarnings("unchecked")
	public List<SeguimientoClientes> listaSeguimientoCampania(Integer campania) {
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_CAMPANIA);
			query.setParameter("campania", campania);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista seguimiento campania por campania", "SEGCLI-LIST-ERR", e);
		}
	}
	/**
	 * Consulta en base a la campaña los clientes que no tienen asignado un vendedor
	 * @param campaniaS
	 * @return
	 * @throws DaoException
	 */
	public List<SeguimientoClientes> listaSeguimientoCampaniaVendedor(Integer campaniaS) {
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_VENDEDOR);
			query.setParameter("campaniaS", campaniaS);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista seguimiento cliente por campania", "SEGCLI-LIST-ERR", e);
		}
	}
	/**
	 * Alcance de la campania
	 * @param campania
	 * @return
	 * @throws DaoException
	 */
	public BigInteger alcanceCampania(int campania) {
		try {
			BigInteger alcance = null;
			String queryString;
			queryString = " select count(segc_id) from cap.seguimientoclientes where camp_id=" + campania;
			Query query = getEntityManager().createNativeQuery(queryString);						
			Object obj= query.getSingleResult();
			alcance = new BigInteger(obj.toString());
			return alcance;
			
		} catch (Exception e) {
			throw new SystemException("Error al buscar alcance campania", "SEGCLI-SINGLE-ERR", e);
		}
	}
	
	public BigInteger prospectosCampania(int campania, String estado) {
		try {
			BigInteger alcance = null;
			String queryString;
			queryString = " select count(segc_id) from cap.seguimientoclientes where camp_id=" + campania +" AND segc_estado='"+ estado + "'";
			Query query = getEntityManager().createNativeQuery(queryString);
			Object obj= query.getSingleResult();
			alcance = new BigInteger(obj.toString());
			return alcance;
		} catch (Exception e) {
			throw new SystemException("Error al buscar prospecto campania por campania y estado", "SEGCLI-SINGLE-ERR", e);
		}
	}
	public List<SeguimientoClientes> listaSeguimientoCampaniaCurso(Integer curso) {
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_CURSO);
			query.setParameter("curso", curso);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista seguimiento campania por curso", "SEGCLI-LIST-ERR", e);
		}
	}
	public List<SeguimientoClientes> listaSeguimientoCampaniaFechas(Date inicio, Date fin) {
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.LISTA_SEGUIMIENTO_FECHAS);
			query.setParameter("fechaInicio", inicio);
			query.setParameter("fechaFin", fin);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar lista seguimiento campania por fecha inicio y fecha fin", "SEGCLI-LIST-ERR", e);
		}
	}
	
	public void actualizarSeguimiento(SeguimientoClientes seguimiento){
		try{
			if(seguimiento.getSegcId() != null) 			
				getEntityManager().merge(seguimiento);				
			
			
		}catch(PersistenceException e){
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en seguimiento cliente", "SEGCLI-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en seguimiento cliente", "SEGCLI-UNEXPECTED-ERR", e);
		}	
	}
	/**
	 * Busca informacion del seguimiento cliente por el id
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public SeguimientoClientes seguimiento(int id){
		try {			
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.BUSCA_SEGUIMIENTO);
			query.setParameter("id", id);								
			return (SeguimientoClientes) query.getSingleResult();			
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar seguimiento cliente por codigo", "SEGCLI-SINGLE-ERR", e);
		}
	}
	
	public SeguimientoClientes validaNumero(String telefono, int curso, int campania) {
		try {			
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.VALIDA_NUMERO);
			query.setParameter("telefono", telefono);
			query.setParameter("curso", curso);
			query.setParameter("campania", campania);
			return (SeguimientoClientes) query.getSingleResult();			
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar seguimiento cliente por telefono curso y campania", "SEGCLI-SINGLE-ERR", e);
		}
	}
	/**
	 * Busca la proxima llamada
	 * @return
	 * @throws DaoException
	 */
	public List<SeguimientoClientes> listaPendientesLlamada() {
		try {
			Query query=getEntityManager().createNamedQuery(SeguimientoClientes.PENDIENTE_LLAMADAS);
			query.setParameter("proximallamada", new Date());			
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar seguimiento lista pendientes llamadas", "SEGCLI-LIST-ERR", e);
		}
	}
	/**
	 * Devuelve el total del movimiento de datos del CRM.
	 * @param estado
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public BigDecimal totalDatosCRM(String estado) {
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
	public BigDecimal totalDatosCRMVendedor(String estado, Integer vendedor, Integer campania) {
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
	public List<DtoMatriculasCurso> listaInteresadosCursoCRM() {
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
	public List<DtoMatriculasCurso> listaEstadosContactoCursoCRM(String estado) {
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
