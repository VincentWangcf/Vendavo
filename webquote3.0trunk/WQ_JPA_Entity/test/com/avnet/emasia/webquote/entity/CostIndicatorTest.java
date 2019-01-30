package com.avnet.emasia.webquote.entity;

import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.vo.RfqItemVO;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;


@Test
public class CostIndicatorTest {
	private CostIndicator ci;
	private Material material;;
	private NormalPricer np;
	
	private RfqItemVO rfqItem;
	
	Date date1, date2;
	
	@BeforeMethod
	public void setup() {
		
		Calendar cal = Calendar.getInstance();
		date1 = cal.getTime();
		cal.add(Calendar.MONTH, 1);
		date2 = cal.getTime();
		
		ci = new CostIndicator();
		ci.setName("A-MPP Cost");
		ci.setMoq(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);
		ci.setMpq(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);
		ci.setMov(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);
		ci.setLeadTime(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);
		ci.setMpqValue("MPQ");
		ci.setMoqValue("MOQ");
		ci.setMovValue("MOV");
		ci.setPriceValidity(1);
		ci.setShipmentValidity(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);
		ci.setPartDescription(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);
		ci.setPriceListRemarks(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);
		
		material =  MaterialFactory.getInstance().createMaterial();
		
		np =  (NormalPricer) material.getPricers().get(0);
		
		
		rfqItem = new RfqItemVO();
		rfqItem.setMpq(999);
		rfqItem.setMoq(999);
		rfqItem.setMov(999);
		rfqItem.setLeadTime("99 wks");
		rfqItem.setPriceListRemarks1("pr1");
		rfqItem.setPriceListRemarks2("pr2");
		rfqItem.setPriceListRemarks3("pr3");
		rfqItem.setPriceListRemarks4("pr4");
		rfqItem.setDescription("pd");
		rfqItem.setPriceValidity("30");
		rfqItem.setShipmentValidity(date1);
	}
	
	public void applyTest1() {
		ci.apply(np, rfqItem);
		assertEquals(rfqItem.getMpq().intValue(), 999);
		assertEquals(rfqItem.getMoq().intValue(), 999);
		assertEquals(rfqItem.getMoq().intValue(), 999);
		assertEquals(rfqItem.getLeadTime(), "99 wks");
		assertEquals(rfqItem.getPriceListRemarks1(), "pr1");
		assertEquals(rfqItem.getPriceListRemarks2(), "pr2");
		assertEquals(rfqItem.getPriceListRemarks3(), "pr3");
		assertEquals(rfqItem.getPriceListRemarks4(), "pr4");
		assertEquals(rfqItem.getDescription(), "pd");
		assertEquals(rfqItem.getPriceValidity(), "30");
		assertEquals(rfqItem.getShipmentValidity(), date1);
		
	}
	
	public void applyTest2() {
		ci.setMpq(QuoteSBConstant.COST_INDICATOR_RULE_USE_A_BOOK_IF_NOT_FOUND);

		rfqItem.setMpq(null);
		rfqItem.setMoq(null);
		rfqItem.setMov(null);
		rfqItem.setLeadTime(null);
		rfqItem.setPriceListRemarks1(null);
		rfqItem.setPriceListRemarks2(null);
		rfqItem.setPriceListRemarks3(null);
		rfqItem.setPriceListRemarks4(null);
		rfqItem.setDescription(null);
		rfqItem.setPriceValidity(null);
		rfqItem.setShipmentValidity(null);
		ci.apply(np, rfqItem);
		assertEquals(rfqItem.getMpq().intValue(), np.getMpq().intValue());
		assertEquals(rfqItem.getMoq().intValue(), np.getMoq().intValue());
		assertEquals(rfqItem.getMov().intValue(), np.getMov().intValue());
		assertEquals(rfqItem.getLeadTime(), np.getLeadTime());
		assertEquals(rfqItem.getPriceListRemarks1(), np.getPriceListRemarks1());
		assertEquals(rfqItem.getPriceListRemarks2(), np.getPriceListRemarks2());
		assertEquals(rfqItem.getPriceListRemarks3(), np.getPriceListRemarks3());
		assertEquals(rfqItem.getPriceListRemarks4(), np.getPriceListRemarks4());
		assertEquals(rfqItem.getDescription(), np.getPartDescription());
		assertEquals(rfqItem.getPriceValidity(), np.getPriceValidity());
		assertEquals(rfqItem.getShipmentValidity(), np.getShipmentValidity());
	}

	/*
	public void applyTest3() {
		ci.setMpq(QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND);
		ci.apply(np, rfqItem);
		assertEquals(rfqItem.getMpq().intValue(), 999);
		assertEquals(rfqItem.getMoq().intValue(), 999);
		assertEquals(rfqItem.getMoq().intValue(), 999);


	}
	
	public void applyTest4() {
		ci.setMpq(QuoteSBConstant.COST_INDICATOR_RULE_USE_BLANK_IF_NOT_FOUND);

		rfqItem.setMpq(null);
		rfqItem.setMoq(null);
		rfqItem.setMov(null);
		ci.apply(np, rfqItem);
		assertEquals(rfqItem.getMpq().intValue(), 300);
		assertEquals(rfqItem.getMoq().intValue(), 300);
		assertEquals(rfqItem.getMoq().intValue(), 300);
	}
*/	
	
	
	
}
