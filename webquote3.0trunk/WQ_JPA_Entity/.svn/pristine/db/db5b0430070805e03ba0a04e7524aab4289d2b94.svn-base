//package com.avnet.emasia.webquote.entity;
//
//import static org.testng.Assert.assertEquals;
//import static org.testng.Assert.*;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//import javax.persistence.TypedQuery;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//@Test
//public class MaterialTest2 {
//
//	
//	private EntityManagerFactory emf;
//	private EntityManager em;
//	
//	@Before
//	public void setUp(){
//		emf = Persistence.createEntityManagerFactory("Server_Source");
//		em = emf.createEntityManager();
//	}
//
//	@Test
////	@Ignore
//	public void findMaterial(){
//		
//		/*Query query = em.createQuery("select u from com.avnet.emasia.webquote.entity.User u left join fetch u.roles " +
//				"left join fetch u.suppliers left join fetch u.productGroups left join fetch u.programTypes " +
//				"left join fetch u.customers left join fetch u.groups left join fetch u.regions left join fetch u.bizUnits " +
//				"left join fetch u.reportTo " +
//				"where u.employeeId=:employeeId");
//		query.setParameter("employeeId", "admin");
//		User user = (User)query.getSingleResult();
//		
//		Assert.assertEquals("admin", user.getEmployeeId());*/
//		User user = em.find(User.class, 132L);
//		BizUnit china = em.find(BizUnit.class, "CHINA");
//		
//		BizUnit asia = em.find(BizUnit.class, "ASIA");
//		Assert.assertEquals(china.getHigherBizUnit(), asia);
//		
//		for (BizUnit bizUnit : asia.getSubBizUnits()) {
//			System.out.println(bizUnit.getName());
//		}
//		System.out.println(asia.getSelfAndAllSubBizUnits());
//		Assert.assertEquals("901518", user.getEmployeeId());
//		
//		Material material =  em.find(Material.class, 10136347569L);
//		material.getPricers().size();
//		
//		TypedQuery<QtyBreakPricer> query = em.createQuery("select q from QtyBreakPricer q where q.id =10277401196", QtyBreakPricer.class);
//		List<QtyBreakPricer> list = query.getResultList();
//		System.out.println(list.size());
//		System.out.println(list.get(0).getQuantityBreakPrices().get(0).getMaterialDetail().getAvnetQcComments());
//	}
//	
//	@Test
//	@Ignore
//	public void pricer() {
//		Material material = em.find(Material.class, 10266752339L );
//		CostIndicator costIndicator = em.find(CostIndicator.class, "A-Book Cost");
//		
//		Date date  = new Date();
//		SalesCostPricer pricer = new SalesCostPricer();
//		pricer.setBizUnitBean(new BizUnit("CHINA"));
//		pricer.setSalesCostType(SalesCostType.ZBMD);
//		pricer.setMaterial(material);
//		pricer.setCost(0.2d);
//		pricer.setCostIndicator(costIndicator);
//		pricer.setPriceValidity("30");
//		pricer.setShipmentValidity(date);
//		pricer.setMpq(100);
//		pricer.setMoq(200);
//		pricer.setMov(300);
//		pricer.setLeadTime("3wk");
//		pricer.setDisplayQuoteEffDate(date);
//		pricer.setTermsAndConditions("ni ma");
//		pricer.setAvnetQcComments("ni ma 2");
//		pricer.setDisplayOnTop(1);
//		pricer.setSpecialItemFlag(3);
//		pricer.setAvailableToSellQty(1000);
//		pricer.setQtyControl(10000);
//		
//		QuantityBreakPrice qbp = new QuantityBreakPrice();
//		qbp.setSalesCost(new BigDecimal(3.2d));
//		qbp.setQuantityBreak(1000);
//		qbp.setMaterialDetail(pricer);
//		
//		QuantityBreakPrice qbp2 = new QuantityBreakPrice();
//		qbp2.setSalesCost(new BigDecimal(3.1d));
//		qbp2.setQuantityBreak(10000);
//		qbp2.setMaterialDetail(pricer);
//		
//		List<QuantityBreakPrice> qbps = new ArrayList<>();
//		qbps.add(qbp);
//		qbps.add(qbp2);
//		
//		pricer.setQuantityBreakPrices(qbps);
//		
//		pricer.setQtyIndicator("MPQ");
//		pricer.setNewItemIndicator(false);
//		pricer.setAllocationFlag(true);
//		pricer.setAqFlag(true);
//		
//		em.getTransaction().begin();
//		em.persist(pricer);
//		em.getTransaction().commit();
//		
//		System.out.println(pricer.getId());
//		
//	}
//	
//	@Test
//	@Ignore
//	public void pricer2() {
//		Material material = em.find(Material.class, 10135372810l );
//		CostIndicator costIndicator = em.find(CostIndicator.class, "A-Book Cost");
//		ProgramType programType = em.find(ProgramType.class, 1L);
//		
//		Date date  = new Date();
//		SimplePricer pricer = new SimplePricer();
//		pricer.setBizUnitBean(new BizUnit("CHINA"));
//		pricer.setMaterial(material);
//		pricer.setCost(0.2d);
//		pricer.setCostIndicator(costIndicator);
//		pricer.setPriceValidity("30");
//		pricer.setShipmentValidity(date);
//		pricer.setMpq(100);
//		pricer.setMoq(200);
//		pricer.setMov(300);
//		pricer.setLeadTime("3wk");
//		pricer.setDisplayQuoteEffDate(date);
//		pricer.setTermsAndConditions("ni ma");
//		pricer.setAvnetQcComments("ni ma 2");
//		pricer.setProgramType(programType);;
//		pricer.setDisplayOnTop(1);
//		pricer.setSpecialItemFlag(3);
//		pricer.setAvailableToSellQty(1000);
//		pricer.setQtyControl(10000);
//		
//		QuantityBreakPrice qbp = new QuantityBreakPrice();
//		qbp.setSalesCost(new BigDecimal(3.2d));
//		qbp.setQuantityBreak(1000);
//		qbp.setMaterialDetail(pricer);
//		
//		QuantityBreakPrice qbp2 = new QuantityBreakPrice();
//		qbp2.setSalesCost(new BigDecimal(3.1d));
//		qbp2.setQuantityBreak(10000);
//		qbp2.setMaterialDetail(pricer);
//		
//		List<QuantityBreakPrice> qbps = new ArrayList<>();
//		qbps.add(qbp);
//		qbps.add(qbp2);
//		
//		pricer.setQuantityBreakPrices(qbps);
//		
//		
//		em.getTransaction().begin();
//		em.persist(pricer);
//		em.getTransaction().commit();
//		
//		System.out.println(pricer.getId());
//		
//	}	
//	
//	@Test
//	@Ignore
//	public void xxx() {
//		SapDpaCust cg = new SapDpaCust();
//		cg.setName("group1");
//		cg.setBizUnit(new BizUnit("ASEAN"));
//		cg.setSalesOrg("HK01");
//		cg.setSoldToCustNumber("2202");
//		cg.setShipToCustNumber("2203");
//		cg.setEndCustNumber("2204");
//		Date date = new Date();
//		cg.setCreatedOn(date);
//		cg.setLastUpdatedOn(date);
////		em.getTransaction().begin();
////		User user = em.find(User.class, 132L);
////		user.setLastUpdatedOn(new Date());
////		em.merge(user);
////		em.getTransaction().commit();
//		em.getTransaction().begin();
//		em.persist(cg);
//		em.getTransaction().commit();
//	}
//		
//	@After
//	public void tearDown(){
//		if( em != null){
//			em.close();
//		}
//		if (emf != null){
//			emf.close();
//		}
//		
//	}
//
//}