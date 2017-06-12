package io.github.projecturutau.vraptor.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.h2.tools.RunScript;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAHibernateTest {
	private static final Logger logger = LoggerFactory.getLogger(JPAHibernateTest.class);
	
	protected static EntityManagerFactory entityManagerFactory;
	protected static EntityManager entityManager;

	@BeforeClass
	public static void init() throws FileNotFoundException, SQLException {
		entityManagerFactory = Persistence.createEntityManagerFactory("default");
		entityManager = entityManagerFactory.createEntityManager();
	}

	@Before
	public void initializeDatabase() {
		Session session = entityManager.unwrap(Session.class);
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					File script = new File(getClass().getResource("/data.sql").getFile());
					RunScript.execute(connection, new FileReader(script));
				} catch (FileNotFoundException e) {
					throw new RuntimeException("could not initialize with script");
				}
			}
		});
	}

	@AfterClass
	public static void tearDown() {
		entityManager.clear();
		entityManager.close();
		entityManagerFactory.close();
	}

	public void initTransaction() {
		EntityTransaction transaction = entityManager.getTransaction();

		if(!transaction.isActive()) {
			transaction.begin();
		} else {
			logger.info("Transaction already open!");
		}
	}

	public void commitTransaction() {
		EntityTransaction transaction = entityManager.getTransaction();

		if(transaction.isActive()) {
			transaction.commit();
		} else {
			logger.info("Have none transaction open");
		}
	}
}
