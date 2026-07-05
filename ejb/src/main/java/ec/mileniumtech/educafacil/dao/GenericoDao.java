package ec.mileniumtech.educafacil.dao;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

/**
 * @author [ Christian Baez ]cbaez
 *
 */
public interface GenericoDao <T,K> {
	Optional<T> findById(K id);

	T guardar(T entity);

	void remover(T entity);

	void detach(T dto);	

	T actualizar(T entity);
	
	boolean validarCadenaNula(String label);
	
	List<T> findAll();
	
	/**
	 * Obtiene el EntityManager para consultas nativas o de entidades relacionadas.
	 * @return EntityManager de JPA
	 */
	EntityManager getEntityManager();
}
