/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.usuarios;

import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ObjetosMenuDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.service.AuthService;
import ec.mileniumtech.educafacil.service.MatriculaDataService;
import ec.mileniumtech.educafacil.service.SistemaDataService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.correo.Correo;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CifradorBase;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import ec.mileniumtech.educafacil.utilitarios.seguridad.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Sep 24, 2024
*
*/
@Named("backingLogin")
@SessionScoped
public class BackingLogin implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(BackingLogin.class);
	@Getter
	@Setter
	private boolean usuarioValido;
	@Getter
	@Setter
	private MenuModel menumodel = new DefaultMenuModel();
	
	@EJB
	private AuthService authService;

	@EJB
	@Getter
	private MatriculaDataService matriculaDataService;
	
	@EJB
	@Getter
	private SistemaDataService sistemaDataService;
	
	@Inject
	@Getter	
	private BeanLogin beanLogin;
	
	@Getter
	@Inject
	private MensajesBacking mensajesBacking;
	
	private List<ObjetosMenuDto> listaMenuUsuario;
	@Getter
	private HttpSession sesion;
	
	private ExternalContext ec;

	public String validarUsuario() {
		String respuesta=null;
		listaMenuUsuario= new ArrayList<>();
		this.menumodel=new DefaultMenuModel();
		
		if(getBeanLogin().getUsuario()!=null) {
			Usuario usuario = authService.autenticar(
				getBeanLogin().getDocumentoIdentidad(),
				getBeanLogin().getClave()
			);
			if(usuario != null) {
				ec = FacesContext.getCurrentInstance().getExternalContext();
				sesion=(HttpSession)ec.getSession(true);
				sesion.setAttribute("logeado", true);
				listaMenuUsuario = matriculaDataService.buscarAccesosUsuario(usuario.getUsuaUsuario());
				boolean esAdmin = false;
				if(listaMenuUsuario!=null && !listaMenuUsuario.isEmpty()) {
					List<UsuarioRol> listaRoles = matriculaDataService.listaUsuarioRolPorUsuario(usuario.getUsuaId());
					if(listaRoles!=null) {
						for (UsuarioRol usuarioRol : listaRoles) {
							sesion.setAttribute("rol", usuarioRol.getRol().getRolId());
							if (usuarioRol.getRol() != null && usuarioRol.getRol().getRolNombre() != null && usuarioRol.getRol().getRolNombre().equalsIgnoreCase("Administrador")) {
								esAdmin = true;
							}
						}
					}
					String perfil=null;
					boolean flagPrimero=true;
					DefaultSubMenu submenu = new DefaultSubMenu();
					
					for (ObjetosMenuDto objetosMenuDto : listaMenuUsuario) {
						
						if(flagPrimero) {
							perfil=objetosMenuDto.getPer_id();								
				            submenu.setIcon(null);
				            submenu.setLabel(objetosMenuDto.getPer_nombre());
				            this.menumodel.getElements().add(submenu);
				            
				            DefaultMenuItem item= DefaultMenuItem.builder().value(objetosMenuDto.getAcc_nombre()).url(objetosMenuDto.getAcc_ruta()).icon(objetosMenuDto.getAcc_icono()).build();
							submenu.getElements().add(item);
				            flagPrimero=false;
						}else {
							if(perfil.equals(objetosMenuDto.getPer_id())) {
								DefaultMenuItem item= DefaultMenuItem.builder().value(objetosMenuDto.getAcc_nombre()).url(objetosMenuDto.getAcc_ruta()).icon(objetosMenuDto.getAcc_icono()).build();
								submenu.getElements().add(item);
							}else {
								submenu = new DefaultSubMenu();
								perfil=objetosMenuDto.getPer_id();								
					            submenu.setIcon(null);
					            submenu.setLabel(objetosMenuDto.getPer_nombre());
					            this.menumodel.getElements().add(submenu);

					            DefaultMenuItem item= DefaultMenuItem.builder().value(objetosMenuDto.getAcc_nombre()).url(objetosMenuDto.getAcc_ruta()).icon(objetosMenuDto.getAcc_icono()).build();

					            submenu.getElements().add(item);
							}
							
						}
					}
					getBeanLogin().setConfiguraciones(sistemaDataService.listaConfiguraciones().get(0));
				}
				this.menumodel.getElements();
				if (esAdmin) {
					getBeanLogin().setMostrarDialogoModulos(true);
					PrimeFaces.current().executeScript("PF('dialogoModulos').show();");
				} else {
					respuesta="/paginas/index.cap?faces-redirect=true";
				}
			}else {
				Mensaje.verMensaje("growl",FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.clave"));
			}
				
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.usuario"));
		}

		return respuesta;
		
	}
	/**
	 * Cierra la sesion 
	 */
	public void cerrarSesion()
	{
		ec = FacesContext.getCurrentInstance().getExternalContext();
//	    ec.invalidateSession();
//	    try {
//			ec.redirect(ec.getRequestContextPath() + "/login.cap");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	    
		try {
			if (!(sesion!=null && (boolean) sesion.getAttribute("logeado"))) {
//				ec=FacesContext.getCurrentInstance().getExternalContext();
				ec.redirect(ec.getRequestContextPath() + "/finsesion.xhtml");
			}else {
				ec.invalidateSession();
				ec.redirect(ec.getRequestContextPath() + "/login.xhtml");
			}
		}catch(IOException e) {
			log.error("Error al cerrar sesion", e);
			throw new RuntimeException(e);
		}
	}
	public void volverIniciarSesion() {
		try {
			redirect("/capacitaciones-web/login.xhtml?faces-redirect=true&redirected=true");
		}catch(Exception e) {
			log.error("Error al redireccionar", e);
			throw new RuntimeException(e);
		}
	}
	public void redirect(final String url)throws IOException{
		FacesContext.getCurrentInstance().getExternalContext().redirect(url);
	}
	@PostConstruct
	public void init() {
		listaMenuUsuario=new ArrayList<>();
		getBeanLogin().setPanelDocumento(true);
		getBeanLogin().setPanelValida(false);
	}
	/**
	 * Valida el documento de identidad
	 */
	public void validarDocumentoIdentidadUsuario() {

			getBeanLogin().setUsuario(new Usuario());
			getBeanLogin().setUsuario(matriculaDataService.consultarUsuarioPorDocumento(getBeanLogin().getDocumentoIdentidad()));
			if(getBeanLogin().getUsuario()!=null) {
				getBeanLogin().setPanelDocumento(false);
				getBeanLogin().setPanelValida(true);
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.usuario"));
			}

	}
	/**
	 * Regresa a pantalla login
	 */
	public void retornarLogin() {
		getBeanLogin().setPanelDocumento(true);
		getBeanLogin().setPanelValida(false);
		getBeanLogin().setClave("");
		getBeanLogin().setDocumentoIdentidad("");
		getBeanLogin().setUsuario(null);
	}
	/**
	 * Valida si la sesion esta activa
	 */
	public void validarSesion() {
		try {
			if (!(sesion!=null && (boolean) sesion.getAttribute("logeado"))) {
				ec=FacesContext.getCurrentInstance().getExternalContext();
				ec.redirect(ec.getRequestContextPath() + "/finsesion.xhtml");
			}
		}catch(IOException e) {
			log.error("Error al validar sesion", e);
			throw new RuntimeException(e);
		}
	}
	public void validarCambioClave() {
		try {
			Persona persona=matriculaDataService.buscarPersonaPorCedulaCorreo(getBeanLogin().getCedula(),getBeanLogin().getCorreo());
			if(persona!=null) {
				Usuario usuario = matriculaDataService.consultarUsuarioPorDocumento(persona.getPersDocumentoIdentidad());
				SecureRandom secureRandom = new SecureRandom();
				int token = 100000 + secureRandom.nextInt(900000);
				usuario.setUsuaToken(token);
				matriculaDataService.actualizaUsuario(usuario);				
				enviarCorreo(token,persona,usuario);
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.cambioClave"));	
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			    ec.invalidateSession();
			}else 
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.personaNoExiste"));
				
			
		}catch(Exception e) {
			log.error("Error en validarCambioClave", e);
		}
	}
	/**
	 * Permite enviar el correo para cambio de clave
	 * @param token
	 * @throws ConfiguracionesException 
	 */
	private void enviarCorreo(int token,Persona persona,Usuario usuario)  {
		getBeanLogin().setConfiguraciones(sistemaDataService.listaConfiguraciones().get(0));
		String asunto = "Cambio de contraseña en el sistema";
		String mensajeCorreo = "<fieldset>\r\n" + "<table> <tr ><th colspan='2'><h4>Sistema de Administración de Centros de Capacitación.</h4></th></tr>\r\n" + "<tr><td colspan='2'>Estimado(a) Sr./Sra.: </td></tr>\r\n" + "<tr><td colspan='2'>El sistema le informa que debe ingresar al siguiente enlace para cambiar su clave. Si usted no solicitó el cambio de clave por favor contáctese con el administrador del sistema.</td></tr>\r\n" + "<tr><td></td></tr><tr><td width='10%'>"
			+ "</td></tr>\r\n"+
				"<tr></tr> "+
				"<tr><td>Enlace: </td><td><a href='"+Mensaje.obtenerUrlServidor()+ "/paginas/cambioClave.cap?tk="+CifradorBase.cifrarBase64(Encriptar.encriptarSHA256(String.valueOf(token)))
				+"&prm="+CifradorBase.cifrarBase64(persona.getPersDocumentoIdentidad())+"'>DAR CLICK AQUÍ</a></td></tr>";
		
		mensajeCorreo += "\r\n" + "<tr><td></td></tr></table>\r\n";
		Correo correo=new Correo(asunto, mensajeCorreo, true, persona.getPersCorreoElectronico(), null,beanLogin.getConfiguraciones().getConfServidorSmtp(),beanLogin.getConfiguraciones().getConfUsuarioCorreo(),beanLogin.getConfiguraciones().getConfClaveCorreo(),beanLogin.getConfiguraciones().getConfEnviadoMailDesde());
		correo.start();
				
	}
	
	public JsonObject generaToken() {
		String jwt = JwtUtil.generarToken("MileniumTech", List.of());
		return Json.createObjectBuilder()
							.add("JWT", jwt).build();

	}
}
