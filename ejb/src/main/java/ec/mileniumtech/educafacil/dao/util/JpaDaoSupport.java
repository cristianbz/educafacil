package ec.mileniumtech.educafacil.dao.util;

import java.util.Collections;
import java.util.List;

import ec.mileniumtech.educafacil.dao.excepciones.BusinessException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;

public final class JpaDaoSupport {
	private JpaDaoSupport() {
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> resultListOrEmpty(Query query) {
		try {
			List<T> result = query.getResultList();
			return result == null ? Collections.emptyList() : result;
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}

	public static <T> T singleResultOrNull(Query query) {
		try {
			@SuppressWarnings("unchecked")
			T result = (T) query.getSingleResult();
			return result;
		} catch (NoResultException e) {
			return null;
		}
	}

	public static void throwIfConstraintViolationDuplicate(PersistenceException e) {
		Throwable t = e.getCause();
		while ((t != null) && !(t instanceof ConstraintViolationException)) {
			t = t.getCause();
		}
		if (t instanceof ConstraintViolationException) {
			throw new BusinessException("El registro ya existe en el sistema.", "DUPLICATE-ENTITY", e);
		}
	}
}

