/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.backing.estudiantes;


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.stream.Collectors;


import javax.imageio.ImageIO;

import org.apache.log4j.Logger;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.estudiantes.BeanListadoEstudiantes;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.dao.excepciones.DaoException;
import ec.mileniumtech.educafacil.dao.impl.CursoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.MatriculaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.OfertaCursosDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.OfertaCursos;
import ec.mileniumtech.educafacil.utilitario.GeneracionPdf;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitario.Utilitario;
import ec.mileniumtech.educafacil.utilitarios.dto.pdf.InscripcionMatriculaDto;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import ec.mileniumtech.educafacil.utilitarios.fechas.FechaFormato;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * @author [ Christian Baez ] cbaez Jan 29, 2020
 *
 */
@ViewScoped
@Named
public class BackingListadoEstudiantes implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BackingListadoEstudiantes.class);
	
	@EJB
	@Getter
	private MatriculaDaoImpl matriculaServicioImpl;
	
	@Inject
	@Getter
	private BeanListadoEstudiantes beanListadoEstudiantes;
	@Getter
	@Inject
	private MensajesBacking mensajesBacking;

	@Inject
	@Getter	
	private BeanLogin beanLogin;
	
	@EJB
	@Getter
	private CursoDaoImpl cursosServicio;
	
	@EJB
	@Getter
	private OfertaCursosDaoImpl ofertaCursosServicio;

	
	@PostConstruct
	public void iniciar() {
		nuevaBusqueda();
		try {
		getBeanListadoEstudiantes().setListaCursos(new ArrayList<>());
		getBeanListadoEstudiantes().setListaCursos(getCursosServicio().listaCursos());
		getBeanListadoEstudiantes().setOfertaSeleccionada(new OfertaCursos());
		getBeanListadoEstudiantes().getOfertaSeleccionada().setOcurFechaFin(new Date());
		getBeanListadoEstudiantes().getOfertaSeleccionada().setOcurFechaInicio(new Date());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Vacia los datos de la tabla
	 */
	public void vaciarTablaDatos() {
		getBeanListadoEstudiantes().setListaMatriculas(new ArrayList<>());
	}
	/**
	 * Permite buscar las matriculas/inscripciones/culimaciones existentes
	 */
	public void buscarMatriculas() {
		try {
			getBeanListadoEstudiantes().setListaMatriculas(new ArrayList<>());
//			getBeanListadoEstudiantes().setListaMatriculas(getMatriculaServicioImpl().listaMatriculasCurso(getBeanListadoEstudiantes().getCodigoEstadoMatricula(), getBeanListadoEstudiantes().getCursoSeleccionado().getCursId()));			
			getBeanListadoEstudiantes().setListaMatriculas(getMatriculaServicioImpl().listaMatriculadosPorOfertaCurso(getBeanListadoEstudiantes().getOfertaSeleccionada().getOcurId()));
			getBeanListadoEstudiantes().setListaMatriculas(getBeanListadoEstudiantes().getListaMatriculas().stream().sorted((m1,m2)-> m1.getEstudiante().getPersona().getPersApellidos().compareTo(m2.getEstudiante().getPersona().getPersApellidos())).collect(Collectors.toList()));
			if(getBeanListadoEstudiantes().getListaMatriculas()==null || getBeanListadoEstudiantes().getListaMatriculas().isEmpty()) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.noHayDatos"));
				Mensaje.actualizarComponente("growl");
			}
			Mensaje.ocultarDialogo("dlgBuscar");	
		}catch(DaoException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.cargarMatriculas"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "buscarMatriculas" + ": ").append(e.getMessage()));			
		}
	}
	/**
	 * Realiza una nueva busqueda
	 */
	public void nuevaBusqueda() {

		getBeanListadoEstudiantes().setListaMatriculas(new ArrayList<>());
		getBeanListadoEstudiantes().setCodigoEstadoMatricula(null);
		getBeanListadoEstudiantes().setCodigoCurso(null);
		getBeanListadoEstudiantes().setCursoSeleccionado(new Curso());
		getBeanListadoEstudiantes().setListaOfertaCursos(new ArrayList<>());
	}
	/**
	 * Permite generar el documento pdf
	 */
	public void generarPdf() {
	
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String directorioArchivoPDF = new StringBuilder().append(ctx.getRealPath("")).append(File.separator).append("reportes").append(File.separator).append(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrId()).append(".pdf").toString();		
		String logoQR = ctx.getRealPath("/") + File.separator + "imagenes" + File.separator + "logotipo" + File.separator + "logoQR.png";
		String codigoQR = ctx.getRealPath("/") + File.separator + "imagenes" + File.separator + "logotipo" + File.separator + "codigoQR.png";
		String logotipoURL = ctx.getRealPath("/")  + File.separator + "imagenes" + File.separator + "logotipo" + File.separator +"logoCEIMSCAP.png";
		try {
			InscripcionMatriculaDto inscripcionMatriculaDto=new InscripcionMatriculaDto();

			inscripcionMatriculaDto.setNombres(getBeanListadoEstudiantes().getMatriculaSeleccionada().getEstudiante().getPersona().getPersNombres());
			inscripcionMatriculaDto.setApellidos(getBeanListadoEstudiantes().getMatriculaSeleccionada().getEstudiante().getPersona().getPersApellidos());
			inscripcionMatriculaDto.setCurso(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre());
			inscripcionMatriculaDto.setFechaInicioCurso(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getOcurFechaInicio());
			inscripcionMatriculaDto.setFechaFinCurso(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getOcurFechaFin());
			System.out.println(getBeanListadoEstudiantes().getCodigoEstadoMatricula());
			System.out.println(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrEstado());
			System.out.println(EnumEstadosMatricula.INSCRITO.getCodigo());
			if(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrEstado().equals(EnumEstadosMatricula.INSCRITO.getCodigo()))
				inscripcionMatriculaDto.setFechaMatriculaInscripcion(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrFechaInscripcion());
			else
				inscripcionMatriculaDto.setFechaMatriculaInscripcion(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrFechaMatricula());

			Document document = new Document(null);

			document.setPageSize(PageSize.A4);
			document.setMargins(35, 35, 35, 35);
			document.setMarginMirroring(true);

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(directorioArchivoPDF));
			Rectangle rect = new Rectangle(100, 30, 500, 800);
			writer.setBoxSize("art", rect);


			document.open();

			Font fontTitulos = FontFactory.getFont(FontFactory.HELVETICA_BOLD.toString(), 11);
			Font fontCabecera = new Font(FontFamily.HELVETICA, 15, Font.BOLD, BaseColor.BLACK);
			Font fontContenidoTablas = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

			PdfPTable tablaCabecera = new PdfPTable(1);
			Image logotipo = Image.getInstance(logotipoURL);
			logotipo.scalePercent(15);
			logotipo.setAlignment(Element.ALIGN_MIDDLE);
			PdfPCell cell = new PdfPCell(logotipo);
			cell.setColspan(1);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(PdfPCell.NO_BORDER);
			tablaCabecera.addCell(cell);				
			tablaCabecera.setWidthPercentage(150f);
			document.add(tablaCabecera);
			Paragraph parrafoHoja = new Paragraph();
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			
			parrafoHoja.add(new Phrase(getBeanLogin().getConfiguraciones().getConfEmpresa(), fontCabecera));
			parrafoHoja.setAlignment(Element.ALIGN_CENTER);
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			parrafoHoja.add(new Phrase("REGISTRO DE INSCRIPCION / MATRICULA", fontTitulos));
			parrafoHoja.setAlignment(Element.ALIGN_CENTER);
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			parrafoHoja.add(new Phrase(Chunk.NEWLINE));
			document.add(parrafoHoja);

			Paragraph parrafoInscripcion = new Paragraph();
			if(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrEstado().equals(EnumEstadosMatricula.INSCRITO.getCodigo()))
				parrafoInscripcion.add(new Phrase("Datos de la inscripcion:", fontTitulos));
			else
				parrafoInscripcion.add(new Phrase("Datos de la matricula:", fontTitulos));
			parrafoInscripcion.add(new Phrase(Chunk.NEWLINE));
			parrafoInscripcion.add(new Phrase(Chunk.NEWLINE));
			parrafoInscripcion.add(new Phrase(Chunk.NEWLINE));

			document.add(parrafoInscripcion);
			PdfPTable tablaDatosPersonales = new PdfPTable(2);
			PdfPCell celdaDatosPersonales;

			celdaDatosPersonales = new PdfPCell(new Phrase("APELLIDOS Y NOMBRES:", fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase(getBeanListadoEstudiantes().getMatriculaSeleccionada().getEstudiante().getPersona().getPersApellidos().concat(" ").concat(getBeanListadoEstudiantes().getMatriculaSeleccionada().getEstudiante().getPersona().getPersNombres()), fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase("FECHA INSCRIPCION:", fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			if(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrFechaInscripcion()!=null)
				celdaDatosPersonales = new PdfPCell(new Phrase(FechaFormato.cambiarFormato(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrFechaInscripcion(),"yyyy-MM-dd"), fontContenidoTablas));
			else
				celdaDatosPersonales = new PdfPCell(new Phrase("",fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase("FECHA MATRICULA:", fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			if(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrFechaMatricula()!=null)
				celdaDatosPersonales = new PdfPCell(new Phrase(FechaFormato.cambiarFormato(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrFechaMatricula(),"yyyy-MM-dd"), fontContenidoTablas));
			else
				celdaDatosPersonales = new PdfPCell(new Phrase("",fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase("CURSO:", fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre(), fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase("INSTRUCTOR:", fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getInstructor().getPersona().getPersApellidos().concat(" ").concat(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getInstructor().getPersona().getPersNombres()) , fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase("FECHA DE INICIO:", fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase(FechaFormato.cambiarFormato(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getOcurFechaInicio(),"yyyy-MM-dd"), fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase("FECHA DE FIN:", fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);
			celdaDatosPersonales = new PdfPCell(new Phrase(FechaFormato.cambiarFormato(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getOcurFechaFin(),"yyyy-MM-dd") , fontContenidoTablas));
			celdaDatosPersonales.setBorder(PdfPCell.NO_BORDER);
			tablaDatosPersonales.addCell(celdaDatosPersonales);

			String mensajeQR="";
			mensajeQR=getBeanListadoEstudiantes().getMatriculaSeleccionada().getEstudiante().getPersona().getPersApellidos().concat(" ").concat(getBeanListadoEstudiantes().getMatriculaSeleccionada().getEstudiante().getPersona().getPersNombres()).concat(" ").concat(getBeanListadoEstudiantes().getMatriculaSeleccionada().getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre().concat(" ").concat(FechaFormato.cambiarFormato(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrFechaRegistro(),"yyyy-MM-dd")).concat(" ").concat(String.valueOf(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrId())));					
			byte[] pngData=null;
			pngData=getQRCodeImage(mensajeQR, 80, 80, logoQR);

			FileOutputStream fileOuputStream = new FileOutputStream(codigoQR);
			fileOuputStream.write(pngData);
			fileOuputStream.close();
			
			tablaDatosPersonales.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
			tablaDatosPersonales.setWidthPercentage(70f);
			document.add(tablaDatosPersonales);
			
			Paragraph parrafo = new Paragraph();
			parrafo.add(new Phrase(Chunk.NEWLINE));
			parrafo.add(new Phrase(Chunk.NEWLINE));
			document.add(parrafo);
			
			PdfPTable tablaQr = new PdfPTable(1);

			Image logotipoQr = Image.getInstance(codigoQR);
			logotipoQr.scalePercent(150);
			logotipoQr.setAlignment(Element.ALIGN_MIDDLE);
			PdfPCell celda = new PdfPCell(logotipoQr);
			celda.setColspan(1);

			celda.setHorizontalAlignment(Element.ALIGN_CENTER);
			celda.setBorder(PdfPCell.NO_BORDER);
			tablaQr.addCell(celda);				
			tablaQr.setWidthPercentage(150f);
			document.add(tablaQr);
			
			document.close();
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + new StringBuilder().append(getBeanListadoEstudiantes().getMatriculaSeleccionada().getEstudiante().getPersona().getPersApellidos().concat("-").concat(String.valueOf(getBeanListadoEstudiantes().getMatriculaSeleccionada().getMatrId()))).append(".pdf").toString());
			response.getOutputStream().write(Utilitario.getBytesFromFile(new File(directorioArchivoPDF)));
			response.getOutputStream().flush();
			response.getOutputStream().close();
			context.responseComplete();

		}  catch (DocumentException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.crearPdf"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "generarPdf" + ": ").append(e.getMessage()));			
		} catch (IOException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.crearArchivo"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "generarPdf" + ": ").append(e.getMessage()));			
		} catch (WriterException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), getMensajesBacking().getPropiedad("error.escribirArchivo"));			
			log.error(new StringBuilder().append(this.getClass().getName() + "." + "generarPdf" + ": ").append(e.getMessage()));			
		}
	}
	/**
	 * Genera el codigo QR
	 * @param text
	 * @param width
	 * @param height
	 * @param logoQR
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  byte[] getQRCodeImage(String text, int width, int height, String logoQR) throws WriterException, IOException {
		final Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height,hints);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] pngData = pngOutputStream.toByteArray();
		if (logoQR != null) {
			InputStream in = new ByteArrayInputStream(pngData);
			BufferedImage qrImage = ImageIO.read(in);
			BufferedImage overly = getOverly(logoQR);
			int deltaHeight = qrImage.getHeight() - overly.getHeight();
			int deltaWidth = qrImage.getWidth() - overly.getWidth();
			BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) combined.getGraphics();
			g.drawImage(qrImage, 0, 0, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			g.drawImage(overly, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
			ImageIO.write(combined, "png", os);
			return os.toByteArray();
		}
		return pngOutputStream.toByteArray();
	}
	/**
	 * Almacena en memoria el codigo QR
	 * @param LOGO
	 * @return
	 * @throws IOException
	 */
	private  BufferedImage getOverly(String LOGO) throws IOException {
		File file = new File(LOGO);
		return ImageIO.read(file);
	}
	/**
	 * Genera el certificado en formato pdf
	 */
	public void generaCertificadoPdf() {
		GeneracionPdf.generarCertificado(getBeanListadoEstudiantes().getMatriculaSeleccionada(), getBeanLogin(), getMensajesBacking());
	}
	public void mostrarDialogoBuscar() {
		nuevaBusqueda();
		Mensaje.verDialogo("dlgBuscar");
	}
	
	public void cargarOfertaCursos() {
		try {
			getBeanListadoEstudiantes().setListaOfertaCursos(new ArrayList<>());
			getBeanListadoEstudiantes().setListaOfertaCursos(getOfertaCursosServicio().listaOfertaCursosPorCurso(getBeanListadoEstudiantes().getCursoSeleccionado().getCursId()));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public String formatDate(Date fecha, String pattern) {
        return (new SimpleDateFormat(pattern)).format(fecha);
    }
	
	public void csvMoodle() {
		try {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String directorioArchivoCSV = new StringBuilder().append(ctx.getRealPath("")).append(File.separator).append("reportes").append(File.separator).append("usuariosMoodle").append(".csv").toString();		
			CSVWriter writer = new CSVWriter(new FileWriter(directorioArchivoCSV));
			String[] cabecera = new String[] { "username", "firstname","lastname","email" };
			writer.writeNext(cabecera);
			for (Matricula matricula : getBeanListadoEstudiantes().getListaMatriculas()) {
				String[] line = new String[] { matricula.getEstudiante().getPersona().getPersDocumentoIdentidad(), matricula.getEstudiante().getPersona().getPersNombres(),matricula.getEstudiante().getPersona().getPersApellidos(),matricula.getEstudiante().getPersona().getPersCorreoElectronico() };
	            writer.writeNext(line);
			}
			writer.close();
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/csv");
			response.setHeader("Content-Disposition", "attachment; filename=" + new StringBuilder().append("usuariosMoodle").append(".csv").toString());
			response.getOutputStream().write(Utilitario.getBytesFromFile(new File(directorioArchivoCSV)));
			response.getOutputStream().flush();
			response.getOutputStream().close();
			context.responseComplete();
			
		}catch(Exception e) {
			
			e.printStackTrace();
		}
	}
}
