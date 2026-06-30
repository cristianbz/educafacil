package ec.mileniumtech.educafacil.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Cuota;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@LocalBean
@Stateless
public class CuotaDaoImpl extends GenericoDaoImpl<Cuota, Long> {

    public CuotaDaoImpl() {
    }

    @SuppressWarnings("unchecked")
    public List<Cuota> listarCuotasPorMatricula(int codigoMatricula) {
        try {
            Query query = getEntityManager().createNamedQuery(Cuota.LISTAR_POR_MATRICULA);
            query.setParameter("codigoMatricula", codigoMatricula);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar cuotas por matricula", "CUOTA-LIST-ERR", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Cuota> listarCuotasPendientesPorMatricula(int codigoMatricula) {
        try {
            Query query = getEntityManager().createNamedQuery(Cuota.LISTAR_PENDIENTES_POR_MATRICULA);
            query.setParameter("codigoMatricula", codigoMatricula);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new SystemException("Error al listar cuotas pendientes por matricula", "CUOTA-LIST-ERR", e);
        }
    }

    public void guardarCuota(Cuota cuota) {
        try {
            getEntityManager().persist(cuota);
        } catch (Exception e) {
            throw new SystemException("Error al guardar cuota", "CUOTA-PERSIST-ERR", e);
        }
    }

    public void actualizarCuota(Cuota cuota) {
        try {
            getEntityManager().merge(cuota);
        } catch (Exception e) {
            throw new SystemException("Error al actualizar cuota", "CUOTA-UPDATE-ERR", e);
        }
    }
    public void eliminarCuota(Cuota cuota) {
        try {
            Cuota managed = getEntityManager().merge(cuota);
            getEntityManager().remove(managed);
        } catch (Exception e) {
            throw new SystemException("Error al eliminar cuota", "CUOTA-DELETE-ERR", e);
        }
    }
}
