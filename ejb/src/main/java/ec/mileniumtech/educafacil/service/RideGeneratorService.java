package ec.mileniumtech.educafacil.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.mileniumtech.educafacil.modelo.sri.Factura;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Servicio para generar el RIDE (Representación Impresa de Documento Electrónico) en formato PDF.
 */
@Stateless
@LocalBean
public class RideGeneratorService {

    /**
     * Genera el PDF de la factura.
     * 
     * @param factura     Objeto factura con los datos.
     * @param logoStream  Stream del logo de la empresa.
     * @param jrxmlStream Stream del archivo .jasper o .jrxml de la plantilla.
     * @return Arreglo de bytes del PDF generado.
     * @throws Exception Si ocurre un error en la generación.
     */
    public byte[] generarRidePdf(Factura factura, InputStream logoStream, InputStream jrxmlStream) throws Exception {
        Map<String, Object> parametros = new HashMap<>();
        
        // Parámetros principales para el encabezado
        parametros.put("RUC", factura.getInfoTributaria().getRuc());
        parametros.put("RAZON_SOCIAL", factura.getInfoTributaria().getRazonSocial());
        parametros.put("DIR_MATRIZ", factura.getInfoTributaria().getDirMatriz());
        parametros.put("CLAVE_ACCESO", factura.getInfoTributaria().getClaveAcceso());
        parametros.put("NUM_FACTURA", factura.getInfoTributaria().getEstab() + "-" + 
                                     factura.getInfoTributaria().getPtoEmi() + "-" + 
                                     factura.getInfoTributaria().getSecuencial());
        parametros.put("AMBIENTE", factura.getInfoTributaria().getAmbiente().equals("1") ? "PRUEBAS" : "PRODUCCIÓN");
        parametros.put("EMISION", "NORMAL");
        parametros.put("LOGO", logoStream);
        
        // Datos del comprador
        parametros.put("CLIENTE", factura.getInfoFactura().getRazonSocialComprador());
        parametros.put("IDENTIFICACION", factura.getInfoFactura().getIdentificacionComprador());
        parametros.put("FECHA_EMISION", factura.getInfoFactura().getFechaEmision());
        
        // Totales
        parametros.put("TOTAL_SIN_IMPUESTOS", factura.getInfoFactura().getTotalSinImpuestos());
        parametros.put("IMPORTE_TOTAL", factura.getInfoFactura().getImporteTotal());
        

        // Formas de Pago
        StringBuilder sbPagos = new StringBuilder();
        if (factura.getInfoFactura().getPagos() != null) {
            for (ec.mileniumtech.educafacil.modelo.sri.Factura.PagoSRI p : factura.getInfoFactura().getPagosList()) {
                sbPagos.append("Cod. ").append(p.getFormaPago()).append(" - $").append(String.format("%.2f", p.getTotal())).append("\n");
            }
        }
        parametros.put("FORMAS_PAGO", sbPagos.toString());

        // DataSource para los detalles
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(factura.getDetallesList());

        // Cargar y llenar el reporte
        JasperReport report;
        if (jrxmlStream.markSupported()) {
            jrxmlStream.mark(1000000); // Reservar espacio para leer
        }
        
        try {
            // Intentar cargar como objeto compilado (.jasper)
            report = (JasperReport) JRLoader.loadObject(jrxmlStream);
        } catch (Exception e) {
            // Si falla, intentar compilar (.jrxml)
            if (jrxmlStream.markSupported()) {
                jrxmlStream.reset();
            }
            report = net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlStream);
        }
        
        JasperPrint print = JasperFillManager.fillReport(report, parametros, ds);

        // Exportar a PDF
        return JasperExportManager.exportReportToPdf(print);
    }
}
