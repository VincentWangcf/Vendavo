package com.avnet.emasia.webquote.entity;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuoteItemTest {

	
	private EntityManagerFactory emf;
	private EntityManager em;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Server_Source");
		em = emf.createEntityManager();
	}

	@Test
	public void findMaterial(){
		
		
		QuoteItem item =  em.find(QuoteItem.class, 10260254366L);
		QuoteItem newItem =  QuoteItem.newInstance(item);
		
		assertNotNull(newItem.getQuoteItemDesign());
		assertNotNull(newItem.getAttachments());
		
		newItem.setQuoteNumber("SDJHM");
		
		em.getTransaction().begin();
		em.persist(newItem);
		em.persist(newItem.getQuoteItemDesign());
		em.persist(newItem.getAttachments().get(0));
		em.persist(newItem.getQuoteResponseTimeHistorys().get(0));
		
		em.getTransaction().commit();
		System.out.println(newItem.getId());
	}
	
	
		
	@After
	public void tearDown(){
		if( em != null){
			em.close();
		}
		if (emf != null){
			emf.close();
		}
		
	}

}