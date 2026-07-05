package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import ec.mileniumtech.educafacil.dao.CodigoSriRetencionDao;
import ec.mileniumtech.educafacil.dao.PuntoEmisionDao;
import ec.mileniumtech.educafacil.dao.RetencionDao;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CodigoSriRetencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleRetencion;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio de negocio para la gestiÃ³n de Comprobantes de RetenciÃ³n.
 */
@Stateless
@LocalBean
public class RetencionService {

    private static final Logger log = LogManager.getLogger(RetencionService.class);

    @EJB
    private RetencionDao retencionDao;

    @EJB
    private PuntoEmisionDao puntoEmisionDao;

    @EJB
    private CodigoSriRetencionDao codigoSriRetencionDao;

    @EJB
    private RetencionIntegracionService retencionIntegracionService;

    /**
     * Guarda una retenciÃ³n y procesa su envÃ­o al SRI.
     * 
     * @param retencion Entidad con cabecera y detalles.
     * @throws Exception Si ocurre un error.
     */
    @Transactional
    public void guardarYEmitirRetencion(Retencion retencion) throws Exception {
        // 1. Validar y asignar secuencial
        PuntoEmision puem = retencion.getPuntoEmision();
        if (puem == null) {
            List<PuntoEmision> puntos = puntoEmisionDao.listarPuntosEmisionActivos();
            if (puntos.isEmpty()) throw new BusinessException("No hay puntos de emisiÃ³n activos.", "BIZ-RET-NO-PUNTO");
            puem = puntos.get(0);
            retencion.setPuntoEmision(puem);
        }

        // Generar nÃºmero secuencial
        int nuevoSecuencial = puem.getSecuencialRetencion() + 1;
        String numero = String.format("%03d-%03d-%09d", 
                Integer.parseInt(puem.getEstablecimientos().getEstaCodigo()), 
                Integer.parseInt(puem.getCodigo()), 
                nuevoSecuencial);
        retencion.setNumero(numero);
        
        // Actualizar secuencial en el punto de emisiÃ³n
        puem.setSecuencialRetencion(nuevoSecuencial);
        puntoEmisionDao.actualizar(puem);

        // 2. Persistir en base de datos
        // Asegurar relaciÃ³n bidireccional para JPA
        if (retencion.getDetalles() != null) {
            for (DetalleRetencion det : retencion.getDetalles()) {
                det.setRetencion(retencion);
            }
        }
        
        retencionDao.guardar(retencion);

        // 3. Procesar electrÃ³nicamente
        try {
            retencionIntegracionService.procesarRetencionElectronica(retencion);
        } catch (Exception e) {
            log.error("Error al emitir retenciÃ³n {} al SRI. La retenciÃ³n se guardÃ³ en BD pero no se autorizÃ³.", retencion.getNumero(), e);
        }
    }

    /**
     * Intenta emitir nuevamente una retenciÃ³n que quedÃ³ en estado PENDIENTE o RECHAZADO.
     * 
     * @param retencionId ID de la retenciÃ³n.
     * @throws Exception Si falla el proceso.
     */
    public void reemitirRetencion(Integer retencionId) throws Exception {
        Retencion ret = retencionDao.buscarRetencionPorId(retencionId);
        if (ret == null) throw new BusinessException("RetenciÃ³n no encontrada.", "BIZ-RET-NOT-FOUND");
        retencionIntegracionService.procesarRetencionElectronica(ret);
    }

    /**
     * Lista todas las retenciones registradas.
     * @return Lista de retenciones.
     */
    public List<Retencion> listarRetenciones() {
        return retencionDao.listarTodas();
    }

    /**
     * Lista los cÃ³digos SRI activos para un tipo de impuesto.
     * Se invoca al cambiar el selector de tipo de impuesto en el diÃ¡logo.
     *
     * @param tipoImpuesto "1" (Renta), "2" (IVA) o "6" (ISD)
     * @return Lista de CodigoSriRetencion activos.
     */
    public List<CodigoSriRetencion> listarCodigosPorTipoImpuesto(String tipoImpuesto) {
        return codigoSriRetencionDao.listarPorTipoImpuesto(tipoImpuesto);
    }

    /**
     * Busca cÃ³digos SRI por tipo de impuesto y texto libre (para p:autoComplete).
     *
     * @param tipoImpuesto Tipo de impuesto seleccionado.
     * @param query        Texto escrito por el usuario.
     * @return Lista filtrada.
     */
    public List<CodigoSriRetencion> buscarCodigosSri(String tipoImpuesto, String query) {
        return codigoSriRetencionDao.buscarPorTipoYTexto(tipoImpuesto, query);
    }

    /**
     * Busca un cÃ³digo SRI por su ID (usado por el Converter del autoComplete).
     *
     * @param id ID del cÃ³digo SRI.
     * @return CodigoSriRetencion encontrado, o null si no existe.
     */
    public CodigoSriRetencion buscarCodigoSriPorId(Integer id) {
        return codigoSriRetencionDao.findById(id).orElse(null);
    }
}

