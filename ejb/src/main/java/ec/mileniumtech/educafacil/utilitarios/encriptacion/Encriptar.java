/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.encriptacion;

import org.apache.commons.codec.digest.DigestUtils;

/**
*@author christian  Jul 6, 2024
*
*/
public class Encriptar {
	/**
	 * Encripta una cadena a formato SHA-512
	 * @param valor
	 * @return
	 */
	public static String encriptarSHA512(String entrada) {
		return DigestUtils.sha512Hex(entrada);
	}
	
	/**
	 * Metodo que encripta en sha 256.
	 * @param entrada cadena entrada
	 * @return resultado
	 */
	public static String encriptarSHA256(String entrada) {
		return DigestUtils.sha256Hex(entrada);
	}
	
	/**
	 * Metodo que encripta en sha 1.
	 * @param entrada cadena entrada
	 * @return resultado
	 */
	public static String encriptarSHA1(String entrada) {
		return DigestUtils.shaHex(entrada);
	}

}
