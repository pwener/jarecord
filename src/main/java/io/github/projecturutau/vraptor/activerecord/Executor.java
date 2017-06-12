package io.github.projecturutau.vraptor.activerecord;

import javax.persistence.EntityManager;

public interface Executor<T> {
	T execute(EntityManager manager);
}
