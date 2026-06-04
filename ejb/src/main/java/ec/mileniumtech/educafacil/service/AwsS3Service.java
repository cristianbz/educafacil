package ec.mileniumtech.educafacil.service;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

/**
 * Servicio EJB para interactuar con AWS S3.
 * Permite subir archivos y generar pre-signed URLs para descarga segura.
 *
 * <p><b>Variables de entorno requeridas en el servidor WildFly:</b></p>
 * <ul>
 *   <li>AWS_ACCESS_KEY_ID</li>
 *   <li>AWS_SECRET_ACCESS_KEY</li>
 *   <li>AWS_REGION (ej: us-east-1)</li>
 *   <li>AWS_S3_BUCKET_NAME (nombre del bucket)</li>
 * </ul>
 */
@Stateless
@LocalBean
public class AwsS3Service {

	private static final Logger log = LogManager.getLogger(AwsS3Service.class);

    /** Duración de validez de las pre-signed URLs (en minutos). */
    private static final long PRESIGNED_URL_DURACION_MINUTOS = 60;

    /**
     * Sube un archivo al bucket S3 configurado y retorna la clave (key) del objeto.
     *
     * @param contenido       Bytes del archivo a subir.
     * @param claveObjeto     Ruta/nombre del objeto en S3 (ej: "facturas/001-001-000000001/RIDE.pdf").
     * @param contentType     Tipo MIME del archivo (ej: "application/pdf" o "text/xml").
     * @return La clave del objeto subido en S3.
     * @throws Exception Si ocurre un error al subir el archivo.
     */
    public String subirArchivo(byte[] contenido, String claveObjeto, String contentType) throws Exception {
        String bucketName = obtenerBucketName();
        Region region = obtenerRegion();

        try (S3Client s3Client = S3Client.builder()
                .region(region)
                .build()) {

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(claveObjeto)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(contenido));
            log.info("Archivo subido a S3 exitosamente: s3://" + bucketName + "/" + claveObjeto);

            return claveObjeto;

        } catch (Exception e) {
            log.error("Error al subir archivo a S3: " + claveObjeto, e);
            throw new Exception("Error al subir archivo a AWS S3: " + e.getMessage(), e);
        }
    }

    /**
     * Genera una pre-signed URL temporal para descargar un objeto de S3.
     *
     * @param claveObjeto Ruta/nombre del objeto en S3.
     * @return URL pre-firmada válida por {@value #PRESIGNED_URL_DURACION_MINUTOS} minutos.
     * @throws Exception Si ocurre un error al generar la URL.
     */
    public String generarUrlDescarga(String claveObjeto) throws Exception {
        String bucketName = obtenerBucketName();
        Region region = obtenerRegion();

        try (S3Presigner presigner = S3Presigner.builder()
                .region(region)
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(claveObjeto)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_DURACION_MINUTOS))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toExternalForm();
            log.info("Pre-signed URL generada para: " + claveObjeto);
            return url;

        } catch (Exception e) {
            log.error("Error al generar pre-signed URL para: " + claveObjeto, e);
            throw new Exception("Error al generar URL de descarga desde S3: " + e.getMessage(), e);
        }
    }

    /**
     * Construye la clave S3 para el PDF (RIDE) de una factura.
     *
     * @param numeroFactura Número de factura (ej: 001-001-000000001).
     * @return Clave S3 (ruta dentro del bucket).
     */
    public String construirClavePdf(String numeroFactura) {
        return "facturas/" + numeroFactura + "/RIDE_" + numeroFactura + ".pdf";
    }

    /**
     * Construye la clave S3 para el XML de una factura.
     *
     * @param numeroFactura Número de factura.
     * @return Clave S3 (ruta dentro del bucket).
     */
    public String construirClaveXml(String numeroFactura) {
        return "facturas/" + numeroFactura + "/Factura_" + numeroFactura + ".xml";
    }

    // ---- Métodos privados de configuración ----

    private String obtenerBucketName() throws Exception {
        String bucketName = System.getenv("AWS_S3_BUCKET_NAME");
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new Exception("La variable de entorno AWS_S3_BUCKET_NAME no está configurada en el servidor.");
        }
        return bucketName.trim();
    }

    private Region obtenerRegion() throws Exception {
        String regionStr = System.getenv("AWS_REGION");
        if (regionStr == null || regionStr.trim().isEmpty()) {
            // Intentar con la variable alternativa
            regionStr = System.getenv("AWS_DEFAULT_REGION");
        }
        if (regionStr == null || regionStr.trim().isEmpty()) {
            throw new Exception("La variable de entorno AWS_REGION no está configurada en el servidor.");
        }
        return Region.of(regionStr.trim());
    }
}
