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
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteNumber;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;



public class ManufacturerSBTest {
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private ManufacturerSB sb;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Local_Source");
		em = emf.createEntityManager();
		
		sb = new ManufacturerSB();
		sb.em = em;
		
	}
	
	@Test
	public void test(){
		List<Manufacturer> mfrs = sb.findAll();
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
