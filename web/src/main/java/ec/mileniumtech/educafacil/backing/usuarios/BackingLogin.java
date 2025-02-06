/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.usuarios;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ObjetosMenuDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
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
	private static final Logger log = Logger.getLogger(BackingLogin.class);
	@Getter
	@Setter
	private boolean usuarioValido;
	@Getter
	@Setter
	private MenuModel menumodel = new DefaultMenuModel();
	
	@EJB
	@Getter	
	private UsuarioDaoImpl usuarioServicioImpl;
	
	@EJB
	@Getter	
	private ConfiguracionesDaoImpl configuracionesServicioImpl;
	
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
	/**
	 * Permite la validacion del usuario
	 */
	public String validarUsuario() {
		String respuesta=null;
		listaMenuUsuario= new ArrayList<>();
		this.menumodel=new DefaultMenuModel();
		try {			
			if(getBeanLogin().getUsuario()!=null) {
//				System.out.println(Encriptar.encriptarSHA512("alex2022"));
				if(getBeanLogin().getUsuario().getUsuaClave().equals(Encriptar.encriptarSHA512(getBeanLogin().getClave()))) {
					String key = "Cpa2020";
					long tiempo= System.currentTimeMillis();		
					String jwt =Jwts.builder()
									.signWith(SignatureAlgorithm.HS256, key)
									.setSubject("Capacitacion Tecnica")
									.setIssuedAt(new Date(tiempo))
									.setExpiration(new Date(tiempo+900000))
									.claim("correo", "")
									.compact();
					JsonObject json= Json.createObjectBuilder()
										.add("JWT", jwt).build();
									
					ec = FacesContext.getCurrentInstance().getExternalContext();
					sesion=(HttpSession)ec.getSession(true);
					sesion.setAttribute("logeado", true);
					listaMenuUsuario=new ArrayList<>();
					listaMenuUsuario=getUsuarioServicioImpl().buscarAccesosUsuario(getBeanLogin().getUsuario().getUsuaUsuario());
					if(listaMenuUsuario!=null && !listaMenuUsuario.isEmpty()) {
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
						getBeanLogin().setConfiguraciones(getConfiguracionesServicioImpl().listaConfiguraciones().get(0));
					}
					this.menumodel.getElements();
					respuesta="/paginas/index.xhtml";
				}else {
					Mensaje.verMensaje("growl",FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.clave"));
				}
					
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.usuario"));
			}
		} catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.usuario"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "validarUsuario" + ": ").append(e.getMessage()));
		}
		return respuesta;
		
	}
	/**
	 * Cierra la sesion 
	 */
	public void cerrarSesion()
	{
		ec = FacesContext.getCurrentInstance().getExternalContext();
	    ec.invalidateSession();
	    try {
			ec.redirect(ec.getRequestContextPath() + "/login.cap");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void volverIniciarSesion() {
		try {
			redirect("/capacitaciones-web/login.xhtml?faces-redirect=true&redirected=true");
		}catch(Exception e) {
			e.printStackTrace();
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
		try {
			getBeanLogin().setUsuario(new Usuario());
			getBeanLogin().setUsuario(getUsuarioServicioImpl().consultarUsuarioPorDocumento(getBeanLogin().getDocumentoIdentidad()));
			if(getBeanLogin().getUsuario()!=null) {
				getBeanLogin().setPanelDocumento(false);
				getBeanLogin().setPanelValida(true);
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.usuario"));
			}
		} catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.usuario"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "validarDatosUsuario" + ": ").append(e.getMessage()));
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
				ec.redirect(ec.getRequestContextPath() + "/paginas/login.cap");
			}
		}catch(IOException e) {
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "validarSesion" + ": ").append(e.getMessage()));
		}
	}
	public void pruebaClick() {
		System.out.println("Probadno evento");
	}
}
