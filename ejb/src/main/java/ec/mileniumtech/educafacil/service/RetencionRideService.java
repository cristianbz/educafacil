package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Servicio para generar el RIDE de Comprobantes de Retención.
 */
@Stateless
@LocalBean
public class RetencionRideService {

    /**
     * Genera el PDF de la retención.
     * 
     * @param retencion   Objeto JAXB con los datos.
     * @param logoStream  Stream del logo.
     * @param jrxmlStream Stream del archivo .jrxml.
     * @param numeroAutorizacion Número de autorización del SRI.
     * @param fechaAutorizacion Fecha de autorización del SRI.
     * @return Arreglo de bytes del PDF.
     * @throws Exception Si ocurre un error.
     */
    public byte[] generarRidePdf(ComprobanteRetencion retencion, InputStream logoStream, InputStream jrxmlStream, 
                                 String numeroAutorizacion, String fechaAutorizacion) throws Exception {
        Map<String, Object> parametros = new HashMap<>();
        
        // Info Tributaria
        parametros.put("RUC", retencion.getInfoTributaria().getRuc());
        parametros.put("RAZON_SOCIAL", retencion.getInfoTributaria().getRazonSocial());
        parametros.put("DIR_MATRIZ", retencion.getInfoTributaria().getDirMatriz());
        parametros.put("CLAVE_ACCESO", retencion.getInfoTributaria().getClaveAcceso());
        parametros.put("NUM_COMPROBANTE", retencion.getInfoTributaria().getEstab() + "-" + 
                                         retencion.getInfoTributaria().getPtoEmi() + "-" + 
                                         retencion.getInfoTributaria().getSecuencial());
        parametros.put("AMBIENTE", retencion.getInfoTributaria().getAmbiente().equals("1") ? "PRUEBAS" : "PRODUCCIÓN");
        parametros.put("EMISION", "NORMAL");
        parametros.put("LOGO", logoStream);
        parametros.put("NUM_AUTORIZACION", numeroAutorizacion);
        parametros.put("FECHA_AUTORIZACION", fechaAutorizacion);
        parametros.put("NOM_COMERCIAL", retencion.getInfoTributaria().getNombreComercial());
        
        // Info Retención
        parametros.put("SUJETO_RETENIDO", retencion.getInfoRetencion().getRazonSocialSujetoRetenido());
        parametros.put("IDENTIFICACION_SUJETO", retencion.getInfoRetencion().getIdentificacionSujetoRetenido());
        parametros.put("FECHA_EMISION", retencion.getInfoRetencion().getFechaEmision());
        parametros.put("EJERCICIO_FISCAL", retencion.getInfoRetencion().getPeriodoFiscal());
        
        // DataSource para los impuestos retenidos
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(retencion.getImpuestosList());
        
        byte[] jrxmlBytes = jrxmlStream.readAllBytes();
        JasperReport report = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxmlBytes));
        
        JasperPrint print = JasperFillManager.fillReport(report, parametros, ds);
        return JasperExportManager.exportReportToPdf(print);
    }
}
