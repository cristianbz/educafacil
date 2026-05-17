package ec.mileniumtech.educafacil.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RetencionDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion.Impuesto;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion.InfoCompRetencion;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion.InfoTributaria;
import ec.mileniumtech.educafacil.service.sri.autorizacion.Autorizacion;
import ec.mileniumtech.educafacil.service.sri.autorizacion.RespuestaComprobante;
import ec.mileniumtech.educafacil.service.sri.recepcion.RespuestaSolicitud;
import ec.mileniumtech.educafacil.utilitarios.ValidacionUtil;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CriptografiaUtil;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * Orquestador del proceso de Comprobantes de Retención: Generación -> Firma -> Envío -> Autorización.
 */
@Stateless
@LocalBean
public class RetencionIntegracionService {

    @EJB
    private ClaveAccesoGenerator claveAccesoGenerator;

    @EJB
    private RetencionXmlService retencionXmlService;

    @EJB
    private XadesSignatureService xadesSignatureService;

    @EJB
    private SriWebServiceService sriWebServiceService;

    @EJB
    private RetencionDaoImpl retencionDao;

    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;

    @EJB
    private RetencionRideService retencionRideService;

    /**
     * Procesa una retención electrónica completa.
     * 
     * @param retencionEntity Entidad Retencion cargada.
     * @throws Exception Si falla algún paso.
     */
    public void procesarRetencionElectronica(Retencion retencionEntity) throws Exception {
        
        Configuraciones configuraciones = configuracionesDao.findAll().get(0);
        EmpresaMatriz empresa = retencionEntity.getPuntoEmision().getEstablecimientos().getEmpresaMatriz();
        
        SimpleDateFormat sdfSri = new SimpleDateFormat("dd/MM/yyyy");
        String fechaEmisionStr = sdfSri.format(retencionEntity.getFechaEmision());
        
        String[] partesNumero = retencionEntity.getNumero().split("-");
        String estab = String.format("%03d", Integer.parseInt(retencionEntity.getPuntoEmision().getEstablecimientos().getEstaCodigo()));
        String ptoEmi = String.format("%03d", Integer.parseInt(retencionEntity.getPuntoEmision().getCodigo()));
        String secuencial = String.format("%09d", Integer.parseInt(partesNumero[2]));
        
        String serie = estab + ptoEmi;
        int random8Digits = ThreadLocalRandom.current().nextInt(10000000, 100000000);
        
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                retencionEntity.getFechaEmision(), "07", empresa.getEmpmRuc(), 
                empresa.getEmpmAmbiente().toString(), serie, secuencial, 
                String.valueOf(random8Digits), "1");

        // 1. Construcción del objeto JAXB
        ComprobanteRetencion retSri = new ComprobanteRetencion();
        
        // Info Tributaria
        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente(empresa.getEmpmAmbiente().toString());
        infoTrib.setTipoEmision(empresa.getEmpmTipoEmision().toString());
        infoTrib.setRazonSocial(empresa.getEmpmRazonSocial());
        infoTrib.setNombreComercial(empresa.getEmpmNombreComercial());
        infoTrib.setRuc(empresa.getEmpmRuc());
        infoTrib.setClaveAcceso(claveAcceso);
        infoTrib.setCodDoc("07");
        infoTrib.setEstab(estab);
        infoTrib.setPtoEmi(ptoEmi);
        infoTrib.setSecuencial(secuencial);
        infoTrib.setDirMatriz(empresa.getEmpmDireccion());
        retSri.setInfoTributaria(infoTrib);

        // Info Retencion
        InfoCompRetencion infoRet = new InfoCompRetencion();
        infoRet.setFechaEmision(fechaEmisionStr);
        infoRet.setDirEstablecimiento(retencionEntity.getPuntoEmision().getEstablecimientos().getEstaUbicacion());
        infoRet.setObligadoContabilidad(empresa.isEmpmObligadoContabilidad() ? "SI" : "NO");
        
        // Sujeto Retenido (Proveedor)
        String rucProv = retencionEntity.getEgreso().getProveedor().getProvRuc().trim();
        String tipoIdSujeto = rucProv.length() == 13 ? "04" : (rucProv.length() == 10 ? "05" : "06");
        infoRet.setTipoIdentificacionSujetoRetenido(tipoIdSujeto);
        infoRet.setRazonSocialSujetoRetenido(retencionEntity.getEgreso().getProveedor().getProvNombre());
        infoRet.setIdentificacionSujetoRetenido(rucProv);
        infoRet.setPeriodoFiscal(retencionEntity.getEjercicioFiscal());
        retSri.setInfoCompRetencion(infoRet);

