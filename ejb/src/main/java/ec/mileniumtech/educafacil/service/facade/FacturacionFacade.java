package ec.mileniumtech.educafacil.service.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.CatalogoItemDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ClienteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EmpresaMatrizDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EstudianteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FacturaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.NotaCreditoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PuntoEmisionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RetencionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.SriformapagoDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.ComprobanteReporteDto;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentoElectronico;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PagosFacturados;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Sriformapago;
import ec.mileniumtech.educafacil.service.AwsS3Service;
import ec.mileniumtech.educafacil.service.IntegracionSriService;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;

@Stateless
@LocalBean
public class FacturacionFacade {

    @EJB
    private FacturaDaoImpl facturaDao;

    @EJB
    private ClienteDaoImpl clienteDao;

    @EJB
    private CatalogoItemDaoImpl catalogoItemDao;

    @EJB
    private PuntoEmisionDaoImpl puntoEmisionDao;

    @EJB
    private EmpresaMatrizDaoImpl empresaMatrizDao;

    @EJB
    private SriformapagoDaoImpl sriformapagoDao;

    @EJB
    private EstudianteDaoImpl estudianteDao;

    @EJB
    private PersonaDaoImpl personaDao;

    @EJB
    private IntegracionSriService integracionSriService;

    @EJB
    private AwsS3Service awsS3Service;
    
    @EJB
    private NotaCreditoDaoImpl notaCreditoDao;

    @EJB
    private RetencionDaoImpl retencionDao;

    // ========== Factura CRUD ==========

    public Factura guardarFactura(Factura factura) {
        return facturaDao.guardar(factura);
    }

    public List<Factura> listarTodasLasFacturasDelDia() {
        return facturaDao.listarTodasLasFacturasDelDia();
    }

    public Factura buscarFacturaPorId(Integer id) {
        return facturaDao.buscarFacturaPorId(id);
    }
    
    public NotaCredito buscarNotaCreditoporId(Integer id) {
    	return notaCreditoDao.buscarNotaCreditoPorId(id);
    }
    
    public Retencion buscarRetencionporId(Integer id) {
    	return retencionDao.buscarRetencionPorId(id);
    }
    public List<Factura> buscarFacturasPorFiltros(LocalDate fechaInicio, LocalDate fechaFin, String identificacion, String numeroAutorizacion, String estadoAutorizacion) {
        return facturaDao.buscarFacturasPorFiltros(fechaInicio, fechaFin, identificacion, numeroAutorizacion, estadoAutorizacion);
    }

    public void actualizar(Factura factura) {
        facturaDao.actualizar(factura);
    }
    
    public void actualizarNotaCredito(NotaCredito notaCredito) {
    	notaCreditoDao.actualizar(notaCredito);
    }
    
    public void actualizarRetencion(Retencion retencion) {
    	retencionDao.actualizarRetencion(retencion);
    }
    
