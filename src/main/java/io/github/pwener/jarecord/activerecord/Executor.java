package io.github.pwener.jarecord.activerecord;

import javax.persistence.EntityManager;

public interface Executor<T> {
	T execute(EntityManager manager);
}
