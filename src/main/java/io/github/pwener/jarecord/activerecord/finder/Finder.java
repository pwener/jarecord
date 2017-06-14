package io.github.pwener.jarecord.activerecord.finder;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.pwener.jarecord.activerecord.ActiveRecord;

@RequestScoped
public class Finder {
	private static final Logger logger = LoggerFactory.getLogger(Finder.class);

	private static final String PARAMETER_NAME = "value";

	private final EntityManager entityManager;

	private Class<?> entityClass;

	/**
     * @deprecated CDI only
     */
	public Finder() {
		this(null);
	}

	@Inject
	public Finder(EntityManager entityManager) {
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
	public ArrayList<ActiveRecord<?>> get(String field, Object value) {
		List<ActiveRecord<?>> results = null;

		if (value != null) {
			final String sql = getSelectQuery(entityClass, field);
			Query query = entityManager.createQuery(sql);
			query.setParameter(PARAMETER_NAME, value);

			results = query.getResultList();
		} else {
			throw new IllegalArgumentException("You was pass a null value like parameter");
		}

		return (ArrayList<ActiveRecord<?>>) results;
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

    public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
}
