package io.github.projecturutau.vraptor.activerecord.finder;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.projecturutau.vraptor.activerecord.ActiveRecord;
import io.github.projecturutau.vraptor.helper.DaoHelper;

public class Finder {

	private static final Logger logger = LoggerFactory.getLogger(ActiveRecord.class);

	private EntityManager entityManager;
	
	private Class<?> entityClass;
	
	// Used in SQL to be replaced by real parameter of search
	private static final String PARAMETER_NAME = "value";

	@Inject
	private DaoHelper daoHelper;

	public Finder(Class<?> entityClass, EntityManager entityManager) {
		this.entityClass = entityClass;
		this.entityManager = entityManager;
	}

	/**
	 * Runs a select SQL
	 * 
	 * @return {@link List} of entities
	 */
	@SuppressWarnings("unchecked")
	public List<Entity> findUsing(final String sql) {
		logger.trace("Finding by: " + sql);

		List<Entity> results = null;

		try {
			Query query = entityManager.createQuery(sql);

			results = query.getResultList();
		} catch (IllegalArgumentException illegalArgumentException) {
			illegalArgumentException.printStackTrace();
			// throw new SQLException("SQL was not mounted rightly..");
		}

		return results;
	}

	public Entity find(Long id) {
		return (Entity) entityManager.find(entityClass, id);
	}

	/**
	 * Gets a Entity by a field with certain value
	 * 
	 * @param field
	 *            Column name into database
	 * @param value
	 *            Simple data
	 * @return Object correspondent to Entity defined by DAO
	 */
	@SuppressWarnings("unchecked")
	public List<Entity> get(String field, Object value) {
		List<Entity> results = null;

		if (value != null && daoHelper.isValidParameter(value)) {
			final String sql = daoHelper.getSelectQuery(entityClass, field);
			Query query = entityManager.createQuery(sql);
			query.setParameter(PARAMETER_NAME, value);

			results = query.getResultList();
		} else {
			throw new IllegalArgumentException("An invalid parameter has been passed to get method in GenericDAO");
		}

		return results;
	}
}
