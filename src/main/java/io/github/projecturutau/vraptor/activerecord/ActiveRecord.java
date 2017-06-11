package io.github.projecturutau.vraptor.activerecord;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.projecturutau.vraptor.activerecord.finder.Finder;

public abstract class ActiveRecord<T> {

	private static final Logger logger = LoggerFactory.getLogger(ActiveRecord.class);

	// Used to get class type of Entity
	private final Class<T> entityClass;

	@Inject
	private EntityManager entityManager;

	private Finder finder;

	/**
	 * Set with reflection, which Object type is Entity
	 */
	public ActiveRecord() {
		this.entityClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];

		this.finder = new Finder(entityClass, entityManager);
	}

	/**
	 * Creates a new instance of User into database
	 * 
	 * @return true if operation do not throw any exception
	 */
	public Object create() {

		logger.info("Creating one " + entityClass.getName());

		try {
			entityManager.persist(this);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException("An invalid param has been passed to create method");
		} catch (EntityExistsException entityExistsException) {
			entityExistsException.printStackTrace();
		}

		return this;
	}

	/**
	 * Delete the entity instance from database.
	 * 
	 * @param Entity removed
	 */
	public void destroy() {
		entityManager.remove(this);
	}

	/**
	 * Gets entities found with these params
	 *  
	 * @param params refers to attributes of object
	 * @return all instances found
	 */
	public List<Entity> where(HashMap<String, Object> params) {
		List<Entity> results = new ArrayList<>();

		for(String key : params.keySet()) {
			List<Entity> paramsResult = finder.get(key, params.get(key));
			results.addAll(paramsResult);
		}

		return results;
	}

	/**
	 * Gets unique entity by your id
	 */
	public Entity findById(Long id) {
		return finder.find(id);
	}

	/**
	 * Set used entity manager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
