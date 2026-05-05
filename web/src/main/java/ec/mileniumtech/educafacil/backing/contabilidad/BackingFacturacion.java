package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanFacturacion;
import ec.mileniumtech.educafacil.dao.impl.CatalogoItemDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ClienteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EstudianteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FacturaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PuntoEmisionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EmpresaMatrizDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentoElectronico;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.service.FacturacionService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean para la gestión de facturación electrónica en la UI.
 */
@Named("backingFacturacion")
@ViewScoped
public class BackingFacturacion implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(BackingFacturacion.class);

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
    private ec.mileniumtech.educafacil.dao.impl.SriformapagoDaoImpl sriformapagoDao;

    @EJB
    private EstudianteDaoImpl estudianteDao;

    @EJB
    private PersonaDaoImpl personaDao;

    @EJB
    private FacturacionService facturacionService;

    @Inject
    @Getter
    private BeanFacturacion beanFacturacion;

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @PostConstruct
    public void init() {
        try {
            cargarFacturas();
            prepararNuevaFactura();            
            getBeanFacturacion().setListaItems(catalogoItemDao.findAll());
            getBeanFacturacion().setListaFormasPagoSri(sriformapagoDao.findAll());
        } catch (Exception e) {
            log.error("Error en init de BackingFacturacion", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Error al inicializar la página: " + e.getMessage());
        }
    }

    /**
     * Prepara los objetos para una nueva factura.
     */
    public void prepararNuevaFactura() {
        getBeanFacturacion().setNuevaFactura(new Factura());
        getBeanFacturacion().getNuevaFactura().setFechaEmision(LocalDate.now());
        getBeanFacturacion().setClienteSeleccionado(null);
        getBeanFacturacion().setListaDetallesNueva(new ArrayList<>());
        getBeanFacturacion().setDetalleNuevo(new DetalleFactura());
        getBeanFacturacion().setIdentificacionBusqueda("");
        getBeanFacturacion().getDetalleNuevo().setCantidad(0);
        getBeanFacturacion().setMostrarFormularioNuevoCliente(false);
        getBeanFacturacion().setListaFormasPagoAgregadas(new ArrayList<>());
        getBeanFacturacion().setNuevaFormaPago(new ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura());
        getBeanFacturacion().setIdFormaPagoSeleccionada(null);
    }

    /**
     * Busca un cliente por identificación con la siguiente lógica:
     * 1. Busca en Estudiante.
     * 2. Busca en Cliente.
     * 3. Busca en Persona (fallback).
     * 4. Si no existe, permite creación manual.
     */
    public void buscarCliente() {
        try {
            String id = getBeanFacturacion().getIdentificacionBusqueda();
            if (id == null || id.trim().isEmpty()) return;

            getBeanFacturacion().setMostrarFormularioNuevoCliente(false);
            
            // 1. Buscar en Cliente (Directo)
            Cliente c = clienteDao.buscarPorIdentificacion(id);
            if (c != null) {
                getBeanFacturacion().setClienteSeleccionado(c);
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Cliente Encontrado", c.getNombresCompletos());
                return;
            }

            // 2. Buscar en Estudiante
            Estudiante e = estudianteDao.estudiantesPorCedula(id);
            if (e != null && e.getPersona() != null) {
                prepararNuevoClienteDesdePersona(e.getPersona());
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Datos recuperados de Estudiante", getBeanFacturacion().getClienteSeleccionado().getNombresCompletos());
                return;
            }

            // 3. Buscar en Persona (General)
            Persona p = personaDao.buscarPersonaPorCedula(id);
            if (p != null) {
                prepararNuevoClienteDesdePersona(p);
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Datos recuperados de Persona", getBeanFacturacion().getClienteSeleccionado().getNombresCompletos());
                return;
            }

            // 4. No existe en ningún lado
            Cliente nuevo = new Cliente();
            nuevo.setNumeroIdentificacion(id);
            nuevo.setTipoIdentificacion(1); // Default Cédula
            nuevo.setEstado(true);
            getBeanFacturacion().setClienteSeleccionado(nuevo);
            getBeanFacturacion().setMostrarFormularioNuevoCliente(true);
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Identificación no encontrada. Ingrese los datos manualmente.");

        } catch (Exception e) {
            log.error("Error al buscar cliente", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error en la búsqueda: " + e.getMessage());
        }
    }

    private void prepararNuevoClienteDesdePersona(Persona p) {
        Cliente nuevo = new Cliente();
        nuevo.setNumeroIdentificacion(p.getPersDocumentoIdentidad());
        nuevo.setNombresCompletos(p.getPersApellidos() + " " + p.getPersNombres());
        nuevo.setCorreo(p.getPersCorreoElectronico());
        nuevo.setTelefono(p.getPersTelefonoMobil() != null ? p.getPersTelefonoMobil() : p.getPersTelefonoCasa());
        nuevo.setDireccion(p.getPersDomicilio());
        nuevo.setTipoIdentificacion(1); // Asumimos cédula por defecto
        nuevo.setEstado(true);
        
        getBeanFacturacion().setClienteSeleccionado(nuevo);
        getBeanFacturacion().setMostrarFormularioNuevoCliente(true); // Permitir revisar/completar datos
    }

    /**
     * Prepara el objeto para un nuevo ítem de catálogo.
     */
    public void prepararNuevoItem() {
        getBeanFacturacion().setNuevoItem(new CatalogoItem());
        getBeanFacturacion().getNuevoItem().setPrecio(BigDecimal.ZERO);
        getBeanFacturacion().getNuevoItem().setTipo("BIEN"); // Default
    }

    /**
     * Guarda el nuevo ítem en la base de datos.
     */
    public void guardarNuevoItem() {
        try {
            log.info("Iniciando guardado de nuevo ítem...");
            CatalogoItem item = getBeanFacturacion().getNuevoItem();
            if (item.getCodigo() == null || item.getCodigo().isEmpty()) throw new Exception("El código es obligatorio.");
            if (item.getNombre() == null || item.getNombre().isEmpty()) throw new Exception("El nombre es obligatorio.");
            
            catalogoItemDao.guardar(item);
            
            // Actualizar lista y seleccionar el nuevo ítem
            getBeanFacturacion().setListaItems(catalogoItemDao.findAll());
            getBeanFacturacion().setItemSeleccionado(item);
            
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Ítem creado correctamente.");
            Mensaje.ocultarDialogo("dlgNuevoItem");
            
        } catch (Exception e) {
            log.error("Error al guardar nuevo ítem", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Agrega un ítem a la lista de detalles de la nueva factura.
     */
    public void agregarDetalle() {
        if (getBeanFacturacion().getItemSeleccionado() != null) {
            DetalleFactura df = getBeanFacturacion().getDetalleNuevo();
            df.setItem(getBeanFacturacion().getItemSeleccionado());
            df.setFactura(getBeanFacturacion().getNuevaFactura());
            df.setDescripcion(df.getItem().getDescripcion());
            if (df.getDescuento() == null) {
                df.setDescuento(BigDecimal.ZERO);
            }
            
            List<EmpresaMatriz> empresas = empresaMatrizDao.listaEmpresas();
            if (empresas != null && !empresas.isEmpty()) {
                df.setImpuestoIva(empresas.get(0).getEmpmPorcentajeIva());
            } else {
                df.setImpuestoIva(0);
            }
            
            // Si el precio viene del ítem
            if (df.getPrecioUnitario() == null || df.getPrecioUnitario().compareTo(BigDecimal.ZERO) == 0) {
                df.setPrecioUnitario(getBeanFacturacion().getItemSeleccionado().getPrecio());
            }
            
            getBeanFacturacion().getListaDetallesNueva().add(df);
            getBeanFacturacion().setDetalleNuevo(new DetalleFactura());
            calcularTotales();
        }
    }

    /**
     * Calcula los totales de la nueva factura.
     */
    public void calcularTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        BigDecimal totalImpuestos = BigDecimal.ZERO;

        for (DetalleFactura df : getBeanFacturacion().getListaDetallesNueva()) {
            BigDecimal cant = new BigDecimal(df.getCantidad());
            BigDecimal precio = df.getPrecioUnitario() != null ? df.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal desc = df.getDescuento() != null ? df.getDescuento() : BigDecimal.ZERO;
            
            BigDecimal subtotalItem = precio.multiply(cant).subtract(desc);
            subtotal = subtotal.add(precio.multiply(cant));
            descuentoTotal = descuentoTotal.add(desc);
            
            if (df.getImpuestoIva() != null && df.getImpuestoIva() > 0) {
                BigDecimal iva = new BigDecimal(df.getImpuestoIva()).divide(new BigDecimal(100));
                totalImpuestos = totalImpuestos.add(subtotalItem.multiply(iva));
            }
        }
        
        getBeanFacturacion().getNuevaFactura().setSubtotal(subtotal);
        getBeanFacturacion().getNuevaFactura().setDescuentoTotal(descuentoTotal);
        getBeanFacturacion().getNuevaFactura().setTotalImpuestos(totalImpuestos);
        getBeanFacturacion().getNuevaFactura().setTotal(subtotal.subtract(descuentoTotal).add(totalImpuestos));
    }
    
    public void removerDetalle(DetalleFactura det) {
        getBeanFacturacion().getListaDetallesNueva().remove(det);
        calcularTotales();
    }
    
    public void agregarFormaPago() {
        try {
            Integer idFp = getBeanFacturacion().getIdFormaPagoSeleccionada();
            if (idFp == null || idFp == 0) throw new Exception("Debe seleccionar una forma de pago.");
            
            ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura fp = getBeanFacturacion().getNuevaFormaPago();
            if (fp.getValor() == null || fp.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("El valor debe ser mayor a cero.");
            }
            
            ec.mileniumtech.educafacil.modelo.persistencia.entity.Sriformapago sriFp = sriformapagoDao.findById(idFp).orElse(null);
            if (sriFp == null) throw new Exception("Forma de pago no encontrada.");
            
            fp.setSriformapagos(sriFp);
            
            getBeanFacturacion().getListaFormasPagoAgregadas().add(fp);
            
            getBeanFacturacion().setNuevaFormaPago(new ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura());
            getBeanFacturacion().setIdFormaPagoSeleccionada(null);
            
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Forma de pago agregada.");
        } catch (Exception e) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void removerFormaPago(ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura fp) {
        getBeanFacturacion().getListaFormasPagoAgregadas().remove(fp);
        Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Forma de pago removida.");
    }

    /**
     * Guarda la nueva factura y procesa la emisión electrónica.
     */
    public void guardarFactura() {
        try {
            if (getBeanFacturacion().getClienteSeleccionado() == null) {
                throw new Exception("Debe seleccionar un cliente.");
            }
            if (getBeanFacturacion().getListaDetallesNueva().isEmpty()) {
                throw new Exception("Debe agregar al menos un detalle.");
            }
            if (getBeanFacturacion().getListaFormasPagoAgregadas().isEmpty()) {
                throw new Exception("Debe agregar al menos una forma de pago.");
            }

            BigDecimal totalFactura = getBeanFacturacion().getNuevaFactura().getTotal();
            BigDecimal totalPagos = BigDecimal.ZERO;
            for (ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura fp : getBeanFacturacion().getListaFormasPagoAgregadas()) {
                totalPagos = totalPagos.add(fp.getValor());
            }

            if (totalPagos.compareTo(totalFactura) != 0) {
                throw new Exception("La suma de las formas de pago (" + totalPagos + ") no coincide con el total de la factura (" + totalFactura + ").");
            }

            Factura f = getBeanFacturacion().getNuevaFactura();
            Cliente c = getBeanFacturacion().getClienteSeleccionado();
            
            // Si el cliente no existe en la base de datos (ID nulo), lo guardamos primero
            if (c.getId() == null) {
                clienteDao.guardar(c);
            }
            
            f.setCliente(c);
            f.setDetalles(getBeanFacturacion().getListaDetallesNueva());
            
            List<ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura> formasPago = new ArrayList<>();
            for (ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura fp : getBeanFacturacion().getListaFormasPagoAgregadas()) {
                fp.setFactura(f);
                formasPago.add(fp);
            }
            f.setFormaPagoFacturas(formasPago);
            
            // Obtener Punto de Emisión
            List<PuntoEmision> puntos = puntoEmisionDao.listarPuntosEmisionActivos();
            if (puntos.isEmpty()) throw new Exception("No hay puntos de emisión activos.");
            PuntoEmision puem = puntos.get(0);
            f.setPuntoEmision(puem);
            
            // Secuencial
            int nuevoSec = puem.getSecuencialFactura() + 1;
            f.setNumero(String.format("001-%s-%09d", puem.getCodigo(), nuevoSec));
            puem.setSecuencialFactura(nuevoSec);
            puntoEmisionDao.actualizar(puem);
            
            f.setTotalImpuestos(BigDecimal.ZERO);
            f.setDescuentoTotal(BigDecimal.ZERO);

            facturaDao.guardar(f);
            
            // Emitir electrónicamente
            facturacionService.emitirFactura(f.getId());
            
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Factura generada y enviada al SRI.");
            cargarFacturas();
            prepararNuevaFactura();
            Mensaje.ocultarDialogo("dlgNuevaFactura");
            
        } catch (Exception e) {
            log.error("Error al guardar factura", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Carga la lista de facturas desde la base de datos.
     */
    public void cargarFacturas() {
        try {
            getBeanFacturacion().setListaFacturas(facturaDao.listarTodasLasFacturas());
        } catch (Exception e) {
            log.error("Error al cargar facturas", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar las facturas.");
        }
    }

    /**
     * Procesa la emisión electrónica de una factura seleccionada.
     * @param factura Factura a emitir.
     */
    public void emitirFactura(Factura factura) {
        try {
            facturacionService.emitirFactura(factura.getId());
            cargarFacturas(); // Refrescar lista para ver cambios en estado
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Proceso de facturación iniciado.");
        } catch (Exception e) {
            log.error("Error al emitir factura", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Genera la descarga del archivo RIDE (PDF) de la factura.
     * @param factura Factura seleccionada.
     * @return StreamedContent para la descarga.
     */
    public StreamedContent descargarRide(Factura factura) {
        DocumentoElectronico doc = factura.getDocumentoElectronico();
        if (doc != null && doc.getPdfRide() != null) {
            return DefaultStreamedContent.builder()
                    .name("RIDE_" + factura.getNumero() + ".pdf")
                    .contentType("application/pdf")
                    .stream(() -> new ByteArrayInputStream(doc.getPdfRide()))
                    .build();
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El RIDE no está disponible para esta factura.");
            return null;
        }
    }

    /**
     * Genera la descarga del archivo XML de la factura.
     * @param factura Factura seleccionada.
     * @return StreamedContent para la descarga.
     */
    public StreamedContent descargarXml(Factura factura) {
        DocumentoElectronico doc = factura.getDocumentoElectronico();
        if (doc != null && doc.getXmlAutorizadoSri() != null) {
            return DefaultStreamedContent.builder()
                    .name("Factura_" + factura.getNumero() + ".xml")
                    .contentType("text/xml")
                    .stream(() -> new ByteArrayInputStream(doc.getXmlAutorizadoSri()))
                    .build();
        } else {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El XML no está disponible para esta factura.");
            return null;
        }
    }
}
