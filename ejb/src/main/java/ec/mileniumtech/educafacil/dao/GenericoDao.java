package ec.mileniumtech.educafacil.dao;

import java.util.List;
import java.util.Optional;

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
}
