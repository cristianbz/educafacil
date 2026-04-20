package ec.mileniumtech.educafacil.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
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

/**
 * Servicio para envío de correos electrónicos con los comprobantes autorizados.
 */
@Stateless
@LocalBean
public class NotificacionService {

    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;

    /**
     * Envía la factura por correo electrónico.
     * 
     * @param destinatario Correo del cliente.
     * @param xmlContent   Contenido del XML firmado.
     * @param pdfContent   Contenido del PDF (RIDE).
     * @param numFactura   Número de factura para el asunto.
     * @throws Exception Si ocurre un error en el envío.
     */
    public void enviarComprobante(String destinatario, byte[] xmlContent, byte[] pdfContent, String numFactura) throws Exception {
        List<Configuraciones> configs = configuracionesDao.listaConfiguraciones();
        if (configs == null || configs.isEmpty()) {
            throw new Exception("No hay configuraciones de correo disponibles en la base de datos.");
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
        xmlAttachment.setContent(xmlContent, "text/xml");
        xmlAttachment.setFileName("Factura_" + numFactura + ".xml");
        multipart.addBodyPart(xmlAttachment);

        // Adjunto PDF
        MimeBodyPart pdfAttachment = new MimeBodyPart();
        pdfAttachment.setContent(pdfContent, "application/pdf");
        pdfAttachment.setFileName("Factura_" + numFactura + ".pdf");
        multipart.addBodyPart(pdfAttachment);

        message.setContent(multipart);

        Transport.send(message);
    }
}
