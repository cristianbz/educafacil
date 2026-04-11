package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.dao.GenericoDao;
import ec.mileniumtech.educafacil.dao.excepciones.SystemException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author [ Christian Baez ]cbaez
 *
 */
@Stateless
@Getter
@Setter
public class GenericoDaoImpl <T,K> implements GenericoDao<T,K> {
	@PersistenceContext
	private EntityManager entityManager;
    private Class<T> entityClass;

    public GenericoDaoImpl(EntityManager em, Class<T> entityClass) {
        this.entityManager = em;
        this.entityClass = entityClass;
    }
    public GenericoDaoImpl() {
    	
    }

	@Override
	public T guardar(T entity) {
		try {
			entityManager.persist(entity);
			return entity;
		} catch (PersistenceException e) {
			throw new SystemException("Error al persistir la entidad " + entityClass.getSimpleName(), "DB-PERSIST-ERR", e);
		}
	}

	@Override
	public void remover(T entity) {
		try {
			entityManager.remove(entityManager.merge(entity));
		} catch (PersistenceException e) {
			throw new SystemException("Error al remover la entidad " + entityClass.getSimpleName(), "DB-REMOVE-ERR", e);
		}
	}

	@Override
	public void detach(T entity) {
		entityManager.detach(entity);
	}

	@Override
	public T actualizar(T entity) {		
		try {
			entityManager.merge(entity);
			entityManager.flush();
			return entity;
		} catch (PersistenceException e) {
			throw new SystemException("Error al actualizar la entidad " + entityClass.getSimpleName(), "DB-UPDATE-ERR", e);
		}
	}	

	public void cerrarConexion() {
		entityManager.getEntityManagerFactory().close();
	}

	@Override
	public boolean validarCadenaNula(String label) {
		boolean isNull = true;
		if (null != label && !"".equals(label)) {
			isNull = false;
		}
		return isNull;
	}

	@Override
	public Optional<T> findById(K id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<T> findAll() {
		return getEntityManager().createQuery("SELECT E FROM "+ entityClass.getSimpleName() + "e",entityClass).getResultList();
	}
	
}
