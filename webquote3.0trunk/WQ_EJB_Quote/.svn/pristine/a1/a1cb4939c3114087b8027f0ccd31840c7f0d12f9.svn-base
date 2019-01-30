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

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;



public class MaterialSBTest {
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private MaterialSB sb;
	
	@Before
	public void setUp(){
		emf = Persistence.createEntityManagerFactory("Local_Source");
		em = emf.createEntityManager();
		
		sb = new MaterialSB();
		sb.em = em;
		
	}
	
	@Test
	public void testWFindMaterialByMfrAndFullMfrPart(){
		BizUnit bizUnit = em.find(BizUnit.class, "AEMC");
		//sb.wFindMaterialByMfrAndFullMfrPart("TIS", "12", bizUnit);
	}
	
	@Ignore
	public void wFindMaterialByMfrAndFullMfrPart(){
		
		BizUnit bu = em.find(BizUnit.class, "AEMC");
	//	List<Material> m = sb.findMaterialByMfrAndFullMfrPart("PHA", "74HC02D,653", bu);
		

//		sb.wFindMaterialByMfrAndFullMfrPart("INF","X","AEMC");
				
	}
	
	@Ignore
	public void testFindProgramMaterialbyMfrAndPartNumber(){
		BizUnit bu = em.find(BizUnit.class, "AEMC");
		List<String[]> mfrAndPartNumbers = new ArrayList<String[]>();
		String[] mfrAndPartNumber = new String[2];
		mfrAndPartNumber[0] = "STM";
		mfrAndPartNumber[1] = "L7815CV-DGX";
		mfrAndPartNumbers.add(mfrAndPartNumber);

		mfrAndPartNumber = new String[2];
		mfrAndPartNumber[0] = "STM";
		mfrAndPartNumber[1] = "L7815CV-DGY";
		
		mfrAndPartNumbers.add(mfrAndPartNumber);
		
		
		//List<ProgramMaterial> programMaterials = sb.findProgramMaterialbyMfrAndPartNumber(mfrAndPartNumbers	);
		
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
