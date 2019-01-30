package com.avnet.emasia.webquote.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;



public class PricerUploadTest {
	private EntityManagerFactory emf;
	private EntityManager em;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Server_Source");
		em = emf.createEntityManager();
	}


	
	@Test
	public void pricer() {
		
		em.getTransaction().begin();
		
/*		Material material = em.find(Material.class, 10135372810l );
		Material material2 = null;
		TypedQuery<Material> query = em.createQuery("select m from Material m where (m.valid is null or m.valid=true) and "
				+ "m.manufacturer.name like :mfr and m.fullMfrPartNumber= :fullMfrPartNumber", Material.class);
		query.setParameter("mfr", "MCC");
		query.setParameter("fullMfrPartNumber", "DS51025");
		List<Material> materials = query.getResultList();
		if (! materials.isEmpty()) {
			material2 = materials.get(0);
		}
		System.out.println(material == material2);
		
		CostIndicator costIndicator = em.find(CostIndicator.class, "A-Book Cost");
		
		Date date  = new Date();
		SalesCostPricer pricer = new SalesCostPricer();
		pricer.setBizUnitBean(new BizUnit("CHINA"));
		pricer.setSalesCostType(SalesCostType.ZBMD);
		pricer.setMaterial(material);
		pricer.setCost(0.2d);
		pricer.setCostIndicator(costIndicator);
		pricer.setPriceValidity("30");
		pricer.setShipmentValidity(date);
		pricer.setMpq(100);
		pricer.setMoq(200);
		pricer.setMov(300);
		pricer.setLeadTime("3wk");
		pricer.setDisplayQuoteEffDate(date);
		pricer.setTermsAndConditions("ni ma");
		pricer.setAventQcCommnets("ni ma 2");
		pricer.setDisplayOnTop(1);
		pricer.setSpecialItemFlag(3);
		pricer.setAvailableToSellQty(1000);
		pricer.setQtyControl(10000);
		
		QuantityBreakPrice qbp = new QuantityBreakPrice();
		qbp.setSalesCost(new BigDecimal(3.2d));
		qbp.setQuantityBreak(1000);
		qbp.setMaterialDetail(pricer);
		
		QuantityBreakPrice qbp2 = new QuantityBreakPrice();
		qbp2.setSalesCost(new BigDecimal(3.1d));
		qbp2.setQuantityBreak(10000);
		qbp2.setMaterialDetail(pricer);
		
		List<QuantityBreakPrice> qbps = new ArrayList<>();
		qbps.add(qbp);
		qbps.add(qbp2);
		
		pricer.setQuantityBreakPrices(qbps);
		
		pricer.setQtyIndicator("MPQ");
		pricer.setNewItemIndicator(false);
		pricer.setAllocationFlag(true);
		pricer.setAqFlag(true);
		
		material.getPricers().add(pricer);
		System.out.println(material.getPricers().size());
		System.out.println(material2.getPricers().size());
		em.merge(material);
		material2.getPricers().add(pricer);
		System.out.println(material.getPricers().size());
		System.out.println(material2.getPricers().size());*/
		
		User user  = em.find(User.class, 132L);
		user.setSalsCostAccessFlag(! user.isSalsCostAccessFlag());
		em.getTransaction().commit();
		
//		System.out.println(pricer.getId());
		
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
