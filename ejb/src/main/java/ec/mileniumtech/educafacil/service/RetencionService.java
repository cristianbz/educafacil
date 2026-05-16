package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.PuntoEmisionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.RetencionDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Retencion;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleRetencion;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;

/**
 * Servicio de negocio para la gestión de Comprobantes de Retención.
 */
@Stateless
@LocalBean
public class RetencionService {

    @EJB
    private RetencionDaoImpl retencionDao;

    @EJB
    private PuntoEmisionDaoImpl puntoEmisionDao;

    @EJB
    private RetencionIntegracionService retencionIntegracionService;

    /**
     * Guarda una retención y procesa su envío al SRI.
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
            if (puntos.isEmpty()) throw new Exception("No hay puntos de emisión activos.");
            puem = puntos.get(0);
            retencion.setPuntoEmision(puem);
        }

        // Generar número secuencial
        int nuevoSecuencial = puem.getSecuencialRetencion() + 1;
        String numero = String.format("%03d-%03d-%09d", 
                Integer.parseInt(puem.getEstablecimientos().getEstaCodigo()), 
                Integer.parseInt(puem.getCodigo()), 
                nuevoSecuencial);
        retencion.setNumero(numero);
        
        // Actualizar secuencial en el punto de emisión
        puem.setSecuencialRetencion(nuevoSecuencial);
        puntoEmisionDao.actualizar(puem);

        // 2. Persistir en base de datos
        // Asegurar relación bidireccional para JPA
        if (retencion.getDetalles() != null) {
            for (DetalleRetencion det : retencion.getDetalles()) {
                det.setRetencion(retencion);
            }
        }
        
        retencionDao.guardar(retencion);

        // 3. Procesar electrónicamente
        try {
            retencionIntegracionService.procesarRetencionElectronica(retencion);
        } catch (Exception e) {
            // Loguear error pero no revertir transacción si la retención ya se guardó en DB
            System.err.println("Error al emitir retención al SRI: " + e.getMessage());
        }
    }

    /**
     * Intenta emitir nuevamente una retención que quedó en estado PENDIENTE o RECHAZADO.
     * 
     * @param retencionId ID de la retención.
     * @throws Exception Si falla el proceso.
     */
    public void reemitirRetencion(Integer retencionId) throws Exception {
        Retencion ret = retencionDao.buscarRetencionPorId(retencionId);
        if (ret == null) throw new Exception("Retención no encontrada.");
        retencionIntegracionService.procesarRetencionElectronica(ret);
    }

    /**
     * Lista todas las retenciones registradas.
     * @return Lista de retenciones.
     */
    public List<Retencion> listarRetenciones() {
        return retencionDao.listarTodas();
    }
}
