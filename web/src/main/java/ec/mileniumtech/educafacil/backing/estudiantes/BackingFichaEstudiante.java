/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.util.ArrayList;


import org.apache.log4j.Logger;



import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanFichaEstudiante;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.impl.EstudianteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.utilitario.GeneracionPdf;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumRol;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;

/**
 * @author [ Christian Baez ] cbaez Nov 9, 2020
 *
 */
@ViewScoped
@Named
public class BackingFichaEstudiante implements Serializable {


	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingFichaEstudiante.class);
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@Inject
	@Getter
	private BeanFichaEstudiante beanFichaEstudiante;
	
	@EJB
	@Getter
	private EstudianteDaoImpl estudianteServicioImpl;
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicioImpl;
	
	@Inject
	@Getter
	private ComponenteBuscaEstudiante componenteBuscaEstudiante;
	
	@Inject
	@Getter	
	private BeanLogin beanLogin;
	@Getter
	private HttpSession sesion;
	
	private ExternalContext ec;

	
	@PostConstruct
	public void init() {
		getBeanFichaEstudiante().setListaEstudiante(new ArrayList<>());
		getComponenteBuscaEstudiante().getBeanBuscaEstudiante().setTipoBusqueda(1);
		////nuevo
		ec = FacesContext.getCurrentInstance().getExternalContext();
		sesion=(HttpSession)ec.getSession(true);
		int rol = Integer.parseInt(sesion.getAttribute("rol").toString());
		
		try {
			if (rol== EnumRol.ESTUDIANTE.getCodigo()) {
				getBeanFichaEstudiante().setEstudiante(getEstudianteServicioImpl().estudiantesPorCedula(getBeanLogin().getUsuario().getUsuaUsuario()));
				getBeanFichaEstudiante().setEstudiante(getBeanFichaEstudiante().getEstudiante());
				getBeanFichaEstudiante().setCodigoCliente(getBeanFichaEstudiante().getEstudiante().getEstuId());
				getBeanFichaEstudiante().setCodigoCargo( getBeanFichaEstudiante().getEstudiante().getEstuCargoOcupa());
				getBeanFichaEstudiante().setCodigoNivelEstudio(getBeanFichaEstudiante().getEstudiante().getEstuNivelEstudio());
				cargaMatriculas();
				Mensaje.actualizarComponente("panelEstudiante");
				
			}
		}catch(DaoException e) {
			e.printStackTrace();
		}
	}
	public void nuevaBusqueda() {
		getBeanFichaEstudiante().setApellidos("");
		getBeanFichaEstudiante().setCedula("");
		getBeanFichaEstudiante().setEstudiante(new Estudiante());
		getBeanFichaEstudiante().setCodigoCargo("");
		getBeanFichaEstudiante().setCodigoModalidadCurso("");
		getBeanFichaEstudiante().setCodigoNivelEstudio("");
		getBeanFichaEstudiante().setListaMatricula(new ArrayList<>());
	}
	/**
	 * Busca estudiante por cedula
	 */
	public void buscarPorCedula() {
		try {
			getBeanFichaEstudiante().setEstudiante(getEstudianteServicioImpl().estudiantesPorCedula(getBeanFichaEstudiante().getCedula()));
			if(getBeanFichaEstudiante().getEstudiante()==null) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noHayDatos"));
				Mensaje.actualizarComponente("growl");
			}else {				
				getBeanFichaEstudiante().setCodigoCliente(getBeanFichaEstudiante().getEstudiante().getEstuId());
				getBeanFichaEstudiante().setCodigoCargo( getBeanFichaEstudiante().getEstudiante().getEstuCargoOcupa());
				getBeanFichaEstudiante().setCodigoNivelEstudio(getBeanFichaEstudiante().getEstudiante().getEstuNivelEstudio());
				cargaMatriculas();
				Mensaje.ocultarDialogo("dlgClientes");
			}
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaCedula"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscarPorCedula " + ": ").append(e.getMessage()));

		}
	}
	/**
	 * Busca estudiante por apellidos
	 */
	public void buscarPorApellido() {
		try {
			getBeanFichaEstudiante().setListaEstudiante(getEstudianteServicioImpl().estudiantesPorApellido(getBeanFichaEstudiante().getApellidos()));
			if(getBeanFichaEstudiante().getListaEstudiante().size()>0) {
				getBeanFichaEstudiante().setMatriculaSeleccionada(null);
				Mensaje.verDialogo("dlgClientes");
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noHayDatos"));
				Mensaje.actualizarComponente("growl");
			}
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaApellidos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscarPorApellido " + ": ").append(e.getMessage()));

		}
	}
	/**
	 * Busca por cedula o apellidos
	 */
	public void buscarPorCedulaOApellido() {
		if(getBeanFichaEstudiante().getCedula().trim().equals("") && getBeanFichaEstudiante().getApellidos().trim().equals("")) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.ingresecedulapellido"));
		}else if(getBeanFichaEstudiante().getCedula().equals(""))
			buscarPorApellido();
		else
			buscarPorCedula();
	}
	/**
	 * Vacia la cedula
	 */
	public void vaciarCedula() {
		getBeanFichaEstudiante().setCedula("");
	}
	/**
	 * Vacia los apellidos
	 */
	public void vaciarApellidos() {
		getBeanFichaEstudiante().setApellidos("");
	}
	/**
	 * Selecciona un estudiante filtrado por apellido
	 */
	public void seleccionarEstudiante() {
		
		if(getBeanFichaEstudiante().getListaEstudiante()!=null) {
			getBeanFichaEstudiante().getListaEstudiante().forEach(estudiante->{
				if(getBeanFichaEstudiante().getCodigoCliente()==estudiante.getEstuId()) {
					getBeanFichaEstudiante().setEstudiante(estudiante);
					getBeanFichaEstudiante().setCodigoCargo( getBeanFichaEstudiante().getEstudiante().getEstuCargoOcupa());
					getBeanFichaEstudiante().setCodigoNivelEstudio(getBeanFichaEstudiante().getEstudiante().getEstuNivelEstudio());
					cargaMatriculas();
				}
			});
			
		}
	}
	/**
	 * Carga las matriculas del estudiante
	 */
	public void cargaMatriculas() {
		try {
			getBeanFichaEstudiante().setListaMatricula(new ArrayList<>());
			getBeanFichaEstudiante().setListaMatricula(getMatriculaServicioImpl().listaMatriculasEstudiante(getBeanFichaEstudiante().getCodigoCliente()));
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarMatriculas"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargaMatriculas " + ": ").append(e.getMessage()));

		}
	}
	/**
	 * Genera el certificado digital
	 */
	public void generarPdf() {
		GeneracionPdf.generarCertificado(getBeanFichaEstudiante().getMatriculaSeleccionada(), beanLogin, mensajesBacking);
	}
	/**
	 * Muestra el cuadro dialogo actualizar estudiante
	 */
	public void mostrarDialogoActualizar() {
		if(getBeanFichaEstudiante().getEstudiante()!=null && getBeanFichaEstudiante().getEstudiante().getEstuId()>0) {
			Mensaje.verDialogo("dlggrabar");
		}
	}
	/**
	 * Actualiza el estudiante
	 */
	public void actualizarEstudiante() {
		try {
			getBeanFichaEstudiante().getEstudiante().setEstuNivelEstudio(getBeanFichaEstudiante().getCodigoNivelEstudio());
			getBeanFichaEstudiante().getEstudiante().setEstuCargoOcupa(getBeanFichaEstudiante().getCodigoCargo());
			getEstudianteServicioImpl().actualizaEstudiante(getBeanFichaEstudiante().getEstudiante());
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.procesoexito"));
		} catch (DaoException | EntidadDuplicadaException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.actualizar"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "actualizarEstudiante " + ": ").append(e.getMessage()));
		}
	}
	public void nuevaBusquedaCliente() {
		getBeanFichaEstudiante().setListaEstudiante(new ArrayList<Estudiante>());
		getBeanFichaEstudiante().setEstudiante(null);
		getBeanFichaEstudiante().setCodigoNivelEstudio(null);
		getBeanFichaEstudiante().setCodigoCargo(null);
		getBeanFichaEstudiante().setListaMatricula(new ArrayList<Matricula>());
		vaciarApellidos();
		vaciarCedula();
		Mensaje.verDialogo("dlgClientes");
	}
	
	public void metodo() {
		System.out.println("Busqueda");
	}
}
