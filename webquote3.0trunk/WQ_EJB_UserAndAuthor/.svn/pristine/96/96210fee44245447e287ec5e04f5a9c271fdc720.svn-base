package com.avnet.emasia.webquote.user.ejb;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.User;




public class UserSBTest {
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private UserSB sb;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Local_Source");
		em = emf.createEntityManager();
		sb = new UserSB();
		sb.em = em;
	}

	@Test
	public void testAllSubordinates(){
		em.getTransaction().begin();
		User user = new User();
//		user.setId(1L);
		user = em.find(User.class, 6L);
//		user = sb.findByEmployeeIdWithAllRelation("900001");
//		List<User> users = sb.findAllSubordinates(user);
		em.getTransaction().commit();
		
	}
	
	@Ignore
	public void testAllSalesForCs(){
		em.getTransaction().begin();
		User user = new User();
		user.setId(970L);
		user = em.find(User.class, 970L);
		List<User> subOrdinates = sb.findAllSubordinates(user, 10);
		List<User> sales = sb.findAllSalesForCs(user);
		em.getTransaction().commit();
		
	}	
	
	
	@Ignore
	public void testFindbyDataAccess(){
		em.getTransaction().begin();
		List<DataAccess> dataAccesses = new ArrayList<DataAccess>();
		
		DataAccess dataAccess = em.find(DataAccess.class, 1L);
		
		dataAccesses.add(dataAccess);
		
		List<BizUnit> bizUnits = new ArrayList<BizUnit>();
		
		BizUnit bizUnit = em.find(BizUnit.class, "AEMC");
		
		bizUnits.add(bizUnit);
		
//		List<User> users  = sb.findByDataAccess(dataAccesses, bizUnits);
		
		em.getTransaction().commit();
		
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
