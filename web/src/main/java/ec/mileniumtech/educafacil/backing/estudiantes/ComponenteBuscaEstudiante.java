/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanPagos;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanBuscaEstudiante;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanFichaEstudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.service.facade.ContabilidadFacade;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Inject;
import lombok.Getter;

/**
 * @author [ Christian Baez ] christian Apr 23, 2022
 *
 */
@Dependent
public class ComponenteBuscaEstudiante implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(ComponenteBuscaEstudiante.class);

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
	private MatriculaFacade matriculaDataService;

	@EJB
	@Getter
	private ContabilidadFacade contabilidadDataService;
	
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

			getBeanBuscaEstudiante().setListaEstudiante(matriculaDataService.estudiantesPorApellido(getBeanBuscaEstudiante().getApellidos()));
			if(getBeanBuscaEstudiante().getListaEstudiante().size()>0) {
				getBeanBuscaEstudiante().setMatriculaSeleccionada(null);
//				Mensaje.verDialogo("dlgClientes");
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noHayDatos"));
				Mensaje.actualizarComponente("growl");
			}

	}
	
	public void buscarPorCedula() {

			getBeanBuscaEstudiante().setEstudiante(matriculaDataService.estudiantesPorCedula(getBeanBuscaEstudiante().getCedula()));
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

	}
	
	public void cargaMatriculas() {
	
			getBeanFichaEstudiante().setListaMatricula(new ArrayList<>());
			getBeanFichaEstudiante().setListaMatricula(matriculaDataService.listaMatriculasEstudiante(getBeanFichaEstudiante().getCodigoCliente()));
			

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
							
								getBeanPagos().setEstudiante(estudiante);
								getBeanPagos().setListaCursosMatriculados(new ArrayList<>());
								getBeanPagos().setListaCursosMatriculados(matriculaDataService.listaMatriculasEstudianteActivas(estudiante.getEstuId()));
								if(getBeanPagos().getListaCursosMatriculados().size()==1) {
									getBeanPagos().setMatricula(getBeanPagos().getListaCursosMatriculados().get(0));
									getBeanPagos().setListaDetallePagosRealizados(new ArrayList<>());
									getBeanPagos().setListaDetallePagosRealizados(contabilidadDataService.buscaPagosPorMatricula(getBeanPagos().getListaCursosMatriculados().get(0).getMatrId()));
								}else if(getBeanPagos().getListaCursosMatriculados().size()==0) {
									getBeanPagos().setEstudiante(new Estudiante());
									Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.matriculaNoExiste"));
									break;
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
