package ec.mileniumtech.educafacil.utilitarios.sri;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.CertificateInfoDto;

public class CertificateUtils {
	/**
     * Obtiene la fecha de caducidad de un certificado .p12
     */
    public static LocalDate getCertificateExpiration(byte[] pkcs12Cert, String password) 
            throws Exception {
        
        // Cargar el almacén de claves PKCS#12
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        
//        try (FileInputStream fis = new FileInputStream(p12FilePath)) {
//            keyStore.load(fis, password.toCharArray());
//        }
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(pkcs12Cert), password.toCharArray());
        
        // Obtener el primer certificado (generalmente es el de firma)
        String alias = keyStore.aliases().nextElement();
        
        // Obtener el certificado como X509Certificate
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
        
        // Obtener la fecha de caducidad (notAfter)
        Date expirationDate = certificate.getNotAfter();
        
        // Convertir a LocalDate
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    
    /**
     * Obtiene toda la información del certificado .p12
     */
    public static CertificateInfoDto getCertificateInfo(byte[] pkcs12Cert, String password) 
            throws Exception {
        
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        
//        try (FileInputStream fis = new FileInputStream(p12FilePath)) {
//            keyStore.load(fis, password.toCharArray());
//        }
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(pkcs12Cert), password.toCharArray());
        
        String alias = keyStore.aliases().nextElement();
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
        
        CertificateInfoDto info = new CertificateInfoDto();
        
        // Fechas de validez
        info.setIssueDate(certificate.getNotBefore().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        info.setExpirationDate(certificate.getNotAfter().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        
        // Información del sujeto (quien posee el certificado)
        info.setSubject(certificate.getSubjectX500Principal().getName());
        info.setIssuer(certificate.getIssuerX500Principal().getName());
        
        // Serial number
        info.setSerialNumber(certificate.getSerialNumber().toString());
        
        // Algoritmo de firma
        info.setSignatureAlgorithm(certificate.getSigAlgName());
        
        // Versión
        info.setVersion(certificate.getVersion());
        
        return info;
    }
    
    /**
     * Verifica si el certificado está vigente
     */
    public static boolean isCertificateValid(byte[] pkcs12Cert, String password) 
            throws Exception {
        
        Date now = new Date();
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        
//        try (FileInputStream fis = new FileInputStream(p12FilePath)) {
//            keyStore.load(fis, password.toCharArray());
//        }
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(pkcs12Cert), password.toCharArray());
        
        String alias = keyStore.aliases().nextElement();
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
        
        // Verificar si está dentro del periodo de validez
        return certificate.getNotBefore().before(now) && 
               certificate.getNotAfter().after(now);
    }
    
    /**
     * Verifica si el certificado está caducado
     */
    public static boolean isCertificateExpired(byte[] pkcs12Cert, String password) 
            throws Exception {
        
        Date now = new Date();
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        
//        try (FileInputStream fis = new FileInputStream(p12FilePath)) {
//            keyStore.load(fis, password.toCharArray());
//        }
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(pkcs12Cert), password.toCharArray());
        
        String alias = keyStore.aliases().nextElement();
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
        
        return certificate.getNotAfter().before(now);
    }
}
