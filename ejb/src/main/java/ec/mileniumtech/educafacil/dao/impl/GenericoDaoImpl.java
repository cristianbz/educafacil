package ec.mileniumtech.educafacil.dao.impl;

import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.dao.GenericoDao;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
		entityManager.persist(entity);
		return entity;
	}

	@Override
	public void remover(T entity) {
		entityManager.remove(entityManager.merge(entity));
	}

	@Override
	public void detach(T entity) {
		entityManager.detach(entity);
	}

	@Override
	public T actualizar(T entity) {		
		entityManager.merge(entity);
		entityManager.flush();
		return entity;
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
