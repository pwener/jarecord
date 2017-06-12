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

public abstract class ActiveRecord<ActiveType> implements Cloneable {

	private static final Logger logger = LoggerFactory.getLogger(ActiveRecord.class);

	// Used to get class type of Entity
	private final Class<ActiveType> entityClass;

	@Inject
	private static EntityManager entityManager;
	
	private Finder finder;

	/**
	 * Set with reflection, which Object type is Entity
	 */
	public ActiveRecord() {
		this.entityClass = (Class<ActiveType>) ((ParameterizedType) getClass()
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
		ActiveRecord.entityManager = entityManager;
	}
	
	public static EntityManager getEntityManager() {
		return entityManager;
	}
	
	protected static <Type> Type execute(Executor<Type> executor) {
		return executor.execute(getEntityManager());
	}
}
