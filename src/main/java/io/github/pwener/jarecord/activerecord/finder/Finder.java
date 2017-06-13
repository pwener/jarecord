package io.github.pwener.jarecord.activerecord.finder;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.pwener.jarecord.activerecord.ActiveRecord;

public class Finder {

	private static final Logger logger = LoggerFactory.getLogger(ActiveRecord.class);

	private EntityManager entityManager;
	
	private Class<?> entityClass;
	
	// Used in SQL to be replaced by real parameter of search
	private static final String PARAMETER_NAME = "value";

	public Finder(Class<?> entityClass, EntityManager entityManager) {
		this.entityClass = entityClass;
		this.entityManager = entityManager;
	}

	public Object find(Object id) {
		return entityManager.find(entityClass, id);
	}

	/**
	 * Gets a Entity by a field with certain value
	 * 
	 * @param field Column name into database
	 * @param value Simple data
	 * 
	 * @return Object correspondent to Entity defined by DAO
	 */
	@SuppressWarnings("unchecked")
	public List<Entity> get(String field, Object value) {
		List<Entity> results = null;

		if (value != null) {
			final String sql = getSelectQuery(entityClass, field);
			Query query = entityManager.createQuery(sql);
			query.setParameter(PARAMETER_NAME, value);

			results = query.getResultList();
		} else {
			throw new IllegalArgumentException("You pass a null value inside your parameters");
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Entity> all() {
		String selectQuery = "Select entity from " 
				+ entityClass.getSimpleName() + " entity";

		return entityManager
				.createQuery(selectQuery)
				.getResultList();
	}

	/**
     * Generate String to pass in {@link EntityManager#createQuery(String)}
     * 
     * @param field Object key of search
     * 
     * @return String with command SQL
     */
    private String getSelectQuery(final Class<?> entity, final Object field) {
        String sql = "SELECT table FROM " + entity.getName() + " table"
                + " WHERE table." + field + "=:value";
        
        logger.info(sql);
        
        return sql;
    }

	public void setEntityManager(EntityManager manager) {
		this.entityManager = manager;
	}
}
