package com.avnet.emasia.webquote.quote.ejb;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;



public class AttachmentSBTest {
	
	private EntityManagerFactory emf;
	private EntityManager em;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Local_Source");
		em = emf.createEntityManager();
		
		
	}
	
	@Test
	public void save(){
		Attachment a = new Attachment();
		File file = new File("D:\\temp\\Copy.asmx");
		try {
			FileInputStream in = new FileInputStream(file);
			byte[] image = 	new byte[(int)file.length()];
			in.read(image);
			a.setFileImage(image);
			
			em.getTransaction().begin();
			QuoteItem item = em.find(QuoteItem.class, 285755L);
			a.setQuoteItem(item);
			a.setFileName(file.getName());
			
			em.persist(a);
			em.getTransaction().commit();
			
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, "exception in persistoing the object : "+e.getMessage(),e);
		}
		
				
	}
	
	@Test
	public void get(){
		Attachment attachmetn = em.find(Attachment.class, 55600L);
		
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
