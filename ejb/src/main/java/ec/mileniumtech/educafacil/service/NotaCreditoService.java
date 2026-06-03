package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.datatype.XMLGregorianCalendar;

import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.NotaCreditoDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleNotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.Detalle;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.InfoNotaCredito;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.InfoTributaria;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.TotalImpuesto;
import ec.mileniumtech.educafacil.service.sri.autorizacion.Autorizacion;
import ec.mileniumtech.educafacil.service.sri.autorizacion.RespuestaComprobante;
import ec.mileniumtech.educafacil.service.sri.recepcion.RespuestaSolicitud;
import ec.mileniumtech.educafacil.utilitarios.ValidacionUtil;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CriptografiaUtil;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
@Stateless
@LocalBean
public class NotaCreditoService {
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
    private NotaCreditoDaoImpl notaCreditoDao;
    
    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;
    
    @EJB
    private AwsS3Service awsS3Service;
    public void procesarNotaCreditoElectronica(NotaCredito notaCreditoEntity) throws Exception {
    	// Persistir la entidad primero para obtener un ID y evitar inserciones duplicadas luego con merge
    	notaCreditoDao.guardar(notaCreditoEntity);
    	Configuraciones configuraciones = configuracionesDao.findAll().get(0);
        EmpresaMatriz empresa = notaCreditoEntity.getPuntoEmision().getEstablecimientos().getEmpresaMatriz();
        if (empresa == null) {
            throw new Exception("La nota de crédito no tiene una empresa matriz asociada.");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaEmisionStr = notaCreditoEntity.getFechaEmision().format(dtf);
        String fechaSustentoStr = notaCreditoEntity.getFactura().getFechaEmision().format(dtf);
        
        String[] partesNumero = notaCreditoEntity.getNumero().split("-");
        String estab = String.format("%03d", Integer.parseInt(notaCreditoEntity.getPuntoEmision().getEstablecimientos().getEstaCodigo()));
        String ptoEmi = String.format("%03d", Integer.parseInt(notaCreditoEntity.getPuntoEmision().getCodigo()));
        String secuencial = String.format("%09d", Integer.parseInt(partesNumero.length > 2 ? partesNumero[2] : notaCreditoEntity.getId().toString()));
        
        String serie = estab + ptoEmi;
        
        java.util.Date fechaEmisionDate = java.sql.Date.valueOf(notaCreditoEntity.getFechaEmision());
        int random8Digits = ThreadLocalRandom.current().nextInt(10000000, 100000000);
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
        		fechaEmisionDate, "04", empresa.getEmpmRuc(), 
                empresa.getEmpmAmbiente().toString(), serie, secuencial, 
                String.valueOf(random8Digits), "1");
        notaCreditoEntity.setClaveAcceso(claveAcceso);
        ec.mileniumtech.educafacil.modelo.sri.NotaCredito ncSri = new ec.mileniumtech.educafacil.modelo.sri.NotaCredito();
        
        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente(empresa.getEmpmAmbiente().toString());
        infoTrib.setTipoEmision(empresa.getEmpmTipoEmision().toString());
        String razonSocial = (empresa.getEmpmRazonSocial() != null && !empresa.getEmpmRazonSocial().isEmpty()) ? empresa.getEmpmRazonSocial() : empresa.getEmpmNombreComercial();
        infoTrib.setRazonSocial(razonSocial);
        
        String nombreComercial = empresa.getEmpmNombreComercial();
        infoTrib.setNombreComercial(nombreComercial);
        infoTrib.setRuc(empresa.getEmpmRuc());
        infoTrib.setClaveAcceso(claveAcceso);
        infoTrib.setCodDoc("04"); // 04 es Nota de Crédito
        infoTrib.setEstab(estab);
        infoTrib.setPtoEmi(ptoEmi);
        infoTrib.setSecuencial(secuencial);
        String dirMatriz = empresa.getEmpmDireccion();
        infoTrib.setDirMatriz(dirMatriz);
        ncSri.setInfoTributaria(infoTrib);
        InfoNotaCredito infoNC = new InfoNotaCredito();
        infoNC.setFechaEmision(fechaEmisionStr);
        String dirEst = notaCreditoEntity.getPuntoEmision().getEstablecimientos().getEstaUbicacion();
        infoNC.setDirEstablecimiento(dirEst);
        infoNC.setObligadoContabilidad(empresa.isEmpmObligadoContabilidad() ? "SI" : "NO");
        
        String tipoIdentComprador = "05"; 
        switch (notaCreditoEntity.getCliente().getTipoIdentificacion()) {
            case 1: tipoIdentComprador = "05"; break; 
            case 2: tipoIdentComprador = "04"; break; 
            case 3: tipoIdentComprador = "06"; break; 
            case 4: tipoIdentComprador = "07"; break; 
            default: tipoIdentComprador = "05";
        }
        infoNC.setTipoIdentificacionComprador(tipoIdentComprador);
        infoNC.setIdentificacionComprador(notaCreditoEntity.getCliente().getNumeroIdentificacion());
        infoNC.setRazonSocialComprador(notaCreditoEntity.getCliente().getNombresCompletos());
        
        infoNC.setCodDocModificado("01"); // Factura
        infoNC.setNumDocModificado(notaCreditoEntity.getFactura().getNumero());
        infoNC.setFechaEmisionDocSustento(fechaSustentoStr);
        infoNC.setMotivo(notaCreditoEntity.getMotivo());
        
        infoNC.setTotalSinImpuestos(notaCreditoEntity.getSubtotal().setScale(2, RoundingMode.HALF_UP));
        infoNC.setValorModificacion(notaCreditoEntity.getTotal().setScale(2, RoundingMode.HALF_UP));
        
        if (notaCreditoEntity.getDetalles() != null) {
            for (DetalleNotaCredito dnc : notaCreditoEntity.getDetalles()) {
                Detalle det = new Detalle();
                det.setCodigoInterno(dnc.getItem() != null ? dnc.getItem().getId().toString() : "SERV");
                det.setDescripcion(dnc.getDescripcion());
                BigDecimal cantidadBd = BigDecimal.valueOf(dnc.getCantidad());
                det.setCantidad(cantidadBd.setScale(2, RoundingMode.HALF_UP));
                det.setPrecioUnitario(dnc.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP));
                det.setDescuento(dnc.getDescuento().setScale(2, RoundingMode.HALF_UP));
                
                det.setPrecioTotalSinImpuesto(dnc.getPrecioUnitario().multiply(cantidadBd).subtract(dnc.getDescuento()).setScale(2, RoundingMode.HALF_UP));
                
                ec.mileniumtech.educafacil.modelo.sri.NotaCredito.Impuesto impDet = new ec.mileniumtech.educafacil.modelo.sri.NotaCredito.Impuesto();
                impDet.setCodigo("2"); 
                String codPorcDetalle = mapCodigoIva(empresa.getEmpmPorcentajeIva().intValueExact());
                impDet.setCodigoPorcentaje(codPorcDetalle);
                
                impDet.setBaseImponible(det.getPrecioTotalSinImpuesto());
                
                BigDecimal tarifaDetalle = empresa.getEmpmPorcentajeIva() != null ? empresa.getEmpmPorcentajeIva() : BigDecimal.ZERO;
                impDet.setTarifa(tarifaDetalle.toPlainString());
                
                BigDecimal valorIvaDetalle = det.getPrecioTotalSinImpuesto()
                        .multiply(tarifaDetalle)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        
                impDet.setValor(valorIvaDetalle);
                det.getImpuestosList().add(impDet);
                
                ncSri.getDetallesList().add(det);
            }
        }
        
