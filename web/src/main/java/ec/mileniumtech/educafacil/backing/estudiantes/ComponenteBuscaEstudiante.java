/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.util.ArrayList;

import jakarta.enterprise.context.Dependent;

import jakarta.inject.*;

import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanPagos;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanBuscaEstudiante;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanFichaEstudiante;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.impl.EstudianteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PagosDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;

/**
 * @author [ Christian Baez ] christian Apr 23, 2022
 *
 */
@Dependent
public class ComponenteBuscaEstudiante implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ComponenteBuscaEstudiante.class);

	@Getter
	@Inject
	private BeanFichaEstudiante beanFichaEstudiante;
	
	@Getter
	@Inject
	private BeanPagos beanPagos;

	@Getter
	@Inject
	private BeanBuscaEstudiante beanBuscaEstudiante;
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@EJB
	@Getter
	private EstudianteDaoImpl estudianteServicioImpl;
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicioImpl;
	
	@EJB
	@Getter
	private PagosDaoImpl pagosServicioImpl;
	
	@Inject
	@Getter
	private BackingFichaEstudiante backingFichaEstudiante;
	
	public void vaciarApellidos() {
		getBeanBuscaEstudiante().setApellidos("");
	}
	
	public void vaciarCedula() {
		getBeanBuscaEstudiante().setCedula("");
	}
	
	public void buscarPorCedulaOApellido() {
		if(getBeanBuscaEstudiante().getCedula().trim().equals("") && getBeanBuscaEstudiante().getApellidos().trim().equals("")) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.ingresecedulapellido"));
		}else if(getBeanBuscaEstudiante().getCedula().equals(""))
			buscarPorApellido();
		else
			buscarPorCedula();
	}
	
	public void buscarPorApellido() {
		try {
			getBeanBuscaEstudiante().setListaEstudiante(getEstudianteServicioImpl().estudiantesPorApellido(getBeanBuscaEstudiante().getApellidos()));
			if(getBeanBuscaEstudiante().getListaEstudiante().size()>0) {
				getBeanBuscaEstudiante().setMatriculaSeleccionada(null);
//				Mensaje.verDialogo("dlgClientes");
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noHayDatos"));
				Mensaje.actualizarComponente("growl");
			}
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaApellidos"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscarPorApellido " + ": ").append(e.getMessage()));
		}
	}
	
	public void buscarPorCedula() {
		try {
			getBeanBuscaEstudiante().setEstudiante(getEstudianteServicioImpl().estudiantesPorCedula(getBeanBuscaEstudiante().getCedula()));
			if(getBeanBuscaEstudiante().getEstudiante()==null) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noHayDatos"));
				Mensaje.actualizarComponente("growl");
			}else {		
				if(getBeanBuscaEstudiante().getTipoBusqueda() == 1) {
					getBeanFichaEstudiante().setEstudiante(getBeanBuscaEstudiante().getEstudiante());
					getBeanFichaEstudiante().setCodigoCliente(getBeanFichaEstudiante().getEstudiante().getEstuId());
					getBeanFichaEstudiante().setCodigoCargo( getBeanFichaEstudiante().getEstudiante().getEstuCargoOcupa());
					getBeanFichaEstudiante().setCodigoNivelEstudio(getBeanFichaEstudiante().getEstudiante().getEstuNivelEstudio());
					cargaMatriculas();
					getBeanBuscaEstudiante().getListaEstudiante().add(getBeanBuscaEstudiante().getEstudiante());
//					Mensaje.ocultarDialogo("dlgClientes");
				}else if(getBeanBuscaEstudiante().getTipoBusqueda() == 2) {
					getBeanPagos().setEstudiante(getBeanBuscaEstudiante().getEstudiante());
				}
			}
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.buscaCedula"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscarPorCedula " + ": ").append(e.getMessage()));

		}
	}
	
	public void cargaMatriculas() {
		try {
			getBeanFichaEstudiante().setListaMatricula(new ArrayList<>());
			getBeanFichaEstudiante().setListaMatricula(getMatriculaServicioImpl().listaMatriculasEstudiante(getBeanFichaEstudiante().getCodigoCliente()));
			
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarMatriculas"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "cargaMatriculas " + ": ").append(e.getMessage()));

		}
	}
	public void seleccionarEstudiante() {
		
		if (getBeanBuscaEstudiante().getCodigoCliente() == 0) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.selecionEstudiante"));
			Mensaje.verDialogo("dlgClientes");	
		}else {
			if(getBeanBuscaEstudiante().getListaEstudiante()!=null && !getBeanBuscaEstudiante().getListaEstudiante().isEmpty()) {
//				getBeanBuscaEstudiante().getListaEstudiante().forEach(estudiante->{
				for(Estudiante estudiante: getBeanBuscaEstudiante().getListaEstudiante()) {
					if(getBeanBuscaEstudiante().getCodigoCliente()==estudiante.getEstuId()) {
						if(getBeanBuscaEstudiante().getTipoBusqueda() == 1) {
							getBeanFichaEstudiante().setEstudiante(estudiante);
							getBeanFichaEstudiante().setCodigoCliente(estudiante.getEstuId());
							getBeanFichaEstudiante().setCodigoCargo( getBeanFichaEstudiante().getEstudiante().getEstuCargoOcupa());
							getBeanFichaEstudiante().setCodigoNivelEstudio(getBeanFichaEstudiante().getEstudiante().getEstuNivelEstudio());
							cargaMatriculas();
						}else if(getBeanBuscaEstudiante().getTipoBusqueda() == 2) {
							try {
								getBeanPagos().setEstudiante(estudiante);
								getBeanPagos().setListaCursosMatriculados(new ArrayList<>());
								getBeanPagos().setListaCursosMatriculados(getMatriculaServicioImpl().listaMatriculasEstudianteActivas(estudiante.getEstuId()));
								if(getBeanPagos().getListaCursosMatriculados().size()==1) {
									getBeanPagos().setMatricula(getBeanPagos().getListaCursosMatriculados().get(0));
									getBeanPagos().setListaDetallePagosRealizados(new ArrayList<>());
									getBeanPagos().setListaDetallePagosRealizados(getPagosServicioImpl().buscaPagosPorMatricula(getBeanPagos().getListaCursosMatriculados().get(0).getMatrId()));
								}else if(getBeanPagos().getListaCursosMatriculados().size()==0) {
									getBeanPagos().setEstudiante(new Estudiante());
									Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.matriculaNoExiste"));
									break;
								}
								
							}catch(DaoException e) {
								Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarMatriculas"));			
								log.error(new StringBuilder().append(this.getClass().getName() + "." + "seleccionarEstudiante " + ": ").append(e.getMessage()));
							}
						}
					}
//				});
				}
				Mensaje.ocultarDialogo("dlgClientes");				
			}
		}		
	}
	
	public void cancelaBusqueda() {
		getBeanBuscaEstudiante().setListaEstudiante(new ArrayList<>());
		getBeanBuscaEstudiante().setCodigoCliente(0);
		getBeanBuscaEstudiante().setCedula("");
		getBeanBuscaEstudiante().setApellidos("");
		Mensaje.ocultarDialogo("dlgClientes");
	}
	public void mostrarDialogo() {
		getBeanBuscaEstudiante().setListaEstudiante(new ArrayList<>());
		getBeanBuscaEstudiante().setCodigoCliente(0);
		getBeanBuscaEstudiante().setCedula("");
		getBeanBuscaEstudiante().setApellidos("");
		if(getBeanBuscaEstudiante().getTipoBusqueda() == 2) {
			getBeanPagos().setEstudiante(new Estudiante());
			getBeanPagos().setListaDetallePagosRealizados(new ArrayList<>());
			getBeanPagos().setListaDetallePagos(new ArrayList<>());
			getBeanPagos().setListaCursosMatriculados(new ArrayList<>());
			getBeanPagos().setMatricula(new Matricula());
		}
		Mensaje.verDialogo("dlgClientes");
	}
}
