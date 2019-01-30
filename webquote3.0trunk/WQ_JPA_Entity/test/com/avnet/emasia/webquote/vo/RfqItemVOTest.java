package com.avnet.emasia.webquote.vo;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class RfqItemVOTest {

	private RfqItemVO rfqItem;
	private Date oneDayBefore, oneDayAfter;
	
	@BeforeMethod
	public void setup() {
		Calendar cal =  Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		oneDayBefore =  cal.getTime();
		cal.add(Calendar.DATE, 2);
		oneDayAfter =  cal.getTime();
		
		rfqItem =  new RfqItemVO();
		
		rfqItem.setMpq(100);
		rfqItem.setMoq(300);
		rfqItem.setMov(200);
		rfqItem.setLeadTime("11 wks");
		rfqItem.setCostIndicator("A Book Cost");
		rfqItem.setPriceValidity("30");
		rfqItem.setShipmentValidity(oneDayAfter);
		rfqItem.setAqFlag(true);
		
		rfqItem.setSalesCostFlag(false);
		rfqItem.setCost(1d);
		rfqItem.setTargetResale("1.1");
		rfqItem.setMinSellPrice(1.05d);
		
		
				
	}
	
	public void matchAQConditionsTest1() {
		assertTrue(rfqItem.matchAQConditions());
	}

	public void matchAQConditionsTest2() {
		rfqItem.setMoq(null);
		rfqItem.setMov(null);
		assertFalse(rfqItem.matchAQConditions());
		rfqItem.setMoq(100);
		assertTrue(rfqItem.matchAQConditions());
		rfqItem.setMoq(null);
		rfqItem.setMov(100);
		assertTrue(rfqItem.matchAQConditions());
		
		
		
	}

	public void matchAQConditionsTest3() {
		rfqItem.setLeadTime("");
		assertFalse(rfqItem.matchAQConditions());
	}

	public void matchAQConditionsTest4() {
		rfqItem.setCostIndicator(null);
		assertFalse(rfqItem.matchAQConditions());
	}

	public void matchAQConditionsTest5() {
		rfqItem.setPriceValidity("30");
		assertTrue(rfqItem.matchAQConditions());
		rfqItem.setPriceValidity("01/01/2017");
		assertFalse(rfqItem.matchAQConditions());
	}
	
	public void matchAQConditionsTest6() {
		rfqItem.setShipmentValidity(oneDayBefore);;
		assertFalse(rfqItem.matchAQConditions());
	}

	public void matchAQConditionsTest7() {
		rfqItem.setTargetResale("0.7");;
		assertFalse(rfqItem.matchAQConditions());
		rfqItem.setTargetResale("1.05");
		assertTrue(rfqItem.matchAQConditions());
	}

	public void matchAQConditionsTest8() {
		rfqItem.setSalesCostFlag(true);
		rfqItem.setSalesCost(new BigDecimal("1.1"));
		assertFalse(rfqItem.matchAQConditions());
		
		rfqItem.setSuggestedResale(new BigDecimal("1.15"));
		assertTrue(rfqItem.matchAQConditions());
		
		rfqItem.setSalesCost(new BigDecimal("0.9"));
		rfqItem.setTargetResale(null);
		assertTrue(rfqItem.matchAQConditions());
	}


	
	
}
