package ec.mileniumtech.educafacil.backing.administracion;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.html2pdf.HtmlConverter;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanCreacionCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCapacitacion;
import ec.mileniumtech.educafacil.service.OfertaService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 *@author christian  Oct 25, 2024
 *
 */
@Named("backingCreacionCursos")
@ViewScoped
public class BackingCreacionCursos implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(BackingCreacionCursos.class);
	
	@Inject
	@Getter
	private MensajesBacking mensajesBacking;
	
	@Inject
	@Getter
	private BeanCreacionCursos beanCreacionCursos;
	
	@EJB
	private OfertaService ofertaService;
	
	@PostConstruct
	public void init() {
			getBeanCreacionCursos().setAsignarOferta(false);
			getBeanCreacionCursos().setListaCursos(ofertaService.listarTodosCursosOrdenados());
			getBeanCreacionCursos().setListaAreas(ofertaService.listarAreasOrdenadas());
			getBeanCreacionCursos().setListaEspecialidades(ofertaService.listarTodasEspecialidadesOrdenadas());
			getBeanCreacionCursos().setListaOfertaCapacitacion(ofertaService.listarOfertasCapacitacion());

			getBeanCreacionCursos().setCurso(new Curso());
			getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());
	}
	/**
	 * Permite crear un nuevo curso
	 */
	public void nuevoCurso() {
		getBeanCreacionCursos().setCurso(new Curso());
		getBeanCreacionCursos().setAsignarOferta(false);
	}
	/**
	 * Permite crear una nueva Oferta de Capacitacion
	 */
	public void nuevaOferta() {
		getBeanCreacionCursos().setAsignarOferta(true);
		getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());
		getBeanCreacionCursos().setCursoActivo(false);
		getBeanCreacionCursos().setCodigoArea(0);
		getBeanCreacionCursos().setCodigoCurso(0);
		getBeanCreacionCursos().setCodigoEspecialidad(0);
		Mensaje.verDialogo("dlgNuevoCurso");
	}
	/**
	 * Permite ocultar el panel de creación Oferta de Capacitación
	 */
	public void ocultarOferta() {
		getBeanCreacionCursos().setAsignarOferta(false);
		getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());
		getBeanCreacionCursos().setCursoActivo(false);
		getBeanCreacionCursos().setCodigoArea(0);
		getBeanCreacionCursos().setCodigoCurso(0);
		getBeanCreacionCursos().setCodigoEspecialidad(0);
		Mensaje.ocultarDialogo("dlgNuevoCurso");
	}
	/**
	 * Permite editar una Oferta de Capacitación
	 */
	public void editarOferta() {
		if(getBeanCreacionCursos().getOfertaCapacitacion()!=null) {
			getBeanCreacionCursos().setAsignarOferta(true);
			getBeanCreacionCursos().setCursoActivo(getBeanCreacionCursos().getOfertaCapacitacion().isOfcaEstado());
			getBeanCreacionCursos().setCodigoArea(getBeanCreacionCursos().getOfertaCapacitacion().getArea().getAreaId());
			getBeanCreacionCursos().setCodigoCurso(getBeanCreacionCursos().getOfertaCapacitacion().getCurso().getCursId());
			getBeanCreacionCursos().setCodigoEspecialidad(getBeanCreacionCursos().getOfertaCapacitacion().getEspecialidad().getEspeId());
			Mensaje.verDialogo("dlgNuevoCurso");
		}else {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.editarOferta"));	
		}
	}
	/**
	 * Permite mostrar el cuadro dialogo actualizar curso
	 */
	public void mostrarDialogoActualizaCurso() {
			if(getBeanCreacionCursos().getCodigoCurso()>0) {
				getBeanCreacionCursos().setCurso(getBeanCreacionCursos().getListaCursos().stream().filter(c-> c.getCursId()==getBeanCreacionCursos().getCodigoCurso()).collect(Collectors.toList()).get(0));				
				Mensaje.verDialogo("dlgGrabaCurso");
			}					
	}
	/**
	 * Muestra el cuadro de dialogo nuevo curso
	 */
	public void mostrarDialogoNuevoCurso() {		
			getBeanCreacionCursos().setCurso(new Curso());				
			Mensaje.verDialogo("dlgGrabaCurso");		
	}
	/**
	 * Muestra el dialogo Grabar Oferta
	 */
	public void mostrarDialogoGrabaOferta() {
		if(getBeanCreacionCursos().getOfertaCapacitacion().getOfcaId()==null ) 
			getBeanCreacionCursos().setOfertaCapacitacion(new OfertaCapacitacion());			
		getBeanCreacionCursos().getOfertaCapacitacion().setArea(getBeanCreacionCursos().getListaAreas().stream().filter(a->a.getAreaId()==getBeanCreacionCursos().getCodigoArea()).collect(Collectors.toList()).get(0));
		getBeanCreacionCursos().getOfertaCapacitacion().setCurso(getBeanCreacionCursos().getListaCursos().stream().filter(c->c.getCursId()==getBeanCreacionCursos().getCodigoCurso()).collect(Collectors.toList()).get(0));
		getBeanCreacionCursos().getOfertaCapacitacion().setEspecialidad(getBeanCreacionCursos().getListaEspecialidades().stream().filter(e->e.getEspeId()==getBeanCreacionCursos().getCodigoEspecialidad()).collect(Collectors.toList()).get(0));
		getBeanCreacionCursos().getOfertaCapacitacion().setOfcaEstado(getBeanCreacionCursos().isCursoActivo());
		Mensaje.verDialogo("dlgGrabaOferta");
	}
	/**
	 * Muestra el dialogo Grabar Oferta
	 */
	public void grabarOferta() {
			ofertaService.guardarNuevaOfertaCapacitacion(getBeanCreacionCursos().getOfertaCapacitacion());
			
			getBeanCreacionCursos().setListaOfertaCapacitacion(ofertaService.listarOfertasCapacitacion());
			getBeanCreacionCursos().setAsignarOferta(false);
			getBeanCreacionCursos().setCursoActivo(false);
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.agregar"));
	}
	/**
	 * Permite grabar / actualizar un curso
	 */
	public void grabarActualizarCurso() {
			if(getBeanCreacionCursos().getCurso().getCursId()==0) {
				getBeanCreacionCursos().setCodigoArea(0);
				getBeanCreacionCursos().setCodigoEspecialidad(0);
				getBeanCreacionCursos().setCursoActivo(false);
			}
			ofertaService.actualizarCurso(getBeanCreacionCursos().getCurso());
			getBeanCreacionCursos().setListaCursos(ofertaService.listarTodosCursosOrdenados());
			Mensaje.ocultarDialogo("dlgGrabaCurso");
	}

	/**
	 * Prepara el contenido del curso en el editor al abrir el diálogo.
	 */
	public void prepararContenidoCurso() {
		Curso curso = getBeanCreacionCursos().getOfertaCapacitacion().getCurso();
		if (curso != null) {
			getBeanCreacionCursos().setContenidoCurso(curso.getCursContenido());
		} else {
			getBeanCreacionCursos().setContenidoCurso("");
		}
	}

	/**
	 * Guarda el contenido HTML del curso (cursContenido) desde el editor enriquecido.
	 */
	public void guardarContenidoCurso() {
		try {
			OfertaCapacitacion oferta = getBeanCreacionCursos().getOfertaCapacitacion();
			if (oferta == null || oferta.getCurso() == null) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
					getMensajesBacking().getPropiedad("error"),
					"No se ha seleccionado un curso.");
				return;
			}
			Curso curso = oferta.getCurso();
			curso.setCursContenido(getBeanCreacionCursos().getContenidoCurso());
			ofertaService.actualizarCurso(curso);
			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO,
				getMensajesBacking().getPropiedad("info"),
				"Contenido guardado correctamente.");
		} catch (Exception e) {
			log.error("Error al guardar contenido del curso", e);
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
				getMensajesBacking().getPropiedad("error"),
				"Error al guardar el contenido.");
		}
	}

	/**
	 * Descarga el contenido del curso como PDF.
	 */
	public void descargarPdfContenido() {
		try {
			String contenidoHtml = getBeanCreacionCursos().getContenidoCurso();
			if (contenidoHtml == null || contenidoHtml.isBlank()) {
				contenidoHtml = "<p style='color: #999; font-style: italic;'>Sin contenido disponible.</p>";
			}

			// Armar HTML completo con estilos básicos
			String htmlCompleto = """
				<!DOCTYPE html>
				<html>
				<head>
					<meta charset="UTF-8" />
					<style>
						body { font-family: 'Helvetica', 'Arial', sans-serif; font-size: 11pt; color: #1e293b; line-height: 1.6; padding: 30px; }
						h1 { color: #1d4ed8; font-size: 18pt; border-bottom: 2px solid #1d4ed8; padding-bottom: 8px; }
						h2 { color: #2563eb; font-size: 14pt; }
						h3 { color: #3b82f6; font-size: 12pt; }
						p { margin: 6px 0; }
						ul, ol { margin: 6px 0; padding-left: 20px; }
						li { margin: 3px 0; }
						strong { font-weight: bold; }
						em { font-style: italic; }
						table { border-collapse: collapse; width: 100%%; margin: 10px 0; }
						th, td { border: 1px solid #cbd5e1; padding: 6px 10px; text-align: left; }
						th { background: #eff6ff; font-weight: 600; }
						img { max-width: 100%%; height: auto; }
					</style>
				</head>
				<body>
					<h1>%s</h1>
					%s
				</body>
				</html>
				""".formatted(
					escapaHtml(getBeanCreacionCursos().getOfertaCapacitacion().getCurso().getCursNombre()),
					contenidoHtml
				);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			HtmlConverter.convertToPdf(htmlCompleto, baos);
			byte[] pdfBytes = baos.toByteArray();

			// Escribir respuesta HTTP
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition",
				"attachment; filename=\"contenido_%s.pdf\"".formatted(
					getBeanCreacionCursos().getOfertaCapacitacion().getCurso().getCursNombre()
						.replaceAll("[^a-zA-Z0-9\\-]", "_")
				));
			response.setContentLength(pdfBytes.length);
			response.getOutputStream().write(pdfBytes);
			response.getOutputStream().flush();
			fc.responseComplete();

		} catch (Exception e) {
			log.error("Error al generar PDF", e);
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
				getMensajesBacking().getPropiedad("error"),
				"Error al generar el PDF.");
		}
	}

	/**
	 * Escapa caracteres HTML para evitar inyección.
	 */
	private String escapaHtml(String texto) {
		if (texto == null) return "";
		return texto
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&#39;");
	}
}