    public List<ComprobanteReporteDto> buscarComprobantesPorFiltros(
            LocalDate fechaInicio, LocalDate fechaFin, String identificacion,
            String numeroAutorizacion, String estadoAutorizacion, String comprobanteTipo) {
        List<ComprobanteReporteDto> resultado = new ArrayList<>();

        if (comprobanteTipo == null || comprobanteTipo.isEmpty() || "Factura".equals(comprobanteTipo)) {
            List<Factura> facturas = facturaDao.buscarFacturasPorFiltros(
                fechaInicio, fechaFin, identificacion, numeroAutorizacion, estadoAutorizacion);
            for (Factura f : facturas) {
                resultado.add(mapFacturaToDto(f));
            }
        } else if ("Nota de Credito".equals(comprobanteTipo)) {
            List<NotaCredito> notas = notaCreditoDao.buscarNotasCreditoPorFiltros(
                fechaInicio, fechaFin, identificacion, numeroAutorizacion, estadoAutorizacion);
            for (NotaCredito nc : notas) {
                resultado.add(mapNotaCreditoToDto(nc));
            }
        } else if ("Retencion".equals(comprobanteTipo)) {
            List<Retencion> retenciones = retencionDao.buscarRetencionesPorFiltros(
                fechaInicio, fechaFin, identificacion, numeroAutorizacion, estadoAutorizacion);
            for (Retencion r : retenciones) {
                resultado.add(mapRetencionToDto(r));
            }
        }

        return resultado;
    }
    private ComprobanteReporteDto mapFacturaToDto(Factura f) {
        DocumentoElectronico doc = f.getDocumentoElectronico();
        return ComprobanteReporteDto.builder()
            .fechaEmision(f.getFechaEmision())
            .tipoComprobante("Factura")
            .numero(f.getNumero())
            .identificacion(f.getCliente() != null ? f.getCliente().getNumeroIdentificacion() : null)
            .razonSocial(f.getCliente() != null ? f.getCliente().getNombresCompletos() : null)
            .claveAcceso(doc != null ? doc.getClaveAcceso() : null)
            .numeroAutorizacion(doc != null ? doc.getNumeroAutorizacion() : null)
            .total(f.getTotal())
            .estado(doc != null ? doc.getEstado() : null)
            .entityId(f.getId())
            .entityType("Factura")
            .urlXml(doc != null ? doc.getUrlXml() : null)
            .urlPdf(doc != null ? doc.getUrlPdf() : null)
            .correo(f.getCliente() != null ? f.getCliente().getCorreo() : null)
            .build();
    }

    private ComprobanteReporteDto mapNotaCreditoToDto(NotaCredito nc) {
        return ComprobanteReporteDto.builder()
            .fechaEmision(nc.getFechaEmision())
            .tipoComprobante("Nota de Credito")
            .numero(nc.getNumero())
            .identificacion(nc.getCliente() != null ? nc.getCliente().getNumeroIdentificacion() : null)
            .razonSocial(nc.getCliente() != null ? nc.getCliente().getNombresCompletos() : null)
            .claveAcceso(nc.getClaveAcceso())
            .numeroAutorizacion(nc.getNumeroAutorizacion())
            .total(nc.getTotal())
            .estado(nc.getEstado())
            .entityId(nc.getId())
            .entityType("NotaCredito")
            .urlXml(nc.getUrlXml())
            .urlPdf(nc.getUrlPdf())
            .correo(nc.getCliente() != null ? nc.getCliente().getCorreo() : null)
            .build();
    }

    private ComprobanteReporteDto mapRetencionToDto(Retencion r) {
        java.time.LocalDate fechaEmision = null;
        if (r.getFechaEmision() != null) {
            fechaEmision = new java.sql.Date(r.getFechaEmision().getTime()).toLocalDate();
        }
        return ComprobanteReporteDto.builder()
            .fechaEmision(fechaEmision)
            .tipoComprobante("Retencion")
            .numero(r.getNumero())
            .identificacion(r.getEgreso() != null && r.getEgreso().getProveedor() != null
                ? r.getEgreso().getProveedor().getProvRuc() : null)
            .razonSocial(r.getEgreso() != null && r.getEgreso().getProveedor() != null
                ? r.getEgreso().getProveedor().getProvNombre() : null)
            .claveAcceso(r.getClaveAcceso())
            .numeroAutorizacion(r.getNumeroAutorizacion())
            .total(r.getEgreso() != null ? r.getEgreso().getEgreValor() : null)
            .estado(r.getEstado())
            .entityId(r.getId())
            .entityType("Retencion")
            .correo(r.getEgreso() != null && r.getEgreso().getProveedor() != null
                ? r.getEgreso().getProveedor().getProvCorreo() : null)
            .build();
    }

    // ========== Cliente ==========

    public Cliente buscarClientePorIdentificacion(String id) {
        return clienteDao.buscarPorIdentificacion(id);
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteDao.guardar(cliente);
    }

    public Cliente actualizarCliente(Cliente cliente) {
        return clienteDao.actualizar(cliente);
    }

