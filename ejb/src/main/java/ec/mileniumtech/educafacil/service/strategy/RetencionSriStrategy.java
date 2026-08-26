package ec.mileniumtech.educafacil.service.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import ec.mileniumtech.educafacil.dao.ConfiguracionesDao;
import ec.mileniumtech.educafacil.dao.RetencionDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion.Impuesto;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion.InfoCompRetencion;
import ec.mileniumtech.educafacil.modelo.sri.ComprobanteRetencion.InfoTributaria;
import ec.mileniumtech.educafacil.service.RetencionRideService;
import ec.mileniumtech.educafacil.service.RetencionXmlService;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadoDocumentoElectronico;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
@LocalBean
public class RetencionSriStrategy implements DocumentoElectronicoStrategy {

    private static final Logger log = LogManager.getLogger(RetencionSriStrategy.class);


    @EJB
    private ClaveAccesoGenerator claveAccesoGenerator;

    @EJB
    private RetencionXmlService retencionXmlService;

    @EJB
    private RetencionRideService retencionRideService;

    @EJB
    private RetencionDao retencionDao;

    @EJB
    private ConfiguracionesDao configuracionesDao;

    @Override
    public String getCodigoDocumento() {
        return "07";
    }

    @Override
    public Object construirJaxb(Object entidad, EmpresaMatriz empresa, SriProcessingContext context) throws Exception {
        Retencion retencionEntity = (Retencion) entidad;
        context.setConfiguraciones(configuracionesDao.findAll().get(0));
        context.setEntidad(entidad);

        SimpleDateFormat sdfSri = new SimpleDateFormat("dd/MM/yyyy");
        String fechaEmisionStr = sdfSri.format(retencionEntity.getFechaEmision());

        String[] partesNumero = retencionEntity.getNumero().split("-");
        String estab = String.format("%03d", Integer.parseInt(retencionEntity.getPuntoEmision().getEstablecimientos().getEstaCodigo()));
        String ptoEmi = String.format("%03d", Integer.parseInt(retencionEntity.getPuntoEmision().getCodigo()));
        String secuencial = String.format("%09d", Integer.parseInt(partesNumero[2]));
        String serie = estab + ptoEmi;

        String claveAcceso;
        if (retencionEntity.getClaveAcceso() != null && !retencionEntity.getClaveAcceso().isBlank()) {
            claveAcceso = retencionEntity.getClaveAcceso();
        } else {
            int random8Digits = ThreadLocalRandom.current().nextInt(10000000, 100000000);
            claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                    retencionEntity.getFechaEmision(), getCodigoDocumento(), empresa.getEmpmRuc(),
                    empresa.getEmpmAmbiente().toString(), serie, secuencial,
                    String.valueOf(random8Digits), "1");
        }
        context.setClaveAcceso(claveAcceso);
        retencionEntity.setClaveAcceso(claveAcceso);

        ComprobanteRetencion retSri = new ComprobanteRetencion();

        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente(empresa.getEmpmAmbiente().toString());
        infoTrib.setTipoEmision(empresa.getEmpmTipoEmision().toString());
        infoTrib.setRazonSocial(empresa.getEmpmRazonSocial());
        infoTrib.setNombreComercial(empresa.getEmpmNombreComercial());
        infoTrib.setRuc(empresa.getEmpmRuc());
        infoTrib.setClaveAcceso(claveAcceso);
        infoTrib.setCodDoc(getCodigoDocumento());
        infoTrib.setEstab(estab);
        infoTrib.setPtoEmi(ptoEmi);
        infoTrib.setSecuencial(secuencial);
        infoTrib.setDirMatriz(empresa.getEmpmDireccion());
        retSri.setInfoTributaria(infoTrib);

        InfoCompRetencion infoRet = new InfoCompRetencion();
        infoRet.setFechaEmision(fechaEmisionStr);
        infoRet.setDirEstablecimiento(retencionEntity.getPuntoEmision().getEstablecimientos().getEstaUbicacion());
        infoRet.setObligadoContabilidad(empresa.isEmpmObligadoContabilidad() ? "SI" : "NO");

        String rucProv = retencionEntity.getEgreso().getProveedor().getProvRuc().trim();
        String tipoIdSujeto = rucProv.length() == 13 ? "04" : (rucProv.length() == 10 ? "05" : "06");
        infoRet.setTipoIdentificacionSujetoRetenido(tipoIdSujeto);
        infoRet.setRazonSocialSujetoRetenido(retencionEntity.getEgreso().getProveedor().getProvNombre());
        infoRet.setIdentificacionSujetoRetenido(rucProv);
        infoRet.setPeriodoFiscal(retencionEntity.getEjercicioFiscal());
        retSri.setInfoCompRetencion(infoRet);

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

        return retSri;
    }

    @Override
    public String generarXml(Object jaxbObject) throws Exception {
        return retencionXmlService.generarXml((ComprobanteRetencion) jaxbObject);
    }

    @Override
    public byte[] generarRide(Object jaxbObject, EmpresaMatriz empresa, SriProcessingContext context) throws Exception {
        ComprobanteRetencion retSri = (ComprobanteRetencion) jaxbObject;
        InputStream jrxmlStream = getClass().getResourceAsStream(getRutaReporteJrxml());
        InputStream logoStream = (empresa.getEmpmLogo() != null) ? new ByteArrayInputStream(empresa.getEmpmLogo()) : null;
        if (jrxmlStream == null) {
            return null;
        }
        String fechaAutStr = context.getFechaAutorizacion() != null
                ? context.getFechaAutorizacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        return retencionRideService.generarRidePdf(retSri, logoStream, jrxmlStream,
                context.getNumeroAutorizacion(), fechaAutStr);
    }

    @Override
    public String getRutaReporteJrxml() {
        return "/reportes/retencion.jrxml";
    }

    @Override
    public void actualizarEntidad(Object entidad, SriProcessingContext context) {
        Retencion ret = (Retencion) entidad;
        ret.setEstado(context.getEstadoAutorizacion());
        ret.setNumeroAutorizacion(context.getNumeroAutorizacion());
        if (context.getFechaAutorizacion() != null) {
            ret.setFechaAutorizacion(context.getFechaAutorizacion());
        }
        if (context.getMensajeSri() != null) {
            ret.setMensajeSri(context.getMensajeSri());
        }
    }

    @Override
    public void persistir(Object entidad) {
        retencionDao.actualizarRetencion((Retencion) entidad);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void persistirProgreso(Object entidad, SriProcessingContext context) {
        Retencion ret = (Retencion) entidad;
        if (context.getClaveAcceso() == null) {
            return;
        }
        if (ret.getClaveAcceso() == null || ret.getClaveAcceso().isBlank()) {
            ret.setClaveAcceso(context.getClaveAcceso());
        }
        if (context.getXmlFirmado() != null) {
            ret.setXmlFirmado(context.getXmlFirmado());
        }
        if (ret.getEstado() == null || ret.getEstado().isBlank()) {
            ret.setEstado(EnumEstadoDocumentoElectronico.EN_PROCESO.getLabel());
        }
        retencionDao.actualizarRetencion(ret);
    }

    @Override
    public void marcarEnProceso(Object entidad) {
        Retencion ret = (Retencion) entidad;
        ret.setEstado(EnumEstadoDocumentoElectronico.EN_PROCESO.getLabel());
        retencionDao.actualizarRetencion(ret);
    }

    @Override
    public String getEntityIdentifier(Object entidad) {
        Retencion ret = (Retencion) entidad;
        return ret.getNumero().replace("/", "-");
    }
}

