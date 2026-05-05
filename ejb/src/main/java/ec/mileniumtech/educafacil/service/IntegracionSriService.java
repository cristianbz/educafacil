package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import ec.mileniumtech.educafacil.dao.impl.FacturaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentoElectronico;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura;
import ec.mileniumtech.educafacil.modelo.sri.Factura.Detalle;
import ec.mileniumtech.educafacil.modelo.sri.Factura.InfoFactura;
import ec.mileniumtech.educafacil.modelo.sri.Factura.InfoTributaria;
import ec.mileniumtech.educafacil.modelo.sri.Factura.PagoSRI;
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
 * Refactorizado para utilizar la entidad Factura.
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
    private FacturaDaoImpl facturaDao;

    /**
     * Procesa una factura electrónica completa a partir de la entidad Factura.
     * 
     * @param facturaEntity Entidad Factura con toda la información cargada.
     * @throws Exception Si falla algún paso del proceso.
     */
    public void procesarFacturaElectronica(Factura facturaEntity) throws Exception {
        // 1. Obtener información del emisor y configuración
        EmpresaMatriz empresa = facturaEntity.getPuntoEmision().getEstablecimientos().getEmpresaMatriz();
        if (empresa == null) {
            throw new Exception("La factura no tiene una empresa matriz asociada.");
        }

        // Datos para la clave de acceso
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaEmisionStr = facturaEntity.getFechaEmision().format(dtf);
        
        // El número de factura suele venir como XXX-XXX-XXXXXXXXX
        String[] partesNumero = facturaEntity.getNumero().split("-");
        String estab = String.format("%03d", Integer.parseInt(partesNumero.length > 0 ? partesNumero[0] : "1"));
        String ptoEmi = String.format("%03d", Integer.parseInt(partesNumero.length > 1 ? partesNumero[1] : "1"));
        String secuencial = String.format("%09d", Integer.parseInt(partesNumero.length > 2 ? partesNumero[2] : facturaEntity.getId().toString()));
        
        String serie = estab + ptoEmi;
        
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                new Date(), "01", empresa.getEmpmRuc(), 
                empresa.getEmpmAmbiente().toString(), serie, secuencial, 
                "12345678", "1");

        // 2. Construcción del objeto Factura SRI (JAXB)
        ec.mileniumtech.educafacil.modelo.sri.Factura facturaSri = new ec.mileniumtech.educafacil.modelo.sri.Factura();
        
        // Info Tributaria
        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente(empresa.getEmpmAmbiente().toString());
        infoTrib.setTipoEmision("1");
        infoTrib.setRazonSocial(empresa.getEmpmNombreComercial());
        infoTrib.setNombreComercial(empresa.getEmpmNombreComercial());
        infoTrib.setRuc(empresa.getEmpmRuc());
        infoTrib.setClaveAcceso(claveAcceso);
        infoTrib.setCodDoc("01");
        infoTrib.setEstab(estab);
        infoTrib.setPtoEmi(ptoEmi);
        infoTrib.setSecuencial(secuencial);
        infoTrib.setDirMatriz(empresa.getEmpmDireccion());
        facturaSri.setInfoTributaria(infoTrib);

        // Info Factura
        InfoFactura infoFact = new InfoFactura();
        infoFact.setFechaEmision(fechaEmisionStr);
        infoFact.setDirEstablecimiento(empresa.getEmpmDireccion());
        infoFact.setObligadoContabilidad(empresa.isEmpmObligadoContabilidad() ? "SI" : "NO");
        
        // Mapeo de códigos de identificación del comprador según SRI
        // 04: RUC, 05: Cédula, 06: Pasaporte, 07: Consumidor Final, 08: Exterior
        String tipoIdentComprador = "05"; // Por defecto cédula
        switch (facturaEntity.getCliente().getTipoIdentificacion()) {
            case 1: tipoIdentComprador = "05"; break; // Asumiendo 1 = Cedula
            case 2: tipoIdentComprador = "04"; break; // Asumiendo 2 = RUC
            case 3: tipoIdentComprador = "06"; break; // Pasaporte
            case 4: tipoIdentComprador = "07"; break; // Consumidor Final
            default: tipoIdentComprador = "05";
        }
        infoFact.setTipoIdentificacionComprador(tipoIdentComprador);
        infoFact.setIdentificacionComprador(facturaEntity.getCliente().getNumeroIdentificacion());
        infoFact.setRazonSocialComprador(facturaEntity.getCliente().getNombresCompletos());
        
        infoFact.setTotalSinImpuestos(facturaEntity.getSubtotal().setScale(2, RoundingMode.HALF_UP));
        infoFact.setTotalDescuento(facturaEntity.getDescuentoTotal().setScale(2, RoundingMode.HALF_UP));
        infoFact.setImporteTotal(facturaEntity.getTotal().setScale(2, RoundingMode.HALF_UP));
        
        // Mapeo de Detalles
        if (facturaEntity.getDetalles() != null) {
            for (DetalleFactura df : facturaEntity.getDetalles()) {
                Detalle det = new Detalle();
                det.setCodigoPrincipal(df.getItem() != null ? df.getItem().getId().toString() : "SERV");
                det.setDescripcion(df.getDescripcion());
                BigDecimal cantidadBd = BigDecimal.valueOf(df.getCantidad());
                det.setCantidad(cantidadBd.setScale(2, RoundingMode.HALF_UP));
                det.setPrecioUnitario(df.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP));
                det.setDescuento(df.getDescuento().setScale(2, RoundingMode.HALF_UP));
                det.setPrecioTotalSinImpuesto(df.getPrecioUnitario().multiply(cantidadBd).subtract(df.getDescuento()).setScale(2, RoundingMode.HALF_UP));
                
                // SRI requiere impuestos por detalle
                ec.mileniumtech.educafacil.modelo.sri.Factura.Impuesto impDet = new ec.mileniumtech.educafacil.modelo.sri.Factura.Impuesto();
                impDet.setCodigo("2"); // IVA
                impDet.setCodigoPorcentaje("0"); // 0%
                impDet.setBaseImponible(det.getPrecioTotalSinImpuesto());
                impDet.setTarifa("0");
                impDet.setValor(BigDecimal.ZERO.setScale(2));
                det.getImpuestosList().add(impDet);
                
                facturaSri.getDetallesList().add(det);
            }
        }
        
        // SRI requiere totales con impuestos
        TotalImpuesto ti = new TotalImpuesto();
        ti.setCodigo("2"); // IVA
        ti.setCodigoPorcentaje("0"); // 0%
        ti.setBaseImponible(facturaEntity.getSubtotal().setScale(2, RoundingMode.HALF_UP));
        ti.setValor(facturaEntity.getTotalImpuestos().setScale(2, RoundingMode.HALF_UP));
        infoFact.getTotalConImpuestosList().add(ti);

        // Mapeo de Pagos — REQUERIDO por el SRI
        if (facturaEntity.getFormaPagoFacturas() != null && !facturaEntity.getFormaPagoFacturas().isEmpty()) {
            for (FormaPagoFactura fpf : facturaEntity.getFormaPagoFacturas()) {
                PagoSRI pagoSri = new PagoSRI();
                pagoSri.setFormaPago(fpf.getSriformapagos().getSrfpCodigoSri());
                pagoSri.setTotal(fpf.getValor().setScale(2, RoundingMode.HALF_UP));
                infoFact.getPagosList().add(pagoSri);
            }
        } else {
            PagoSRI pagoDefecto = new PagoSRI();
            pagoDefecto.setFormaPago("01");
            pagoDefecto.setTotal(facturaEntity.getTotal().setScale(2, RoundingMode.HALF_UP));
            infoFact.getPagosList().add(pagoDefecto);
        }

        facturaSri.setInfoFactura(infoFact);

        // Información Adicional
        if (facturaEntity.getCliente().getDireccion() != null) {
            ec.mileniumtech.educafacil.modelo.sri.Factura.CampoAdicional campoDir = new ec.mileniumtech.educafacil.modelo.sri.Factura.CampoAdicional();
            campoDir.setNombre("Direccion");
            campoDir.setValor(facturaEntity.getCliente().getDireccion());
            facturaSri.getInfoAdicionalList().add(campoDir);
        }
        
        if (facturaEntity.getCliente().getCorreo() != null) {
            ec.mileniumtech.educafacil.modelo.sri.Factura.CampoAdicional campoEmail = new ec.mileniumtech.educafacil.modelo.sri.Factura.CampoAdicional();
            campoEmail.setNombre("Email");
            campoEmail.setValor(facturaEntity.getCliente().getCorreo());
            facturaSri.getInfoAdicionalList().add(campoEmail);
        }

        // 3. Inicializar o recuperar DocumentoElectronico
        DocumentoElectronico docElec = facturaEntity.getDocumentoElectronico();
        if (docElec == null) {
            docElec = new DocumentoElectronico();
            docElec.setFactura(facturaEntity);
            facturaEntity.setDocumentoElectronico(docElec);
        }
        docElec.setClaveAcceso(claveAcceso);

        // 4. Generación y Firma
        String xmlString = facturaXmlService.generarXml(facturaSri);
        byte[] pkcs12 = empresa.getEmpmCertificado();
        String password = CriptografiaUtil.desencriptar(empresa.getEmpmPasswordCertificado());
            
        if (pkcs12 == null || password == null) {
            throw new Exception("Certificado o contraseña no configurados en la empresa.");
        }
            
        byte[] xmlFirmado = xadesSignatureService.firmarDocumento(xmlString.getBytes("UTF-8"), pkcs12, password);
        docElec.setXmlFirmado(xmlFirmado);

        // 5. Envío al SRI
        boolean esProduccion = empresa.getEmpmAmbiente() == 2;
        RespuestaSolicitud respuestaEnvio = sriWebServiceService.enviarComprobante(xmlFirmado, esProduccion);
        
        if ("RECIBIDA".equals(respuestaEnvio.getEstado())) {
            Thread.sleep(3000);
            RespuestaComprobante respuestaAut = sriWebServiceService.autorizarComprobante(claveAcceso, esProduccion);
            
            if (!respuestaAut.getAutorizaciones().getAutorizacion().isEmpty()) {
                Autorizacion aut = respuestaAut.getAutorizaciones().getAutorizacion().get(0);
                docElec.setEstado(aut.getEstado());
                docElec.setNumeroAutorizacion(aut.getNumeroAutorizacion());
                
                if ("AUTORIZADO".equals(aut.getEstado())) {
                    docElec.setXmlAutorizadoSri(xmlFirmado); // Idealmente el XML del SRI
                    
                    // 6. Generar RIDE (PDF)
                    InputStream jrxmlStream = getClass().getResourceAsStream("/reportes/factura.jrxml");
                    InputStream logoStream = (empresa.getEmpmLogo() != null) ? new ByteArrayInputStream(empresa.getEmpmLogo()) : null;
                    
                    if (jrxmlStream != null) {
                        byte[] pdfContent = rideGeneratorService.generarRidePdf(facturaSri, logoStream, jrxmlStream);
                        docElec.setPdfRide(pdfContent);
                        
                        // 7. Enviar Notificación
                        String destinatario = facturaEntity.getCliente().getCorreo();
                        notificacionService.enviarComprobante(destinatario, xmlFirmado, pdfContent, secuencial);
                    }
                } else {
                    if (!aut.getMensajes().getMensaje().isEmpty()) {
                        docElec.setMensajeSri(aut.getMensajes().getMensaje().get(0).getMensaje());
                    }
                }
            }
        } else {
            docElec.setEstado("RECHAZADO");
            if (respuestaEnvio.getComprobantes() != null && !respuestaEnvio.getComprobantes().getComprobante().isEmpty()) {
                docElec.setMensajeSri(respuestaEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje());
            }
            facturaDao.actualizarFactura(facturaEntity);
            throw new Exception("Error en envío al SRI: " + docElec.getMensajeSri());
        }
        
        // 8. Actualizar persistencia
        facturaDao.actualizarFactura(facturaEntity);
    }
}
