package ec.mileniumtech.educafacil.utilitarios;

import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Utilidad para validaciones comunes (Cédula, RUC, etc.)
 */
public class ValidacionUtil {

    /**
     * Valida una cédula ecuatoriana.
     * @param cedula La cédula a validar.
     * @return true si es válida, false en caso contrario.
     */
    public static boolean validarCedula(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return false;
        }

        // Verificar que sean solo dígitos
        if (!cedula.matches("\\d+")) {
            return false;
        }

        // Provincia (dos primeros dígitos entre 01 y 24)
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        // Tercer dígito (menor a 6)
        int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
        if (tercerDigito >= 6) {
            return false;
        }

        // Algoritmo de Modulo 10
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int verificador = Integer.parseInt(cedula.substring(9, 10));
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int valor = Integer.parseInt(cedula.substring(i, i + 1)) * coeficientes[i];
            if (valor >= 10) {
                valor -= 9;
            }
            suma += valor;
        }

        int total = ((suma / 10) + 1) * 10;
        if (suma % 10 == 0) {
            total = suma;
        }

        int digitoValidado = total - suma;

        return digitoValidado == verificador;
    }
    /**
     * Verifica si una URL es alcanzable (útil para comprobar conexión al SRI).
     * @param urlStr La URL a verificar.
     * @param timeoutMs Tiempo de espera en milisegundos.
     * @return true si la conexión fue exitosa (código 200), false en caso contrario.
     */
    public static boolean verificarConexion(String urlStr, int timeoutMs) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            connection.setRequestMethod("GET");
            // Agregamos un User-Agent para que el SRI no bloquee la petición por parecer un bot básico
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            // Intentamos obtener el código de respuesta. 
            // Si llegamos aquí sin excepciones, significa que el servidor RESPONDIÓ, 
            // por lo tanto hay internet y el servidor está vivo.
            int responseCode = connection.getResponseCode();
            return true; 
        } catch (java.net.SocketTimeoutException e) {
            // El servidor existe pero está muy lento o saturado
            return false;
        } catch (java.io.IOException e) {
            // Error de red (No hay internet, DNS falló, conexión rechazada)
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
