package ec.mileniumtech.educafacil.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.ClienteDao;
import ec.mileniumtech.educafacil.dao.FacturaDao;
import ec.mileniumtech.educafacil.dao.PuntoEmisionDao;
import ec.mileniumtech.educafacil.dao.CatalogoItemDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PagosFacturados;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio orquestador para el manejo de la entidad Factura y su integraciÃ³n con el SRI.
 */
@Stateless
@LocalBean
public class FacturacionService {

    private static final Logger log = LogManager.getLogger(FacturacionService.class);

    @EJB
    private FacturaDao facturaDao;

    @EJB
    private ClienteDao clienteDao;

    @EJB
    private PuntoEmisionDao puntoEmisionDao;

    @EJB
    private CatalogoItemDao catalogoItemDao;

    @EJB
    private IntegracionSriService integracionSriService;
    
    @EJB
    private AwsS3Service awsS3Service;

    /**
     * Crea una factura a partir de un registro de pago y procesa la facturaciÃ³n electrÃ³nica.
     * 
     * @param pago Entidad Pagos.
     * @throws Exception Si ocurre un error en el proceso.
     */
    @Transactional
    public void crearFacturaDesdePago(Pagos pago) throws Exception {
        // 1. Obtener Punto de EmisiÃ³n activo
        List<PuntoEmision> puntos = puntoEmisionDao.listarPuntosEmisionActivos();
        if (puntos.isEmpty()) {
            throw new BusinessException("No hay puntos de emisiÃ³n activos configurados.", "BIZ-FACT-NO-PUNTO");
        }
        PuntoEmision puem = puntos.get(0);

        // 2. Buscar o crear Cliente
        Persona persona = pago.getMatricula().getEstudiante().getPersona();
        Cliente cliente = clienteDao.buscarPorIdentificacion(persona.getPersDocumentoIdentidad());
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setNumeroIdentificacion(persona.getPersDocumentoIdentidad());
            cliente.setTipoIdentificacion(5); // CÃ©dula por defecto
            cliente.setNombresCompletos(persona.getPersApellidos() + " " + persona.getPersNombres());
            cliente.setCorreo(persona.getPersCorreoElectronico());
            cliente.setDireccion(persona.getPersDomicilio() != null ? persona.getPersDomicilio() : "QUITO");
            cliente.setTelefono(persona.getPersTelefonoMobil() != null ? persona.getPersTelefonoMobil() : "0999999999");
            cliente.setEstado(true);
            clienteDao.guardar(cliente);
        }

        // 3. Crear cabecera de Factura
        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setPuntoEmision(puem);
        factura.setFechaEmision(LocalDate.now());
        
        // Generar nÃºmero secuencial (Formato: EST-PTO-SEC)
        int nuevoSecuencial = puem.getSecuencialFactura() + 1;
        String numeroFactura = String.format("%03d-%03d-%09d", 
                Integer.parseInt(puem.getEstablecimientos().getEstaCodigo()), 
                Integer.parseInt(puem.getCodigo()), 
                nuevoSecuencial);
        factura.setNumero(numeroFactura);
        
        // Actualizar secuencial en Punto de EmisiÃ³n
        puem.setSecuencialFactura(nuevoSecuencial);
        puntoEmisionDao.actualizar(puem);

        // 4. Mapear Detalles
        List<DetalleFactura> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Intentar obtener un Ã­tem de catÃ¡logo para servicios
        CatalogoItem itemDefecto = catalogoItemDao.buscarPorCodigo("SERV");
        if (itemDefecto == null) {
            // Si no existe, usamos el primero que encontremos o creamos uno ficticio para el ejemplo
            List<CatalogoItem> items = catalogoItemDao.findAll();
            if (!items.isEmpty()) {
                itemDefecto = items.get(0);
            }
        }
        
        for (DetallePagos dp : pago.getDetallePagos()) {
            DetalleFactura df = new DetalleFactura();
            df.setFactura(factura);
            df.setCantidad(1);
            df.setPrecioUnitario(dp.getDepaValor());
            df.setDescuento(BigDecimal.ZERO);
            df.setItem(itemDefecto);
            detalles.add(df);
            
            subtotal = subtotal.add(df.getPrecioUnitario());
        }

