package com.avnet.emasia.webquote.quote.ejb;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.MaterialType;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteNumber;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;



public class QuoteSBTest {
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private MaterialSB sb;
	private QuoteSB quoteSB;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Local_Source");
		em = emf.createEntityManager();
		
		sb = new MaterialSB();
		quoteSB = new QuoteSB()	;
		quoteSB.em = em;
		sb.em = em;
		
	}
	
	@Test
	public void findQuotesByMfrAndBizUnit(){
		QuoteItem item = em.find(QuoteItem.class, 285755L);
		
	}
	
	@Ignore
	public void saveQuote(){
    	BizUnit bizUnit = new BizUnit();
    	bizUnit.setName("AEMC");
    	
    	User user = new User();
    	user.setId(1);
    	
    	Customer customer = new Customer();
    	customer.setCustomerNumber("000012345");
    	
    	Quote quote = new Quote();
    	quote.setBizUnit(bizUnit);
    	//quote.setBomFlag(false);
    	quote.setCreatedBy(user.getCreatedBy());
    	quote.setCreatedName(user.getName());
    	quote.setCreatedOn(new Date());
    	
    	
    	
    	
//    	quote.setFormNumber(quoteNumberSB.nextFormNumber(bizUnit));
//    	quote.setLoaFlag(false);
//    	quote.setNewCustomerFlag(false);
//    	quote.setOrderOnHandFlag(false);
//    	quote.setProjectName("HUAWEI");
//    	quote.setSalesEmployeeNumber("906341");
//    	quote.setSentOutTime(new Date());
//    	quote.setSubmittedBy("906341");
//    	quote.setTeam("000");
    	quote.setCreatedOn(new Date());
//    	quote.setUploadTime(new Date());
    	quote.setYourReference("Requester Reference");
    	
    	List<QuoteItem> items = new ArrayList<QuoteItem>();
    	
/*    	for(int i = 0; i < 100; i++){
        	QuoteItem item = new QuoteItem();
        	item.setQuote(quote);
        	item.setQuoteNumber(quoteNumberSB.nextNumber(quoteNumber));
//        	item.setMfr(nw"NSC");
//        	item.setFullMfrPartNumber("LM324N");
        	item.setRequestedQty(new BigDecimal("100"));
        	item.setCreatedOn(new Date());
        	item.setCreatedBy(user);
        	items.add(item);
    	}*/
    	
    	
    	
//    	QuoteItem item2 = new QuoteItem();
//    	item2.setSoldToCustomer(customer);
//    	item2.setQuote(quote);
//    	Material material = new Material();
//    	material.setId(2311L);
////    	item2.setRequestedMaterial(material);
//    	item2.setRequestedQty(new Integer("10000"));    	
//    	item2.setSentOutTime(new Date());
    	
    	
//    	items.add(item2);
    	
    	quote.setQuoteItems(items);
    	
    	em.getTransaction().begin();
    	
//    	em.merge(item2);
    	em.merge(quote);
    	
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
