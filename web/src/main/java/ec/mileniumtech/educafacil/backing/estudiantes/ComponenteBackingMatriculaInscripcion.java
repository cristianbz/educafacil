/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanInscripcionMatricula;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.impl.AreaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EspecialidadDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MedioInformacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCapacitacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Empresa;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.MedioInformacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.utilitario.Cadenas;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.correo.Correo;
import ec.mileniumtech.educafacil.utilitarios.dto.registrodatos.ClientesRegistradosDto;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosOfertaCurso;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumModalidadCurso;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ] cbaez
 *
 */
@Dependent
public class ComponenteBackingMatriculaInscripcion implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ComponenteBackingMatriculaInscripcion.class);
	@Getter
	@Setter
	private int indiceTab;
	@Getter
	@Setter
	private boolean nuevaEmpresa;
	@Getter
	@Setter
	private boolean esMatricula;
	@Getter
	@Setter
	private boolean esInscripcion;
	@Getter
	@Setter
	private boolean datosCursoSeleccionado;
	@Getter
	@Setter
	private boolean matriculaDesdeRegistroDatos;
	@Getter
	@Inject
	private MensajesBacking mensajesBacking;
	
	@Inject
	@Getter	
	private BeanLogin beanLogin;
		
	@EJB
	@Getter
	private AreaDaoImpl areaServicioImpl;
	
	@EJB
	@Getter
	private MedioInformacionDaoImpl medioInformacionServicioImpl;
	
	@EJB
	@Getter
	private EspecialidadDaoImpl especialidadServicioImpl;
	
	@EJB
	@Getter
	private CursoDaoImpl cursoServicioImpl;
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicioImpl;
	
	@EJB
	@Getter
	private OfertaCapacitacionDaoImpl ofertaCapacitacionServicioImpl;
	
	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaCursosServicioImpl; 
	
	@EJB
	@Getter
	private PersonaDaoImpl personaServicioImpl;
	
	@Inject
	@Getter
	private BeanInscripcionMatricula beanInscripcionMatricula;
	
	@PostConstruct
	public void init() {
		cargarArea();

		getBeanInscripcionMatricula().setCodigoArea(0);
		getBeanInscripcionMatricula().setCodigoCurso(0);
		getBeanInscripcionMatricula().setCodigoEspecialidad(0);
		setDatosCursoSeleccionado(false);
		getBeanInscripcionMatricula().setOfertaCursosSeleccionado(new OfertaCursos());
		getBeanInscripcionMatricula().setClienteRegistradoSeleccionado(new ClientesRegistradosDto());
		setNuevaEmpresa(false);
		setMatriculaDesdeRegistroDatos(false);
		getBeanInscripcionMatricula().setListaRegistrados(new ArrayList<>());
	}
	/**
	 * Carga la modalidad de estudio
	 */
	public void cargarArea() {
		try {
			getBeanInscripcionMatricula().setListaAreas(new ArrayList<>());
			getBeanInscripcionMatricula().setListaAreas(getAreaServicioImpl().listaDeAreas());
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarmodalidad"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarModalidad" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Carga las Especialidades
	 */
	public void cargaEspecialidades() {
		try {
			getBeanInscripcionMatricula().setListaEspecialidad(new ArrayList<>());
			getBeanInscripcionMatricula().setListaCurso(new ArrayList<>());
			getBeanInscripcionMatricula().setListaEspecialidad(getOfertaCapacitacionServicioImpl().listaEspecialidadPorArea(getBeanInscripcionMatricula().getCodigoArea()));
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarespecialidad"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarEspecialidades" + ": ").append(e.getMessage()));
		}		
	}
	/**
	 * Carga los cursos
	 */
	public void cargarCursos() {
		try {
			getBeanInscripcionMatricula().setListaCurso(new ArrayList<>());
			getBeanInscripcionMatricula().setListaCurso(getOfertaCapacitacionServicioImpl().listaCursosPorAreaEspecilidad(getBeanInscripcionMatricula().getCodigoArea(), getBeanInscripcionMatricula().getCodigoEspecialidad()));
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarcursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarCursos" + ": ").append(e.getMessage()));
		}
	}
	
	/**
	 * Graba una matricula o inscripcion
	 */
	public void grabar() {
//		try {
			MedioInformacion medioInformacion=new MedioInformacion();	
			List<Estudiante> listaEstudiante=new ArrayList<>();
			if (getBeanInscripcionMatricula().getOfertaCursosSeleccionado()!=null || isEsInscripcion()) {
				eliminarEspaciosBlancoNombres();

				getBeanInscripcionMatricula().getMatricula().setOfertaCursos(getBeanInscripcionMatricula().getOfertaCursosSeleccionado());
				getBeanInscripcionMatricula().getMatricula().setMatrFechaRegistro(new Date());
				Empresa empresa=new Empresa();
				empresa.setEmprId(1);
				if(getBeanInscripcionMatricula().getPersona().getPersId()>0)
					getBeanInscripcionMatricula().setEstudiante( getBeanInscripcionMatricula().getPersona().getEstudiantes().get(0));
				if(isEsInscripcion()) {					
					getBeanInscripcionMatricula().getMatricula().setMatrFechaInscripcion(new Date());
					getBeanInscripcionMatricula().getMatricula().setMatrFacturacionEmpresa(false);
					getBeanInscripcionMatricula().getMatricula().setMatrEstado(EnumEstadosMatricula.INSCRITO.getCodigo());
					getBeanInscripcionMatricula().getMatricula().setEmpresa(empresa);
					getBeanInscripcionMatricula().getPersona().setPersFechaNacimiento(null);
				}else {
					
					getBeanInscripcionMatricula().getMatricula().setMatrFechaMatricula(new Date());
					getBeanInscripcionMatricula().getMatricula().setMatrEstado(EnumEstadosMatricula.MATRICULADO.getCodigo());
					getBeanInscripcionMatricula().getEstudiante().setEstuCargoOcupa(getBeanInscripcionMatricula().getCodigoCargo());
					getBeanInscripcionMatricula().getEstudiante().setEstuNivelEstudio(getBeanInscripcionMatricula().getCodigoNivelEstudio());
					getBeanInscripcionMatricula().getMatricula().setMatrMedioInformacion(getBeanInscripcionMatricula().getCodigoMedioInformacion());					
					if(getBeanInscripcionMatricula().getMatricula().isMatrFacturacionEmpresa())
						getBeanInscripcionMatricula().getMatricula().setEmpresa(getBeanInscripcionMatricula().getEmpresaSeleccionada());
					else
						getBeanInscripcionMatricula().getMatricula().setEmpresa(empresa);
				}
				getBeanInscripcionMatricula().getMatricula().setEstudiante(getBeanInscripcionMatricula().getEstudiante());
				getBeanInscripcionMatricula().getEstudiante().setPersona(getBeanInscripcionMatricula().getPersona());
				getBeanInscripcionMatricula().getEstudiante().setEstuIngresosMensuales(getBeanInscripcionMatricula().getCodigoIngresosMensuales());
//				listaEstudiante.add(getBeanInscripcionMatricula().getEstudiante());
//				getBeanInscripcionMatricula().getPersona().setEstudiantes(listaEstudiante);

//				getMatriculaServicioImpl().grabarMatriculaInscripcion(getBeanInscripcionMatricula().getPersona(), getBeanInscripcionMatricula().getMatricula());
//				if(matriculaDesdeRegistroDatos)
//					actualizarPersonaRegistroDatos();
				if(isEsInscripcion())
					Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.inscripcionCorrecta"));
				else
					Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.matriculaCorrecta"));
				Mensaje.actualizarComponente(":form:matricula:growl");
				if(isEsInscripcion()) {
					if(getBeanLogin().getConfiguraciones().isConfEnviarmail())
						enviarCorreo(esInscripcion);
				}
				nuevaInscripcionMatricula();				
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noExisteOfertaCursos"));
				Mensaje.actualizarComponente(":form:matricula:growl");
				
			}
//		}catch(EntidadDuplicadaException e) {
//			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.entidadDuplicada"));			
//			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabar" + ": ").append(e.getMessage()));
//		}catch(MatriculaException e) {
//			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabar"));			
//			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabar" + ": ").append(e.getMessage()));			
//		}
	}
	/**
	 * Busca una persona por su cedula
	 * @return
	 */
	public void buscaPersonaPorCedula() {
		Persona persona=null;
		try {			
			persona=getPersonaServicioImpl().buscarPersonaPorCedula(getBeanInscripcionMatricula().getPersona().getPersDocumentoIdentidad());
			datosMatriculaAlBuscarPersona(persona);
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaCedula"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscaPersonaPorCedula " + ": ").append(e.getMessage()));
		} 	
	}
	/**
	 * Actualiza la informacion de la persona
	 */
	public void actualizarPersona() {
		try {
			List<Estudiante> listaEstudiante=new ArrayList<>();
			getBeanInscripcionMatricula().getEstudiante().setEstuCargoOcupa(getBeanInscripcionMatricula().getCodigoCargo());
			getBeanInscripcionMatricula().getEstudiante().setEstuNivelEstudio(getBeanInscripcionMatricula().getCodigoNivelEstudio());
			listaEstudiante.add(getBeanInscripcionMatricula().getEstudiante());
			getBeanInscripcionMatricula().getPersona().setEstudiantes(listaEstudiante);
			getBeanInscripcionMatricula().setPersona(getPersonaServicioImpl().actualizarPersona(getBeanInscripcionMatricula().getPersona()));
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabar"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabar" + ": ").append(e.getMessage()));
		} catch (EntidadDuplicadaException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.entidadDuplicada"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabar" + ": ").append(e.getMessage()));
		}
	}
	/**
	 * Carga la oferta de cursos configurados mediante grupo
	 */
	public void cargarGruposCursos() {
		try {
			if(FacesMessage.SEVERITY_ERROR.toString().trim().equals("ERROR 2")){
				OfertaCapacitacion ofertaCapacitacion=new OfertaCapacitacion();
				ofertaCapacitacion=getOfertaCapacitacionServicioImpl().buscarOfertaCapacitacion(getBeanInscripcionMatricula().getCodigoArea(), getBeanInscripcionMatricula().getCodigoEspecialidad(), getBeanInscripcionMatricula().getCodigoCurso());
				if(ofertaCapacitacion!=null) {
					getBeanInscripcionMatricula().setListaOfertaCursos(new ArrayList<>());
					getBeanInscripcionMatricula().setListaOfertaCursos(getOfertaCursosServicioImpl().listaCursosDisponibles(ofertaCapacitacion.getOfcaId()));					
					Mensaje.verDialogo("dlgOfertaCursos");
				}else {
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noExisteOfertaCapacitacion"));
					Mensaje.actualizarComponente(":form:matricula:growl");
				}
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.datosEstudiante"));
				Mensaje.actualizarComponente(":form:matricula:growl");
			}
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarOfertaCursos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarGruposCursos" + ": ").append(e.getMessage()));
		}
	}
	
    /**
     * Cierra el dialogo de seleccion de oferta de cursos
     */
    public void cerrarDialogoOfertaCursos() {
    	if(getBeanInscripcionMatricula().getOfertaCursosSeleccionado()==null) {
    		Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noExisteOfertaCursos"));
    		Mensaje.actualizarComponente(":form:matricula:growl");
    	}else {
    		Mensaje.ocultarDialogo("dlgOfertaCursos");
    		setDatosCursoSeleccionado(true);
    	}
    }
    /**
     * Organiza los datos de las matriculas / inscripciones de un alumno
     * @param listaMatriculas
     */
    public void datosMatriculas(List<Matricula> listaMatriculas) {
    	if(listaMatriculas.size()==1) {
    		Matricula matricula=listaMatriculas.get(0);
    		getBeanInscripcionMatricula().setMatricula(matricula);
    		getBeanInscripcionMatricula().setCodigoArea(matricula.getOfertaCursos().getOfertaCapacitacion().getArea().getAreaId());
    		cargaEspecialidades();
    		getBeanInscripcionMatricula().setCodigoEspecialidad(matricula.getOfertaCursos().getOfertaCapacitacion().getEspecialidad().getEspeId());
    		getBeanInscripcionMatricula().setCodigoCurso(matricula.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursId());
    		cargarCursos();

    		setDatosCursoSeleccionado(true);
    		getBeanInscripcionMatricula().setOfertaCursosSeleccionado(matricula.getOfertaCursos());
    		if(getBeanInscripcionMatricula().getOfertaCursosSeleccionado().getOcurEstado().equals(EnumEstadosOfertaCurso.PORDEFECTO.getCodigo()) && !isEsMatricula()) {
    			seteaValoresMatriculaPorDefecto();
    		}else {
    			if(getBeanInscripcionMatricula().getOfertaCursosSeleccionado().getOcurEstado().equals(EnumEstadosOfertaCurso.PORDEFECTO.getCodigo())) {    				
    				seteaValoresMatriculaPorDefecto();
        			getBeanInscripcionMatricula().setOfertaCursosSeleccionado(null);
    			}
    		}
    	}
    }
    /**
     * Setea valores por defecto en la matricula
     */
    public void seteaValoresMatriculaPorDefecto() {
    	getBeanInscripcionMatricula().setCodigoArea(0);
		getBeanInscripcionMatricula().setCodigoEspecialidad(0);
		getBeanInscripcionMatricula().setCodigoCurso(0);
		getBeanInscripcionMatricula().setListaEspecialidad(new ArrayList<>());
		getBeanInscripcionMatricula().setListaCurso(new ArrayList<>());
    }
    /**
     * Validacion de los tabs
     */
    public void validaTabs() {
    	if((isEsMatricula() || isEsInscripcion()) && indiceTab==1) {
	    		if(validaDatosPersona()) {
	    			indiceTab=1;
	    		}else {
	    			indiceTab=0;
	        		Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.datosEstudiante"));
	        		Mensaje.actualizarComponente(":form:matricula:growl");
	    		}
	    }
    }
    /**
     * Valida si existe informacion ingresada en una persona 
     * @return
     */
    public boolean validaDatosPersona() {
    	boolean validado=false;
    	if(getBeanInscripcionMatricula().getPersona()!=null ) {
    		
    		if((isEsMatricula() && getBeanInscripcionMatricula().getPersona().getPersDocumentoIdentidad()!=null &&	
    				!getBeanInscripcionMatricula().getPersona().getPersDocumentoIdentidad().isEmpty()) && 
    				(getBeanInscripcionMatricula().getPersona().getPersApellidos()!=null && !getBeanInscripcionMatricula().getPersona().getPersApellidos().isEmpty())
    				&& (getBeanInscripcionMatricula().getPersona().getPersNombres()!=null && !getBeanInscripcionMatricula().getPersona().getPersNombres().isEmpty()) 
    				&& (getBeanInscripcionMatricula().getPersona().getPersCorreoElectronico()!=null && !getBeanInscripcionMatricula().getPersona().getPersCorreoElectronico().isEmpty())    				
    				&& (getBeanInscripcionMatricula().getPersona().getPersTelefonoMobil()!=null && !getBeanInscripcionMatricula().getPersona().getPersTelefonoMobil().isEmpty())
    				&& (getBeanInscripcionMatricula().getPersona().getPersDomicilio()!=null &&!getBeanInscripcionMatricula().getPersona().getPersDomicilio().isEmpty())) {
    			validado=true;
    		}else if(isEsInscripcion() && (getBeanInscripcionMatricula().getPersona().getPersApellidos()!=null && !getBeanInscripcionMatricula().getPersona().getPersApellidos().isEmpty())
    				&& (getBeanInscripcionMatricula().getPersona().getPersNombres()!=null && !getBeanInscripcionMatricula().getPersona().getPersNombres().isEmpty()) 
    				&& (getBeanInscripcionMatricula().getPersona().getPersCorreoElectronico()!=null && !getBeanInscripcionMatricula().getPersona().getPersCorreoElectronico().isEmpty())    				
    				&& (getBeanInscripcionMatricula().getPersona().getPersTelefonoMobil()!=null && !getBeanInscripcionMatricula().getPersona().getPersTelefonoMobil().isEmpty())) {
    			validado=true;
    		}
    	}
    	return validado;
    }
    /**
     * Reinicio de datos para ingresar nueva persona , inscripcion / matricula
     */
    public void nuevaInscripcionMatricula() {
		getBeanInscripcionMatricula().setCodigoArea(0);
		getBeanInscripcionMatricula().setCodigoCurso(0);
		getBeanInscripcionMatricula().setCodigoEspecialidad(0);
		setDatosCursoSeleccionado(false);
		getBeanInscripcionMatricula().setOfertaCursosSeleccionado(new OfertaCursos());
		getBeanInscripcionMatricula().setMatricula(new Matricula());
		getBeanInscripcionMatricula().setPersona(new Persona());

		getBeanInscripcionMatricula().setEmpresaSeleccionada(new Empresa());
		getBeanInscripcionMatricula().setCodigoCargo("");
		getBeanInscripcionMatricula().setCodigoNivelEstudio("");
		getBeanInscripcionMatricula().setEmpresa(new Empresa());
		getBeanInscripcionMatricula().setListaRegistrados(new ArrayList<>());
		getBeanInscripcionMatricula().setEstudiante(new Estudiante());

		getBeanInscripcionMatricula().setCodigoModalidadCurso("");
		getBeanInscripcionMatricula().setCodigoMedioInformacion("");
		indiceTab=0;
    }
    /**
     * Genera el email para matricula o inscripcion
     * @param tipo
     */
    public void enviarCorreo(boolean tipo) {
    	StringBuilder contenido=new StringBuilder();
    	contenido.append("<h1>").append(getBeanLogin().getConfiguraciones().getConfEmpresa()).append("</h1>");
    	if(tipo)
    		contenido.append("<h4>Estimado cliente, usted se ha inscrito en uno de nuestros cursos</h4>");
    	else
    		contenido.append("<h4>Estimado cliente, usted se ha matriculado en uno de nuestros cursos</h4>");
    	contenido.append("<table>");
    	contenido.append("<tr><td><h4>NOMBRES:</h4></td><td>");
    	contenido.append(getBeanInscripcionMatricula().getPersona().getPersNombres());
    	contenido.append("<tr><td><h4>APELLIDOS:</h4></td><td>");
    	contenido.append(getBeanInscripcionMatricula().getPersona().getPersApellidos());
    	contenido.append("<tr><td><h4>CURSO:</h4></td><td>");
    	contenido.append(getBeanInscripcionMatricula().getMatricula().getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre());
    	contenido.append("<tr><td><h4>FECHA INICIO:</h4></td><td>");
    	contenido.append(getBeanInscripcionMatricula().getMatricula().getOfertaCursos().getOcurFechaInicio());
    	contenido.append("<tr><td><h4>FECHA FIN:</h4></td><td>");
    	contenido.append(getBeanInscripcionMatricula().getMatricula().getOfertaCursos().getOcurFechaFin());
    	contenido.append("<tr><td><h4>INSTRUCTOR:</h4></td><td>");
    	contenido.append(getBeanInscripcionMatricula().getMatricula().getOfertaCursos().getInstructor().getPersona().getPersNombres()).append(" ") .append(getBeanInscripcionMatricula().getMatricula().getOfertaCursos().getInstructor().getPersona().getPersApellidos()) ;
    	contenido.append("</td></tr></table>");
    	Correo correo=new Correo("Registro de inscripci�n en uno de nuestros cursos", contenido.toString(), true, getBeanInscripcionMatricula().getPersona().getPersCorreoElectronico(), null,beanLogin.getConfiguraciones().getConfServidorSmtp(),beanLogin.getConfiguraciones().getConfUsuarioCorreo(),beanLogin.getConfiguraciones().getConfClaveCorreo(),beanLogin.getConfiguraciones().getConfEnviadoMailDesde());
    	correo.start();
    }
    /**
     * Busca una persona por Apellido
     */
    public void buscaPersonaPorApellidos() {
    	try {
    		eliminarEspaciosBlancoApellidos();
    		getBeanInscripcionMatricula().setListaPersonas(getPersonaServicioImpl().buscarPersonaPorApellidos(getBeanInscripcionMatricula().getPersona().getPersApellidos().toLowerCase()));
    		if(getBeanInscripcionMatricula().getListaPersonas()!=null && !getBeanInscripcionMatricula().getListaPersonas().isEmpty()) {
    			Mensaje.verDialogo("dlgfiltroPersona");    			
    		}
    	}catch(DaoException e) {
    		Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaApellidos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscaPersonaPorApellidos " + ": ").append(e.getMessage()));
    	}
    }
    /**
     * Selecciona una persona de la lista filtrada
     */
    public void seleccionarPersona() {
    	Persona persona=new Persona();
    	persona=getBeanInscripcionMatricula().getListaPersonas().stream().filter(p -> p.getPersId()==getBeanInscripcionMatricula().getCodigoPersona()).collect(Collectors.toList()).get(0);
    	datosMatriculaAlBuscarPersona(persona);    	
    	Mensaje.ocultarDialogo("dlgfiltroPersona");
    }
    /**
     * Obtiene los datos de matricula de la persona
     * @param persona
     */
    public void datosMatriculaAlBuscarPersona(Persona persona) {
    	try {
	    	if(persona!=null) {
	    		persona=getPersonaServicioImpl().buscarPersonaPorId(persona.getPersId());
		    	getBeanInscripcionMatricula().setPersona(persona);	
		    	getBeanInscripcionMatricula().setEstudiante(persona.getEstudiantes().get(0));
		    	getBeanInscripcionMatricula().setCodigoCargo(persona.getEstudiantes().get(0).getEstuCargoOcupa());
		    	getBeanInscripcionMatricula().setCodigoNivelEstudio(persona.getEstudiantes().get(0).getEstuNivelEstudio());
				if(isEsInscripcion()) {
					getBeanInscripcionMatricula().setListaMatriculas(getMatriculaServicioImpl().listaMatriculasAlumno(persona.getPersId(), EnumEstadosMatricula.INSCRITO.getCodigo()));					
				}else if(isEsMatricula()) {
					getBeanInscripcionMatricula().setListaMatriculas(getMatriculaServicioImpl().listaMatriculasAlumno(persona.getPersId(), EnumEstadosMatricula.INSCRITO.getCodigo()));
					if(getBeanInscripcionMatricula().getListaMatriculas().size()==0) {
						getBeanInscripcionMatricula().setListaMatriculas(getMatriculaServicioImpl().listaMatriculasAlumno(persona.getPersId(), EnumEstadosMatricula.MATRICULADO.getCodigo()));
					}
				}				
				datosMatriculas(getBeanInscripcionMatricula().getListaMatriculas());
	    	}
    	}catch (DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarMatriculas"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "datosMatriculaAlBuscarPersona " + ": ").append(e.getMessage()));
		}	
    }
    /**
     * Carga mediante web service personas registradas en formulario
     */
