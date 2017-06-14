package io.github.pwener.jarecord.activerecord.singleton;

import javax.persistence.EntityManager;

import io.github.pwener.jarecord.activerecord.ActiveRecord;

/**
 * This Singleton is only to keep one static reference of EntityManager, 
 * to be possible uses instance into static {@link ActiveRecord} methods 
 */
public class EntityManagerSingleton {

	private static EntityManager entityManager;
	
	public static EntityManager get() {
		return entityManager;
	}
	
	public static void push(EntityManager entityManager) {
		EntityManagerSingleton.entityManager = entityManager;
	}
}
