package ec.mileniumtech.educafacil.dao;

import java.util.List;
import java.util.Optional;

import ec.mileniumtech.educafacil.dao.excepciones.DaoException;

/**
 * @author [ Christian Baez ]cbaez
 *
 */
public interface GenericoDao <T,K> {
	Optional<T> findById(K id);

	T guardar(T entity) throws DaoException;

	void remover(T entity) throws DaoException;

	void detach(T dto) throws DaoException;	

	T actualizar(T entity) throws DaoException;
	
	boolean validarCadenaNula(String label);
	
	List<T> findAll();
}
