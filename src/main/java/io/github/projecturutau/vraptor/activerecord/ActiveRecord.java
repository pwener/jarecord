package io.github.projecturutau.vraptor.activerecord;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.projecturutau.vraptor.activerecord.finder.Finder;
import io.github.projecturutau.vraptor.activerecord.finder.Options;

public abstract class ActiveRecord<ActiveType> implements Serializable{

	private static final long serialVersionUID = 6819093423193006048L;

	private static final Logger logger = LoggerFactory.getLogger(ActiveRecord.class);
	
	// Used like index to get first result of search
	private static final int FIRST = 0;

	// Used to get class type of Entity
	private final Class<ActiveType> entityClass;

	@Inject
	private static EntityManager entityManager;

	@Inject
	private static Finder finder;

	/**
	 * Set with reflection, which Object type is Entity
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public ActiveRecord() {
		try{
			this.entityClass = (Class<ActiveType>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		} catch (ClassCastException classCastException) {
			throw new IllegalArgumentException(getClassName() 
					+ " must be one @Entity, please use annotation.");
		}

		this.finder = new Finder(entityClass, entityManager);
	}

	/**
	 * Creates a new instance of User into database
	 * 
	 * @return true if operation do not throw any exception
	 */
	public Object create() {
		logger.info("Creating one " + getClassName());

		try {
			execute(entityManager -> {
				entityManager.persist(this);
				return null;
			});
		} catch(EntityExistsException e) {
			throw new EntityExistsException("Entity already persisted");
		}

		return this;
	}

	/**
	 * Delete the entity instance from database.
	 * 
	 * @param Entity removed
	 */
	public void destroy() {
		execute(entityManager -> {
			entityManager.remove(ActiveRecord.this);
			return null;
		});
	}

	/**
	 * Gets entities found with these params
	 *  
	 * @param params refers to attributes of object
	 * 
	 * @return all instances found
	 */
	public static List<Entity> where(HashMap<String, Object> params) {
		List<Entity> results = new ArrayList<>();

		for(String key : params.keySet()) {
			List<Entity> paramsResult = finder.get(key, params.get(key));
			results.addAll(paramsResult);
		}

		return results;
	}

	/**
	 * Gets unique entity by your primary key
	 * 
	 * @param primary key of object
	 */
	public static ActiveRecord<?> find(Object primaryKey) {
		return (ActiveRecord<?>) finder.find(primaryKey);
	}

	/**
	 * Finds first matched occurrence
	 *  
	 * @param attr Attribute name
	 * @param value Attribute value
	 * 
	 * @return first matched found with this attribute or null
	 */
	public static ActiveRecord<Entity> findBy(String attr, Object value) {
		ActiveRecord<Entity> result = null;

		List<ActiveRecord<Entity>> allResults = new ArrayList<>();
		allResults.addAll((Collection<? extends ActiveRecord<Entity>>) finder.get(attr, value));

		if(!allResults.isEmpty()) {
			result = allResults.get(FIRST);
		} else {
			// do nothing, return null
		}

		return result;
	}

	/**
	 * Used to create customized find
	 * 
	 * @param options customized params of search
	 * @return Active
	 */
	public static Query findBy(Options options) {
		logger.info("Running a customizable finder");

		final String sql = "SELECT entity FROM " + getClassName()
				+ " entity WHERE entity."
				+ options.getAttribute() + " LIKE '" + options.getAttributeValue() + "' "
				+ " ORDER BY " + options.getOrderAtribute()
				+ " " + options.getOrder().toString();

		logger.info(sql);

		Query query = null;

		try {
			query = entityManager.createQuery(sql);
		} catch(IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException("Please, verify your options values.");
		}

		return query; 
	}

	/**
	 * Search by all Entity called
	 * 
	 * @return one List of all instance
	 */
	public static List<Entity> all() {
		return finder.all();
	}
	
	private static String getClassName() {
		return Thread.currentThread().getStackTrace()[2].getClassName();
	}

	/**
	 * Set used entity manager
	 */
	@SuppressWarnings("static-access")
	public void setEntityManager(EntityManager entityManager) {
		// this set is only for tests
		this.finder.setEntityManager(entityManager);

		ActiveRecord.entityManager = entityManager;
	}

	public static EntityManager getEntityManager() {
		return entityManager;
	}

	private static <Type> Type execute(Executor<Type> executor) {
		return executor.execute(getEntityManager());
	}
}