        TotalImpuesto ti = new TotalImpuesto();
        ti.setCodigo("2"); 
        ti.setCodigoPorcentaje(mapCodigoIva(empresa.getEmpmPorcentajeIva().intValueExact()));
        ti.setBaseImponible(notaCreditoEntity.getSubtotal().setScale(2, RoundingMode.HALF_UP));
        ti.setValor(notaCreditoEntity.getTotalImpuestos().setScale(2, RoundingMode.HALF_UP));
        infoNC.getTotalConImpuestosList().add(ti);
        ncSri.setInfoNotaCredito(infoNC);
        if (notaCreditoEntity.getCliente().getDireccion() != null) {
            ec.mileniumtech.educafacil.modelo.sri.NotaCredito.CampoAdicional campoDir = new ec.mileniumtech.educafacil.modelo.sri.NotaCredito.CampoAdicional();
            campoDir.setNombre("Direccion");
            campoDir.setValor(notaCreditoEntity.getCliente().getDireccion());
            ncSri.getInfoAdicionalList().add(campoDir);
        }
        
        if (notaCreditoEntity.getCliente().getCorreo() != null) {
            ec.mileniumtech.educafacil.modelo.sri.NotaCredito.CampoAdicional campoEmail = new ec.mileniumtech.educafacil.modelo.sri.NotaCredito.CampoAdicional();
            campoEmail.setNombre("Email");
            campoEmail.setValor(notaCreditoEntity.getCliente().getCorreo());
            ncSri.getInfoAdicionalList().add(campoEmail);
        }
        String xmlString = facturaXmlService.generarXmlNotaCredito(ncSri);
        byte[] pkcs12 = empresa.getEmpmCertificado();
        String password = CriptografiaUtil.desencriptar(empresa.getEmpmPasswordCertificado());
            
