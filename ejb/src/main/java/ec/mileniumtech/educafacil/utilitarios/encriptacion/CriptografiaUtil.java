/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.encriptacion;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utilitario para cifrado simétrico AES.
 * 
 * @author christian
 *
 */
public class CriptografiaUtil {
    private static final String ALGORITMO = "AES";
    
    /**
     * Llave maestra para el cifrado. 
     * NOTA: En un entorno real, esta llave debe ser configurada como una variable de entorno 
     * o propiedad del sistema en el servidor de aplicaciones (WildFly).
     */
    private static final String LLAVE_MAESTRA = "EducaFacilSRIKey"; // Debe ser de 16 caracteres para AES-128

    /**
     * Cifra una cadena de texto plana.
     * @param datos Cadena a cifrar.
     * @return Cadena cifrada en Base64.
     * @throws Exception
     */
    public static String encriptar(String datos) throws Exception {
        if (datos == null || datos.isEmpty()) return datos;
        SecretKeySpec secretKey = new SecretKeySpec(LLAVE_MAESTRA.getBytes(), ALGORITMO);
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosCifrados = cipher.doFinal(datos.getBytes());
        return Base64.getEncoder().encodeToString(datosCifrados);
    }

    /**
     * Descifra una cadena de texto previamente cifrada en Base64.
     * @param datosCifrados Cadena cifrada.
     * @return Cadena de texto plana.
     * @throws Exception
     */
    public static String desencriptar(String datosCifrados) throws Exception {
        if (datosCifrados == null || datosCifrados.isEmpty()) return datosCifrados;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(LLAVE_MAESTRA.getBytes(), ALGORITMO);
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] datosDecodificados = Base64.getDecoder().decode(datosCifrados);
            byte[] datosOriginales = cipher.doFinal(datosDecodificados);
            return new String(datosOriginales);
        } catch (Exception e) {
            // Si falla el descifrado, asumimos que el dato no estaba cifrado (caso de transición)
            return datosCifrados;
        }
    }
}
