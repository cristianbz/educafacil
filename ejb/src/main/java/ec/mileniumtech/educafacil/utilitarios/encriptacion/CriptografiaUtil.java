/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.encriptacion;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CriptografiaUtil {
    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private static final String LLAVE_MAESTRA;

    static {
        String key = System.getenv("CRIPTO_LLAVE_MAESTRA");
        if (key == null || key.isBlank()) {
            key = System.getProperty("CRIPTO_LLAVE_MAESTRA");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                "La variable de entorno CRIPTO_LLAVE_MAESTRA no está configurada. " +
                "Configúrala en WildFly con una clave de 16 caracteres para AES-128."
            );
        }
        LLAVE_MAESTRA = key;
    }

    public static String encriptar(String datos) throws Exception {
        if (datos == null || datos.isEmpty()) return datos;
        SecretKey secretKey = new SecretKeySpec(LLAVE_MAESTRA.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        byte[] datosCifrados = cipher.doFinal(datos.getBytes());
        byte[] ivYDatos = new byte[GCM_IV_LENGTH + datosCifrados.length];
        System.arraycopy(iv, 0, ivYDatos, 0, GCM_IV_LENGTH);
        System.arraycopy(datosCifrados, 0, ivYDatos, GCM_IV_LENGTH, datosCifrados.length);
        return Base64.getEncoder().encodeToString(ivYDatos);
    }

    private static final String ALGORITMO_LEGACY = "AES";

    public static String desencriptar(String datosCifrados) throws Exception {
        if (datosCifrados == null || datosCifrados.isEmpty()) return datosCifrados;
        try {
            return desencriptarGCM(datosCifrados);
        } catch (Exception e) {
            try {
                return desencriptarECB(datosCifrados);
            } catch (Exception ex) {
                return datosCifrados;
            }
        }
    }

    private static String desencriptarGCM(String datosCifrados) throws Exception {
        SecretKey secretKey = new SecretKeySpec(LLAVE_MAESTRA.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        byte[] ivYDatos = Base64.getDecoder().decode(datosCifrados);
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(ivYDatos, 0, iv, 0, GCM_IV_LENGTH);
        byte[] datosCifradosPuros = new byte[ivYDatos.length - GCM_IV_LENGTH];
        System.arraycopy(ivYDatos, GCM_IV_LENGTH, datosCifradosPuros, 0, datosCifradosPuros.length);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        byte[] datosOriginales = cipher.doFinal(datosCifradosPuros);
        return new String(datosOriginales);
    }

    private static String desencriptarECB(String datosCifrados) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(LLAVE_MAESTRA.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITMO_LEGACY);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDecodificados = Base64.getDecoder().decode(datosCifrados);
        byte[] datosOriginales = cipher.doFinal(datosDecodificados);
        return new String(datosOriginales);
    }
}