        factura.setDetalles(detalles);
        factura.setSubtotal(subtotal);
        factura.setDescuentoTotal(BigDecimal.ZERO);
        factura.setTotalImpuestos(BigDecimal.ZERO); // EducaciÃ³n suele ser 0%
        factura.setTotal(subtotal);
        factura.setNotas(pago.getPagoObservacion() != null ? pago.getPagoObservacion() : "Generada desde Pago ID: " + pago.getPagoId());

        // 5. Vincular Pago a Factura (PagosFacturados)
        List<PagosFacturados> pagosFact = new ArrayList<>();
        PagosFacturados pafa = new PagosFacturados();
        pafa.setFactura(factura);
        pafa.setFecha(LocalDate.now());
        pafa.setMonto(pago.getDetallePagos().stream()
                .map(DetallePagos::getDepaValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        pafa.setMetodo("01"); // Sin utilizaciÃ³n del sistema financiero por defecto
        pafa.setReferencia("PAGO-" + pago.getPagoId());
        pagosFact.add(pafa);
        factura.setPagos(pagosFact);

        // 6. Persistir Factura
        facturaDao.guardar(factura);
        
        // 7. Disparar FacturaciÃ³n ElectrÃ³nica
        try {
            integracionSriService.procesarFacturaElectronica(factura);
        } catch (Exception e) {
            log.error("Error al procesar factura electrÃ³nica para facturaId={}. La factura se creÃ³ en BD pero no se emitiÃ³ electrÃ³nicamente.", factura.getId(), e);
        }
    }

    /**
     * Procesa la emisiÃ³n electrÃ³nica de una factura existente.
     * @param facturaId ID de la factura.
     * @throws Exception Si ocurre un error.
     */
    public void emitirFactura(Integer facturaId,List<InfoAdicionalDto> informacionAdicional) throws Exception {
        Factura factura = facturaDao.buscarFacturaPorId(facturaId);
        if (factura == null) {
            throw new BusinessException("No se encontrÃ³ la factura con ID: " + facturaId, "BIZ-FACT-NOT-FOUND");
        }
        factura.setListaInfoAdicional(informacionAdicional);
        integracionSriService.procesarFacturaElectronica(factura);
    }

    /**
     * Sube manualmente los documentos (PDF y XML) de una factura ya autorizada a AWS S3.
     * Ãštil para migrar facturas existentes que tienen bytes en BD pero no tienen URLs de S3.
     *
     * @param facturaId ID de la factura a migrar.
     * @throws Exception Si no se puede subir alguno de los documentos.
     */
    public void subirDocumentosFacturaAws(Integer facturaId) throws Exception {
        Factura factura = facturaDao.buscarFacturaPorId(facturaId);
        if (factura == null) {
            throw new BusinessException("No se encontrÃ³ la factura con ID: " + facturaId, "BIZ-FACT-NOT-FOUND");
        }

        ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentoElectronico doc = factura.getDocumentoElectronico();
        if (doc == null) {
            throw new BusinessException("La factura no tiene un documento electrÃ³nico asociado.", "BIZ-FACT-NO-DOC");
        }
        if (!"AUTORIZADO".equals(doc.getEstado())) {
            throw new BusinessException("Solo se pueden subir documentos de facturas en estado AUTORIZADO.", "BIZ-FACT-NOT-AUT");
        }

        String numeroFactura = factura.getNumero().replace("/", "-");
        boolean huboError = false;
        StringBuilder errMsg = new StringBuilder();

        String documento = "factura";
        String ambiente = factura.getPuntoEmision().getEstablecimientos().getEmpresaMatriz().getEmpmAmbiente() == 2 ? "produccion" : "pruebas";

        // Subir PDF si existe en BD
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

        // Subir XML si existe en BD
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
            throw new BusinessException("Se completÃ³ parcialmente: " + errMsg.toString().trim(), "BIZ-FACT-PARTIAL");
        }
    }
}