//    public void cargarRegistroDatos() {
//
//		 try {
//			 ResourceBundle recursos =  ResourceBundle.getBundle("com.capacitaciones.utilitarios.resources.rutas");
//			 final String GET_REGISTRADOS_ENDPOINT_URL = recursos.getString("serviciocliente")+ "api/registrados";
//			 getBeanInscripcionMatricula().setListaRegistrados(new ArrayList<>());
//			 RestTemplateBuilder builder = new RestTemplateBuilder();
//			 RestTemplate restTemplate = builder.errorHandler(new RestTemplateResponseErrorHandler())
//					 .build();
//			 HttpHeaders headers = new HttpHeaders();	        
//			 headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			 headers.setBasicAuth("capa", "Mk#298");
//			 HttpEntity < String > entity = new HttpEntity < String > ("parameters", headers);	        
//			 ResponseEntity<ClientesRegistradosDto[]> result = restTemplate.exchange(GET_REGISTRADOS_ENDPOINT_URL, HttpMethod.GET, entity, ClientesRegistradosDto[].class);
//			 ClientesRegistradosDto[] clientes=result.getBody();	        
//			 getBeanInscripcionMatricula().setListaRegistrados(Arrays.asList(clientes));
//			 if(esInscripcion) 
//				 getBeanInscripcionMatricula().setListaRegistrados(getBeanInscripcionMatricula().getListaRegistrados().stream().filter(r -> r.getPer_nivel_estudio().equals("")).collect(Collectors.toList()));
//			 else
//				 getBeanInscripcionMatricula().setListaRegistrados(getBeanInscripcionMatricula().getListaRegistrados().stream().filter(r -> !r.getPer_nivel_estudio().equals("")).collect(Collectors.toList()));
//			 getBeanInscripcionMatricula().setListaRegistrados(getBeanInscripcionMatricula().getListaRegistrados().stream().sorted((c1,c2) -> c1.getPer_apellidos().compareTo(c2.getPer_apellidos())).collect(Collectors.toList()));
//			 if(getBeanInscripcionMatricula().getListaRegistrados().isEmpty())
//				 Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.nodatos"));
//		 }catch(ResourceAccessException e) {
//			 Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.registrodatosconexion"));			
//			 log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarRegistroDatos " + ": ").append(e.getMessage()));
//		 }catch(UnknownContentTypeException e) {
//			 Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.registrodatosconexion"));			
//			 log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarRegistroDatos " + ": ").append(e.getMessage()));
//		 }
//	}
    /**
     * Selecciona una persona del registro de datos
     */
    public void seleccionarPersonaRegistrada() {
    	try {
    		if(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado()!=null) {
    			setMatriculaDesdeRegistroDatos(true);
    			Persona persona= personaServicioImpl.buscarPersonaPorCedula(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_cedula());
    			if (persona!=null) {
    				getBeanInscripcionMatricula().setPersona(persona);
    				getBeanInscripcionMatricula().setCodigoModalidadCurso(EnumModalidadCurso.VIRTUAL.getCodigo());
    				if(getBeanInscripcionMatricula().getPersona().getEstudiantes().size()>1) {
	    				getBeanInscripcionMatricula().setCodigoNivelEstudio(getBeanInscripcionMatricula().getPersona().getEstudiantes().get(0).getEstuNivelEstudio());
	    				getBeanInscripcionMatricula().setCodigoCargo(getBeanInscripcionMatricula().getPersona().getEstudiantes().get(0).getEstuCargoOcupa());
	    				
    				}
    			}else {
			    	getBeanInscripcionMatricula().getPersona().setPersApellidos(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_apellidos());
			    	getBeanInscripcionMatricula().getPersona().setPersNombres(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_nombres());
			    	getBeanInscripcionMatricula().getPersona().setPersCorreoElectronico(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_correo_electronico());
			    	getBeanInscripcionMatricula().getEstudiante().setEstuDireccionTrabajo(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_direccion_trabajo());
			    	getBeanInscripcionMatricula().getPersona().setPersDocumentoIdentidad(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_cedula());
			    	if(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_cargo_ocupa().isEmpty())
			    		getBeanInscripcionMatricula().getPersona().setPersFechaNacimiento(null);
			    	else
			    		getBeanInscripcionMatricula().getPersona().setPersFechaNacimiento(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_fecha_nacimiento());
			    	getBeanInscripcionMatricula().getPersona().setPersDomicilio(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_domicilio());
			    	getBeanInscripcionMatricula().setCodigoCargo(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_cargo_ocupa());
			    	getBeanInscripcionMatricula().setCodigoNivelEstudio(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_nivel_estudio());
			    	getBeanInscripcionMatricula().getPersona().setPersTelefonoMobil(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getPer_telefono_mobil());
			    	getBeanInscripcionMatricula().setCodigoModalidadCurso(EnumModalidadCurso.VIRTUAL.getCodigo());
			    	getBeanInscripcionMatricula().setCodigoMedioInformacion(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getReg_medio_informacion()); 
	
			    	getBeanInscripcionMatricula().getMatricula().setMatrObservacion(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getReg_observacion());    	
			    	}
    		}else {
    			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.seleccioneregistro"));
    		}
    	}catch(DaoException e) {
    		Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaCedula"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "seleccionarPersonaRegistrada " + ": ").append(e.getMessage()));
    	}catch(Exception e) {
    		Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.generico"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "seleccionarPersonaRegistrada " + ": ").append(e.getMessage()));
    	}
    }
    /**
     * Actualiza la tabla registro en la base de datos registrodatos
     */
