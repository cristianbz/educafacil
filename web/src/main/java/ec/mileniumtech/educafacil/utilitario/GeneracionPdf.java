/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.utilitario;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.usuarios.BeanLogin;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Matricula;
import ec.mileniumtech.educafacil.utilitarios.dto.pdf.InscripcionMatriculaDto;
import ec.mileniumtech.educafacil.utilitarios.fechas.FechaFormato;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author [ Christian Baez ] cbaez Nov 11, 2020
 *
 */
public class GeneracionPdf {
	private static final Logger log = Logger.getLogger(GeneracionPdf.class);
	/**
	 * Genera certificado digital
	 * @param matricula
	 * @param beanLogin
	 * @param mensajesBacking
	 */
	public static void generarCertificado(Matricula matricula,BeanLogin beanLogin,MensajesBacking mensajesBacking) {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String directorioArchivoPDF = new StringBuilder().append(ctx.getRealPath("")).append(File.separator).append("reportes").append(File.separator).append(matricula.getMatrId()).append(".pdf").toString();		
		String logoQR = ctx.getRealPath("/") + File.separator + "imagenes" + File.separator + "logotipo" + File.separator + "logoQR.png";
		String codigoQR = ctx.getRealPath("/") + File.separator + "imagenes" + File.separator + "logotipo" + File.separator + "codigoQR.png";
		String logotipoURL = ctx.getRealPath("/")  + File.separator + "imagenes" + File.separator + "certificado" + File.separator +"certificadoDigital.png";
		
		try {
			InscripcionMatriculaDto inscripcionMatriculaDto=new InscripcionMatriculaDto();

			inscripcionMatriculaDto.setNombres(matricula.getEstudiante().getPersona().getPersNombres());
			inscripcionMatriculaDto.setApellidos(matricula.getEstudiante().getPersona().getPersApellidos());
			inscripcionMatriculaDto.setCurso(matricula.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre());
			inscripcionMatriculaDto.setFechaInicioCurso(matricula.getOfertaCursos().getOcurFechaInicio());
			inscripcionMatriculaDto.setFechaFinCurso(matricula.getOfertaCursos().getOcurFechaFin());
			inscripcionMatriculaDto.setFechaMatriculaInscripcion(matricula.getMatrFechaMatricula());

			Document document = new Document(null);

			document.setPageSize(PageSize.A4.rotate());
			
			document.setMargins(35, 35, 35, 35);
			document.setMarginMirroring(true);

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(directorioArchivoPDF));
			Rectangle rect = new Rectangle(100, 30, 500, 800);
			writer.setBoxSize("art", rect);


			document.open();


			PdfPTable tablaCabecera = new PdfPTable(1);
			Image logotipo = Image.getInstance(logotipoURL);
			logotipo.scalePercent(100);
			logotipo.setAlignment(Element.ALIGN_MIDDLE);
			PdfPCell cell = new PdfPCell(logotipo);
			cell.setColspan(1);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(PdfPCell.NO_BORDER);
			tablaCabecera.addCell(cell);				
			tablaCabecera.setWidthPercentage(150f);
			document.add(tablaCabecera);
			
			
			String mensajeQR="";
			mensajeQR=matricula.getEstudiante().getPersona().getPersApellidos().concat(" ").concat(matricula.getEstudiante().getPersona().getPersNombres()).concat(" ").concat(matricula.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre().concat(" ").concat(FechaFormato.cambiarFormato(matricula.getMatrFechaRegistro(),"yyyy-MM-dd")).concat(" ").concat(String.valueOf(matricula.getMatrId())));					
			byte[] pngData=null;
			pngData=getQRCodeImage(mensajeQR, 100, 100, logoQR);

			FileOutputStream fileOuputStream = new FileOutputStream(codigoQR);
			fileOuputStream.write(pngData);
			fileOuputStream.close();
						
			PdfContentByte capaNombres = writer.getDirectContent();
			capaNombres.saveState();
			

			Image logotipoQr = Image.getInstance(codigoQR);
			logotipoQr.scalePercent(150);
			logotipoQr.setAlignment(Element.ALIGN_MIDDLE);
			logotipoQr.setAbsolutePosition(650f, 0f);
			
			

			
			//document.add(tablaQr);
			capaNombres.addImage(logotipoQr);
			

		     BaseFont bf = BaseFont.createFont();
		     capaNombres.beginText();
		     capaNombres.setFontAndSize(bf, 20);
		     
		     
		     capaNombres.setTextMatrix((float)1, (float)0, (float) 0,2, 285, 285);
		     capaNombres.showText(matricula.getEstudiante().getPersona().getPersApellidos().concat(" ").concat(matricula.getEstudiante().getPersona().getPersNombres()));
		     capaNombres.endText();
		     capaNombres.restoreState();

		     PdfContentByte capaCurso = writer.getDirectContent();
		     capaCurso.saveState();     

		     capaCurso.beginText();
		     capaCurso.setFontAndSize(bf, 20);
		     capaCurso.setTextMatrix((float)1, (float)0, (float) 0,2, 250, 210);
		     capaCurso.showText(matricula.getOfertaCursos().getOfertaCapacitacion().getCurso().getCursNombre());
		     capaCurso.endText();
		     capaCurso.restoreState();
		 
		     
		     PdfContentByte capaDuracion = writer.getDirectContent();
		     capaDuracion.saveState();     

		     capaDuracion.beginText();
		     capaDuracion.setFontAndSize(bf, 12);
		     capaDuracion.setTextMatrix((float)1, (float)0, (float) 0,2, 380, 170);
		     capaDuracion.showText(String.valueOf(matricula.getOfertaCursos().getOcurDuracion()).concat(" ").concat("horas") );
		     capaDuracion.endText();
		     capaDuracion.restoreState();
		     
		     PdfContentByte capaMes = writer.getDirectContent();
		     capaMes.saveState();     
		     Calendar fecha = Calendar.getInstance();		     		   
		     fecha.setTime(matricula.getOfertaCursos().getOcurFechaInicio());
		     Calendar fechaFin = Calendar.getInstance();		     		   
		     fechaFin.setTime(matricula.getOfertaCursos().getOcurFechaFin());
		     
		     capaMes.beginText();
		     capaMes.setFontAndSize(bf, 12);
		     capaMes.setTextMatrix((float)1, (float)0, (float) 0,2, 580, 190);
		     if(fecha.get(Calendar.MONTH)!= fechaFin.get(Calendar.MONTH))
		    	 capaMes.showText(obtieneMes(fecha.get(Calendar.MONTH)).concat(" - ").concat(obtieneMes(fechaFin.get(Calendar.MONTH))));
		     else
		    	 capaMes.showText(obtieneMes(fecha.get(Calendar.MONTH)));
		     capaMes.endText();
		     capaMes.restoreState();
		     
		     PdfContentByte capaFecha = writer.getDirectContent();
		     capaFecha.saveState();     

		     capaFecha.beginText();
		     capaFecha.setFontAndSize(bf, 8);
		     capaFecha.setTextMatrix((float)1, (float)0, (float) 0,2, 665, 150);
		     capaFecha.showText("Quito, " + FechaFormato.cambiarFormato(matricula.getOfertaCursos().getOcurFechaFin(),"yyyy-MM-dd"));
		     capaFecha.endText();
		     capaFecha.restoreState();
		     
		     document.close();
			
			
			//document.close();
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + new StringBuilder().append("CERT-").append(matricula.getEstudiante().getPersona().getPersApellidos()).append(".pdf").toString());
			response.getOutputStream().write(Utilitario.getBytesFromFile(new File(directorioArchivoPDF)));
			response.getOutputStream().flush();
			response.getOutputStream().close();
			context.responseComplete();

		}  catch (DocumentException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, mensajesBacking.getPropiedad("error"), mensajesBacking.getPropiedad("error.crearPdf"));			
			log.error(new StringBuilder().append("GeneracionPdf." + "generarPdf" + ": ").append(e.getMessage()));			
		} catch (IOException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, mensajesBacking.getPropiedad("error"), mensajesBacking.getPropiedad("error.crearArchivo"));			
			log.error(new StringBuilder().append("GeneracionPdf." + "generarPdf" + ": ").append(e.getMessage()));			
		} catch (WriterException e) {
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, mensajesBacking.getPropiedad("error"), mensajesBacking.getPropiedad("error.escribirArchivo"));			
			log.error(new StringBuilder().append("GeneracionPdf." + "generarPdf" + ": ").append(e.getMessage()));			
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] getQRCodeImage(String text, int width, int height, String logoQR) throws WriterException, IOException {
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
	private static  BufferedImage getOverly(String LOGO) throws IOException {
		File file = new File(LOGO);
		return ImageIO.read(file);
	}
	
	public static String obtieneMes(int numeromes) {
		String mes="";
		switch(numeromes) {
		case 0:
			mes = "Enero";
			break;
		case 1:
			mes = "Febrero";
			break;
		case 2:
			mes = "Marzo";
			break;
		case 3:
			mes = "Abril";
			break;
		case 4:
			mes = "Mayo";
			break;
		case 5:
			mes = "Junio";
			break;
		case 6:
			mes = "Julio";
			break;
		case 7:
			mes = "Agosto";
			break;
		case 8:
			mes = "Septiembre";
			break;
		case 9:
			mes = "Octubre";
			break;
		case 10:
			mes = "Noviembre";
			break;
		case 11:
			mes = "Diciembre";
			break;
		}
		return mes;
	}
}
