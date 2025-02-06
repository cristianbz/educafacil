/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.encuestas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.encuestas.BeanAplicarEncuesta;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.impl.DetalleEvaluaCursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EstudianteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EvaluacionCursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RespuestasDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.TipoEncuestaPreguntaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleEvaluaCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EvaluacionCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pregunta;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Respuestas;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
*@author christian  Jul 13, 2024
*
*/
@Named
@ViewScoped
public class BackingAplicarEncuesta implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingAplicarEncuesta.class);
	@EJB
	@Getter
	private EstudianteDaoImpl estudianteServicioImpl;
	
	@EJB
	@Getter
	private EvaluacionCursoDaoImpl evaluacionCursoServicioImpl;
	
	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaCursosServicioImpl;
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicioImpl;
	
	@EJB
	@Getter
	private TipoEncuestaPreguntaDaoImpl tipoEncuestaPreguntaServicioImpl;
	
	@EJB
	@Getter
	private RespuestasDaoImpl respuestasServicioImpl;
	
	@EJB
	@Getter
	private DetalleEvaluaCursoDaoImpl detalleEvaluaCursoServicioImpl;
	
	@Inject
	@Getter
	private BeanAplicarEncuesta beanAplicarEncuesta;
	
	@Inject
	@Getter
	private BeanLogin beanLogin;
	
	@Setter
	@Getter
	private boolean mostrarEncuestas;
	
	@Setter
	@Getter
	private boolean abrirBoton;
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	
	private int tamanio;
	
	@Getter
	@Setter
	private int posicion;
	
	@Getter
	@Setter
	private boolean encuDisponibles;
	
	@Getter
	@Setter
	private boolean encuNoDisponibles;
	
	@PostConstruct
	public void init() {
		try {
			getBeanAplicarEncuesta().setPosicionPregunta(0);
			Estudiante estudiante = getEstudianteServicioImpl().estudiantesPorCedula(getBeanLogin().getUsuario().getPersona().getPersDocumentoIdentidad());
			if(estudiante != null) {
				getBeanAplicarEncuesta().setCodigoEstudiante(estudiante.getEstuId());
				List<Matricula> listaMatriculas = getMatriculaServicioImpl().listaMatriculasEstudianteActivas(estudiante.getEstuId());
				getBeanAplicarEncuesta().setListaEvaluacionCurso(new ArrayList<>());

				for (Matricula matricula : listaMatriculas) {
					getBeanAplicarEncuesta().getListaEvaluacionCurso().addAll(getEvaluacionCursoServicioImpl().listaDeEvaluacionesDeCursoActivas(matricula.getOfertaCursos().getOcurId()));
					if(!matricula.getMatrEstado().equals(EnumEstadosMatricula.CULMINADO.getCodigo())) {
						encuDisponibles=true;
					}
				}
				listaMatriculas=listaMatriculas.stream().sorted((l1,l2)->l1.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre().compareTo(l2.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList());
				getBeanAplicarEncuesta().setListaEvaluacionCurso(getBeanAplicarEncuesta().getListaEvaluacionCurso().stream().sorted((e1,e2)-> e1.getOfertacursos().getOfertaCapacitacion().getCurso().getCursNombre().compareTo(e2.getOfertacursos().getOfertaCapacitacion().getCurso().getCursNombre())).collect(Collectors.toList()));		
				armarArbolCursosMatriculados(listaMatriculas);
			}else {
				Mensaje.actualizarComponente(":form:growl");
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noEstudiante"));
			}
		}catch(Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	
	public void armarArbolCursosMatriculados(List<Matricula> lista) {
	    getBeanAplicarEncuesta().setRaiz(new DefaultTreeNode("Root", null));
	    TreeNode nodoCursos;
	    TreeNode nodoEncuesta;
	    for (Matricula matricula : lista) {
	    	String infoCurso=matricula.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre().concat("-").concat(matricula.getOfertaCursos().getOcurHorario()).toString();
	    	NodoEvaluacion nodoC = new NodoEvaluacion();
	    	nodoC.setNombre(infoCurso);
	    	nodoCursos = new DefaultTreeNode(nodoC,getBeanAplicarEncuesta().getRaiz());
	    	
	    	for (EvaluacionCurso  evaluacion : getBeanAplicarEncuesta().getListaEvaluacionCurso()) {
	    		String nodoEnc= evaluacion.getTipoEncuesta().getTipeDescripcion();//.concat(evaluacion.getEvcuId().toString());
	    		NodoEvaluacion nodoE = new NodoEvaluacion();
	    		nodoE.setNombre(nodoEnc);
	    		nodoE.setCurso(infoCurso);	    		
				if(evaluacion.getOfertacursos().getOcurId() == matricula.getOfertaCursos().getOcurId()) {
					nodoE.setEvaluacionCursoId(evaluacion.getEvcuId().toString());
					nodoE.setTipoEncuestaId(evaluacion.getTipoEncuesta().getTipeId().toString());
					nodoE.setMatriculaId(matricula.getMatrId().toString());					
					nodoE.setEvaRealizadas(matricula.getMatrEvaluacionesRealizadas());
					nodoE.setOfertaCursoId(String.valueOf(matricula.getOfertaCursos().getOcurId()));
					nodoEncuesta = new DefaultTreeNode(nodoE,nodoCursos);		
				}
			}
		}
	}
	
	public void cargarEncuesta() {
		try {
			getBeanAplicarEncuesta().setListaRespuestasAGrabar(new ArrayList<>());
			getBeanAplicarEncuesta().setPosicionPregunta(0);
			getBeanAplicarEncuesta().setCodigoRespuesta(0);
			posicion=0;
			boolean encontrado = false;

			getBeanAplicarEncuesta().setListaEncuestas(new ArrayList<>());
			NodoEvaluacion nodo = (NodoEvaluacion) getBeanAplicarEncuesta().getNodoSeleccionado().getData();
			if (nodo.getEvaluacionCursoId()!=null) {
				getBeanAplicarEncuesta().setEvaluacionCursoId(Integer.parseInt(nodo.getEvaluacionCursoId()));
				getBeanAplicarEncuesta().setMatriculasId(Integer.parseInt(nodo.getMatriculaId()));
				getBeanAplicarEncuesta().setNombreCursoSeleccionado(nodo.getCurso());
				getBeanAplicarEncuesta().setNombreEncuestaSeleccionada(nodo.getNombre());
				getBeanAplicarEncuesta().setOfertaCursos(Integer.parseInt(nodo.getOfertaCursoId()));
				int codigoEncuesta = Integer.parseInt(nodo.getTipoEncuestaId());
				Matricula matricula = getMatriculaServicioImpl().existeMatricula(getBeanAplicarEncuesta().getOfertaCursos(), getBeanAplicarEncuesta().getCodigoEstudiante());
				String idEncuesta = matricula.getMatrEvaluacionesRealizadas();
				if(idEncuesta!=null) {
					String[] encuestas = idEncuesta.split("-");
					for(String encuestasRealizadas : encuestas) {
						if(encuestasRealizadas.equals(nodo.getEvaluacionCursoId())) {
							encontrado=true;
							break;
						}
					}
				}
				if(encontrado) {
					mostrarEncuestas=false;
					Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.realizadoEncu"));
				}else {				
					getBeanAplicarEncuesta().setListaEncuestas(getTipoEncuestaPreguntaServicioImpl().listaDeEncuestas(codigoEncuesta));	
					tamanio=getBeanAplicarEncuesta().getListaEncuestas().size();

					if(!getBeanAplicarEncuesta().getListaEncuestas().isEmpty()) {
						getBeanAplicarEncuesta().setNombrePregunta(getBeanAplicarEncuesta().getListaEncuestas().get(0).getPregunta().getPregDescripcion());
						cargarRespuestas(getBeanAplicarEncuesta().getListaEncuestas().get(0).getPregunta().getCategoriaRespuesta().getCatrId());
						mostrarEncuestas=true;
						if(tamanio==1) 
							abrirBoton=false;
						else
							abrirBoton=true;
					}else {
						mostrarEncuestas=false;
						Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.noEncuesta"));
					}
				}
			}else {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.encuestaNoValida"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cargarRespuestas(int codigoCategoria) {
		try {
			getBeanAplicarEncuesta().setListaRespuestas(new ArrayList<>());
			getBeanAplicarEncuesta().setListaRespuestas(getRespuestasServicioImpl().listaRespuestasPorCategoria(codigoCategoria));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void siguientePregunta() {
		posicion=getBeanAplicarEncuesta().getPosicionPregunta();
		if(posicion <= tamanio -1) {
			getBeanAplicarEncuesta().getListaRespuestasAGrabar().add(armarEvaluacionCurso());
			posicion = posicion+1;		
			getBeanAplicarEncuesta().setPosicionPregunta(posicion);
			cargarRespuestas(getBeanAplicarEncuesta().getListaEncuestas().get(posicion).getPregunta().getCategoriaRespuesta().getCatrId());
			getBeanAplicarEncuesta().setNombrePregunta(getBeanAplicarEncuesta().getListaEncuestas().get(posicion).getPregunta().getPregDescripcion());
			if(posicion == tamanio -1){
				abrirBoton=false;
			}
		}
	}
	
	public DetalleEvaluaCurso armarEvaluacionCurso() {
		Pregunta pregunta= new Pregunta();
		Respuestas respuestas= new Respuestas();
		EvaluacionCurso evaluacionCurso = new EvaluacionCurso();
		Matricula matricula = new Matricula();
		DetalleEvaluaCurso detalleEvaluaCurso = new DetalleEvaluaCurso();
		pregunta.setPregId(getBeanAplicarEncuesta().getListaEncuestas().get(posicion).getPregunta().getPregId());
		respuestas.setRespId(getBeanAplicarEncuesta().getCodigoRespuesta());
		evaluacionCurso.setEvcuId(getBeanAplicarEncuesta().getEvaluacionCursoId());
		matricula.setMatrId(getBeanAplicarEncuesta().getMatriculasId());
		detalleEvaluaCurso.setEvaluacionCurso(evaluacionCurso);
		detalleEvaluaCurso.setMatricula(matricula);
		detalleEvaluaCurso.setPregunta(pregunta);
		detalleEvaluaCurso.setRespuestas(respuestas);
		detalleEvaluaCurso.setDevcFechaRegistro(new Date());
		
		return detalleEvaluaCurso;
	}
	
	public void guardarEncuesta() {
		
		try {
			String codigoEncuestas = "";
			codigoEncuestas=String.valueOf(getBeanAplicarEncuesta().getEvaluacionCursoId()).concat("-");
			Matricula matricula = getMatriculaServicioImpl().existeMatricula(getBeanAplicarEncuesta().getOfertaCursos(), getBeanAplicarEncuesta().getCodigoEstudiante());
			if (matricula.getMatrEvaluacionesRealizadas()!=null)
				codigoEncuestas=matricula.getMatrEvaluacionesRealizadas().concat(codigoEncuestas);			
			matricula.setMatrEvaluacionesRealizadas(codigoEncuestas);
			
			getMatriculaServicioImpl().actualizaMatricula(matricula);
			getBeanAplicarEncuesta().getListaRespuestasAGrabar().add(armarEvaluacionCurso());
			for (DetalleEvaluaCurso respuestas : getBeanAplicarEncuesta().getListaRespuestasAGrabar()) 				
				getDetalleEvaluaCursoServicioImpl().guardarEncuesta(respuestas);
				
			mostrarEncuestas=false;
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//	Clase para armar el arbol	
	public class NodoEvaluacion{
		@Getter
		@Setter
		private String nombre;
		@Getter
		@Setter
		private String curso;
		@Getter
		@Setter
		private String evaluacionCursoId;
		@Getter
		@Setter
		private String tipoEncuestaId;
		@Getter
		@Setter
		private String matriculaId;
		@Getter
		@Setter
		private String evaRealizadas;
		@Getter
		@Setter
		private String ofertaCursoId;
		
	}
	
	
	
}
