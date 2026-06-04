package ec.mileniumtech.educafacil.service;

import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.CatalogoItemDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.ClienteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EmpresaMatrizDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.EstudianteDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.FacturaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PersonaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.PuntoEmisionDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.SriformapagoDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.CatalogoItem;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cliente;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Estudiante;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Factura;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PuntoEmision;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Sriformapago;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class FacturacionDataService {

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

    public Factura guardarFactura(Factura factura) {
        return facturaDao.guardar(factura);
    }

    public List<Factura> listarTodasLasFacturasDelDia() {
        return facturaDao.listarTodasLasFacturasDelDia();
    }

    public Factura buscarFacturaPorId(Integer id) {
        return facturaDao.buscarFacturaPorId(id);
    }

    public Cliente buscarClientePorIdentificacion(String id) {
        return clienteDao.buscarPorIdentificacion(id);
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteDao.guardar(cliente);
    }

    public Cliente actualizarCliente(Cliente cliente) {
        return clienteDao.actualizar(cliente);
    }

    public List<CatalogoItem> listarCatalogoItems() {
        return catalogoItemDao.findAll();
    }

    public CatalogoItem guardarCatalogoItem(CatalogoItem item) {
        return catalogoItemDao.guardar(item);
    }

    public CatalogoItem actualizarCatalogoItem(CatalogoItem item) {
        return catalogoItemDao.actualizar(item);
    }

    public List<PuntoEmision> listarPuntosEmisionActivos() {
        return puntoEmisionDao.listarPuntosEmisionActivos();
    }

    public PuntoEmision actualizarPuntoEmision(PuntoEmision puem) {
        return puntoEmisionDao.actualizar(puem);
    }

    public List<EmpresaMatriz> listaEmpresas() {
        return empresaMatrizDao.listaEmpresas();
    }

    public List<EmpresaMatriz> listarTodasEmpresas() {
        return empresaMatrizDao.findAll();
    }

    public List<Sriformapago> listarFormasPago() {
        return sriformapagoDao.findAll();
    }

    public Sriformapago buscarFormaPagoPorId(Integer id) {
        return sriformapagoDao.findById(id).orElse(null);
    }

    public List<Factura> buscarFacturasPorFiltros(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin, String identificacion, String numeroAutorizacion, String estadoAutorizacion) {
        return facturaDao.buscarFacturasPorFiltros(fechaInicio, fechaFin, identificacion, numeroAutorizacion, estadoAutorizacion);
    }

    public void actualizar(Factura factura) {
        facturaDao.actualizar(factura);
    }

    public Estudiante buscarEstudiantePorCedula(String cedula) {
        return estudianteDao.estudiantesPorCedula(cedula);
    }

    public Persona buscarPersonaPorCedula(String cedula) {
        return personaDao.buscarPersonaPorCedula(cedula);
    }
}
