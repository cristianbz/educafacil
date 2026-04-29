package ec.mileniumtech.educafacil.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.ClienteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FacturaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PuntoEmisionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.CatalogoItemDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetallePagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Pagos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PagosFacturados;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;

/**
 * Servicio orquestador para el manejo de la entidad Factura y su integración con el SRI.
 */
@Stateless
@LocalBean
public class FacturacionService {

    @EJB
    private FacturaDaoImpl facturaDao;

    @EJB
    private ClienteDaoImpl clienteDao;

    @EJB
    private PuntoEmisionDaoImpl puntoEmisionDao;

    @EJB
    private CatalogoItemDaoImpl catalogoItemDao;

    @EJB
    private IntegracionSriService integracionSriService;

    /**
     * Crea una factura a partir de un registro de pago y procesa la facturación electrónica.
     * 
     * @param pago Entidad Pagos.
     * @throws Exception Si ocurre un error en el proceso.
     */
    @Transactional
    public void crearFacturaDesdePago(Pagos pago) throws Exception {
        // 1. Obtener Punto de Emisión activo
        List<PuntoEmision> puntos = puntoEmisionDao.listarPuntosEmisionActivos();
        if (puntos.isEmpty()) {
            throw new Exception("No hay puntos de emisión activos configurados.");
        }
        PuntoEmision puem = puntos.get(0);

        // 2. Buscar o crear Cliente
        Persona persona = pago.getMatricula().getEstudiante().getPersona();
        Cliente cliente = clienteDao.buscarPorIdentificacion(persona.getPersDocumentoIdentidad());
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setNumeroIdentificacion(persona.getPersDocumentoIdentidad());
            cliente.setTipoIdentificacion(5); // Cédula por defecto
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
        
        // Generar número secuencial (Formato: EST-PTO-SEC)
        int nuevoSecuencial = puem.getSecuencialFactura() + 1;
        String numeroFactura = String.format("%s-%s-%09d", 
                "001", // Establecimiento (debería venir del punto de emisión)
                puem.getCodigo(), 
                nuevoSecuencial);
        factura.setNumero(numeroFactura);
        
        // Actualizar secuencial en Punto de Emisión
        puem.setSecuencialFactura(nuevoSecuencial);
        puntoEmisionDao.actualizar(puem);

        // 4. Mapear Detalles
        List<DetalleFactura> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Intentar obtener un ítem de catálogo para servicios
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
//            df.setDescripcion(dp.getDepaObservacion() != null ? dp.getDepaObservacion() : "Servicio de Capacitación");
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
        factura.setTotalImpuestos(BigDecimal.ZERO); // Educación suele ser 0%
        factura.setTotal(subtotal);
        factura.setNotas(pago.getPagoObservacion() != null ? pago.getPagoObservacion() : "Generada desde Pago ID: " + pago.getPagoId());

        // 5. Vincular Pago a Factura (PagosFacturados)
        List<PagosFacturados> pagosFact = new ArrayList<>();
        PagosFacturados pafa = new PagosFacturados();
        pafa.setFactura(factura);
        pafa.setFecha(LocalDate.now());
        pafa.setMonto(new BigDecimal(pago.getDetallePagos().stream().mapToDouble(DetallePagos::getDepaValor).sum()));
        pafa.setMetodo("01"); // Sin utilización del sistema financiero por defecto
        pafa.setReferencia("PAGO-" + pago.getPagoId());
        pagosFact.add(pafa);
        factura.setPagos(pagosFact);

        // 6. Persistir Factura
        facturaDao.guardar(factura);
        
        // 7. Disparar Facturación Electrónica
        try {
            integracionSriService.procesarFacturaElectronica(factura);
        } catch (Exception e) {
            // El error en el SRI no debería revertir la creación de la factura en DB
            // pero informamos al usuario
            System.err.println("Error SRI: " + e.getMessage());
        }
    }

    /**
     * Procesa la emisión electrónica de una factura existente.
     * @param facturaId ID de la factura.
     * @throws Exception Si ocurre un error.
     */
    public void emitirFactura(Integer facturaId) throws Exception {
        Factura factura = facturaDao.buscarFacturaPorId(facturaId);
        if (factura == null) {
            throw new Exception("No se encontró la factura con ID: " + facturaId);
        }
        integracionSriService.procesarFacturaElectronica(factura);
    }
}
