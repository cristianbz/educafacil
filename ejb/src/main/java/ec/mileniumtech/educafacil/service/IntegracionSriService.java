package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ec.mileniumtech.educafacil.dao.impl.PagosDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.sri.Factura;
import ec.mileniumtech.educafacil.modelo.sri.Factura.Detalle;
import ec.mileniumtech.educafacil.modelo.sri.Factura.InfoFactura;
import ec.mileniumtech.educafacil.modelo.sri.Factura.InfoTributaria;
import ec.mileniumtech.educafacil.modelo.sri.Factura.TotalImpuesto;
import ec.mileniumtech.educafacil.service.sri.autorizacion.Autorizacion;
import ec.mileniumtech.educafacil.service.sri.autorizacion.RespuestaComprobante;
import ec.mileniumtech.educafacil.service.sri.recepcion.RespuestaSolicitud;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CriptografiaUtil;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Orquestador del proceso de facturación electrónica: Generación -> Firma -> Envío -> Autorización.
 */
@Stateless
@LocalBean
public class IntegracionSriService {

    @EJB
    private ClaveAccesoGenerator claveAccesoGenerator;

    @EJB
    private FacturaXmlService facturaXmlService;

    @EJB
    private XadesSignatureService xadesSignatureService;

    @EJB
    private SriWebServiceService sriWebServiceService;

    @EJB
    private RideGeneratorService rideGeneratorService;

    @EJB
    private NotificacionService notificacionService;

    @EJB
    private PagosDaoImpl pagosDao;

    /**
     * Procesa una factura electrónica completa a partir de un pago.
     * 
     * @param pago    Entidad Pagos con la información de la transacción.
     * @param empresa Entidad Empresa con la configuración del emisor y certificado.
     * @throws Exception Si falla algún paso del proceso.
     */
    public void procesarFacturaElectronica(Pagos pago, EmpresaMatriz empresa) throws Exception {
        // 1. Datos iniciales y clave de acceso
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaEmision = sdf.format(pago.getPagoFecha() != null ? pago.getPagoFecha() : new Date());
        
        // Estos valores deberían venir configurados en la Empresa o Punto de Emisión
        String estab = "001"; 
        String ptoEmi = "001";
        String secuencial = String.format("%09d", pago.getPagoId()); // Usamos el ID como secuencial para el ejemplo
        String serie = estab + ptoEmi;
        
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                new Date(), "01", empresa.getEmpmRuc(), 
                empresa.getEmpmAmbiente().toString(), serie, secuencial, 
                "12345678", "1");

        // 2. Construcción del objeto Factura (JAXB)
        Factura factura = new Factura();
        
        // Info Tributaria
        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente(empresa.getEmpmAmbiente().toString());
        infoTrib.setTipoEmision("1");
        infoTrib.setRazonSocial(empresa.getEmpmNombreComercial());
        infoTrib.setRuc(empresa.getEmpmRuc());
        infoTrib.setClaveAcceso(claveAcceso);
        infoTrib.setCodDoc("01");
        infoTrib.setEstab(estab);
        infoTrib.setPtoEmi(ptoEmi);
        infoTrib.setSecuencial(secuencial);
        infoTrib.setDirMatriz(empresa.getEmpmDireccion());
        factura.setInfoTributaria(infoTrib);

        // Info Factura
        InfoFactura infoFact = new InfoFactura();
        infoFact.setFechaEmision(fechaEmision);
        infoFact.setDirEstablecimiento(empresa.getEmpmDireccion());
        infoFact.setObligadoContabilidad(empresa.isEmpmObligadoContabilidad() ? "SI" : "NO");
        
        // Datos del cliente (Estudiante)
        infoFact.setTipoIdentificacionComprador("05"); // Cédula por defecto
        infoFact.setIdentificacionComprador(pago.getMatricula().getEstudiante().getPersona().getPersDocumentoIdentidad());
        infoFact.setRazonSocialComprador(pago.getMatricula().getEstudiante().getPersona().getPersApellidos() + " " + 
                                         pago.getMatricula().getEstudiante().getPersona().getPersNombres());
        
