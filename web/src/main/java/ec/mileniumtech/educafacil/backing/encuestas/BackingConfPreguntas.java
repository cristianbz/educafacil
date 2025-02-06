/**
 * Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
 */
package ec.mileniumtech.educafacil.backing.encuestas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



import org.apache.log4j.Logger;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.encuestas.BeanConfPreguntas;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.excepciones.EntidadDuplicadaException;
import ec.mileniumtech.educafacil.dao.impl.CategoriaRespuestaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ObjetoEvaluacionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PreguntaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RespuestasDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.TipoEncuestaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.TipoEncuestaPreguntaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CategoriaRespuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.ObjetoEvaluacion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuesta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.TipoEncuestaPregunta;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * @author [Christian Báez] Dec 22, 2023
 *
 */

@Named
@ViewScoped
public class BackingConfPreguntas  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingConfPreguntas.class);

	@Getter
	@Inject
	private BeanConfPreguntas beanConfPreguntas;

	@Inject
	@Getter
	private MensajesBacking mensajesBacking;

	@EJB
	@Getter
	private ObjetoEvaluacionDaoImpl objetoEvaluacionServicioImpl;


	@EJB
	@Getter
	private CategoriaRespuestaDaoImpl categoriaRespuestaServicioImpl;

	@EJB
	@Getter
	private RespuestasDaoImpl respuestasServicioImpl;

	@EJB
	@Getter
	private TipoEncuestaDaoImpl tipoEncuestaServicioImpl;

	@EJB
	@Getter
	private PreguntaDaoImpl preguntaServicioImpl;

	@EJB
	@Getter
	private TipoEncuestaPreguntaDaoImpl tipoEncuestaPreguntaServicioImpl;


	private String evaluacionActual;
	private boolean existe;

	@PostConstruct
	public void init () {
		try {
			getBeanConfPreguntas().setListaObjetoEvaluacion(new ArrayList<>());
			getBeanConfPreguntas().setListaTipoEncuesta(new ArrayList<>());
			getBeanConfPreguntas().setListaCategoriaRespuesta(new ArrayList<>());
			getBeanConfPreguntas().setListaObjetoEvaluacion(getObjetoEvaluacionServicioImpl().listaDeObjetosDeEvaluacion());
			getBeanConfPreguntas().setListaTipoEncuesta(getTipoEncuestaServicioImpl().listaDeTiposDeEncuestas());
			getBeanConfPreguntas().setListaCategoriaRespuesta(getCategoriaRespuestaServicioImpl().listaDeCategorias());
			getBeanConfPreguntas().setListaRespuestas(new ArrayList<>());
			getBeanConfPreguntas().setTabActivo(0);
			getBeanConfPreguntas().setListaPreguntas(new ArrayList<>());
			getBeanConfPreguntas().setListaPreguntas(getPreguntaServicioImpl().listaDePreguntas());
			getBeanConfPreguntas().setCategoriaRespuestaSeleccionada(new CategoriaRespuesta());


		}catch(Exception e) {
			e.printStackTrace();
		}

	}



	public void nuevoObjeto() {

		getBeanConfPreguntas().setObjetoEvaluacion(new ObjetoEvaluacion());
		getBeanConfPreguntas().getObjetoEvaluacion().setObjeEstado(true);
		Mensaje.verDialogo("dlgObjeto");

	}
	public void editarObjeto() {
		try {
			getBeanConfPreguntas().setObjetoEvaluacion(new ObjetoEvaluacion());
			getBeanConfPreguntas().getObjetoEvaluacion().setObjeId(getBeanConfPreguntas().getObjetoEvaluacionEditar().getObjeId());
			getBeanConfPreguntas().getObjetoEvaluacion().setObjeNombre(getBeanConfPreguntas().getObjetoEvaluacionEditar().getObjeNombre());
			getBeanConfPreguntas().getObjetoEvaluacion().setObjeEstado(getBeanConfPreguntas().getObjetoEvaluacionEditar().getObjeEstado());
			Mensaje.verDialogo("dlgObjeto");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void guardarObjeto() {
		try {
			
			existe=false;
		

			for (ObjetoEvaluacion objetoEvaluacion : getBeanConfPreguntas().getListaObjetoEvaluacion()) {
				
				if (objetoEvaluacion.getObjeNombre().toUpperCase().equals(getBeanConfPreguntas().getObjetoEvaluacion().getObjeNombre().toUpperCase())) {
					existe=true;
					break;
				}
			}
			if(getBeanConfPreguntas().getObjetoEvaluacion().getObjeId() == null  && existe) {
			
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado"));
			}else if(getBeanConfPreguntas().getObjetoEvaluacion().getObjeId() != null  && existe && !getBeanConfPreguntas().getObjetoEvaluacion().getObjeNombre().equals(beanConfPreguntas.getObjetoEvaluacionEditar().getObjeNombre()) ){
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado"));
			}else {
					getBeanConfPreguntas().getObjetoEvaluacion().setObjeNombre(getBeanConfPreguntas().getObjetoEvaluacion().getObjeNombre().toUpperCase());
					getObjetoEvaluacionServicioImpl().actualizarObjetoEvaluacion(getBeanConfPreguntas().getObjetoEvaluacion());
					getBeanConfPreguntas().setListaObjetoEvaluacion(getObjetoEvaluacionServicioImpl().listaDeObjetosDeEvaluacion());
					Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
					Mensaje.ocultarDialogo("dlgObjeto");
					getBeanConfPreguntas().setTabActivo(0);
			}
			


		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void nuevoTipoEncuesta() {
		if(getBeanConfPreguntas().getListaObjetoEvaluacion().isEmpty()) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregarObj"));
		}else {
			getBeanConfPreguntas().setTipoEncuesta(new TipoEncuesta());
			getBeanConfPreguntas().getTipoEncuesta().setTipeEstado(true);
			getBeanConfPreguntas().setListaPreguntas(new ArrayList<Pregunta>());
			Mensaje.verDialogo("dlgTipoEncuesta");
		}

	}
	public void guardarTipo() {
		try {

			existe=false;
			ObjetoEvaluacion objEv = new ObjetoEvaluacion();

			objEv.setObjeId(getBeanConfPreguntas().getObjetoEvaluacionSeleccionado());

			getBeanConfPreguntas().getTipoEncuesta().setObjetoEvaluacion(objEv);

			
			for(TipoEncuesta tipoEncuesta : getBeanConfPreguntas().getListaTipoEncuesta()) {
				evaluacionActual = tipoEncuesta.getTipeDescripcion().toUpperCase();
				if (evaluacionActual.equals(getBeanConfPreguntas().getTipoEncuesta().getTipeDescripcion().toUpperCase()) && getBeanConfPreguntas().getObjetoEvaluacionSeleccionado() == tipoEncuesta.getObjetoEvaluacion().getObjeId()) {
					existe = true;
					break;
				}
			}

		
			if(getBeanConfPreguntas().getTipoEncuesta().getTipeId() == null && existe) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado"));
			}else if(getBeanConfPreguntas().getTipoEncuesta().getTipeId() != null && existe && getBeanConfPreguntas().getTipoEncuesta().getTipeDescripcion().equals(beanConfPreguntas.getTipoEncuestaEditar().getTipeDescripcion())) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado"));
			}else {
				getBeanConfPreguntas().getTipoEncuesta().setTipeDescripcion(getBeanConfPreguntas().getTipoEncuesta().getTipeDescripcion().toUpperCase());
				getTipoEncuestaServicioImpl().actualizarTipoEncuesta(getBeanConfPreguntas().getTipoEncuesta());
				getBeanConfPreguntas().setListaTipoEncuesta(getTipoEncuestaServicioImpl().listaDeTiposDeEncuestas());
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
				Mensaje.ocultarDialogo("dlgTipoEncuesta");
				getBeanConfPreguntas().setTabActivo(1);
			}

		}catch(Exception e) {
			e.printStackTrace();
		}

	}


	public void editarTipo() {
		try {
			getBeanConfPreguntas().setObjetoEvaluacionSeleccionado(getBeanConfPreguntas().getTipoEncuestaEditar().getObjetoEvaluacion().getObjeId());
			getBeanConfPreguntas().setTipoEncuesta(new TipoEncuesta());
			getBeanConfPreguntas().getTipoEncuesta().setTipeId(getBeanConfPreguntas().getTipoEncuestaEditar().getTipeId());
			getBeanConfPreguntas().getTipoEncuesta().setTipeDescripcion(getBeanConfPreguntas().getTipoEncuestaEditar().getTipeDescripcion());
			getBeanConfPreguntas().getTipoEncuesta().setTipeEstado(getBeanConfPreguntas().getTipoEncuestaEditar().getTipeEstado());
			Mensaje.verDialogo("dlgTipoEncuesta");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void nuevaCategoria() {
		getBeanConfPreguntas().setCategoriaRespuesta(new CategoriaRespuesta());
		getBeanConfPreguntas().setListaRespuestas(new ArrayList<>());
		getBeanConfPreguntas().getCategoriaRespuesta().setCatrEstado(true);
		Mensaje.verDialogo("dlgCategoria");
	}

	public void agregarRespuestas() {
		Respuestas respuesta = new Respuestas();
		respuesta.setRespEstado(true);
		getBeanConfPreguntas().getListaRespuestas().add(respuesta);
	}

	public void grabarRespuestas() {
		try {
			existe=false;			
			if(getBeanConfPreguntas().getListaRespuestas().size()==0) 
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.informacion"));
			else {
				for(CategoriaRespuesta categoriaRespuesta : getBeanConfPreguntas().getListaCategoriaRespuesta()) {
					evaluacionActual = categoriaRespuesta.getCatrNombre().toUpperCase();
					if(evaluacionActual.equals(getBeanConfPreguntas().getCategoriaRespuesta().getCatrNombre().toUpperCase())) {
						existe=true;
						break;
					}
				}
				if(getBeanConfPreguntas().getCategoriaRespuesta().getCatrId() == null && existe) 
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado"));
				else if(getBeanConfPreguntas().getCategoriaRespuesta().getCatrId() != null && existe && !getBeanConfPreguntas().getCategoriaRespuesta().getCatrNombre().equals(beanConfPreguntas.getCategoriaRespuestaEditar().getCatrNombre())) 
					Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado"));
				else {
					getBeanConfPreguntas().getCategoriaRespuesta().setCatrNombre(getBeanConfPreguntas().getCategoriaRespuesta().getCatrNombre().toUpperCase());
					getCategoriaRespuestaServicioImpl().actualizarCategoriaRespuesta(getBeanConfPreguntas().getCategoriaRespuesta());
					for (Respuestas respuesta : getBeanConfPreguntas().getListaRespuestas()) {
						if(respuesta.getRespId()==null) 
							respuesta.setCategoriaRespuesta(getBeanConfPreguntas().getCategoriaRespuesta());						
						getRespuestasServicioImpl().agregActualizarRespuestas(respuesta);	
					}
					getBeanConfPreguntas().setListaCategoriaRespuesta(getCategoriaRespuestaServicioImpl().listaDeCategorias());
					getBeanConfPreguntas().setTabActivo(2);
					Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
					Mensaje.ocultarDialogo("dlgCategoria");
				}
			}
		}catch(Exception e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.generico"));	
			e.printStackTrace();
		}		
	}

	public void editarCategoriaRespuesta() {
		try {
			getBeanConfPreguntas().setListaRespuestas(new ArrayList<>());
			getBeanConfPreguntas().setCategoriaRespuesta(new CategoriaRespuesta());
			getBeanConfPreguntas().getCategoriaRespuesta().setCatrId(getBeanConfPreguntas().getCategoriaRespuestaEditar().getCatrId());
			getBeanConfPreguntas().getCategoriaRespuesta().setCatrNombre(getBeanConfPreguntas().getCategoriaRespuestaEditar().getCatrNombre());
			getBeanConfPreguntas().getCategoriaRespuesta().setCatrEstado(getBeanConfPreguntas().getCategoriaRespuestaEditar().getCatrEstado());
			getBeanConfPreguntas().setCategoriaRespuesta(getCategoriaRespuestaServicioImpl().buscaCategoria(getBeanConfPreguntas().getCategoriaRespuesta().getCatrId()));
			for (Respuestas res : getBeanConfPreguntas().getCategoriaRespuesta().getRespuestas()) {
				if(res.isRespEstado())
					getBeanConfPreguntas().getListaRespuestas().add(res);
			}
			Mensaje.verDialogo("dlgCategoria");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void agregarPreguntas() {
		Pregunta preguntas = new Pregunta();
		getBeanConfPreguntas().getListaPreguntas().add(preguntas);
	}

	public void nuevaPregunta() {
		try {
			getBeanConfPreguntas().setPregunta(new Pregunta());
			getBeanConfPreguntas().setListaCategoriaRespuesta(getCategoriaRespuestaServicioImpl().listaDeCategorias());
			Mensaje.verDialogo("dlgPregunta");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void grabarPregunta() {
		try {
			getBeanConfPreguntas().getPregunta().setCategoriaRespuesta(getBeanConfPreguntas().getCategoriaRespuestaSeleccionada());
			getBeanConfPreguntas().getPregunta().setPregEstado(true);
			getPreguntaServicioImpl().agregarActualizarPregunta(getBeanConfPreguntas().getPregunta());
			getBeanConfPreguntas().setListaPreguntas(getPreguntaServicioImpl().listaDePreguntas());
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
			Mensaje.ocultarDialogo("dlgPregunta");
			getBeanConfPreguntas().setTabActivo(3);
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.grabarPregunta"));
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "grabarPregunta" + ": ").append(e.getMessage()));
		} catch (EntidadDuplicadaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void editarPregunta() {
		try {
			getBeanConfPreguntas().setCategoriaRespuestaSeleccionada(getBeanConfPreguntas().getPregunta().getCategoriaRespuesta());			
			getBeanConfPreguntas().setListaRespuestas(getRespuestasServicioImpl().listaRespuestasPorCategoria(getBeanConfPreguntas().getCategoriaRespuestaSeleccionada().getCatrId()));
			Mensaje.verDialogo("dlgPregunta");
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	public void cargarRespuestasCategoria() {
		try {
			getBeanConfPreguntas().setListaRespuestas(new ArrayList<>());
			getBeanConfPreguntas().setListaRespuestas(getRespuestasServicioImpl().listaRespuestasPorCategoria(getBeanConfPreguntas().getCategoriaRespuestaSeleccionada().getCatrId()));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void nuevoTipoEncuestaPregunta() {
		try {
			getBeanConfPreguntas().setListaTipoEncuesta(new ArrayList<>());
			getBeanConfPreguntas().setListaTipoEncuesta(getTipoEncuestaServicioImpl().listaDeTiposDeEncuestas());
			getBeanConfPreguntas().setListaPreguntasTE(new ArrayList<>());
			getBeanConfPreguntas().setCategoriaRespuestaSeleccionada(new CategoriaRespuesta());
			getBeanConfPreguntas().setListaPreguntasSelec(new ArrayList<>());
			Mensaje.verDialogo("dlgPreguntaAsociadas");

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void cargaPreguntasPorCategorias() {
		try {

			getBeanConfPreguntas().setListaPreguntasTE(new ArrayList<>());
			getBeanConfPreguntas().setListaPreguntasTE(getPreguntaServicioImpl().listaPreguntasPorCategoria(getBeanConfPreguntas().getCategoriaRespuestaSeleccionada().getCatrId()));
			if(getBeanConfPreguntas().getListaPreguntasTE().size()==0)
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("error.noHayDatos"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void guardarTipoEncuestaPregunta(){
		try {
			String preguntacont="";
			List<TipoEncuestaPregunta> listatemp= new ArrayList<>();
			listatemp= getTipoEncuestaPreguntaServicioImpl().listaPorTipoDeEncuestas(getBeanConfPreguntas().getTipoEncuesta().getTipeId());
			boolean pregExistente=false;
			for (TipoEncuestaPregunta tep : listatemp) {
				for (Pregunta pregunta : getBeanConfPreguntas().getListaPreguntasSelec()) {
					if(pregunta.getPregId().equals(tep.getPregunta().getPregId())) {
						pregExistente=true;
						preguntacont=preguntacont.concat(" ").concat(pregunta.getPregDescripcion());
						break;
					}					
				}
			}
			if(pregExistente==false) {
				for (Pregunta pregunta : getBeanConfPreguntas().getListaPreguntasSelec()) {

					TipoEncuestaPregunta tpe=new TipoEncuestaPregunta();
					tpe.setPregunta(pregunta);
					tpe.setTipoEncuesta(getBeanConfPreguntas().getTipoEncuesta());
					tpe.setTeprEstado(true);
					getTipoEncuestaPreguntaServicioImpl().agregarActualizarTipoEncuestaPregunta(tpe);


				}	
				getBeanConfPreguntas().setListaTipoEncuesta(getTipoEncuestaServicioImpl().listaDeTiposDeEncuestas());
				getBeanConfPreguntas().setListaPreguntasSelec(new ArrayList<>());
				Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
				Mensaje.ocultarDialogo("dlgPreguntaAsociadas");
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.elementoDuplicado").concat(" [").concat(preguntacont).concat("]"));
			}
		}catch(Exception e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.generico"));
			e.printStackTrace();
		}
	}

	public void eliminarTipoEncuestaPregunta() {
		try {
			getBeanConfPreguntas().getTipoEncuestaPreguntaSeleccionada().setTeprEstado(false);
			getTipoEncuestaPreguntaServicioImpl().agregarActualizarTipoEncuestaPregunta(getBeanConfPreguntas().getTipoEncuestaPreguntaSeleccionada());
			getBeanConfPreguntas().setListaTipoEncuesta(getTipoEncuestaServicioImpl().listaDeTiposDeEncuestas());
			getBeanConfPreguntas().setTabActivo(1);
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.eliminar"));

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void eliminarRespuesta() {
		try {

			if(getBeanConfPreguntas().getRespuestaSeleccionada().getRespId()==null) 
				getBeanConfPreguntas().getListaRespuestas().remove(getBeanConfPreguntas().getRespuestaSeleccionada());
			else {
				getBeanConfPreguntas().getRespuestaSeleccionada().setRespEstado(false);
				getRespuestasServicioImpl().agregActualizarRespuestas(getBeanConfPreguntas().getRespuestaSeleccionada());
				getBeanConfPreguntas().setListaCategoriaRespuesta(getCategoriaRespuestaServicioImpl().listaDeCategorias());
				getBeanConfPreguntas().getListaRespuestas().remove(getBeanConfPreguntas().getRespuestaSeleccionada());
				getBeanConfPreguntas().setTabActivo(2);

			}
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.eliminar"));	


		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}


