/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;

/**
*@author christian  Jun 15, 2024
*
*/
@LocalBean
@Stateless
public class MatriculaDaoImpl extends GenericoDaoImpl<Matricula, Long>{
	@EJB
	@Getter
	private UsuarioDaoImpl usuarioDaoImpl;
	@EJB
	@Getter
	private PersonaDaoImpl personaDaoImpl;
	@EJB
	@Getter
	private EstudianteDaoImpl estudianteDaoImpl;
	@EJB
	@Getter
	private UsuarioRolDaoImpl usuarioRolDaoImpl;
	public MatriculaDaoImpl() {
		
	}
	public MatriculaDaoImpl(EntityManager em, Class<Matricula> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Agrega una matricula
	 * @param matricula
	 * @throws DaoException
	 * @throws EntidadDuplicadaException
	 */
	public void agregarMatriculaInscripcion(Persona persona,Matricula matricula, Usuario usuario, UsuarioRol usuarioRol)throws DaoException,EntidadDuplicadaException {
		try{
			if(persona.getPersId()==0) {
				getPersonaDaoImpl().agregarPersona(persona);
				getEstudianteDaoImpl().guardar(matricula.getEstudiante());
				getUsuarioDaoImpl().agregarUsuario(usuario);
				getUsuarioRolDaoImpl().agregarUsuarioRol(usuarioRol);
				
			}
			else {
				Estudiante estudiante=new Estudiante();
				persona=getPersonaDaoImpl().actualizar(persona);
				if(matricula.getEstudiante().getEstuId()==0) {
					estudiante=getEstudianteDaoImpl().guardar(matricula.getEstudiante());
					matricula.setEstudiante(estudiante);
				}
			}
			
			if (matricula.getMatrId()==null)
				getEntityManager().persist(matricula);
			else
				getEntityManager().merge(matricula);
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
	 * Retorna las matriculas o inscripciones o cursos culminados del alumno 
	 * @param codigoPersona
	 * @param codigoEstado
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasAlumno(int codigoPersona,String codigoEstado)throws DaoException{
		try {
			
			Query query=getEntityManager().createNamedQuery(Matricula.INSCRIPCION_MATRICULA_CULMINACION_ALUMNO);
			query.setParameter("codigoEstado", codigoEstado);
			query.setParameter("codigoPersona", codigoPersona);			
			return query.getResultList();
			
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Busca alumnos matriculados/inscritos/culminados
	 * @param estado
	 * @param fechaInicio
	 * @param fechaFin
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasInscripcion(String estado,Date fechaInicio,Date fechaFin) throws DaoException{
		try {
			Query query=null;
			if(estado.equals("INSMAT01")) 
				query=getEntityManager().createNamedQuery(Matricula.BUSCAR_INSCRIPCION);
			else if(estado.equals("INSMAT02") || estado.equals("INSMAT05")) 
				query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULA_PORFECHA);
			query.setParameter("codigoEstado", estado);
			query.setParameter("fechaInicio", fechaInicio);
			query.setParameter("fechaFin", fechaFin);
			return query.getResultList();	
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasCurso(String estado,int codigoCurso) throws DaoException{
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_POR_CURSO_ESTADO);
			query.setParameter("codigoEstado", estado);
			query.setParameter("codigoCurso", codigoCurso);			
			return query.getResultList();	
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Lista de alumnos en estado matriculado o encurso
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculadosOEnCursoPorOferta(int codigoOferta) throws DaoException{
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULADOS_O_ENCURSO_POR_OFERTA);
			query.setParameter("codigoOferta", codigoOferta);
			return query.getResultList();	
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Consulta las oportunidades de ventas
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public List<Matricula> listaOportunidades() throws DaoException{
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_OPORTUNIDADES);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Consulta las matriculas por estudiante
	 * @param codigoEstudiante
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasEstudiante(int codigoEstudiante) throws DaoException{
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULAS_ALUMNO);
			query.setParameter("codigoEstudiante", codigoEstudiante);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * Actualiza la matricula
	 * @param matricula
	 * @throws DaoException
	 */
	public void actualizaMatricula(Matricula matricula) throws DaoException{
		try {
			getEntityManager().merge(matricula);
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public void actualizaMatriculaUsuario(Matricula matricula, Usuario usuario)throws DaoException{
		try {			 
			actualizaMatricula(matricula);
			getUsuarioDaoImpl().actualizaUsuario(usuario);
		}catch(DaoException e) {			
			throw new DaoException(e);
		}
	}
	/**
	 * Permite ver si una matricula de un estudiante ya existe
	 * @param oferta OfertaCurso
	 * @param estudiante codigo del estudiante
	 * @return
	 * @throws DaoException
	 */
	public Matricula existeMatricula(int oferta,int estudiante)throws DaoException{
		try {
			
			Query query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULA_ESTUDIANTE_CURSO);
			query.setParameter("codigoOferta", oferta);
			query.setParameter("codigoEstudiante", estudiante);					
			return (Matricula) query.getSingleResult();
			
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	public List<Matricula> listaMatriculadosPorOfertaCurso(int codigoOferta) throws DaoException{
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULA_POR_OFERTA_CURSO);
			query.setParameter("codigoOferta", codigoOferta);
			for(Object obj: query.getResultList()) {
				Matricula matricula=(Matricula)obj;
				double total=0;
				for(Pagos pago:matricula.getPagos()) {
//					Hibernate.initialize(pago);
					for(DetallePagos dpago:pago.getDetallePagos()) {
						total=total+dpago.getDepaValor();
						Hibernate.initialize(dpago);
					}
				}
				matricula.setTotalPagadoCurso(total);
			}
			
			return query.getResultList();	
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}	
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasEstudianteActivas(int codigoEstudiante) throws DaoException{
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULAS_ALUMNO_ACTIVAS);
			query.setParameter("codigoEstudiante", codigoEstudiante);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new DaoException(e);
		}
	}
	/**
	 * 1 inscritos / matriculados 2 desertados
	 * @param estado
	 * @return
	 * @throws Exception
	 */
	public BigDecimal totalDatosMatricula(int estado) throws DaoException{
		List<Object[]> resultado= null;
		BigDecimal valor= new BigDecimal(0);
		String sql="";
		if(estado == 1)
			sql ="SELECT count(matr_id) FROM cap.matricula WHERE matr_estado IN ('INSMAT02','INSMAT05')";
		else
			sql ="SELECT count(matr_id) FROM cap.matricula WHERE matr_estado = 'INSMAT03'";
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
	 * Devuelve los matriculados por curso
	 * @param 1 inscritos/matriculados/culminados  2 desertados
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<DtoMatriculasCurso> listaMatriculasCurso(int tipo) throws DaoException{
		List<Object[]> resultado= null;
		List<DtoMatriculasCurso> listaResultado = new ArrayList<DtoMatriculasCurso>();
		String sql ="";
		if (tipo ==1) {
			sql ="SELECT count(matr_id) as cantidad, cu.curs_nombre FROM cap.matricula ma, cap.ofertacapacitacion ocap, cap.ofertacursos ofcu, cap.curso cu "+
					 " WHERE ma.ocur_id = ofcu.ocur_id AND ocap.ofca_id = ofcu.ofca_id "+
					 " AND cu.curs_id = ocap.curs_id AND ma.matr_estado IN('INSMAT01','INSMAT02','INSMAT05') GROUP BY cu.curs_nombre ORDER BY cantidad;";
		}else {
			sql ="SELECT count(matr_id) as cantidad, cu.curs_nombre FROM cap.matricula ma, cap.ofertacapacitacion ocap, cap.ofertacursos ofcu, cap.curso cu "+
					" WHERE ma.ocur_id = ofcu.ocur_id and ocap.ofca_id = ofcu.ofca_id  "+
					" AND cu.curs_id = ocap.curs_id AND ma.matr_estado in('INSMAT03') GROUP BY cu.curs_nombre ORDER BY cantidad;";					
		}
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
