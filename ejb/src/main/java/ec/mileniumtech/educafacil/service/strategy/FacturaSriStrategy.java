package ec.mileniumtech.educafacil.service.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ec.mileniumtech.educafacil.dao.impl.ConfiguracionesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FacturaDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
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
import ec.mileniumtech.educafacil.service.FacturaXmlService;
import ec.mileniumtech.educafacil.service.RideGeneratorService;
import ec.mileniumtech.educafacil.utilitarios.sri.ClaveAccesoGenerator;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class FacturaSriStrategy implements DocumentoElectronicoStrategy {

    @EJB
    private ClaveAccesoGenerator claveAccesoGenerator;

    @EJB
    private FacturaXmlService facturaXmlService;

    @EJB
    private RideGeneratorService rideGeneratorService;

    @EJB
    private FacturaDaoImpl facturaDao;

    @EJB
    private ConfiguracionesDaoImpl configuracionesDao;

    @Override
    public String getCodigoDocumento() {
        return "01";
    }

    @Override
    public Object construirJaxb(Object entidad, EmpresaMatriz empresa, SriProcessingContext context) throws Exception {
        Factura facturaEntity = (Factura) entidad;
        context.setConfiguraciones(configuracionesDao.findAll().get(0));
        context.setEntidad(entidad);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaEmisionStr = facturaEntity.getFechaEmision().format(dtf);

        String[] partesNumero = facturaEntity.getNumero().split("-");
        String estab = String.format("%03d", Integer.parseInt(facturaEntity.getPuntoEmision().getEstablecimientos().getEstaCodigo()));
        String ptoEmi = String.format("%03d", Integer.parseInt(facturaEntity.getPuntoEmision().getCodigo()));
        String secuencial = String.format("%09d", Integer.parseInt(partesNumero.length > 2 ? partesNumero[2] : facturaEntity.getId().toString()));
        String serie = estab + ptoEmi;

        java.util.Date fechaEmisionDate = java.sql.Date.valueOf(facturaEntity.getFechaEmision());
        int random8Digits = ThreadLocalRandom.current().nextInt(10000000, 100000000);
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                fechaEmisionDate, getCodigoDocumento(), empresa.getEmpmRuc(),
                empresa.getEmpmAmbiente().toString(), serie, secuencial,
                String.valueOf(random8Digits), "1");
        context.setClaveAcceso(claveAcceso);

        ec.mileniumtech.educafacil.modelo.sri.Factura facturaSri = new ec.mileniumtech.educafacil.modelo.sri.Factura();

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
        facturaSri.setInfoTributaria(infoTrib);

        InfoFactura infoFact = new InfoFactura();
        infoFact.setFechaEmision(fechaEmisionStr);
        infoFact.setDirEstablecimiento(facturaEntity.getPuntoEmision().getEstablecimientos().getEstaUbicacion());
        infoFact.setObligadoContabilidad(empresa.isEmpmObligadoContabilidad() ? "SI" : "NO");

        String tipoIdentComprador = "05";
        switch (facturaEntity.getCliente().getTipoIdentificacion()) {
            case 1: tipoIdentComprador = "05"; break;
            case 2: tipoIdentComprador = "04"; break;
            case 3: tipoIdentComprador = "06"; break;
            case 4: tipoIdentComprador = "07"; break;
            default: tipoIdentComprador = "05";
        }
        infoFact.setTipoIdentificacionComprador(tipoIdentComprador);
        infoFact.setIdentificacionComprador(facturaEntity.getCliente().getNumeroIdentificacion());
        infoFact.setRazonSocialComprador(facturaEntity.getCliente().getNombresCompletos());

        infoFact.setTotalSinImpuestos(facturaEntity.getSubtotal().subtract(facturaEntity.getDescuentoTotal()).setScale(2, RoundingMode.HALF_UP));
        infoFact.setTotalDescuento(facturaEntity.getDescuentoTotal().setScale(2, RoundingMode.HALF_UP));
        infoFact.setImporteTotal(facturaEntity.getTotal().setScale(2, RoundingMode.HALF_UP));

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

                ec.mileniumtech.educafacil.modelo.sri.Factura.Impuesto impDet = new ec.mileniumtech.educafacil.modelo.sri.Factura.Impuesto();
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
                facturaSri.getDetallesList().add(det);
            }
        }

        TotalImpuesto ti = new TotalImpuesto();
        ti.setCodigo("2");
        ti.setCodigoPorcentaje(mapCodigoIva(empresa.getEmpmPorcentajeIva().intValueExact()));
        ti.setBaseImponible(facturaEntity.getSubtotal().subtract(facturaEntity.getDescuentoTotal()).setScale(2, RoundingMode.HALF_UP));
        ti.setValor(facturaEntity.getTotalImpuestos().setScale(2, RoundingMode.HALF_UP));
        infoFact.getTotalConImpuestosList().add(ti);

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
        if (facturaEntity.getListaInfoAdicional() != null && !facturaEntity.getListaInfoAdicional().isEmpty()) {
            for (InfoAdicionalDto infoado : facturaEntity.getListaInfoAdicional()) {
                ec.mileniumtech.educafacil.modelo.sri.Factura.CampoAdicional campoAD = new ec.mileniumtech.educafacil.modelo.sri.Factura.CampoAdicional();
                campoAD.setNombre(infoado.getNombre());
                campoAD.setValor(infoado.getDescripcion());
                facturaSri.getInfoAdicionalList().add(campoAD);
            }
        }

        return facturaSri;
    }

    @Override
    public String generarXml(Object jaxbObject) throws Exception {
        return facturaXmlService.generarXml((ec.mileniumtech.educafacil.modelo.sri.Factura) jaxbObject);
    }

    @Override
    public byte[] generarRide(Object jaxbObject, EmpresaMatriz empresa, SriProcessingContext context) throws Exception {
        ec.mileniumtech.educafacil.modelo.sri.Factura facturaSri = (ec.mileniumtech.educafacil.modelo.sri.Factura) jaxbObject;
        InputStream jrxmlStream = getClass().getResourceAsStream(getRutaReporteJrxml());
        InputStream logoStream = (empresa.getEmpmLogo() != null) ? new ByteArrayInputStream(empresa.getEmpmLogo()) : null;
        if (jrxmlStream == null) {
            return null;
        }
        Factura factura = (Factura) context.getEntidad();
        List<FormaPagoFactura> formaPagos = factura != null ? factura.getFormaPagoFacturas() : null;
        return rideGeneratorService.generarRidePdf(facturaSri, logoStream, jrxmlStream, formaPagos);
    }

    @Override
    public String getRutaReporteJrxml() {
        return "/reportes/factura.jrxml";
    }

    @Override
    public void actualizarEntidad(Object entidad, SriProcessingContext context) {
        Factura factura = (Factura) entidad;
        if (factura.getDocumentoElectronico() == null) {
            DocumentoElectronico docElec = new DocumentoElectronico();
            docElec.setFactura(factura);
            factura.setDocumentoElectronico(docElec);
        }
        factura.getDocumentoElectronico().setClaveAcceso(context.getClaveAcceso());
        factura.getDocumentoElectronico().setEstado(context.getEstadoAutorizacion());
        factura.getDocumentoElectronico().setNumeroAutorizacion(context.getNumeroAutorizacion());
        factura.getDocumentoElectronico().setFechaAutorizacionDb(context.getFechaAutorizacion());
        factura.getDocumentoElectronico().setUrlPdf(context.getUrlPdf());
        factura.getDocumentoElectronico().setUrlXml(context.getUrlXml());
        if (context.getMensajeSri() != null) {
            factura.getDocumentoElectronico().setMensajeSri(context.getMensajeSri());
        }
    }

    @Override
    public void persistir(Object entidad) {
        facturaDao.actualizarFactura((Factura) entidad);
    }

    @Override
    public String getEntityIdentifier(Object entidad) {
        Factura factura = (Factura) entidad;
        return factura.getNumero().replace("/", "-");
    }

    private String mapCodigoIva(Integer porcentaje) {
        if (porcentaje == null || porcentaje == 0) return "0";
        if (porcentaje == 12) return "2";
        if (porcentaje == 14) return "3";
        if (porcentaje == 15) return "4";
        return "0";
    }
}
