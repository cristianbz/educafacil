/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.utilitario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author [ Christian Baez ] cbaez Feb 13, 2020
 *
 */
public class Utilitario {
	public static byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        if (offset < bytes.length) {
            throw new IOException("No se pudo leer completamente el archivo " + file.getName());
        }
        return bytes;
    }
}
