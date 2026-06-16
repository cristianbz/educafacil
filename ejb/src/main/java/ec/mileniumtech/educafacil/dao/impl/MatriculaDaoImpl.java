/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
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
	public void agregarMatriculaInscripcion(Persona persona,Matricula matricula, Usuario usuario, UsuarioRol usuarioRol) {
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
			JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
			throw new SystemException("Error de persistencia en matricula", "MATRI-PERSIST-ERR", e);
		} 	catch (Exception e) {
			throw new SystemException("Error inesperado en matricula", "MATRI-UNEXPECTED-ERR", e);
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
	public List<Matricula> listaMatriculasAlumno(int codigoPersona,String codigoEstado){
		try {
			
			Query query=getEntityManager().createNamedQuery(Matricula.INSCRIPCION_MATRICULA_CULMINACION_ALUMNO);
			query.setParameter("codigoEstado", codigoEstado);
			query.setParameter("codigoPersona", codigoPersona);			
			return query.getResultList();
			
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  matriculasAlumno", "MATRI-LIST-ERR", e);
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
	public List<Matricula> listaMatriculasInscripcion(String estado,Date fechaInicio,Date fechaFin){
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
			throw new SystemException("Error al cargar lista  matriculasInscripcion", "MATRI-LIST-ERR", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasCurso(String estado,int codigoCurso) {
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_POR_CURSO_ESTADO);
			query.setParameter("codigoEstado", estado);
			query.setParameter("codigoCurso", codigoCurso);			
			return query.getResultList();	
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  matriculasCurso", "MATRI-LIST-ERR", e);
		}
	}
	/**
	 * Lista de alumnos en estado matriculado o encurso
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculadosOEnCursoPorOferta(int codigoOferta) {
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULADOS_O_ENCURSO_POR_OFERTA);
			query.setParameter("codigoOferta", codigoOferta);
			return query.getResultList();	
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  matriculadosEnCursoPorOferta", "MATRI-LIST-ERR", e);
		}
	}
	/**
	 * Consulta las oportunidades de ventas
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public List<Matricula> listaOportunidades(){
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_OPORTUNIDADES);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  oportunidades", "MATRI-LIST-ERR", e);
		}
	}
	/**
	 * Consulta las matriculas por estudiante
	 * @param codigoEstudiante
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasEstudiante(int codigoEstudiante) {
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULAS_ALUMNO);
			query.setParameter("codigoEstudiante", codigoEstudiante);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  matriculasEstudiante", "MATRI-LIST-ERR", e);
		}
	}
	/**
	 * Actualiza la matricula
	 * @param matricula
	 * @throws DaoException
	 */
	public void actualizaMatricula(Matricula matricula) {
		try {
			getEntityManager().merge(matricula);
		}catch(Exception e) {
			throw new SystemException("Error al grabar matricula", "MATRI-SINGLE-ERR", e);
		}
	}
	public void actualizaMatriculaUsuario(Matricula matricula, Usuario usuario){
		try {			 
			actualizaMatricula(matricula);
			getUsuarioDaoImpl().actualizaUsuario(usuario);
		}catch(Exception e) {			
			throw new SystemException("Error al actualizar matricula", "MATRI-SINGLE-ERR", e);
		}
	}
	/**
	 * Permite ver si una matricula de un estudiante ya existe
	 * @param oferta OfertaCurso
	 * @param estudiante codigo del estudiante
	 * @return
	 * @throws DaoException
	 */
	public Matricula existeMatricula(int oferta,int estudiante){
		try {
			
			Query query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULA_ESTUDIANTE_CURSO);
			query.setParameter("codigoOferta", oferta);
			query.setParameter("codigoEstudiante", estudiante);					
			return JpaDaoSupport.singleResultOrNull(query);
		}catch(Exception e) {
			throw new SystemException("Error al buscar matricula", "MATRI-SINGLE-ERR", e);
		}
	}
	public List<Matricula> listaMatriculadosPorOfertaCurso(int codigoOferta) {
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULA_POR_OFERTA_CURSO);
			query.setParameter("codigoOferta", codigoOferta);
			for (Object obj : query.getResultList()) {
				Matricula matricula = (Matricula) obj;
				double total = matricula.getPagos().stream()
						.flatMap(pago -> pago.getDetallePagos().stream())
						.peek(dpago -> Hibernate.initialize(dpago))
						.mapToDouble(DetallePagos::getDepaValor)
						.sum();
				matricula.setTotalPagadoCurso(total);
			}
			
			return query.getResultList();	
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  matriculadosPorOferta", "MATRI-LIST-ERR", e);
		}
	}	
	@SuppressWarnings("unchecked")
	public List<Matricula> listaMatriculasEstudianteActivas(int codigoEstudiante) {
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULAS_ALUMNO_ACTIVAS);
			query.setParameter("codigoEstudiante", codigoEstudiante);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  matriculasEstudianteActivas", "MATRI-LIST-ERR", e);
		}
	}
	/**
	 * 1 inscritos / matriculados 2 desertados
	 * @param estado
	 * @return
	 * @throws Exception
	 */
	public BigDecimal totalDatosMatricula(int estado){
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
	public List<DtoMatriculasCurso> listaMatriculasCurso(int tipo){
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
	/**
	 * Total de personas en la tabla matriculas
	 * @return
	 */
	public Integer totalMatriculas(int anio) {
	    Integer valor = 0;
	    
	    // 1. Corregimos el SQL agregando WHERE y parámetros dinámicos (:anio y :mes)
	    String sql = "SELECT COUNT(matr_id) FROM cap.matricula "
	               + "WHERE EXTRACT(YEAR FROM matr_fecha_matricula) = :anio ";
//	               + "AND EXTRACT(MONTH FROM matr_fecha_matricula) = :mes";
	               
	    try {
	        Query q = getEntityManager().createNativeQuery(sql);
	        // 2. Seteamos los parámetros que recibe el método
	        q.setParameter("anio", anio);
//	        q.setParameter("mes", mes);
	        
	        // 3. Como es una función de agregación (COUNT), siempre devuelve una sola fila y columna
	        Object resultado = q.getSingleResult();
	        
	        if (resultado != null) {
	            // Los conteos nativos suelen venir como Long, BigInteger o BigDecimal dependiendo del driver.
	            // Convertirlo a String y luego a BigDecimal es la forma más segura de evitar ClassCastException.
	            valor = Integer.parseInt(resultado.toString());
	        }
	    } catch (Exception e) {
	        // Opcional: Manejar o loggear la excepción si la consulta falla
	    	throw new SystemException("Error al contar  totalMatriculas", "MATRI-TOTAL-ERR", e);
	    }
	    
	    return valor;
	}
	/**
	 * Total de matriculados activos en el mes y anio
	 * @param anio
	 * @param mes
	 * @return
	 */
	public Integer totalMatriculasActivas(int anio,int mes) {
	    Integer valor = 0;	    
	    String sql = "SELECT COUNT(matr_id) FROM cap.matricula "
	               + "WHERE EXTRACT(YEAR FROM matr_fecha_matricula) = :anio "
	               + "AND EXTRACT(MONTH FROM matr_fecha_matricula) <= :mes"
	               + " AND matr_estado in('INSMAT02','INSMAT05')";	               
	    try {
	        Query q = getEntityManager().createNativeQuery(sql);
	        q.setParameter("anio", anio);
	        q.setParameter("mes", mes);	        
	        Object resultado = q.getSingleResult();	        
	        if (resultado != null) 	            
	            valor = Integer.parseInt(resultado.toString());	        
	    } catch (Exception e) { 	        
	    	throw new SystemException("Error al contar  totalMatriculas", "MATRI-TOTAL-ERR", e);
	    }
	    
	    return valor;
	}
	/**
	 * Total de matriculas abandonadas
	 * @param anio
	 * @param mes
	 * @return
	 */
	public Integer totalMatriculasDesertadas(int anio,int mes) {
	    Integer valor = 0;	    
	    String sql = "SELECT COUNT(matr_id) FROM cap.matricula "
	               + "WHERE EXTRACT(YEAR FROM matr_fecha_matricula) = :anio "
	               + "AND EXTRACT(MONTH FROM matr_fecha_matricula) <= :mes"
	               + " AND matr_estado in('INSMAT03')";	               
	    try {
	        Query q = getEntityManager().createNativeQuery(sql);
	        q.setParameter("anio", anio);
	        q.setParameter("mes", mes);	        
	        Object resultado = q.getSingleResult();	        
	        if (resultado != null) 	            
	            valor = new Integer(resultado.toString());	        
	    } catch (Exception e) { 	        
	    	throw new SystemException("Error al contar  totalMatriculas", "MATRI-TOTAL-ERR", e);
	    }
	    
	    return valor;
	}
	
	/**
	 * Obtiene las matrículas mes a mes desde enero hasta el mes límite especificado.
	 * @param anio Año de consulta (ej. 2026)
	 * @param mesLimite Mes hasta el cual se quiere consultar (ej. 5 para Mayo)
	 * @Key El número de mes (1 al 12)
	 * @Value La cantidad de matrículas en formato BigDecimal
	 */
	public Map<Integer, Integer> obtenerMatriculasHastaMes(int anio, int mesLimite) {
	    // Usamos LinkedHashMap para mantener el orden cronológico (Ene, Feb, Mar...)
	    Map<Integer, Integer> mapaResultados = new LinkedHashMap<>();
	    
	    String sql = "SELECT EXTRACT(MONTH FROM matr_fecha_matricula) AS mes, COUNT(matr_id) AS total "
	               + "FROM cap.matricula "
	               + "WHERE EXTRACT(YEAR FROM matr_fecha_matricula) = :anio "
	               + "  AND EXTRACT(MONTH FROM matr_fecha_matricula) <= :mes_limite "
	               + "GROUP BY EXTRACT(MONTH FROM matr_fecha_matricula) "
	               + "ORDER BY mes ASC";
	               
	    try {
	        Query q = getEntityManager().createNativeQuery(sql);
	        q.setParameter("anio", anio);
	        q.setParameter("mes_limite", mesLimite);
	        
	        // Al traer varias columnas y filas, el resultado es una lista de arrays (Object[])
	        List<Object[]> filas = (List<Object[]>) q.getResultList();
	        
	        for (Object[] columna : filas) {
	            if (columna[0] != null && columna[1] != null) {
	                // columna[0] contiene el número de mes (ej: 1, 2, 3...)
	                Integer mesNo = Integer.parseInt(columna[0].toString());
	                
	                // columna[1] contiene el conteo de matrículas
	                Integer totalMatriculas = Integer.parseInt(columna[1].toString());
	                
	                mapaResultados.put(mesNo, totalMatriculas);
	            }
	        }
	    } catch (Exception e) {
	    	throw new SystemException("Error al contar  obtenerMatriculasHastaMes", "MATRI-TOTALMES-ERR", e);
	    }
	    
	    return mapaResultados;
	}
	/**
	 * Devuelve las matriculas por anio
	 * @param anio
	 * @return
	 */
	public List<Matricula> listaMatriculasPorAnio(int anio) {
		try {
			Query query=null;
			query=getEntityManager().createNamedQuery(Matricula.BUSCAR_MATRICULAS_POR_ANIO);
			query.setParameter("anio", anio);
			return query.getResultList();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al cargar lista  matriculasPorAnio", "MATRI-LIST-ERR", e);
		}
	}
}
