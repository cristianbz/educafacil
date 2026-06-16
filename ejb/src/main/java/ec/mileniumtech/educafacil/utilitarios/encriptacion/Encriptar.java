/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.utilitarios.encriptacion;

import org.apache.commons.codec.digest.DigestUtils;
import org.mindrot.jbcrypt.BCrypt;

/**
*@author christian  Jul 6, 2024
*
*/
public class Encriptar {
	public static String encriptarSHA512(String entrada) {
		return DigestUtils.sha512Hex(entrada);
	}
	
	public static String encriptarSHA256(String entrada) {
		return DigestUtils.sha256Hex(entrada);
	}
	
	public static String encriptarSHA1(String entrada) {
		return DigestUtils.shaHex(entrada);
	}

	public static String encriptarBCrypt(String contrasena) {
		return BCrypt.hashpw(contrasena, BCrypt.gensalt(12));
	}

	public static boolean verificarBCrypt(String contrasena, String hashAlmacenado) {
		return BCrypt.checkpw(contrasena, hashAlmacenado);
	}
	
	public static boolean esHashBCrypt(String hash) {
		return hash != null && hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
	}
}
