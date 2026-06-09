package ec.mileniumtech.educafacil.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Sriformapago;
import ec.mileniumtech.educafacil.modelo.sri.Factura;
import ec.mileniumtech.educafacil.modelo.sri.Factura.PagoSRI;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
    public byte[] generarRidePdf(Factura factura, InputStream logoStream, InputStream jrxmlStream,List<FormaPagoFactura> listaFormaPagoFactura) throws Exception {
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
        parametros.put("NOMBRE_COMERCIAL",factura.getInfoTributaria().getNombreComercial());
        
        // Datos del comprador
        parametros.put("CLIENTE", factura.getInfoFactura().getRazonSocialComprador());
        parametros.put("IDENTIFICACION", factura.getInfoFactura().getIdentificacionComprador());
        parametros.put("FECHA_EMISION", factura.getInfoFactura().getFechaEmision());
        parametros.put("TELEFONO_CLIENTE", "");
        parametros.put("CORREO_CLIENTE", "");
        
        // Totales
        parametros.put("TOTAL_SIN_IMPUESTOS", factura.getInfoFactura().getTotalSinImpuestos());
        parametros.put("IMPORTE_TOTAL", factura.getInfoFactura().getImporteTotal());
        parametros.put("IMPUESTO_IVA",  factura.getInfoFactura().getImporteTotal().subtract(factura.getInfoFactura().getTotalSinImpuestos()).setScale(2, RoundingMode.HALF_UP));
        parametros.put("TOTAL_DESCUENTO", factura.getInfoFactura().getTotalDescuento());
        

        // Formas de Pago
        StringBuilder sbPagos = new StringBuilder();
        if (factura.getInfoFactura().getPagos() != null) {
            for (ec.mileniumtech.educafacil.modelo.sri.Factura.PagoSRI p : factura.getInfoFactura().getPagosList()) {
                sbPagos.append("Cod. ").append(p.getFormaPago()).append(" - $").append(String.format("%.2f", p.getTotal())).append("\n");
            }
        }
        parametros.put("FORMAS_PAGO", sbPagos.toString());
        
        // Formas de Pago como DataSource (para tablas o subreportes)
        if (factura.getInfoFactura().getPagosList() != null && !factura.getInfoFactura().getPagosList().isEmpty()) {
        	Map<String, FormaPagoFactura> mapaFormasPago = new HashMap<>();
        	for (FormaPagoFactura fpf : listaFormaPagoFactura) {
        		mapaFormasPago.put(fpf.getSriformapagos().getSrfpCodigoSri(), fpf);
        	}
        	List<PagoSRI> listaPagosSri = new ArrayList<>();
        	for (ec.mileniumtech.educafacil.modelo.sri.Factura.PagoSRI p : factura.getInfoFactura().getPagosList()) {
        		FormaPagoFactura fpf = mapaFormasPago.get(p.getFormaPago());
        		if (fpf != null) {
	        		PagoSRI pagosSri = new PagoSRI();
	        		pagosSri.setFormaPago(fpf.getSriformapagos().getSrfpCodigoSri().concat(" - ").concat(fpf.getSriformapagos().getSrfpDescripcion()));
	        		pagosSri.setTotal(fpf.getValor());
	        		listaPagosSri.add(pagosSri);
        		}
        	}
        	parametros.put("PAGOS_DATASOURCE", new JRBeanCollectionDataSource(listaPagosSri));
        }

        // Información Adicional como DataSource
        if (factura.getInfoAdicionalList() != null && !factura.getInfoAdicionalList().isEmpty()) {
            parametros.put("INFO_ADICIONAL_DATASOURCE", new JRBeanCollectionDataSource(factura.getInfoAdicionalList()));
        }
        
        // DataSource para los detalles
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(factura.getDetallesList());
        // ✅ Leer bytes una sola vez y compilar directo — siempre es .jrxml
        byte[] jrxmlBytes = jrxmlStream.readAllBytes();
        JasperReport report = JasperCompileManager.compileReport(
            new ByteArrayInputStream(jrxmlBytes)
        );

        JasperPrint print = JasperFillManager.fillReport(report, parametros, ds);
        return JasperExportManager.exportReportToPdf(print);

    }
    /**
     * Genera el PDF de la nota de crédito.
     * 
     * @param notaCredito Objeto notaCredito con los datos.
     * @param logoStream  Stream del logo de la empresa.
     * @param jrxmlStream Stream del archivo .jasper o .jrxml de la plantilla.
     * @return Arreglo de bytes del PDF generado.
     * @throws Exception Si ocurre un error en la generación.
     */
    public byte[] generarRideNotaCreditoPdf(NotaCredito notaCredito, InputStream logoStream, InputStream jrxmlStream) throws Exception {
        Map<String, Object> parametros = new HashMap<>();
        
        parametros.put("RUC", notaCredito.getInfoTributaria().getRuc());
        parametros.put("RAZON_SOCIAL", notaCredito.getInfoTributaria().getRazonSocial());
        parametros.put("DIR_MATRIZ", notaCredito.getInfoTributaria().getDirMatriz());
        parametros.put("CLAVE_ACCESO", notaCredito.getInfoTributaria().getClaveAcceso());
        parametros.put("NUM_FACTURA", notaCredito.getInfoTributaria().getEstab() + "-" + 
                                     notaCredito.getInfoTributaria().getPtoEmi() + "-" + 
                                     notaCredito.getInfoTributaria().getSecuencial());
        parametros.put("AMBIENTE", notaCredito.getInfoTributaria().getAmbiente().equals("1") ? "PRUEBAS" : "PRODUCCIÓN");
        parametros.put("EMISION", "NORMAL");
        parametros.put("LOGO", logoStream);
        parametros.put("NOMBRE_COMERCIAL", notaCredito.getInfoTributaria().getNombreComercial());
        
        parametros.put("CLIENTE", notaCredito.getInfoNotaCredito().getRazonSocialComprador());
        parametros.put("IDENTIFICACION", notaCredito.getInfoNotaCredito().getIdentificacionComprador());
        parametros.put("FECHA_EMISION", notaCredito.getInfoNotaCredito().getFechaEmision());
        parametros.put("TELEFONO_CLIENTE", "");
        parametros.put("CORREO_CLIENTE", "");
        
        parametros.put("TOTAL_SIN_IMPUESTOS", notaCredito.getInfoNotaCredito().getTotalSinImpuestos());
        parametros.put("IMPORTE_TOTAL", notaCredito.getInfoNotaCredito().getValorModificacion());
        parametros.put("IMPUESTO_IVA", notaCredito.getInfoNotaCredito().getValorModificacion().subtract(notaCredito.getInfoNotaCredito().getTotalSinImpuestos()).setScale(2, RoundingMode.HALF_UP));
        parametros.put("TOTAL_DESCUENTO", BigDecimal.ZERO); // NC doesn't have discount at header level usually in this model
        
        parametros.put("FORMAS_PAGO", "");
        
        // Add reason and modified doc info to infoAdicional to ensure it's printed
        List<NotaCredito.CampoAdicional> infoAdicionalList = notaCredito.getInfoAdicionalList();
        if (infoAdicionalList == null) {
            infoAdicionalList = new ArrayList<>();
        }
        NotaCredito.CampoAdicional campoMotivo = new NotaCredito.CampoAdicional();
        campoMotivo.setNombre("Motivo");
        campoMotivo.setValor(notaCredito.getInfoNotaCredito().getMotivo());
        infoAdicionalList.add(campoMotivo);
        
        NotaCredito.CampoAdicional campoDocModificado = new NotaCredito.CampoAdicional();
        campoDocModificado.setNombre("Doc. Modificado");
        campoDocModificado.setValor("Factura " + notaCredito.getInfoNotaCredito().getNumDocModificado());
        infoAdicionalList.add(campoDocModificado);
        if (!infoAdicionalList.isEmpty()) {
            parametros.put("INFO_ADICIONAL_DATASOURCE", new JRBeanCollectionDataSource(infoAdicionalList));
        }
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(notaCredito.getDetallesList());
        byte[] jrxmlBytes = jrxmlStream.readAllBytes();
        JasperReport report = JasperCompileManager.compileReport(
            new ByteArrayInputStream(jrxmlBytes)
        );
        JasperPrint print = JasperFillManager.fillReport(report, parametros, ds);
        return JasperExportManager.exportReportToPdf(print);
    }
}
