package ec.mileniumtech.educafacil.service.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.NotaCreditoDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleNotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.Detalle;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.InfoNotaCredito;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.InfoTributaria;
import ec.mileniumtech.educafacil.modelo.sri.NotaCredito.TotalImpuesto;
import ec.mileniumtech.educafacil.service.FacturaXmlService;
import ec.mileniumtech.educafacil.service.RideGeneratorService;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class NotaCreditoSriStrategy implements DocumentoElectronicoStrategy {

    @EJB
    private ClaveAccesoGenerator claveAccesoGenerator;

    @EJB
    private FacturaXmlService facturaXmlService;

    @EJB
    private RideGeneratorService rideGeneratorService;

    @EJB
    private NotaCreditoDaoImpl notaCreditoDao;

    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;

    @Override
    public String getCodigoDocumento() {
        return "04";
    }

    @Override
    public Object construirJaxb(Object entidad, EmpresaMatriz empresa, SriProcessingContext context) throws Exception {
        NotaCredito notaCreditoEntity = (NotaCredito) entidad;
        context.setConfiguraciones(configuracionesDao.findAll().get(0));
        context.setEntidad(entidad);

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
                fechaEmisionDate, getCodigoDocumento(), empresa.getEmpmRuc(),
                empresa.getEmpmAmbiente().toString(), serie, secuencial,
                String.valueOf(random8Digits), "1");
        context.setClaveAcceso(claveAcceso);
        notaCreditoEntity.setClaveAcceso(claveAcceso);

        ec.mileniumtech.educafacil.modelo.sri.NotaCredito ncSri = new ec.mileniumtech.educafacil.modelo.sri.NotaCredito();

        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente(empresa.getEmpmAmbiente().toString());
        infoTrib.setTipoEmision(empresa.getEmpmTipoEmision().toString());
        String razonSocial = (empresa.getEmpmRazonSocial() != null && !empresa.getEmpmRazonSocial().isEmpty())
                ? empresa.getEmpmRazonSocial() : empresa.getEmpmNombreComercial();
        infoTrib.setRazonSocial(razonSocial);
        infoTrib.setNombreComercial(empresa.getEmpmNombreComercial());
        infoTrib.setRuc(empresa.getEmpmRuc());
        infoTrib.setClaveAcceso(claveAcceso);
        infoTrib.setCodDoc(getCodigoDocumento());
        infoTrib.setEstab(estab);
        infoTrib.setPtoEmi(ptoEmi);
        infoTrib.setSecuencial(secuencial);
        infoTrib.setDirMatriz(empresa.getEmpmDireccion());
        ncSri.setInfoTributaria(infoTrib);

        InfoNotaCredito infoNC = new InfoNotaCredito();
        infoNC.setFechaEmision(fechaEmisionStr);
        infoNC.setDirEstablecimiento(notaCreditoEntity.getPuntoEmision().getEstablecimientos().getEstaUbicacion());
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

        infoNC.setCodDocModificado("01");
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
                impDet.setCodigoPorcentaje(mapCodigoIva(empresa.getEmpmPorcentajeIva().intValueExact()));
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

        return ncSri;
    }

    @Override
    public String generarXml(Object jaxbObject) throws Exception {
        return facturaXmlService.generarXmlNotaCredito((ec.mileniumtech.educafacil.modelo.sri.NotaCredito) jaxbObject);
    }

    @Override
    public byte[] generarRide(Object jaxbObject, EmpresaMatriz empresa, SriProcessingContext context) throws Exception {
        ec.mileniumtech.educafacil.modelo.sri.NotaCredito ncSri = (ec.mileniumtech.educafacil.modelo.sri.NotaCredito) jaxbObject;
        InputStream jrxmlStream = getClass().getResourceAsStream(getRutaReporteJrxml());
        InputStream logoStream = (empresa.getEmpmLogo() != null) ? new ByteArrayInputStream(empresa.getEmpmLogo()) : null;
        if (jrxmlStream == null) {
            return null;
        }
        return rideGeneratorService.generarRideNotaCreditoPdf(ncSri, logoStream, jrxmlStream);
    }

    @Override
    public String getRutaReporteJrxml() {
        return "/reportes/notaCredito.jrxml";
    }

    @Override
    public void actualizarEntidad(Object entidad, SriProcessingContext context) {
        NotaCredito nc = (NotaCredito) entidad;
        nc.setEstado(context.getEstadoAutorizacion());
        nc.setNumeroAutorizacion(context.getNumeroAutorizacion());
        nc.setFechaAutorizacionDb(context.getFechaAutorizacion());
        nc.setUrlPdf(context.getUrlPdf());
        nc.setUrlXml(context.getUrlXml());
        if (context.getMensajeSri() != null) {
            nc.setMensajeSri(context.getMensajeSri());
        }
    }

    @Override
    public void persistir(Object entidad) {
        notaCreditoDao.actualizar((NotaCredito) entidad);
    }

    @Override
    public String getEntityIdentifier(Object entidad) {
        NotaCredito nc = (NotaCredito) entidad;
        return nc.getNumero().replace("/", "-");
    }

    private String mapCodigoIva(Integer porcentaje) {
        if (porcentaje == null || porcentaje == 0) return "0";
        if (porcentaje == 12) return "2";
        if (porcentaje == 14) return "3";
        if (porcentaje == 15) return "4";
        return "0";
    }
}