        if (pkcs12 == null || password == null) {
            throw new Exception("Certificado o contraseña no configurados en la empresa.");
        }
            
        byte[] xmlFirmado = xadesSignatureService.firmarDocumento(xmlString.getBytes("UTF-8"), pkcs12, password);
//        notaCreditoEntity.setXmlFirmado(xmlFirmado);
        
        boolean esProduccion = empresa.getEmpmAmbiente() == 2;
        String urlWsdl = esProduccion ? configuraciones.getConfWsRecepcionProduccion() : configuraciones.getConf_wsRecepcionPruebas();
        
        if (!ValidacionUtil.verificarConexion(urlWsdl, 5000)) {
            throw new Exception("No se pudo establecer comunicación con los servidores del SRI.");
        }
        RespuestaSolicitud respuestaEnvio;
        try {
            respuestaEnvio = sriWebServiceService.enviarComprobante(xmlFirmado, esProduccion, configuraciones);
        } catch (Exception e) {
            throw new Exception("Error al comunicar con el SRI: " + e.getMessage());
        }        
        if ("RECIBIDA".equals(respuestaEnvio.getEstado())) {
            Thread.sleep(3000);
            RespuestaComprobante respuestaAut = sriWebServiceService.autorizarComprobante(claveAcceso, esProduccion,configuraciones);
            
            if (!respuestaAut.getAutorizaciones().getAutorizacion().isEmpty()) {
                Autorizacion aut = respuestaAut.getAutorizaciones().getAutorizacion().get(0);
                notaCreditoEntity.setEstado(aut.getEstado());
                notaCreditoEntity.setNumeroAutorizacion(aut.getNumeroAutorizacion());
                notaCreditoEntity.setFechaAutorizacionDb(convertir( aut.getFechaAutorizacion()));
                
                if ("AUTORIZADO".equals(aut.getEstado())) {
//                    notaCreditoEntity.setXmlAutorizadoSri(xmlFirmado); 
                    
                    InputStream jrxmlStream = getClass().getResourceAsStream("/reportes/notaCredito.jrxml");
                    InputStream logoStream = (empresa.getEmpmLogo() != null) ? new ByteArrayInputStream(empresa.getEmpmLogo()) : null;
                    
                    if (jrxmlStream != null) {
                        byte[] pdfContent = rideGeneratorService.generarRideNotaCreditoPdf(ncSri, logoStream, jrxmlStream);
//                        notaCreditoEntity.setPdfRide(pdfContent);
                        
                        try {
                            String numeroNc = notaCreditoEntity.getNumero().replace("/", "-");
                            
                            String clavePdf = awsS3Service.construirClavePdf(numeroNc);
                            awsS3Service.subirArchivo(pdfContent, clavePdf, "application/pdf");
                            notaCreditoEntity.setUrlPdf(clavePdf);
                            
                            String claveXml = awsS3Service.construirClaveXml(numeroNc);
                            awsS3Service.subirArchivo(xmlFirmado, claveXml, "text/xml");
                            notaCreditoEntity.setUrlXml(claveXml);
                            
                        } catch (Exception e3) {
                            System.err.println("[AwsS3] Error al subir documentos a S3: " + e3.getMessage());
                        }
                        
                        notaCreditoDao.actualizar(notaCreditoEntity);
                        
                        String destinatario = notaCreditoEntity.getCliente().getCorreo();
                        notificacionService.enviarComprobante(destinatario, xmlFirmado, pdfContent, secuencial);
                    }
                } else {
                    if (!aut.getMensajes().getMensaje().isEmpty()) {
                        notaCreditoEntity.setMensajeSri(aut.getMensajes().getMensaje().get(0).getMensaje());
                    }
                }
            }
        } else {
            notaCreditoEntity.setEstado("RECHAZADO");
            if (respuestaEnvio.getComprobantes() != null && !respuestaEnvio.getComprobantes().getComprobante().isEmpty()) {
                notaCreditoEntity.setMensajeSri(respuestaEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje());
            }
            notaCreditoDao.actualizar(notaCreditoEntity);
            throw new Exception("Error en envío al SRI: " + notaCreditoEntity.getMensajeSri());
        }
        
        notaCreditoDao.actualizar(notaCreditoEntity);
    }
    private String mapCodigoIva(Integer porcentaje) {
        if (porcentaje == null || porcentaje == 0) return "0";
        if (porcentaje == 12) return "2";
        if (porcentaje == 14) return "3";
        if (porcentaje == 15) return "4";
        return "0"; 
    }
    
    public OffsetDateTime convertir(XMLGregorianCalendar xmlDate) {
        if (xmlDate == null) return null;
        return xmlDate.toGregorianCalendar().toZonedDateTime().toOffsetDateTime();
    }
}