//    public void actualizarPersonaRegistroDatos() {
//    	try {
//    		ResourceBundle recursos =  ResourceBundle.getBundle("com.capacitaciones.utilitarios.resources.rutas");
//    		final String PUT_REGISTRO_ENDPOINT_URL = recursos.getString("serviciocliente")+ "api/registro";
//    		RestTemplateBuilder builder = new RestTemplateBuilder();
//    		RestTemplate restTemplate = builder.errorHandler(new RestTemplateResponseErrorHandler())			
//    				.build();
//    		HttpHeaders headers = new HttpHeaders();	        
//    		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//    		headers.setBasicAuth("capa", "Mk#298");
//
//    		RegistroDto registrodto= new RegistroDto();
//    		registrodto.setReg_id(String.valueOf(getBeanInscripcionMatricula().getClienteRegistradoSeleccionado().getReg_id()));
//    		if(esMatricula)
//    			registrodto.setReg_estado(EnumEstadosMatricula.MATRICULADO.getCodigo());
//    		else
//    			registrodto.setReg_estado(EnumEstadosMatricula.INSCRITO.getCodigo());
//    		registrodto.setReg_descargado("1");
//    		HttpEntity<RegistroDto> request=new HttpEntity<>(registrodto,headers);
//    		restTemplate.put(PUT_REGISTRO_ENDPOINT_URL+"/"+registrodto.getReg_id(), request, new Object[] {});
//    		setMatriculaDesdeRegistroDatos(false);	        
//    	}catch(ResourceAccessException e) {
//    		Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.registrodatosconexion"));			
//    		log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarRegistroDatos " + ": ").append(e.getMessage()));
//    	}catch(UnknownContentTypeException e) {
//    		Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.registrodatosconexion"));			
//    		log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargarRegistroDatos " + ": ").append(e.getMessage()));
//    	}
//    }
    /**
     * Elimina espacios en blanco en apellidos
     */
    public void eliminarEspaciosBlancoApellidos() {
    	getBeanInscripcionMatricula().getPersona().setPersApellidos(Cadenas.eliminarEspaciosEnBlanco(getBeanInscripcionMatricula().getPersona().getPersApellidos()));
    }
    /**
     * Elimina espacios en blanco en nombres
     */
    public void eliminarEspaciosBlancoNombres() {
    	getBeanInscripcionMatricula().getPersona().setPersNombres(Cadenas.eliminarEspaciosEnBlanco(getBeanInscripcionMatricula().getPersona().getPersNombres()));
    }

}
