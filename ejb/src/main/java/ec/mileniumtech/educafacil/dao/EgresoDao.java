package ec.mileniumtech.educafacil.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.DtoFlujoDinero;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Egresos;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;

/**
 * Interfaz DAO para la entidad Egresos.
 * Define operaciones de acceso a datos espec\u00edficas para Egresos.
 */
@Local
public interface EgresoDao extends GenericoDao<Egresos, Long> {

    void agregarActualizarEgreso(Egresos egreso);
    List<Egresos> listaEgresos();
    List<Egresos> listaEgresosFechas(Date fechaUno, Date fechaDos);
    List<DtoFlujoDinero> buscaEgresosReporteria(Date fechaInicial, Date fechaFinal);
}
