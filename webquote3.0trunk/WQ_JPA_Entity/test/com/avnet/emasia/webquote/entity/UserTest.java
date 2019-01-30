package com.avnet.emasia.webquote.entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.avnet.emasia.webquote.entity.User;



public class UserTest {
	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityManager em2;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Server_Source");
		em = emf.createEntityManager();
		em2 = emf.createEntityManager();
	}

	//@Test
	//@Scan
	public void testEmbed(){
		EntityTransaction tran = em.getTransaction();
		tran.begin();
		User user1 = em.find(User.class, 731L);
		user1.setName("Tran1");
		//user1.setName("zdt");
		//user1.userEmbedTestTwo.setName("zdtest");
		//user1.userEmbedTestOne.setName("zdtest2");
		em.persist(user1);
		//em2.s;
		EntityTransaction tran2 = em2.getTransaction();
		User user2 = em2.find(User.class, 731L);
		em2.refresh(user2);;
		System.out.println(user2.getName());
		tran2.commit();
		tran.commit();
		
	}
	
	//@Test
	public void testPricerEmbed(){
		EntityTransaction tran = em.getTransaction();
		tran.begin();
		Pricer p = em.find(Pricer.class, 10136347570L);
		//((NormalPricer)p).setMinSellPriceM(null);
		tran.commit();
		
	}
	
	@org.junit.Ignore
	public void add(){
		User boss1 = new User();
		boss1.setEmployeeId("001");
		
		User user1 = new User();
		user1.setEmployeeId("002");

		User user2 = new User();
		user2.setEmployeeId("003");
		
		Set<User> subordinates = new LinkedHashSet<User>();
		subordinates.add(user1);
		subordinates.add(user2);
		
		user1.setReportTo(boss1);
		user2.setReportTo(boss1);
		
		em.getTransaction().begin();
		
		em.persist(user1);
		em.getTransaction().commit();
		
	}
	
	@org.junit.Ignore
	public void csAndSalesMapping(){
		User cs1 = new User();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		cs1.setActive(true);
		User cs2 = new User();
		
		User sales1 = new User();
		User sales2 = new User();
		
		List<User> sales = new ArrayList<User>();
		sales.add(sales1);
		sales.add(sales1);
		
//		cs1.setSalesForCs(sales);
		
		em.getTransaction().begin();
		
//		em.persist(cs1);
		em.getTransaction().commit();
		
	}
	@org.junit.Ignore
	public void addUser(){
/*		User user1 = new User();
		user1.setEmployeeId("001");
		user1.setFirstName("Li");
		user1.setLastName("Tonmy");

		user1.setMarkedAsDelete(false);
		
		em.getTransaction().begin();
		em.persist(user1);
		em.getTransaction().commit();*/
		
		Screen screen = new Screen();
		screen.setId(2L);
		screen.setPath("/sales/**");
		screen.setVersion(0);
		screen.setPathOrder(0);
//		screen.setCreated(Calendar.getInstance());
		
		Role role = new Role();
		role.setName("ROLE_SALES");
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
//		screen.setRoles(roles);
		
//		em.getTransaction().begin();
//		em.persist(screen);
//		em.getTransaction().commit();
		
		
/*    	Query query = em.createQuery("SELECT a FROM com.avnet.emasia.webquote.entity.Screen a order by a.pathOrder");
    	List<Screen> screens = query.getResultList();
		for(Screen screen1 : screens){
			List<String> roles1= new ArrayList<String>();
			for(Role role1 : screen.getRoles()){
				roles1.add(role.getName());
			}
		
		}*/
		
		Query query = em.createQuery("select u from com.avnet.emasia.webquote.entity.User u left join fetch u.roles " +
				"left join fetch u.suppliers left join fetch u.productGroups left join fetch u.programTypes " +
				"left join fetch u.customers left join fetch u.groups left join fetch u.regions left join fetch u.bizUnits " +
				"left join fetch u.reportTo " +
				"where u.employeeId=:employeeId");
		query.setParameter("employeeId", "admin");
		User user = (User)query.getSingleResult();
		
		Assert.assertEquals("admin", user.getEmployeeId());
		
	}
	
	@Test
	public void testAllSubordinates(){
		User user = new User();
		user.setId(968L);
		
		
		
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