        // Detalle de Impuestos
        if (retencionEntity.getDetalles() != null) {
            for (DetalleRetencion dr : retencionEntity.getDetalles()) {
                Impuesto imp = new Impuesto();
                imp.setCodigo(dr.getCodigoImpuesto());
                imp.setCodigoRetencion(dr.getCodigoRetencion());
                imp.setBaseImponible(dr.getBaseImponible().setScale(2, RoundingMode.HALF_UP));
                imp.setPorcentajeRetener(dr.getPorcentaje().setScale(2, RoundingMode.HALF_UP));
                imp.setValorRetenido(dr.getValorRetenido().setScale(2, RoundingMode.HALF_UP));
                imp.setCodDocSustento(dr.getCodigoDocSustento() != null ? dr.getCodigoDocSustento() : "01");
                imp.setNumDocSustento(dr.getNumeroDocSustento().replace("-", ""));
                imp.setFechaEmisionDocSustento(sdfSri.format(dr.getFechaSustento()));
                retSri.getImpuestosList().add(imp);
            }
        }

        retencionEntity.setClaveAcceso(claveAcceso);

        // 2. Generación y Firma
        String xmlString = retencionXmlService.generarXml(retSri);
        System.out.println(xmlString);
        byte[] pkcs12 = empresa.getEmpmCertificado();
        String password = CriptografiaUtil.desencriptar(empresa.getEmpmPasswordCertificado());
            
        byte[] xmlFirmado = xadesSignatureService.firmarDocumento(xmlString.getBytes("UTF-8"), pkcs12, password);        
        retencionEntity.setXmlFirmado(xmlFirmado);
        
        // 3. Envío al SRI
        boolean esProduccion = empresa.getEmpmAmbiente() == 2;
        String urlWsdl = esProduccion ? configuraciones.getConfWsRecepcionProduccion() : configuraciones.getConf_wsRecepcionPruebas();
        
        if (ValidacionUtil.verificarConexion(urlWsdl, 5000)) {
            RespuestaSolicitud respuestaEnvio = sriWebServiceService.enviarComprobante(xmlFirmado, esProduccion, configuraciones);
            
            if ("RECIBIDA".equals(respuestaEnvio.getEstado())) {
                Thread.sleep(2000);
                RespuestaComprobante respuestaAut = sriWebServiceService.autorizarComprobante(claveAcceso, esProduccion, configuraciones);
                
                if (!respuestaAut.getAutorizaciones().getAutorizacion().isEmpty()) {
                    Autorizacion aut = respuestaAut.getAutorizaciones().getAutorizacion().get(0);
                    retencionEntity.setEstado(aut.getEstado());
                    retencionEntity.setNumeroAutorizacion(aut.getNumeroAutorizacion());
                    
                    if ("AUTORIZADO".equals(aut.getEstado())) {
                        retencionEntity.setXmlAutorizado(xmlFirmado);
                        retencionEntity.setFechaAutorizacion(aut.getFechaAutorizacion().toGregorianCalendar().getTime());
                        
                        // 4. Generación de RIDE (PDF)
                        try {
                            InputStream jrxmlStream = getClass().getResourceAsStream("/reportes/retencion.jrxml");
                            InputStream logoStream = (empresa.getEmpmLogo() != null) ? new ByteArrayInputStream(empresa.getEmpmLogo()) : null;
                            
                            if (jrxmlStream != null) {
                                String fechaAutStr = sdfSri.format(retencionEntity.getFechaAutorizacion());
                                byte[] pdfContent = retencionRideService.generarRidePdf(retSri, logoStream, jrxmlStream, retencionEntity.getNumeroAutorizacion(), fechaAutStr);
                                retencionEntity.setPdfRide(pdfContent);
                            }
                        } catch (Exception e) {
                            System.err.println("Error al generar RIDE de retención: " + e.getMessage());
                        }
                        
                    } else if (!aut.getMensajes().getMensaje().isEmpty()) {
                        retencionEntity.setMensajeSri(aut.getMensajes().getMensaje().get(0).getMensaje());
                    }
                }
            } else {
                retencionEntity.setEstado("RECHAZADO");
                if (respuestaEnvio.getComprobantes() != null && !respuestaEnvio.getComprobantes().getComprobante().isEmpty()) {
                    retencionEntity.setMensajeSri(respuestaEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje());
                }
            }
        } else {
            retencionEntity.setEstado("PENDIENTE");
            retencionEntity.setMensajeSri("Error de conexión con el SRI");
        }
        
        retencionDao.actualizarRetencion(retencionEntity);
    }
}
