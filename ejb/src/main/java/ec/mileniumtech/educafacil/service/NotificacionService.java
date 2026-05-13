package ec.mileniumtech.educafacil.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.activation.DataHandler;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.log4j.Logger;

/**
 * Servicio para envío de correos electrónicos con los comprobantes autorizados.
 */
@Stateless
@LocalBean
public class NotificacionService {

    private static final Logger log = Logger.getLogger(NotificacionService.class);

    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;

    /**
     * Envía la factura por correo electrónico de forma asíncrona.
     * 
     * @param destinatario Correo del cliente.
     * @param xmlContent   Contenido del XML firmado.
     * @param pdfContent   Contenido del PDF (RIDE).
     * @param numFactura   Número de factura para el asunto.
     */
    @Asynchronous
    public void enviarComprobante(String destinatario, byte[] xmlContent, byte[] pdfContent, String numFactura) {
        try {
            List<Configuraciones> configs = configuracionesDao.listaConfiguraciones();
            if (configs == null || configs.isEmpty()) {
                log.error("No hay configuraciones de correo disponibles en la base de datos.");
                return;
            }
            Configuraciones config = configs.get(0);

            String host = config.getConfServidorSmtp();
            String port = "587"; // O agregar a Configuraciones si varía
            final String user = config.getConfUsuarioCorreo();
            final String pass = config.getConfClaveCorreo();
            final String from = config.getConfEnviadoMailDesde();

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Comprobante Electrónico: " + numFactura);

            // Cuerpo del mensaje
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("Estimado cliente, adjunto enviamos su comprobante electrónico generado.", "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Adjunto XML
            MimeBodyPart xmlAttachment = new MimeBodyPart();
            xmlAttachment.setDataHandler(new DataHandler(new ByteArrayDataSource(xmlContent, "text/xml")));
            xmlAttachment.setFileName("Factura_" + numFactura + ".xml");
            multipart.addBodyPart(xmlAttachment);

            // Adjunto PDF
            MimeBodyPart pdfAttachment = new MimeBodyPart();
            pdfAttachment.setDataHandler(new DataHandler(new ByteArrayDataSource(pdfContent, "application/pdf")));
            pdfAttachment.setFileName("Factura_" + numFactura + ".pdf");
            multipart.addBodyPart(pdfAttachment);

            message.setContent(multipart);

            Transport.send(message);
            log.info("Correo enviado exitosamente a: " + destinatario + " para la factura: " + numFactura);
        } catch (Exception e) {
            log.error("Error al enviar el comprobante electrónico por correo a " + destinatario, e);
        }
    }
}