    // ========== Catálogo ==========

    public List<CatalogoItem> listarCatalogoItems() {
        return catalogoItemDao.findAll();
    }

    public CatalogoItem guardarCatalogoItem(CatalogoItem item) {
        return catalogoItemDao.guardar(item);
    }

    public CatalogoItem actualizarCatalogoItem(CatalogoItem item) {
        return catalogoItemDao.actualizar(item);
    }

    // ========== Punto Emisión ==========

    public List<PuntoEmision> listarPuntosEmisionActivos() {
        return puntoEmisionDao.listarPuntosEmisionActivos();
    }

    public PuntoEmision actualizarPuntoEmision(PuntoEmision puem) {
        return puntoEmisionDao.actualizar(puem);
    }

    // ========== Empresa Matriz ==========

    public List<EmpresaMatriz> listaEmpresas() {
        return empresaMatrizDao.listaEmpresas();
    }

    public List<EmpresaMatriz> listarTodasEmpresas() {
        return empresaMatrizDao.findAll();
    }

    // ========== Formas de Pago ==========

    public List<Sriformapago> listarFormasPago() {
        return sriformapagoDao.findAll();
    }

    public Sriformapago buscarFormaPagoPorId(Integer id) {
        return sriformapagoDao.findById(id).orElse(null);
    }

    // ========== Estudiante / Persona ==========

    public Estudiante buscarEstudiantePorCedula(String cedula) {
        return estudianteDao.estudiantesPorCedula(cedula);
    }

    public Persona buscarPersonaPorCedula(String cedula) {
        return personaDao.buscarPersonaPorCedula(cedula);
    }

    // ========== Lógica de Negocio Facturación ==========

    @Transactional
    public void crearFacturaDesdePago(Pagos pago) throws Exception {
        List<PuntoEmision> puntos = puntoEmisionDao.listarPuntosEmisionActivos();
        if (puntos.isEmpty()) {
            throw new Exception("No hay puntos de emisión activos configurados.");
        }
        PuntoEmision puem = puntos.get(0);

        Persona persona = pago.getMatricula().getEstudiante().getPersona();
        Cliente cliente = clienteDao.buscarPorIdentificacion(persona.getPersDocumentoIdentidad());
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setNumeroIdentificacion(persona.getPersDocumentoIdentidad());
            cliente.setTipoIdentificacion(5);
            cliente.setNombresCompletos(persona.getPersApellidos() + " " + persona.getPersNombres());
            cliente.setCorreo(persona.getPersCorreoElectronico());
            cliente.setDireccion(persona.getPersDomicilio() != null ? persona.getPersDomicilio() : "QUITO");
            cliente.setTelefono(persona.getPersTelefonoMobil() != null ? persona.getPersTelefonoMobil() : "0999999999");
            cliente.setEstado(true);
            clienteDao.guardar(cliente);
        }

        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setPuntoEmision(puem);
        factura.setFechaEmision(LocalDate.now());

        int nuevoSecuencial = puem.getSecuencialFactura() + 1;
        String numeroFactura = String.format("%03d-%03d-%09d",
                Integer.parseInt(puem.getEstablecimientos().getEstaCodigo()),
                Integer.parseInt(puem.getCodigo()),
                nuevoSecuencial);
        factura.setNumero(numeroFactura);

        puem.setSecuencialFactura(nuevoSecuencial);
        puntoEmisionDao.actualizar(puem);

        List<DetalleFactura> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        CatalogoItem itemDefecto = catalogoItemDao.buscarPorCodigo("SERV");
        if (itemDefecto == null) {
            List<CatalogoItem> items = catalogoItemDao.findAll();
            if (!items.isEmpty()) {
                itemDefecto = items.get(0);
            }
        }

