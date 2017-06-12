package io.github.projecturutau.vraptor.tests.activerecord;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.projecturutau.vraptor.model.Sample;
import io.github.projecturutau.vraptor.model.SampleNotEntity;
import io.github.projecturutau.vraptor.tests.JPAHibernateTest;

public class PersistTest extends JPAHibernateTest {
	private Sample sample;

	@Before
	public void setUp() {
		this.sample = new Sample();
		this.sample.setEntityManager(entityManager);

		System.out.println("Init with follow samples:");
		for (Sample s : getResults()) {
			System.out.println("Sample avaliable: " + s.getId());
		}
	}

	@Test
	public void testCreate() {
		this.sample.setId(2);
		this.sample.setTitle("Second sample");
		this.sample.create();

		Assert.assertNotNull(entityManager.find(Sample.class, 2));
	}
	
	@Test(expected=EntityExistsException.class)
	public void testDuplicatedCreateException() {
		final Integer copiedIdentifier = 666; 

		Sample original = new Sample();
		original.setId(copiedIdentifier);
		original.create();
		
		Sample duplicated = new Sample();
		duplicated.setId(copiedIdentifier);
		duplicated.create();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidArgumentCreateException() {
		SampleNotEntity notEntity = new SampleNotEntity();
		notEntity.setId(7L);
		notEntity.create();
	}

	@Test
	public void testUpdate() {
		Sample updateSample = entityManager.find(Sample.class, 5);

		updateSample.setTitle("Testing update");

		Assert.assertEquals("Testing update",
				entityManager.find(Sample.class, 5).getTitle());
	}

	@Test
	public void testDestroy() {
		Sample deathSample = entityManager.find(Sample.class, 1);

		deathSample.destroy();

		Assert.assertNull(entityManager.find(Sample.class, 1));
	}

	@SuppressWarnings("unchecked")
	private List<Sample> getResults() {
		return entityManager.createQuery("Select s from Sample s").getResultList();
	}
}
