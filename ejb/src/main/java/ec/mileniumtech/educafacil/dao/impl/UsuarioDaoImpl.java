/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.dao.impl;

import java.util.ArrayList;
import java.util.List;


import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.dao.util.JpaDaoSupport;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ObjetosMenuDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
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
public class UsuarioDaoImpl extends GenericoDaoImpl<Usuario, Long>{
	public UsuarioDaoImpl() {
		
	}
	public UsuarioDaoImpl(EntityManager em, Class<Usuario> entityClass) {
		super(em, entityClass);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Actualiza el usuario
	 * @param usuario
	 * @return
	 * @throws DaoException
	 */
	public Usuario actualizaUsuario(Usuario usuario) {
		Usuario user=null;
		try {
			user =  getEntityManager().merge(usuario);
		}catch(Exception e) {
			throw new SystemException("Error al actualizar usuario", "USUA-SINGL-ERR", e);
		}
		return user;
	}
	
	public Usuario agregarUsuario(Usuario usuario){
	try{
		if(usuario.getUsuaId()==null) 
			getEntityManager().persist(usuario);
		
		
		return usuario;
	}catch(PersistenceException e){
		JpaDaoSupport.throwIfConstraintViolationDuplicate(e);
		throw new SystemException("Error de persistencia en usuario", "USUA-PERSIST-ERR", e);
	} 	catch (Exception e) {
		throw new SystemException("Error inesperado en usuario", "USUA-UNEXPECTED-ERR", e);
	}	
}
	
	/**
	 * Consulta un usuario por usuario 
	 * @param usuario
	 * @return
	 * @throws DaoException
	 */
	public Usuario consultarUsuario(String usuario) {
		try {
			Query query = getEntityManager().createNamedQuery(Usuario.BUSCAR_USUARIO_POR_USUARIO);
			query.setParameter("usuario", usuario);
			return (Usuario) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al consultar usuario por usuario", "USUA-FIND-ERR", e);
		}
	}
	/**
	 * Busca las opciones de menu del usuario
	 * @param correo
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<ObjetosMenuDto> buscarAccesosUsuario(String correo)
			 {
		try {
			
			List <ObjetosMenuDto> listaAccesosUsuario = new ArrayList<ObjetosMenuDto>();
			String queryString;
			queryString=" SELECT  distinct   ROL.rol_id, ROL.rol_nombre, ROL.rol_estado, ROL_PERFIL.rper_estado, PERFIL.perf_id, PERFIL.perf_nombre, PERFIL.perf_estado,  "
					+ "				 ACCION.acc_estado, ACCION.acc_padre, ACCION.acc_hija, ACCION.acc_nombre, ACCION.acc_descripcion, ACCION.acc_ruta,ACCION.acc_icono, PERFIL.perf_icono"
					+ "		FROM        ACCION INNER JOIN "
					+ " 				PERFIL_ACCION ON ACCION.acc_hija = PERFIL_ACCION.acc_hija INNER JOIN "
					+ " 				PERFIL ON PERFIL_ACCION.perf_id = PERFIL.perf_id INNER JOIN "
					+ "                 ROL_PERFIL ON PERFIL.perf_id = ROL_PERFIL.perf_id INNER JOIN "
					+ "                 ROL ON ROL_PERFIL.rol_id = ROL.rol_id INNER JOIN "
					+ "                 USUARIO_ROL ON ROL.rol_id = USUARIO_ROL.rol_id INNER JOIN "
					+ "                 USUARIO ON USUARIO_ROL.usua_id = USUARIO.usua_id "
					+ "		WHERE USUARIO.usua_usuario = '"+correo+"' "
					+ "			  and USUARIO.usua_estado = TRUE"
					+ "			  and USUARIO_ROL.urol_estado =TRUE "
					+ "			  and ROL.rol_estado = TRUE"
					+ "			  and ROL_PERFIL.rper_estado = TRUE "
					+ "			  and PERFIL.perf_estado = TRUE"
					+ "			  and PERFIL_ACCION.pacc_estado = TRUE "
					+ "			  and ACCION.acc_estado = TRUE "
					+ "		ORDER BY rol_nombre, perf_nombre, acc_nombre";
			
			Query query = getEntityManager().createNativeQuery(queryString);
			List<Object[]> objetos = query.getResultList();
			
			if(!objetos.isEmpty()){
		
				for (Object[] registro: objetos) {
					ObjetosMenuDto objetoMenuDTO = new ObjetosMenuDto();
					objetoMenuDTO.setRol_id(registro[0].toString());
					objetoMenuDTO.setRol_nombre(registro[1].toString());
					objetoMenuDTO.setRol_estado((Boolean) registro[2]);
					objetoMenuDTO.setRper_estado((Boolean) registro[3]);
					objetoMenuDTO.setPer_id(registro[4].toString());
					objetoMenuDTO.setPer_nombre(registro[5].toString());
					objetoMenuDTO.setPer_estado((Boolean) registro[6]);
					objetoMenuDTO.setAcc_estado((Boolean) registro[7]);
					objetoMenuDTO.setAcc_padre(registro[8].toString());
					objetoMenuDTO.setAcc_hija(registro[9].toString());
					objetoMenuDTO.setAcc_nombre(registro[10].toString());
					objetoMenuDTO.setAcc_descripcion(registro[11].toString());
					objetoMenuDTO.setAcc_ruta(registro[12].toString());
					objetoMenuDTO.setAcc_icono(registro[13].toString());
					objetoMenuDTO.setPer_icono(registro[14].toString());
					listaAccesosUsuario.add(objetoMenuDTO);
					
				}
				return listaAccesosUsuario;
			}else{
				return null;
			}
		} catch (Exception e) {
			throw new SystemException("Error al consultar lista de acceso usuario", "USUA-LIST-ERR", e);
		}
	}
	/**
	 * Consulta un usuario por documento de identidad
	 * @param documento
	 * @return
	 * @throws DaoException
	 */
	public Usuario consultarUsuarioPorDocumento(String documento) {
		try {
			Query query = getEntityManager().createNamedQuery(Usuario.BUSCAR_USUARIO_POR_NRO_IDENTIFICACION);
			query.setParameter("nrodocumento", documento);
			return (Usuario) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(Exception e) {
			throw new SystemException("Error al buscar usuario por documento", "USUA-FIND-ERR", e);
		}
	}
	/**
	 * Devuelve los usuarios por rol
	 * @param idRol
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	public List<Usuario> consultarUsuariosPorIdRol(int idRol) {
		try {
			String queryString;
			queryString = " SELECT DISTINCT usuario.usua_id, persona.pers_nombres,persona.pers_apellidos " + 
					" FROM persona, usuario, usuario_rol " + 
					" WHERE persona.pers_id=usuario.pers_id AND usuario.usua_id=usuario_rol.usua_id " + 
					" AND usuario_rol.usua_id=usuario.usua_id AND usuario.usua_estado=true AND usuario_rol.rol_id="+idRol+   
					" AND usuario_rol.urol_estado=true ORDER BY persona.pers_apellidos ";

			Query query = getEntityManager().createNativeQuery(queryString);
			List<Object[]> objetos = query.getResultList();
			List<Usuario> listaUsuariosPorIdRol = new ArrayList<Usuario>();
			
			if(!objetos.isEmpty()){
				for (Object[] registro : objetos) {
					Usuario usuario = new Usuario();
					Persona persona = new Persona();
					usuario.setUsuaId(Integer.valueOf(registro[0].toString()));
					persona.setPersNombres(registro[1].toString());
					persona.setPersApellidos(registro[2].toString());
					usuario.setPersona(persona);
					listaUsuariosPorIdRol.add(usuario);
					
				}
				return listaUsuariosPorIdRol;
			}else
				return null;
		} catch (Exception e) {
			throw new SystemException("Error al consultar lista de usuario por rol", "USUA-LIST-ERR", e);
		}
	}
}
