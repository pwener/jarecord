package io.github.projecturutau.vraptor.tests.activerecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.projecturutau.vraptor.model.Sample;
import io.github.projecturutau.vraptor.tests.JPAHibernateTest;

public class FinderTest extends JPAHibernateTest {
	private Sample sample;

	@Before
	public void setUp() {
		this.sample = new Sample();
		this.sample.setEntityManager(entityManager);
	}

	@Test
	public void testWhereWithManyReturns() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("id", 1);
		params.put("title", "Sample to update");

		List<Entity> samples = new ArrayList<>();
		samples.addAll(Sample.where(params));

		Assert.assertEquals(2, samples.size());
	}

	@Test
	public void testWhereWithOneReturn() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("id", 1);

		List<Entity> samples = new ArrayList<>();
		samples.addAll(Sample.where(params));

		Assert.assertEquals(1, samples.size());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidParams() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("id", null);

		Sample.where(params);
	}

	@Test
	public void testFindByPrimaryKey() {
		Assert.assertNotNull(Sample.find(1));
	}
	
	@Test
	public void testAll() {
		Assert.assertEquals(2, Sample.all().size());
	}
	
	@Test
	public void testFindBy() {
		Assert.assertNotNull(Sample.findBy("id", 1));
		
		Assert.assertNull(Sample.findBy("title", "xpto"));
	}
}
