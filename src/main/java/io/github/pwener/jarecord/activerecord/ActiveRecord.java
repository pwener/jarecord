package io.github.pwener.jarecord.activerecord;

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

import io.github.pwener.jarecord.activerecord.finder.Finder;
import io.github.pwener.jarecord.activerecord.finder.Options;
import io.github.pwener.jarecord.activerecord.singleton.EntityManagerSingleton;
import io.github.pwener.jarecord.activerecord.singleton.FinderSingleton;

public abstract class ActiveRecord<ActiveType> implements Serializable {

	private static final long serialVersionUID = 6819093423193006048L;

	private static final Logger logger = LoggerFactory.getLogger(ActiveRecord.class);

	/**
	 * Used like index to get first result of {@link #findBy(String, Object)}
	 */
	private static final int FIRST = 0;

	/**
	 *  Used to get class type of Entity
	 */
	private final Class<ActiveType> entityClass;

	/**
	 * Set with reflection, which Object type is Entity
	 */
	@SuppressWarnings({ "unchecked" })
	public ActiveRecord() {
		try {
			this.entityClass = (Class<ActiveType>) ((ParameterizedType) getClass()
					.getGenericSuperclass())
					.getActualTypeArguments()[0];
		} catch (ClassCastException classCastException) {
			throw new IllegalArgumentException(getClassName() 
					+ " must be one @Entity, please use annotation.");
		}
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
		} catch (EntityExistsException e) {
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

		for (String key : params.keySet()) {
			List<Entity> paramsResult = FinderSingleton.get().get(key, params.get(key));
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
		return (ActiveRecord<?>) FinderSingleton.get().find(primaryKey);
	}

	/**
	 * Finds first matched occurrence
	 * 
	 * @param attr Attribute name
	 * @param value Attribute value
	 * 
	 * @return first matched found with this attribute or null
	 */
	@SuppressWarnings("unchecked")
	public static ActiveRecord<Entity> findBy(String attr, Object value) {
		ActiveRecord<Entity> result = null;

		List<ActiveRecord<Entity>> allResults = new ArrayList<>();
		allResults.addAll(
				(ArrayList<? extends ActiveRecord<Entity>>) FinderSingleton.get()
					.get(attr, value));

		if (!allResults.isEmpty()) {
			result = allResults.get(FIRST);
		} else {
			// do nothing, return null
		}

		return result;
	}

	/**
	 * Used to create customized find. With this method you could create a find
	 * with options using pattern string to LIKE queries, by example:
	 * 
	 * new Options("title", "Sample%");
	 * 
	 * It will use % like multi character wildcards.
	 * 
	 * @param options customized params of search
	 * 
	 * @return Active
	 */
	public static Query findBy(Options options) {
		logger.info("Running a customizable finder");

		String sql = "SELECT entity FROM " + getClassName() 
				+ " entity WHERE entity." + options.getAttribute()
				+ " LIKE '" + options.getAttributeValue() + "' ";

		if (options.isOrdanable()) {
			sql += " ORDER BY " + options.getOrderAtribute() 
				+ " " + options.getOrder().toString();
		} else {
			logger.debug("Is not ordenable");
		}

		logger.info(sql);

		Query query = null;

		try {
			query = EntityManagerSingleton.get().createQuery(sql);
		} catch (IllegalArgumentException illegalArgumentException) {
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
		return FinderSingleton.get().all();
	}

	@Inject
	public void setEntityManager(EntityManager entityManager) {
		EntityManagerSingleton.push(entityManager);
	}

	@Inject
	public void setFinder(Finder finder) {
		Finder myOwnfinder = finder;
		myOwnfinder.setEntityClass(entityClass);

		FinderSingleton.push(myOwnfinder);
	}

	private static String getClassName() {
		return Thread.currentThread().getStackTrace()[2].getClassName();
	}

	/**
	 * Interface to support lambda function
	 */
	private <Type> Type execute(Executor<Type> executor) {
		return executor.execute(EntityManagerSingleton.get());
	}
}
