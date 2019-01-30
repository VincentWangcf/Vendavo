package com.avnet.emasia.webquote.entity;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class QtyCalTest {

	private QtyCal qtyCal;
	
	@BeforeMethod
	public void setup() {
		qtyCal = new QtyCal(100, 300, 1100, 650, 0.5, "");
	}
	
	public void roundUpTest() {
		assertEquals(qtyCal.roundUp(1).intValue(), 300);
	}

	public void roundUpTest2() {
		assertEquals(qtyCal.roundUp(300).intValue(), 300);
	}

	public void roundUpTest3() {
		assertEquals(qtyCal.roundUp(400).intValue(), 600);
	}
	
	public void roundDownTest() {
		assertEquals(qtyCal.roundDown(1000).intValue(), 900);
	}
	
	public void roundDownTest2() {
		assertEquals(qtyCal.roundDown(9000).intValue(), 8700);
	}

	public void roundDownTest3() {
		assertEquals(qtyCal.roundDown(250).intValue(), 0);
	}
	

	public void calQtyIndicatorTest() {
		assertEquals(qtyCal.calQtyIndicator(), "MOV");
	}
	
	
	public void calQtyIndicatorTest2() {
		qtyCal.setMoq(1400);
		assertEquals(qtyCal.calQtyIndicator(), "MOQ");
	}
	
	public void qtyIndicatorTest3() {
		qtyCal.setMoq(null);
		assertEquals(qtyCal.calQtyIndicator(), "MOV");
	}
	
	public void qtyIndicatorTest4() {
		qtyCal.setMov(null);
		assertEquals(qtyCal.calQtyIndicator(), "MOQ");
	}
	
	public void calQtyIndicatorTest6() {
		qtyCal.setQtyIndicator("a specific qty indicator");
		assertEquals(qtyCal.calQtyIndicator(), "a specific qty indicator");
	}
	
	public void calMoqTest1() {
		assertEquals(qtyCal.calMoq().intValue(), 1500);
	}
	
	public void calMoqTest2() {
		qtyCal.setMov(800);
		assertEquals(qtyCal.calMoq().intValue(), 1800);
	}

	public void calMoqTest3() {
		qtyCal.setMov(500);
		assertEquals(qtyCal.calMoq().intValue(), 1100);
	}

	

	
	public void calQuotedQtyTest1() {
		qtyCal.setRequiredQty(1000);
		assertEquals(qtyCal.calQuotedQty().intValue(), 1500);
	}
	
	public void calQuotedQtyTest11() {
		qtyCal.setRequiredQty(1600);
		assertEquals(qtyCal.calQuotedQty().intValue(), 1800);
	}

	public void calQuotedQtyTest12() {
		qtyCal.setRequiredQty(1800);
		assertEquals(qtyCal.calQuotedQty().intValue(), 1800);
	}

	
	public void calQuotedQtyTest2() {
		qtyCal.setQtyIndicator("MOQ");
		qtyCal.setRequiredQty(800);
		assertEquals(qtyCal.calQuotedQty().intValue(),1100);
//		qtyCal.setRequiredQty(1900);
//		assertEquals(qtyCal.calQuotedQty().intValue(),2100);
	}

	public void calQuotedQtyTest21() {
		
		qtyCal.setQtyIndicator("MOV");
		qtyCal.setRequiredQty(800);
		assertEquals(qtyCal.calQuotedQty().intValue(),1500);
//		qtyCal.setRequiredQty(1900);
//		assertEquals(qtyCal.calQuotedQty().intValue(),2100);
		
	}

	public void calQuotedQtyTest22() {
		
		qtyCal.setQtyIndicator("MPQ");
		qtyCal.setRequiredQty(800);
		assertEquals(qtyCal.calQuotedQty().intValue(),1500);
//		qtyCal.setRequiredQty(1900);
//		assertEquals(qtyCal.calQuotedQty().intValue(), 2100);
		
	}

	public void calQuotedQtyTest23() {
		qtyCal.setQtyIndicator("EXACT");
		qtyCal.setRequiredQty(1);
		assertEquals(qtyCal.calQuotedQty().intValue(),1);
//		qtyCal.setRequiredQty(1000);
//		assertEquals(qtyCal.calQuotedQty().intValue(),1000);
		
	}

	public void calQuotedQtyTest24() {
		qtyCal.setQtyIndicator("25%");
		qtyCal.setRequiredQty(800);
		assertEquals(qtyCal.calQuotedQty().intValue(),1500);
//		qtyCal.setRequiredQty(1900);
//		assertEquals(qtyCal.calQuotedQty().intValue(),2100);
	}

	
	
	
	public void calQuotedQtyTest3() {
		qtyCal.setRequiredQty(1000);
		assertEquals(qtyCal.calQuotedQty().intValue(), 1500);
//		qtyCal.setRequiredQty(1600);
//		assertEquals(qtyCal.calQuotedQty().intValue(), 1800);
//		qtyCal.setRequiredQty(1800);
//		assertEquals(qtyCal.calQuotedQty().intValue(), 1800);
	}

	
}
