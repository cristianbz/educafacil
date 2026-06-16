package ec.mileniumtech.educafacil.service.facade;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import ec.mileniumtech.educafacil.dao.impl.CampaniaDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.DetalleSeguimientoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.SeguimientoClientesDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.SeguimientoDaoImpl;
import ec.mileniumtech.educafacil.dao.impl.VendedorDaoImpl;
import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoMatriculasCurso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Campania;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.DetalleSeguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Seguimiento;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.SeguimientoClientes;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Vendedor;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

@Stateless
@LocalBean
public class MarketingFacade {

    @EJB
    private SeguimientoClientesDaoImpl seguimientoClientesDao;

    @EJB
    private DetalleSeguimientoDaoImpl detalleSeguimientoDao;

    @EJB
    private SeguimientoDaoImpl seguimientoDao;

    @EJB
    private CampaniaDaoImpl campaniaDao;

    @EJB
    private VendedorDaoImpl vendedorDao;

    // ========== Seguimiento Clientes ==========

    public void agregarSeguimiento(SeguimientoClientes seguimiento, List<DetalleSeguimiento> detalle) {
        seguimientoClientesDao.agregarSeguimiento(seguimiento, detalle);
    }

    public List<SeguimientoClientes> listaSeguimiento() {
        return seguimientoClientesDao.listaSeguimiento();
    }

    public List<SeguimientoClientes> listaSeguimientoVendedorAsignado() {
        return seguimientoClientesDao.listaSeguimientoVendedorAsignado();
    }

    public List<SeguimientoClientes> listaSeguimientoCampania(Integer campania) {
        return seguimientoClientesDao.listaSeguimientoCampania(campania);
    }

    public List<SeguimientoClientes> listaSeguimientoCampaniaVendedor(Integer campaniaS) {
        return seguimientoClientesDao.listaSeguimientoCampaniaVendedor(campaniaS);
    }

    public BigInteger alcanceCampania(int campania) {
        return seguimientoClientesDao.alcanceCampania(campania);
    }

    public BigInteger prospectosCampania(int campania, String estado) {
        return seguimientoClientesDao.prospectosCampania(campania, estado);
    }

    public List<SeguimientoClientes> listaSeguimientoCampaniaCurso(Integer curso) {
        return seguimientoClientesDao.listaSeguimientoCampaniaCurso(curso);
    }

    public List<SeguimientoClientes> listaSeguimientoCampaniaFechas(Date inicio, Date fin) {
        return seguimientoClientesDao.listaSeguimientoCampaniaFechas(inicio, fin);
    }

    public void actualizarSeguimiento(SeguimientoClientes seguimiento) {
        seguimientoClientesDao.actualizarSeguimiento(seguimiento);
    }

    public SeguimientoClientes seguimiento(int id) {
        return seguimientoClientesDao.seguimiento(id);
    }

    public SeguimientoClientes validaNumero(String telefono, int curso, int campania) {
        return seguimientoClientesDao.validaNumero(telefono, curso, campania);
    }

    public List<SeguimientoClientes> listaPendientesLlamada() {
        return seguimientoClientesDao.listaPendientesLlamada();
    }

    public BigDecimal totalDatosCRM(String estado) {
        return seguimientoClientesDao.totalDatosCRM(estado);
    }

    public BigDecimal totalDatosCRMVendedor(String estado, Integer vendedor, Integer campania) {
        return seguimientoClientesDao.totalDatosCRMVendedor(estado, vendedor, campania);
    }

    public List<DtoMatriculasCurso> listaInteresadosCursoCRM() {
        return seguimientoClientesDao.listaInteresadosCursoCRM();
    }

    public List<DtoMatriculasCurso> listaEstadosContactoCursoCRM(String estado) {
        return seguimientoClientesDao.listaEstadosContactoCursoCRM(estado);
    }

    // ========== Detalle Seguimiento ==========

    public void agregarDetalle(DetalleSeguimiento detalle) {
        detalleSeguimientoDao.agregarDetalle(detalle);
    }

    public List<DetalleSeguimiento> listaDetalle(Integer seguimiento) {
        return detalleSeguimientoDao.listaDetalle(seguimiento);
    }

    // ========== Seguimiento (Matrícula) ==========

    public void agregarActualizarSeguimiento(Seguimiento seguimiento) {
        seguimientoDao.agregarActualizarSeguimiento(seguimiento);
    }

    public List<Seguimiento> listarSeguimientoMatricula(int matricula) {
        return seguimientoDao.listaSeguimientoMatricula(matricula);
    }

    // ========== Campañas ==========

    public List<Campania> listaCampanias() {
        return campaniaDao.listaCampanias();
    }

    public List<Campania> listaCampaniasporCurso() {
        return campaniaDao.listaCampaniasporCurso();
    }

    public void agregarActualizarCampania(Campania campania) {
        campaniaDao.agregarActualizarCampania(campania);
    }

    public List<Campania> listaTodasCampanias() {
        return campaniaDao.listaTodasCampanias();
    }

    public Campania campaniaCurso(int curso) {
        return campaniaDao.campaniaCurso(curso);
    }

    public BigDecimal totalGastoCampanias() {
        return campaniaDao.totalGastoCampanias();
    }

    // ========== Vendedores ==========

    public List<Vendedor> listaDeVendedores() {
        return vendedorDao.listaDeVendedores();
    }
}
