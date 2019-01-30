package com.avnet.emasia.webquote.quote.ejb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
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

import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;



public class MyQuoteSBTest {
	
	MyQuoteSearchSB sb;
	QuoteSB quoteSB;
	MyQuoteSearchCriteria criteria;
	private EntityManagerFactory emf;
	private EntityManager em;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Local_Source");
		em = emf.createEntityManager();
		
		sb = new MyQuoteSearchSB();
		sb.em = em;
		
		quoteSB = new QuoteSB();
		quoteSB.em = em;
		
		criteria = new MyQuoteSearchCriteria();
	}
	
	@Test
	public void searchByQuoteNumber1(){
		
		Query query  =em.createQuery("select q from Quote q where q.formNumber = 'FM336288'");
		Quote quote = (Quote) query.getSingleResult();
				
	}
	

	@Ignore
	public void searchByQuoteNumber(){
		
		List<String> names = new ArrayList<String>();
		names.add("cccb");
//		names.add("fullmfrpartnumber11");
		
//		criteria.setCustomerNames(names);
		
//		List<String> mfrs = new ArrayList<String>();
//		mfrs.add("NSC");
//		mfrs.add("PAS");
//		mfrs.add("FSC");
//		mfrs.add("INT");
//		mfrs.add("XLX");
		
//		criteria.setTeam("001");
		
		Calendar from = new GregorianCalendar(2013,0,1);
		Calendar to = new GregorianCalendar(2013,3,1);
		
		criteria.setSentOutDateFrom(from.getTime());
		criteria.setSentOutDateTo(to.getTime());
		
		
		//criteria.setMfr("NSC");
		
//		criteria.setMfrList(mfrs);
		
		em.getTransaction().begin();
		
//		List<QuoteItem> items = sb.search(criteria);
//		
//		em.detach(items);
		
		em.getTransaction().commit();
		
//		for(QuoteItem item : items){
//		}
//		
	}
	

	
	@Ignore
	public void quoteSearch(){
		
//		criteria.setQuoteNumber("WR000123");
//		criteria.setMfr("NSC");
		
		em.getTransaction().begin();
		
		Quote quote =em.find(Quote.class, "FM336288");
		
		QuoteItem quoteItem =em.find(QuoteItem.class, "WR000123");
		

		
		em.getTransaction().commit();
		em.detach(quoteItem);
		
		
		
		
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
