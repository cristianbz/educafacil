/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanMatriculaOnline;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.impl.CatalogoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EmpresaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.UsuarioRolDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Catalogo;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Rol;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.UsuarioRol;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.correo.Correo;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumRol;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] christian 24 feb. 2022
 *
 */
@ViewScoped
@Named
public class BackingMatriculaOnline implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingMatriculaOnline.class);

	@Inject
	@Getter	
	private BeanLogin beanLogin;

	@EJB
	@Getter	
	private ConfiguracionesDaoImpl configuracionesServicioImpl;
	
	@EJB
	@Getter	
	private CatalogoDaoImpl catalogoServicioImpl;
	
	@Inject
	@Getter
	private BeanMatriculaOnline beanMatricula;
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	@Getter
	@EJB
	private EmpresaDaoImpl empresaServicioImpl; 
	@EJB
	@Getter
	private CursoDaoImpl cursoServicioImpl;
	
	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaServicios;
	
	@EJB
	@Getter
	private PersonaDaoImpl personaServicioImpl;
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicio;
	
	@EJB
	@Getter
	private UsuarioDaoImpl usuarioServicioImpl;
	
	@EJB
	@Getter
	private UsuarioRolDaoImpl usuarioRolServicioImpl;
	
	@Getter
	@Setter
	private String password;
	
	@PostConstruct
	public void init() {
		getBeanMatricula().setPersona(new Persona());
		getBeanMatricula().setEstudiante(new Estudiante());
		getBeanMatricula().setMatricula(new Matricula());
		cargarOfertaCursosActivos();
		getBeanMatricula().setMostrarDatosPersona(true);
		getBeanMatricula().setCodigoCargo(null);
		getBeanMatricula().setCodigoMedioInformacion(null);
		getBeanMatricula().setCodigoModalidadCurso(null);
		getBeanMatricula().setCursoSeleccionado(new OfertaCursos());
		getBeanMatricula().setDeshabilitaMatricula(false);
		cargaProvincias();
	}
	
	public void cargaProvincias() {
		try {
			getBeanMatricula().setListaProvincias(new ArrayList<>());
			getBeanMatricula().setListaProvincias(getCatalogoServicioImpl().catalogosPorTipo("TPROV"));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void cargaCantones() {
		try {
			getBeanMatricula().setListaCantones(new ArrayList<>());
			getBeanMatricula().setListaCantones(getCatalogoServicioImpl().catalogosPorPadre(getBeanMatricula().getProvincia()));

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Busca una persona por su cedula
	 * @return
	 */
	public void buscaPersonaPorCedula() {
		Persona persona=null;
		try {			
			persona=getPersonaServicioImpl().buscarPersonaPorCedula(getBeanMatricula().getPersona().getPersDocumentoIdentidad());
			datosMatriculaAlBuscarPersona(persona);
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaCedula"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscaPersonaPorCedula " + ": ").append(e.getMessage()));
		} 	
	}
	/**
     * Obtiene los datos de matricula de la persona
     * @param persona
     */
    public void datosMatriculaAlBuscarPersona(Persona persona) {
    	try {
	    	if(persona!=null) {
	    		persona=getPersonaServicioImpl().buscarPersonaPorId(persona.getPersId());
		    	getBeanMatricula().setPersona(persona);
		    	for (Catalogo cat : getBeanMatricula().getListaProvincias()) {
					if(cat.getCataId() == persona.getPersProvincia()) {
						getBeanMatricula().setProvincia(cat);
						cargaCantones();
						break;
					}
				}
		    	getBeanMatricula().setCodigoCiudad(persona.getPersCiudad());
		    	getBeanMatricula().setCodigoSector(persona.getPersSector());
		    	if(!getBeanMatricula().getPersona().getEstudiantes().isEmpty()) {
			    	getBeanMatricula().setEstudiante(persona.getEstudiantes().get(0));
			    	getBeanMatricula().setCodigoCargo(persona.getEstudiantes().get(0).getEstuCargoOcupa());
			    	getBeanMatricula().setCodigoNivelEstudio(persona.getEstudiantes().get(0).getEstuNivelEstudio());
			    	getBeanMatricula().setCodigoIngresosMensuales(persona.getEstudiantes().get(0).getEstuIngresosMensuales());
		    	}

	    	}
    	} catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaApellidos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "datosMatriculaAlBuscarPersona " + ": ").append(e.getMessage()));
		}	
    }
	/**
	 * Carga la oferta de cursos activos
	 */
	public void cargarOfertaCursosActivos() {
		try {
			
			getBeanMatricula().setListaOfertaCursos(new ArrayList<>());
			getBeanMatricula().setListaOfertaCursos(getOfertaServicios().listaOfertaCursosActivos());
			getBeanMatricula().setListaOfertaCursos(getBeanMatricula().getListaOfertaCursos().stream().sorted((a1,a2) -> a1.getOfertaCapacitacion().getCurso().getCursNombre().compareTo(a2.getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));
		} catch (DaoException e) { 
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarcursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarOfertaCursosActivos" + ": ").append(e.getMessage()));
		}
	}
	public void matricular() {
		try {
			Empresa empresa = new Empresa();
			Usuario usuarioE = new Usuario();
			Rol rol = new Rol();
			empresa.setEmprId(1);
			getBeanMatricula().getPersona().setPersProvincia(getBeanMatricula().getProvincia().getCataId());
			getBeanMatricula().getPersona().setPersCiudad(getBeanMatricula().getCodigoCiudad());
			getBeanMatricula().getPersona().setPersSector(getBeanMatricula().getCodigoSector());
//			getBeanMatricula().getPersona().setPersFacebook(".");
			getBeanMatricula().getEstudiante().setPersona(getBeanMatricula().getPersona());
			getBeanMatricula().getEstudiante().setEstuCargoOcupa(getBeanMatricula().getCodigoCargo());
			getBeanMatricula().getEstudiante().setEstuNivelEstudio(getBeanMatricula().getCodigoNivelEstudio());
			getBeanMatricula().getEstudiante().setEstuIngresosMensuales(getBeanMatricula().getCodigoIngresosMensuales());
			getBeanMatricula().getMatricula().setEstudiante(getBeanMatricula().getEstudiante());
			getBeanMatricula().getMatricula().setMatrFechaMatricula(new Date());
			getBeanMatricula().getMatricula().setMatrFechaRegistro(new Date());
			getBeanMatricula().getMatricula().setEmpresa(empresa);
			getBeanMatricula().getMatricula().setMatrEstado(EnumEstadosMatricula.MATRICULADO.getCodigo());
			getBeanMatricula().getMatricula().setOfertaCursos(getBeanMatricula().getCursoSeleccionado());
			getBeanMatricula().getMatricula().setMatrMedioInformacion(getBeanMatricula().getCodigoMedioInformacion());
			String cedula = getBeanMatricula().getPersona().getPersDocumentoIdentidad();
			usuarioE.setUsuaUsuario(cedula);
			password=getBeanMatricula().getPersona().getPersTelefonoMobil();
			usuarioE.setUsuaClave(Encriptar.encriptarSHA512(password));
			usuarioE.setUsuaFechaRegistro(getBeanMatricula().getMatricula().getMatrFechaRegistro());
			usuarioE.setUsuaFechaInicio(getBeanMatricula().getCursoSeleccionado().getOcurFechaInicio());
			usuarioE.setUsuaFechaCaducidad(getBeanMatricula().getCursoSeleccionado().getOcurFechaFin());
			usuarioE.setPersona(getBeanMatricula().getPersona());
			usuarioE.setUsuaEstado(true);
			rol.setRolId(EnumRol.ESTUDIANTE.getCodigo());
			getBeanMatricula().setUsuarioRol(new UsuarioRol());
			getBeanMatricula().getUsuarioRol().setRol(rol);
			getBeanMatricula().getUsuarioRol().setUsuario(usuarioE);
			getBeanMatricula().getUsuarioRol().setUrolEstado(true);
			getMatriculaServicio().agregarMatriculaInscripcion(getBeanMatricula().getPersona(), getBeanMatricula().getMatricula(), usuarioE, getBeanMatricula().getUsuarioRol());
			getBeanMatricula().setMostrarDatosPersona(false);
			enviarCorreo();
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabar"));
			e.printStackTrace();
		}catch(EntidadDuplicadaException e) {
			e.printStackTrace();
		}
	}
	
	public void redireciona() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("https://www.capacitaciontecnica.ec/sitect/blog/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	}
	public void mostrarDialogoMatricular() {
		Mensaje.verDialogo("dlgCerrar");
	}
	
	public void enviarCorreo() {
		try {
//			Configuraciones conf = new Configuraciones();
//			conf.setConfClaveCorreo("Info2108@");
//			conf.setConfServidorSmtp("mail.capacitaciontecnica.ec");
//			conf.setConfUsuarioCorreo("info@capacitaciontecnica.ec");
//			conf.setConfClaveCorreo("Info2108@");
//			conf.setConfEnviadoMailDesde("info@capacitaciontecnica.ec");
			getBeanLogin().setConfiguraciones(getConfiguracionesServicioImpl().listaConfiguraciones().get(0));
//			getBeanLogin().setConfiguraciones(conf);
			StringBuilder contenido=new StringBuilder();
			contenido.append("<h1>").append(getBeanLogin().getConfiguraciones().getConfEmpresa()).append("</h1>");
			contenido.append("<h4>Estimado cliente, usted se ha matriculado en uno de nuestros cursos</h4>");
			contenido.append("<table>");
			contenido.append("<tr><td><h4>NOMBRES:</h4></td><td>");
			contenido.append(getBeanMatricula().getPersona().getPersNombres());
			contenido.append("<tr><td><h4>APELLIDOS:</h4></td><td>");
			contenido.append(getBeanMatricula().getPersona().getPersApellidos());
			contenido.append("<tr><td><h4>CURSO:</h4></td><td>");
			contenido.append(getBeanMatricula().getMatricula().getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre());
			contenido.append("<tr><td><h4>FECHA INICIO:</h4></td><td>");
			contenido.append(getBeanMatricula().getMatricula().getOfertaCursos().getOcurFechaInicio());
			contenido.append("<tr><td><h4>FECHA FIN:</h4></td><td>");
			contenido.append(getBeanMatricula().getMatricula().getOfertaCursos().getOcurFechaFin());
			contenido.append("<tr><td><h4>HORARIO:</h4></td><td>");
			contenido.append(getBeanMatricula().getMatricula().getOfertaCursos().getOcurHorario());
			contenido.append("<tr><td><h4>INSTRUCTOR:</h4></td><td>");
			contenido.append(getBeanMatricula().getMatricula().getOfertaCursos().getInstructor().getPersona().getPersNombres()).append(" ") .append(getBeanMatricula().getMatricula().getOfertaCursos().getInstructor().getPersona().getPersApellidos()) ;
			contenido.append("</td></tr></table>");
			Correo correo=new Correo("Registro de inscripción/matrícula en uno de nuestros cursos", contenido.toString(), true, getBeanMatricula().getPersona().getPersCorreoElectronico(), null,beanLogin.getConfiguraciones().getConfServidorSmtp(),beanLogin.getConfiguraciones().getConfUsuarioCorreo(),beanLogin.getConfiguraciones().getConfClaveCorreo(),beanLogin.getConfiguraciones().getConfEnviadoMailDesde());
			correo.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void validaMatricula() {
		try {			
			if(getBeanMatricula().getEstudiante()!= null) {
				int oferta = getBeanMatricula().getCursoSeleccionado().getOcurId();
				Matricula matricula = getMatriculaServicio().existeMatricula(oferta, getBeanMatricula().getEstudiante().getEstuId() );
				if(matricula == null)
					getBeanMatricula().setDeshabilitaMatricula(false);
				else {
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.matriculaExiste"));
					getBeanMatricula().setDeshabilitaMatricula(true);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
