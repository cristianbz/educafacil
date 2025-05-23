/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.correo;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import ec.mileniumtech.educafacil.utilitarios.fechas.FechaFormato;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
/**
*@author christian  Jul 7, 2024
*
*/
public class Correo extends Thread {

	private String asunto;
	private String rutaArchivo;
	private String mensaje;
	
	private boolean debug;
	private String para;
	private String smtp;
	private String usuario;
	private String clave;
	private String enviadoDesde;
	
	private final static Properties properties = new Properties();
	private static String password;
	private static String user;
 	private static Session session;
 	private static String hostSmtp;
	private static String to;
	private static String from;
		
	
	public Correo() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Correo(String asunto, String mensaje, boolean debug) {
		super();
		this.asunto = asunto;
		this.mensaje = mensaje;
		
		this.debug = debug;
	}
	public Correo(String asunto, String mensaje, boolean debug,String para,String rutaArchivo,String smtp,String usuario,String clave,String enviadoDesde) {
		super();
		this.asunto = asunto;
		this.mensaje = mensaje;
		this.debug = debug;
		this.para=para;
		this.rutaArchivo=rutaArchivo;
		this.smtp=smtp;
		this.usuario=usuario;
		this.clave=clave;
		this.enviadoDesde=enviadoDesde;
	}
	public Correo(String asunto, String mensaje,String desde, boolean debug,String correo) {
		super();
		this.asunto = asunto;
		this.mensaje = mensaje;
		this.enviadoDesde = desde;
		this.debug = debug;
		this.para = correo;
	}

	@Override
	public void run() {
		
			enviarCorreo(this.asunto, this.mensaje, this.debug, this.para,this.rutaArchivo,this.smtp,this.usuario,this.clave,this.enviadoDesde);
	}

	private static void inicializar(String smtp,String usuario,String clave,String enviadoDesde) {
		 
		password=clave;
		user=usuario;
		hostSmtp=smtp;
		from=enviadoDesde;
		
		properties.put("mail.smtp.host", hostSmtp);
		properties.put("mail.smtp.port",26);
		properties.put("mail.smtp.mail.sender",from);
		properties.put("mail.smtp.user", user);
		properties.put("mail.smtp.auth", "true");
 
		session = Session.getDefaultInstance(properties);
		
		
		
	}
	
	/**
	 * Metodo que permite enviar correos desde el Sam
	 * @param subject
	 * @param mensaje
	 * @param debug
	 * @return
	 */
	public synchronized  static boolean enviarCorreo(String subject, String mensaje, boolean debug,String para,String filename,String smtp,String usuario,String clave,String enviadoDesde) {
		to = para;
//		String to[]= new String[2];
//		to[0]=para;
//		to[1]= "info.ceims@gmail.com";
		inicializar(smtp,usuario,clave,enviadoDesde);
		
		String msgText1=new StringBuilder().append(mensaje).append("  </td></tr></table>")
       			.append("<br/>Saludos cordiales,")
       			.append("<br/>Sistema de Administración de centros de formación.")
       			.append("<br/><br/><span style='font-size: 12px; font-style: italic'><strong>Nota: </strong> Este mensaje fue enviado autom�ticamente por el sistema, por favor no lo responda.</span>")
       			.append("</fieldset>").toString();
		
		msgText1 += "<br/><span style='font-size: 12px; font-style: italic'>"+FechaFormato.cambiarFormato(new Date(), "yyyy-MM-dd HH:mm:ss")+"- Sistema de Administración de centros de formación. </span>";

		// create some properties and get the default Session
		session.setDebug(debug);

		try {
			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			//InternetAddress[] address = { new InternetAddress(to) };   //descomentar cuando es una sola dir
//			msg.setRecipients(Message.RecipientType.BCC, to);
			msg.setRecipients(Message.RecipientType.TO, to);
			msg.setRecipients(Message.RecipientType.CC, "info.ceims@gmail.com");
			msg.setSubject(subject);
			

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(msgText1,"utf-8", "html");

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			////mp.addBodyPart(mbp2);

			if(filename!=null){			
				// create the second message part
				MimeBodyPart mbp2 = new MimeBodyPart();
	
				// attach the file to the message
				mbp2.attachFile(filename);
				mp.addBodyPart(mbp2);
			}
			
			
			// add the Multipart to the message
			msg.setContent(mp);

			// set the Date: header
			msg.setSentDate(new Date());

			/*
			 * If you want to control the Content-Transfer-Encoding of the
			 * attached file, do the following. Normally you should never need
			 * to do this.
			 * 
			 * msg.saveChanges(); mbp2.setHeader("Content-Transfer-Encoding",
			 * "base64");
			 */

			// send the message
			Transport t = session.getTransport("smtp");
			t.connect((String)properties.get("mail.smtp.user"), password);
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
			return true;

		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}


}