        double totalSinImpuestos = 0;
        for (DetallePagos dp : pago.getDetallePagos()) {
            totalSinImpuestos += dp.getDepaValor();
            
            Detalle det = new Detalle();
            det.setCodigoPrincipal(dp.getDepaId().toString());
            det.setDescripcion("Servicio de Capacitación");
            det.setCantidad(1);
            det.setPrecioUnitario(dp.getDepaValor());
            det.setPrecioTotalSinImpuesto(dp.getDepaValor());
            factura.getDetalles().add(det);
        }
        
        infoFact.setTotalSinImpuestos(totalSinImpuestos);
        infoFact.setImporteTotal(totalSinImpuestos); // Asumiendo IVA 0% para educación
        
        // SRI requiere al menos un total con impuesto (aunque sea 0%)
        TotalImpuesto ti = new TotalImpuesto();
        ti.setCodigo("2"); // IVA
        ti.setCodigoPorcentaje("0"); // 0%
        ti.setBaseImponible(totalSinImpuestos);
        ti.setValor(0);
        infoFact.getTotalConImpuestos().add(ti);
        
        factura.setInfoFactura(infoFact);

        // 3. Generación del XML
        String xmlString = facturaXmlService.generarXml(factura);

        // 4. Firma Electrónica
        byte[] pkcs12 = empresa.getEmpmCertificado();
        String password = CriptografiaUtil.desencriptar(empresa.getEmpmPasswordCertificado());
            
        if (pkcs12 == null || password == null) {
            throw new Exception("Certificado o contraseña no configurados en la empresa.");
        }
            
        byte[] xmlFirmado = xadesSignatureService.firmarDocumento(xmlString.getBytes("UTF-8"), pkcs12, password);

        // 5. Envío al SRI
        boolean esProduccion = empresa.getEmpmAmbiente() == 2;
        RespuestaSolicitud respuestaEnvio = sriWebServiceService.enviarComprobante(xmlFirmado, esProduccion);
        
        if ("RECIBIDA".equals(respuestaEnvio.getEstado())) {
            // 6. Autorización (esperar un momento para que el SRI procese)
            Thread.sleep(3000);
            RespuestaComprobante respuestaAut = sriWebServiceService.autorizarComprobante(claveAcceso, esProduccion);
            
            pago.setPagoClaveAcceso(claveAcceso);
            
            if (!respuestaAut.getAutorizaciones().getAutorizacion().isEmpty()) {
                Autorizacion aut = respuestaAut.getAutorizaciones().getAutorizacion().get(0);
                pago.setPagoEstadoSri(aut.getEstado());
                
                if ("AUTORIZADO".equals(aut.getEstado())) {
                    pago.setPagoXmlAutorizado(xmlFirmado); // Se debería guardar el XML que devuelve el SRI con la autorización, pero por ahora guardamos el firmado
                    
                    // 7. Generar RIDE (PDF)
                    // Nota: Se intenta cargar el .jrxml y el servicio lo compila si es necesario
                    InputStream jrxmlStream = getClass().getResourceAsStream("/reportes/factura.jrxml");
                    InputStream logoStream = (empresa.getEmpmLogo() != null) ? new ByteArrayInputStream(empresa.getEmpmLogo()) : null;
                    
                    if (jrxmlStream != null) {
                        byte[] pdfContent = rideGeneratorService.generarRidePdf(factura, logoStream, jrxmlStream);
                        pago.setPagoPdfRide(pdfContent);
                        
                        // 8. Enviar Notificación
                        String destinatario = pago.getMatricula().getEstudiante().getPersona().getPersCorreoElectronico();
                        notificacionService.enviarComprobante(destinatario, xmlFirmado, pdfContent, secuencial);
                    }
                } else {
                    if (!aut.getMensajes().getMensaje().isEmpty()) {
                        pago.setPagoMensajeSri(aut.getMensajes().getMensaje().get(0).getMensaje());
                    }
                }
            }
            // 9. Actualizar el pago con los resultados del SRI
            pagosDao.actualizarPago(pago);
        } else {
            pago.setPagoEstadoSri("RECHAZADO");
            if (!respuestaEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().isEmpty()) {
                pago.setPagoMensajeSri(respuestaEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje());
            }
            pagosDao.actualizarPago(pago);
            throw new Exception("Error en envío al SRI: " + pago.getPagoMensajeSri());
        }
    }
}
