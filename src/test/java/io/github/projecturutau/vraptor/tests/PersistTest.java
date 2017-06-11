package io.github.projecturutau.vraptor.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.projecturutau.vraptor.model.Sample;

public class PersistTest extends JPAHibernateTest {
	private Sample sample;

	@Before
	public void setUp() {
		this.sample = new Sample();
		this.sample.setEntityManager(entityManager);
	}

	@Test
	public void testCreate() {
		this.sample.setId(2);
		this.sample.setTitle("Second sample");
		this.sample.create();

		Assert.assertNotNull(entityManager.find(Sample.class, 2));
	}

	@Test
	public void testDestroy() {
		// TODO
	}

}