        for (DetallePagos dp : pago.getDetallePagos()) {
            DetalleFactura df = new DetalleFactura();
            df.setFactura(factura);
            df.setCantidad(1);
            df.setPrecioUnitario(new BigDecimal(dp.getDepaValor()));
            df.setDescuento(BigDecimal.ZERO);
            df.setItem(itemDefecto);
            detalles.add(df);
            subtotal = subtotal.add(df.getPrecioUnitario());
        }

        factura.setDetalles(detalles);
        factura.setSubtotal(subtotal);
        factura.setDescuentoTotal(BigDecimal.ZERO);
        factura.setTotalImpuestos(BigDecimal.ZERO);
        factura.setTotal(subtotal);
        factura.setNotas(pago.getPagoObservacion() != null ? pago.getPagoObservacion() : "Generada desde Pago ID: " + pago.getPagoId());

        List<PagosFacturados> pagosFact = new ArrayList<>();
        PagosFacturados pafa = new PagosFacturados();
        pafa.setFactura(factura);
        pafa.setFecha(LocalDate.now());
        pafa.setMonto(new BigDecimal(pago.getDetallePagos().stream().mapToDouble(DetallePagos::getDepaValor).sum()));
        pafa.setMetodo("01");
        pafa.setReferencia("PAGO-" + pago.getPagoId());
        pagosFact.add(pafa);
        factura.setPagos(pagosFact);

        facturaDao.guardar(factura);

        try {
            integracionSriService.procesarFacturaElectronica(factura);
        } catch (Exception e) {
            System.err.println("Error SRI: " + e.getMessage());
        }
    }

    public void emitirFactura(Integer facturaId, List<InfoAdicionalDto> informacionAdicional) throws Exception {
        Factura factura = facturaDao.buscarFacturaPorId(facturaId);
        if (factura == null) {
            throw new Exception("No se encontró la factura con ID: " + facturaId);
        }
        factura.setListaInfoAdicional(informacionAdicional);
        integracionSriService.procesarFacturaElectronica(factura);
    }

    public void subirDocumentosFacturaAws(Integer facturaId) throws Exception {
        Factura factura = facturaDao.buscarFacturaPorId(facturaId);
        if (factura == null) {
            throw new Exception("No se encontró la factura con ID: " + facturaId);
        }

        ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentoElectronico doc = factura.getDocumentoElectronico();
        if (doc == null) {
            throw new Exception("La factura no tiene un documento electrónico asociado.");
        }
        if (!"AUTORIZADO".equals(doc.getEstado())) {
            throw new Exception("Solo se pueden subir documentos de facturas en estado AUTORIZADO.");
        }

        String numeroFactura = factura.getNumero().replace("/", "-");
        boolean huboError = false;
        StringBuilder errMsg = new StringBuilder();
        
        String documento = "factura";
        String ambiente = factura.getPuntoEmision().getEstablecimientos().getEmpresaMatriz().getEmpmAmbiente() == 2 ? "produccion" : "pruebas";

        if (doc.getPdfRide() != null && doc.getUrlPdf() == null) {
            try {
                String clavePdf = awsS3Service.construirClavePdf(numeroFactura,documento,ambiente);
                awsS3Service.subirArchivo(doc.getPdfRide(), clavePdf, "application/pdf");
                doc.setUrlPdf(clavePdf);
            } catch (Exception e) {
                huboError = true;
                errMsg.append("Error al subir PDF: ").append(e.getMessage()).append(". ");
            }
        }

        if (doc.getXmlAutorizadoSri() != null && doc.getUrlXml() == null) {
            try {
                String claveXml = awsS3Service.construirClaveXml(numeroFactura,documento,ambiente);
                awsS3Service.subirArchivo(doc.getXmlAutorizadoSri(), claveXml, "text/xml");
                doc.setUrlXml(claveXml);
            } catch (Exception e) {
                huboError = true;
                errMsg.append("Error al subir XML: ").append(e.getMessage()).append(". ");
            }
        }

        facturaDao.actualizarFactura(factura);

        if (huboError) {
            throw new Exception("Se completó parcialmente: " + errMsg.toString().trim());
        }
    }
}
