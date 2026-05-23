package ec.mileniumtech.educafacil.utilitario;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UtilitarioTest {

    @TempDir
    File tempDir;

    @Test
    void getBytesFromFileLeeContenidoCompleto() throws IOException {
        File archivo = new File(tempDir, "prueba.txt");
        byte[] contenido = new byte[] { 1, 2, 3, 4, 5 };
        Files.write(archivo.toPath(), contenido);

        byte[] leido = Utilitario.getBytesFromFile(archivo);
        assertArrayEquals(contenido, leido);
    }

    @Test
    void getBytesFromFileArchivoVacio() throws IOException {
        File archivo = new File(tempDir, "vacio.txt");
        assertTrue(archivo.createNewFile());

        byte[] leido = Utilitario.getBytesFromFile(archivo);
        assertEquals(0, leido.length);
    }
}
