/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.utilitario;

import java.text.DecimalFormat;

/**
 * @author [ Christian Baez ] cbaez Oct 20, 2020
 *
 */
public class Cadenas {
	/**
	 * Elimina espacios en blanco en una cadena
	 * @param cadena
	 * @return
	 */
	public static String eliminarEspaciosEnBlanco(String cadena) {
		return cadena.replaceAll("\\s+", " ");
	}
	/**
	 * Cambia de notacion cientifica a texto
	 * @param número
	 * @return
	 */
    public static String eliminaNotacionCientifica(double número) {

        String d = "####################################";

        return new DecimalFormat("#." + d + d + d).format(número);

    }

}
