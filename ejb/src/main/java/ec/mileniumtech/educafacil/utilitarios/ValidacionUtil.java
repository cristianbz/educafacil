package ec.mileniumtech.educafacil.utilitarios;

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
}
