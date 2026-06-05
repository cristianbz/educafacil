package ec.mileniumtech.educafacil.backing.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.contabilidad.BeanFacturacion;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.InfoAdicionalDto;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleFactura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleNotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DocumentoElectronico;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.NotaCredito;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.service.facade.FacturacionFacade;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.fechas.FechaFormato;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * Backing bean para la gestión de facturación electrónica en la UI.
 */
@Named("backingFacturacion")
@ViewScoped
public class BackingFacturacion implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingFacturacion.class);

    @EJB
    private FacturacionFacade facturacionDataService;

    @EJB
    private FacturacionFacade facturacionService;

    @EJB
    private ec.mileniumtech.educafacil.service.NotaCreditoService notaCreditoService;
    @EJB
    private ec.mileniumtech.educafacil.service.AwsS3Service awsS3Service;


    @Inject
    @Getter
    private BeanFacturacion beanFacturacion;

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;
    
    private EmpresaMatriz empresaMatriz;
    @PostConstruct
    public void init() {
        try {
        	        	
            cargarFacturas();
            prepararNuevaFactura();            
            getBeanFacturacion().setListaItems(facturacionDataService.listarCatalogoItems());
            getBeanFacturacion().setListaFormasPagoSri(facturacionDataService.listarFormasPago());            
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
        getBeanFacturacion().setListaInfoAdicional(new ArrayList<InfoAdicionalDto>());
        getBeanFacturacion().setInfoAdicional(new InfoAdicionalDto());
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
            
            // Validación de Cédula (si tiene 10 dígitos)
            if (id != null && id.length() == 10) {
                if (!ec.mileniumtech.educafacil.utilitarios.ValidacionUtil.validarCedula(id)) {
                    Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "La cédula ingresada no es válida.");
                    getBeanFacturacion().setClienteSeleccionado(null);
                    return;
                }
            }
            // 1. Buscar en Cliente (Directo)
            Cliente c = facturacionDataService.buscarClientePorIdentificacion(id);
            if (c != null) {
                getBeanFacturacion().setClienteSeleccionado(c);
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Cliente Encontrado", c.getNombresCompletos());
                return;
            }

            // 2. Buscar en Estudiante
            Estudiante e = facturacionDataService.buscarEstudiantePorCedula(id);
            if (e != null && e.getPersona() != null) {
                prepararNuevoClienteDesdePersona(e.getPersona());
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Datos recuperados de Estudiante", getBeanFacturacion().getClienteSeleccionado().getNombresCompletos());
                return;
            }

            // 3. Buscar en Persona (General)
            Persona p = facturacionDataService.buscarPersonaPorCedula(id);
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
     * Activa el formulario para editar la información del cliente seleccionado.
     */
    public void activarEdicionCliente() {
        getBeanFacturacion().setMostrarFormularioNuevoCliente(true);
        Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Edición", "Puede modificar los datos del cliente.");
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
     * Prepara el objeto para editar el ítem seleccionado.
     */
    public void prepararEditarItem() {
        CatalogoItem selected = getBeanFacturacion().getItemSeleccionado();
        if (selected != null) {
            CatalogoItem editItem = new CatalogoItem();
            editItem.setId(selected.getId());
            editItem.setCodigo(selected.getCodigo());
            editItem.setNombre(selected.getNombre());
            editItem.setTipo(selected.getTipo());
            editItem.setDescripcion(selected.getDescripcion());
            editItem.setPrecio(selected.getPrecio());
            getBeanFacturacion().setNuevoItem(editItem);
        } else {
            prepararNuevoItem();
        }
    }

    /**
     * Guarda el nuevo o editado ítem en la base de datos.
     */
    public void guardarNuevoItem() {
        try {
            log.info("Iniciando guardado de ítem...");
            CatalogoItem item = getBeanFacturacion().getNuevoItem();
            if (item.getCodigo() == null || item.getCodigo().isEmpty()) throw new Exception("El código es obligatorio.");
            if (item.getNombre() == null || item.getNombre().isEmpty()) throw new Exception("El nombre es obligatorio.");
            
            boolean esNuevo = (item.getId() == null);
            if (esNuevo) {
            	 facturacionDataService.guardarCatalogoItem(item);
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Ítem creado correctamente.");
            } else {
            	facturacionDataService.actualizarCatalogoItem(item);
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Ítem modificado correctamente.");
            }
            
            // Actualizar lista y seleccionar el ítem correcto
            List<CatalogoItem> items = facturacionDataService.listarCatalogoItems();
            getBeanFacturacion().setListaItems(items);
            
            // Seleccionar el ítem correcto por referencia de la nueva lista
            if (item.getId() != null) {
                for (CatalogoItem it : items) {
                    if (it.getId().equals(item.getId())) {
                        getBeanFacturacion().setItemSeleccionado(it);
                        break;
                    }
                }
            } else {
                getBeanFacturacion().setItemSeleccionado(item);
            }
            
            Mensaje.ocultarDialogo("dlgNuevoItem");
            
        } catch (Exception e) {
            log.error("Error al guardar ítem", e);
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
            
            List<EmpresaMatriz> empresas = facturacionDataService.listaEmpresas();
            if (empresas != null && !empresas.isEmpty()) {
                df.setImpuestoIva(empresas.get(0).getEmpmPorcentajeIva());
            } else {
                df.setImpuestoIva(BigDecimal.ZERO);
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
     * Agrega informacion adicional a la factura
     */
    public void agregarInformacionAdicional() {
    	getBeanFacturacion().getListaInfoAdicional().add(getBeanFacturacion().getInfoAdicional());
    	getBeanFacturacion().setInfoAdicional(new InfoAdicionalDto());
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
            
            if (df.getImpuestoIva() != null && df.getImpuestoIva().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal iva = df.getImpuestoIva().divide(new BigDecimal(100));
                totalImpuestos = totalImpuestos.add(subtotalItem.multiply(iva));
            }
        }
        
        getBeanFacturacion().getNuevaFactura().setSubtotal(subtotal);
        getBeanFacturacion().getNuevaFactura().setDescuentoTotal(descuentoTotal);
        getBeanFacturacion().getNuevaFactura().setTotalImpuestos(totalImpuestos);
        getBeanFacturacion().getNuevaFactura().setTotal(subtotal.subtract(descuentoTotal).add(totalImpuestos));
        getBeanFacturacion().getNuevaFormaPago().setValor(getBeanFacturacion().getNuevaFactura().getTotal());
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
            
            ec.mileniumtech.educafacil.modelo.persistencia.entity.Sriformapago sriFp = facturacionDataService.buscarFormaPagoPorId(idFp);
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
    public void removerInfoAdicional(InfoAdicionalDto infoad) {
        getBeanFacturacion().getListaInfoAdicional().remove(infoad);
        Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Información adicional removida.");
    }

    /**
     * Guarda la nueva factura y procesa la emisión electrónica.
     */
    public void guardarFactura() {
        try {
            if (getBeanFacturacion().getClienteSeleccionado() == null) {
                throw new Exception("Debe seleccionar un cliente.");
            }
            
            // Validación de Cédula (si tiene 10 dígitos)
            String identificacion = getBeanFacturacion().getClienteSeleccionado().getNumeroIdentificacion();
            if (identificacion != null && identificacion.length() == 10) {
                if (!ec.mileniumtech.educafacil.utilitarios.ValidacionUtil.validarCedula(identificacion)) {
                    throw new Exception("La cédula del cliente (" + identificacion + ") no es válida. Corríjala para continuar.");
                }
            }
            if (getBeanFacturacion().getListaDetallesNueva().isEmpty()) {
                throw new Exception("Debe agregar al menos un detalle.");
            }
            if (getBeanFacturacion().getListaFormasPagoAgregadas().isEmpty()) {
                throw new Exception("Debe agregar al menos una forma de pago.");
            }

            BigDecimal totalFactura = getBeanFacturacion().getNuevaFactura().getTotal();
            BigDecimal totalPagos = BigDecimal.ZERO;
            BigDecimal totalDescuento = BigDecimal.ZERO;
            BigDecimal totalImpuestos = BigDecimal.ZERO;
            for (ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura fp : getBeanFacturacion().getListaFormasPagoAgregadas()) {
                totalPagos = totalPagos.add(fp.getValor());
            }

            if (totalPagos.compareTo(totalFactura.setScale(2,RoundingMode.HALF_UP)) != 0) {
                throw new Exception("La suma de las formas de pago (" + totalPagos + ") no coincide con el total de la factura (" + totalFactura + ").");
            }

            Factura f = getBeanFacturacion().getNuevaFactura();
            Cliente c = getBeanFacturacion().getClienteSeleccionado();
            
            // Si el cliente no existe en la base de datos (ID nulo), lo guardamos primero
            if (c.getId() == null) {
            	 facturacionDataService.guardarCliente(c);
            } else {
            	facturacionDataService.actualizarCliente(c);
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
            List<PuntoEmision> puntos = facturacionDataService.listarPuntosEmisionActivos();
            if (puntos.isEmpty()) throw new Exception("No hay puntos de emisión activos.");
            PuntoEmision puem = puntos.get(0);
            f.setPuntoEmision(puem);
            
            // Secuencial
            int nuevoSec = puem.getSecuencialFactura() + 1;
            f.setNumero(String.format("001-%s-%09d", puem.getCodigo(), nuevoSec));
            puem.setSecuencialFactura(nuevoSec);
            facturacionDataService.actualizarPuntoEmision(puem);
            
            for (DetalleFactura df : f.getDetalles()) {
                BigDecimal tasaImpuesto = df.getImpuestoIva().divide(
                        BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP
                    );

                // 1. Calcular la base imponible del detalle (Precio * Cantidad - Descuento)
                // Usamos cantidad por si acaso la factura tiene más de 1 unidad
                BigDecimal cantidad = BigDecimal.valueOf(df.getCantidad());
                BigDecimal subtotalNeto = df.getPrecioUnitario().multiply(cantidad).subtract(df.getDescuento());

                // 2. Calcular el impuesto sobre el valor NETO
                BigDecimal impuesto = subtotalNeto.multiply(tasaImpuesto).setScale(2, RoundingMode.HALF_UP);
                
                totalImpuestos = totalImpuestos.add(impuesto);
                totalDescuento = totalDescuento.add(df.getDescuento());
            }
            
            f.setTotalImpuestos(totalImpuestos);
            f.setDescuentoTotal(totalDescuento);
            f.setListaInfoAdicional(getBeanFacturacion().getListaInfoAdicional());

            facturacionDataService.guardarFactura(f);
            
            // Emitir electrónicamente
            facturacionService.emitirFactura(f.getId(),getBeanFacturacion().getListaInfoAdicional());
            
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Factura generada y enviada al SRI.");
            cargarFacturas();
            prepararNuevaFactura();
            Mensaje.ocultarDialogo("dlgNuevaFactura");
            
        } catch (Exception e) {
            log.error("Error al guardar factura", e);
            if (e.getMessage() != null && e.getMessage().contains("No se pudo establecer comunicación")) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "No se pudo enviar el documento al SRI, se procesará luego.");
                cargarFacturas();
                Mensaje.ocultarDialogo("dlgNuevaFactura");
                Mensaje.verDialogo("dlgErrorSri");
            } else {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            }
        }
    }
    

    /**
     * Carga la lista de facturas desde la base de datos.
     */
    public void cargarFacturas() {
        try {
            getBeanFacturacion().setListaFacturas(facturacionDataService.listarTodasLasFacturasDelDia());
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
            facturacionService.emitirFactura(factura.getId(),getBeanFacturacion().getListaInfoAdicional());
            cargarFacturas(); // Refrescar lista para ver cambios en estado
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Proceso de facturación iniciado.");
        } catch (Exception e) {
            log.error("Error al emitir factura", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Prepara la interfaz para generar una Nota de Crédito.
     * @param factura Factura base.
     */
    public void prepararNotaCredito(Factura facturaParcial) {
        try {
            if (facturaParcial.getDocumentoElectronico() == null || !"AUTORIZADO".equals(facturaParcial.getDocumentoElectronico().getEstado())) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Solo se puede generar Nota de Crédito a facturas AUTORIZADAS.");
                return;
            }
            
            // Recargar la factura para asegurar que los detalles lazy se inicialicen correctamente
            Factura factura = facturacionDataService.buscarFacturaPorId(facturaParcial.getId());
            
            getBeanFacturacion().setFacturaSeleccionada(factura);
            getBeanFacturacion().setMotivoNotaCredito("");
            
            NotaCredito nc = new NotaCredito();
            nc.setFactura(factura);
            nc.setCliente(factura.getCliente());
            nc.setPuntoEmision(factura.getPuntoEmision());
            nc.setFechaEmision(LocalDate.now());
            nc.setSubtotal(factura.getSubtotal());
            nc.setTotalImpuestos(factura.getTotalImpuestos());
            nc.setTotal(factura.getTotal());
            BigDecimal porcentajeIva = factura.getPuntoEmision().getEstablecimientos().getEmpresaMatriz().getEmpmPorcentajeIva();
            if (porcentajeIva == null) {
                porcentajeIva = BigDecimal.ZERO;
            }
            
            List<DetalleNotaCredito> detallesNc = new ArrayList<>();
            for (DetalleFactura df : factura.getDetalles()) {
                DetalleNotaCredito dnc = new DetalleNotaCredito();
                dnc.setNotaCredito(nc);
                dnc.setItem(df.getItem());
                dnc.setDescripcion(df.getDescripcion());
                dnc.setCantidad(df.getCantidad());
                dnc.setPrecioUnitario(df.getPrecioUnitario());
                dnc.setDescuento(df.getDescuento());
                dnc.setImpuestoIva(porcentajeIva);
                detallesNc.add(dnc);
            }
            nc.setDetalles(detallesNc);
            
            getBeanFacturacion().setNuevaNotaCredito(nc);
            Mensaje.verDialogo("dlgNuevaNotaCredito");
        } catch (Exception e) {
            log.error("Error al preparar N.C.", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo preparar la Nota de Crédito.");
        }
    }
    /**
     * Genera y envía al SRI la Nota de Crédito.
     */
    public void generarNotaCredito() {
        try {
            if (getBeanFacturacion().getMotivoNotaCredito() == null || getBeanFacturacion().getMotivoNotaCredito().trim().isEmpty()) {
                throw new Exception("El motivo es obligatorio para la Nota de Crédito.");
            }
            calcularTotalesNotaCredito();
            
            if (!validarDetallesNotaCredito()) {
                return; // Validation failed, error messages already set
            }
            
            NotaCredito nc = getBeanFacturacion().getNuevaNotaCredito();
            nc.setMotivo(getBeanFacturacion().getMotivoNotaCredito());
            
            // Asignar numero secuencial
            PuntoEmision puem = nc.getPuntoEmision();
            int nuevoSec = puem.getSecuencialNotaCredito() != null ? puem.getSecuencialNotaCredito() + 1 : 1;
            nc.setNumero(String.format("%03d-%03d-%09d", 
                Integer.parseInt(puem.getEstablecimientos().getEstaCodigo()), 
                Integer.parseInt(puem.getCodigo()), 
                nuevoSec));
            
            puem.setSecuencialNotaCredito(nuevoSec);
            facturacionDataService.actualizarPuntoEmision(puem);
            
            notaCreditoService.procesarNotaCreditoElectronica(nc);
            
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Nota de Crédito generada y procesada correctamente.");
            Mensaje.ocultarDialogo("dlgNuevaNotaCredito");
            cargarFacturas(); // para refrescar estados si fuera necesario
        } catch (Exception e) {
            log.error("Error al generar N.C.", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }
    /*
     * Calcula los totales de la nota de credito
     */
    public void calcularTotalesNotaCredito() {
        if (getBeanFacturacion().getNuevaNotaCredito() == null || getBeanFacturacion().getNuevaNotaCredito().getDetalles() == null) {
            return;
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        BigDecimal totalImpuestos = BigDecimal.ZERO;
        for (DetalleNotaCredito dnc : getBeanFacturacion().getNuevaNotaCredito().getDetalles()) {
            BigDecimal cant = new BigDecimal(dnc.getCantidad() != null ? dnc.getCantidad() : 0);
            BigDecimal precio = dnc.getPrecioUnitario() != null ? dnc.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal desc = dnc.getDescuento() != null ? dnc.getDescuento() : BigDecimal.ZERO;
            
            BigDecimal subtotalItem = precio.multiply(cant).subtract(desc);
            subtotal = subtotal.add(precio.multiply(cant));
            descuentoTotal = descuentoTotal.add(desc);
            
            if (dnc.getImpuestoIva() != null && dnc.getImpuestoIva().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal iva = dnc.getImpuestoIva().divide(new BigDecimal(100));
                totalImpuestos = totalImpuestos.add(subtotalItem.multiply(iva).setScale(2, RoundingMode.HALF_UP));
            }
        }
        
        getBeanFacturacion().getNuevaNotaCredito().setSubtotal(subtotal);
        getBeanFacturacion().getNuevaNotaCredito().setTotalImpuestos(totalImpuestos);
        getBeanFacturacion().getNuevaNotaCredito().setTotal(subtotal.subtract(descuentoTotal).add(totalImpuestos));
    }
    /**
     * Remueve un detalle de la nota de credito
     * @param det
     */
    public void removerDetalleNotaCredito(DetalleNotaCredito det) {
        if (getBeanFacturacion().getNuevaNotaCredito() != null && getBeanFacturacion().getNuevaNotaCredito().getDetalles() != null) {
            getBeanFacturacion().getNuevaNotaCredito().getDetalles().remove(det);
            calcularTotalesNotaCredito();
        }
    }
    /**
     * Valida los detalles de la nota de credito
     * @return
     */
    private boolean validarDetallesNotaCredito() {
        if (getBeanFacturacion().getNuevaNotaCredito().getDetalles() == null || getBeanFacturacion().getNuevaNotaCredito().getDetalles().isEmpty()) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "La nota de crédito debe tener al menos un ítem.");
            return false;
        }
        Factura facturaOriginal = getBeanFacturacion().getFacturaSeleccionada();
        for (DetalleNotaCredito dnc : getBeanFacturacion().getNuevaNotaCredito().getDetalles()) {
            DetalleFactura original = null;
            for (DetalleFactura df : facturaOriginal.getDetalles()) {
                if (df.getItem().getId().equals(dnc.getItem().getId())) {
                    original = df;
                    break;
                }
            }
            if (original == null) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "El ítem " + dnc.getItem().getNombre() + " no pertenece a la factura original.");
                return false;
            }
            if (dnc.getCantidad() == null || dnc.getCantidad() <= 0) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "La cantidad del ítem " + dnc.getItem().getNombre() + " debe ser mayor a cero.");
                return false;
            }
            if (dnc.getCantidad() > original.getCantidad()) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "La cantidad del ítem " + dnc.getItem().getNombre() + " (" + dnc.getCantidad() + ") no puede ser mayor a la original (" + original.getCantidad() + ").");
                return false;
            }
            if (dnc.getPrecioUnitario() == null || dnc.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "El precio del ítem " + dnc.getItem().getNombre() + " no es válido.");
                return false;
            }
            if (dnc.getPrecioUnitario().compareTo(original.getPrecioUnitario()) > 0) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "El precio del ítem " + dnc.getItem().getNombre() + " no puede ser mayor al original.");
                return false;
            }
            if (dnc.getDescuento() == null || dnc.getDescuento().compareTo(BigDecimal.ZERO) < 0) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "El descuento del ítem " + dnc.getItem().getNombre() + " no puede ser negativo.");
                return false;
            }
            BigDecimal subtotalNetoOriginal = original.getPrecioUnitario().multiply(new BigDecimal(original.getCantidad())).subtract(original.getDescuento() != null ? original.getDescuento() : BigDecimal.ZERO);
            BigDecimal subtotalNetoNuevo = dnc.getPrecioUnitario().multiply(new BigDecimal(dnc.getCantidad())).subtract(dnc.getDescuento());
            if (subtotalNetoNuevo.compareTo(subtotalNetoOriginal) > 0) {
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error de Validación", "El subtotal neto del ítem " + dnc.getItem().getNombre() + " excede el valor original facturado.");
                return false;
            }
        }
        return true;
    }
    /**
     * Obtiene el descuento total de la nota de credito
     * @return
     */
    public BigDecimal getDescuentoTotalNotaCredito() {
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        if (getBeanFacturacion().getNuevaNotaCredito() != null && getBeanFacturacion().getNuevaNotaCredito().getDetalles() != null) {
            for (DetalleNotaCredito dnc : getBeanFacturacion().getNuevaNotaCredito().getDetalles()) {
                if (dnc.getDescuento() != null) {
                    descuentoTotal = descuentoTotal.add(dnc.getDescuento());
                }
            }
        }
        return descuentoTotal;
    }
    /**
     * Genera la descarga del archivo RIDE (PDF) de la factura.
     * Si existe URL de S3, genera una pre-signed URL y redirige al cliente.
     * En caso contrario, usa los bytes almacenados en BD (facturas antiguas).
     * @param factura Factura seleccionada.
     * @return StreamedContent para la descarga, o null si no hay PDF disponible.
     */
    public StreamedContent descargarRide(Factura factura) {
        DocumentoElectronico doc = factura.getDocumentoElectronico();
        if (doc == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El RIDE no está disponible para esta factura.");
            return null;
        }

        // Prioridad 1: Descargar desde S3 via pre-signed URL
        if (doc.getUrlPdf() != null && !doc.getUrlPdf().isEmpty()) {
            try {
                String presignedUrl = awsS3Service.generarUrlDescarga(doc.getUrlPdf());
             // ESCAPE Y EJECUCIÓN: Mandamos a abrir la ventana inmediatamente con la URL fresca
                String script = String.format("window.open('%s', '_blank');", presignedUrl);
                PrimeFaces.current().executeScript(script);
                return null;
            } catch (Exception e) {
                log.error("Error al generar pre-signed URL para RIDE", e);
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo generar el enlace de descarga: " + e.getMessage());
                return null;
            }
        }
        return null;
    }


    /**
     * Genera la descarga del archivo XML de la factura.
     * Si existe URL de S3, genera una pre-signed URL y redirige al cliente.
     * En caso contrario, usa los bytes almacenados en BD (facturas antiguas).
     * @param factura Factura seleccionada.
     * @return StreamedContent para la descarga, o null si no hay XML disponible.
     */
    public StreamedContent descargarXml(Factura factura) {
        DocumentoElectronico doc = factura.getDocumentoElectronico();
        if (doc == null) {
            Mensaje.verMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "El XML no está disponible para esta factura.");
            return null;
        }

        // Prioridad 1: Descargar desde S3 via pre-signed URL
        if (doc.getUrlXml() != null && !doc.getUrlXml().isEmpty()) {
            try {
                String presignedUrl = awsS3Service.generarUrlDescarga(doc.getUrlXml());
             // ESCAPE Y EJECUCIÓN: Mandamos a abrir la ventana inmediatamente con la URL fresca
                String script = String.format("window.open('%s', '_blank');", presignedUrl);
                PrimeFaces.current().executeScript(script);
                return null;
            } catch (Exception e) {
                log.error("Error al generar pre-signed URL para XML", e);
                Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo generar el enlace de descarga: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * Sube manualmente los documentos de una factura autorizada al bucket S3.
     * Se invoca desde el botón "Subir a AWS S3" en la tabla de facturas.
     * Solo procesa facturas AUTORIZADAS que aún no tienen URL de S3.
     *
     * @param factura Factura cuyos documentos se desean subir a S3.
     */
    public void subirDocumentosAws(Factura factura) {
        try {
            facturacionService.subirDocumentosFacturaAws(factura.getId());
            cargarFacturas();
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito",
                "Documentos de la factura " + factura.getNumero() + " subidos a AWS S3 correctamente.");
        } catch (Exception e) {
            log.error("Error al subir documentos a S3", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    /**
     * Devuelve la cantidad de facturas autorizadas en la lista actual.
     */
    public int getCantAutorizadas() {
    	if (getBeanFacturacion().getListaFacturas() == null) return 0;
        return (int) getBeanFacturacion().getListaFacturas().stream()
                .filter(f -> f.getDocumentoElectronico() != null && "AUTORIZADO".equals(f.getDocumentoElectronico().getEstado()))
                .count();
    }

    /**
     * Devuelve la cantidad de facturas pendientes en la lista actual.
     */
    public int getCantPendientes() {
        if (getBeanFacturacion().getListaFacturas() == null) return 0;
        return (int) getBeanFacturacion().getListaFacturas().stream()
                .filter(f -> f.getDocumentoElectronico() == null ||
                    (!"AUTORIZADO".equals(f.getDocumentoElectronico().getEstado()) &&
                     !"RECHAZADO".equals(f.getDocumentoElectronico().getEstado()) &&
                     !"ANULADA".equals(f.getDocumentoElectronico().getEstado())))
                .count();
    }

    /**
     * Devuelve la cantidad de facturas rechazadas en la lista actual.
     */
    public int getCantRechazadas() {
        if (getBeanFacturacion().getListaFacturas() == null) return 0;
        return (int) getBeanFacturacion().getListaFacturas().stream()
                .filter(f -> f.getDocumentoElectronico() != null && "RECHAZADO".equals(f.getDocumentoElectronico().getEstado()))
                .count();
    }

    /**
     * Devuelve el monto total facturado en la lista actual.
     */
    public java.math.BigDecimal getTotalFacturado() {
        if (getBeanFacturacion().getListaFacturas() == null) return java.math.BigDecimal.ZERO;
        return getBeanFacturacion().getListaFacturas().stream()
                .filter(f -> f.getTotal() != null && (f.getDocumentoElectronico() == null || !"ANULADA".equals(f.getDocumentoElectronico().getEstado())))
                .map(Factura::getTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * Devuelve el saldo restante por cubrir con las formas de pago.
     */
    public java.math.BigDecimal getSaldoRestante() {
        java.math.BigDecimal totalFactura = getBeanFacturacion().getNuevaFactura().getTotal();
        if (totalFactura == null) {
            return java.math.BigDecimal.ZERO;
        }
        java.math.BigDecimal totalPagos = getBeanFacturacion().getListaFormasPagoAgregadas() != null
                ? getBeanFacturacion().getListaFormasPagoAgregadas().stream()
                        .filter(fp -> fp.getValor() != null)
                        .map(ec.mileniumtech.educafacil.modelo.persistencia.entity.FormaPagoFactura::getValor)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                : java.math.BigDecimal.ZERO;
        return totalFactura.setScale(2,RoundingMode.HALF_UP).subtract(totalPagos.setScale(2,RoundingMode.HALF_UP));
    }
    /**
     * Dias de vigencia de la firma
     * @return
     */
    public long getDiasVigenciaFirma() {
    	empresaMatriz=facturacionDataService.listarTodasEmpresas().get(0);
    	return FechaFormato.calcularDiasRestantesSeguro(empresaMatriz.getEmpmFirmaVigenciaHasta().toString());
    }

    public boolean isSaldoPendiente() {
        return getSaldoRestante().compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    public boolean isSaldoExcedido() {
        return getSaldoRestante().compareTo(java.math.BigDecimal.ZERO) < 0;
    }

    public boolean isSaldoCero() {
        return getSaldoRestante().compareTo(java.math.BigDecimal.ZERO) == 0;
    }

    public java.math.BigDecimal getSaldoAbsoluto() {
        return getSaldoRestante().abs();
    }
}

